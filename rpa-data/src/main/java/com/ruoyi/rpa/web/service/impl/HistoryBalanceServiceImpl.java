package com.ruoyi.rpa.web.service.impl;

import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson2.JSON;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.rpa.util.SignVerifyUtil;
import com.ruoyi.rpa.web.service.IHistoryBalanceService;
import com.ruoyi.system.domain.rpa.AccountDetailsPageDto;
import com.ruoyi.system.domain.rpa.RpaTransactionVerify;
import com.ruoyi.system.domain.rpa.transaction.*;
import com.ruoyi.system.mapper.rpa.RpaTransactionVerifyMapper;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class HistoryBalanceServiceImpl implements IHistoryBalanceService {
    //民航电子 192.168.220.112
    //宜宾天原 100.100.2.85:9091
    //测试 43.136.242.93:10443
//    private static final String API_ENDPOINT = "http://192.168.220.112:9091/v1/erp.do";
    private static final String API_ENDPOINT = "http://100.100.2.85:9091/v1/erp.do";
    private static final String PRIVATE_KEY_PATH = "lib/private.pfx";

    private final ModelMapper modelMapper = new ModelMapper();

    @Autowired
    private RpaTransactionVerifyMapper rpaTransactionVerifyMapper;

    /**
     * 账户余额数据推送接口
     *
     * @return
     */
    @Override
    public AjaxResult historyBalance(BalanceDto balanceDto) {

        // 取值
        String balanceStr = balanceDto.getBalanceStr();
        BigDecimal balanceBig = new BigDecimal(balanceStr);
        String currency = balanceDto.getCurrency();
        String accountName = balanceDto.getAccountName();
        String accountNum = balanceDto.getAccountNum();
        String accountBankName = balanceDto.getAccountBankName();

        // 创建 Balance 对象
        Balance balance = new Balance();
        balance.setBalance(balanceBig);
        balance.setCurrency(currency);
        balance.setAccountName(accountName);
        balance.setAccountNum(accountNum);
        balance.setAccountBankName(accountBankName);
        String date = balanceDto.getBalanceDate();
        // 设置余额日期
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        balance.setBalanceDate(dateFormat.format(new Date()));

        // 创建 HTTP 请求
        HashMap<String, String> headers = createHead("/access/treasury/account/historyBalance/batchInsert");
        List<Balance> balanceList = new ArrayList<>();
        balanceList.add(balance);
        AccountData accountData = createAccount(balanceList);
        RequestParam requestParam = createRequest(accountData);

        // 发送 HTTP 请求
        HttpResponse execute = HttpUtil.createPost(API_ENDPOINT).addHeaders(headers).body(JSON.toJSONString(requestParam)).execute();
        if (StringUtils.hasLength(execute.body()) && execute.body().toLowerCase().contains("success")){
            // 导入成功后入库
            try{
                balanceList.stream().forEach(item -> {
                    if (StringUtils.hasLength(date)){
                        item.setBalanceDate(date);
                    }
                });
                rpaTransactionVerifyMapper.insertHistoryBalance(balanceList);
            }catch (Exception e){
                return AjaxResult.error("司库导入成功,本地数据导入失败，请手动补全数据" + e.getMessage());
            }
        }
        return AjaxResult.success();
    }

    private AccountData createAccount(List<Balance> balanceList) {
        AccountData accountData = new AccountData();
        accountData.setData(new AccountData.Data() {{
            setBalanceList(balanceList);
        }});
        return accountData;
    }

    /**
     * 账户交易明细数据推送接口
     *
     * @return
     */
    @Override
    public AjaxResult transaction(StandardTransaction standardTransaction) throws IOException {
        List<StandardTransaction> standardTransactionList = Collections.singletonList(standardTransaction);
        return handleTransaction(standardTransactionList);
    }

    @Override
    public AjaxResult transactionList(BatchStrandTransaction batchStrandTransaction) throws IOException {
        List<StandardTransaction> standardTransactionList = batchStrandTransaction.getStandardTransactionList();

        if (CollectionUtils.isEmpty(standardTransactionList)) {
            return AjaxResult.error("没有明细数据");
        }

        if (batchStrandTransaction.isDesc()) {
            Collections.reverse(standardTransactionList);
        }

        return handleTransaction(standardTransactionList);
    }

    private AjaxResult handleTransaction(List<StandardTransaction> standardTransactionList) throws IOException {
        String res = bantchTransactionPush(standardTransactionList);

        if (!StringUtils.isEmpty(res) && res.toLowerCase().contains("success")) {
            try {
                List<RpaTransactionVerify> collect = standardTransactionList.stream()
                        .map(standardTransaction -> {
                            RpaTransactionVerify map = modelMapper.map(standardTransaction, RpaTransactionVerify.class);
//                            map.setTransactionTime(LocalDateTime.parse(standardTransaction.getTransactionTime(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                            return map;
                        })
                        .collect(Collectors.toList());

                rpaTransactionVerifyMapper.batchInsert(collect);
            } catch (Exception e) {
                return AjaxResult.error("司库导入成功,本地数据导入失败，请手动补全数据");
            }
        }
        else {
            return AjaxResult.error("导入失败");
        }

        return AjaxResult.success("导入成功");
    }

    /**
     * 电子回单数据推送接口
     *
     * @return
     */
    @Override
    public AjaxResult electronicTransaction(ElectronicTransactionDto electronicTransactionDto) {
        List<ElectronicEntry> electronicList = electronicTransactionDto.getElectronicList();
        if (CollectionUtils.isEmpty(electronicList)){
            return AjaxResult.error("没有需要推送的回单详情数据");
        }
        List<String> collectDetailSqlNo = electronicList.stream().map(item -> {
            return item.getDetailSqNo();
        }).collect(Collectors.toList());
        // 先校验是否缺失对应的明细
        final List<String> serialList = rpaTransactionVerifyMapper.lookupPresentSerialNumber(collectDetailSqlNo);
        if (!CollectionUtils.isEmpty(serialList)){
            return AjaxResult.error("以下回单详情缺失对应明细数据,请先上传明细" + String.join(",", serialList));
        }
        // 批量上传回单详情
        ElectronicTransaction electronicTransaction = new ElectronicTransaction();
        electronicTransaction.setData(new ElectronicTransaction.Data() {{
            setElectronicList(electronicList);
        }});
        ElectronicTransaction.Data data = electronicTransaction.getData();
        data.setElectronicList(electronicList);
        System.out.println(data);
        HashMap<String, String> headers = createHead("/access/treasury/account/electronic/batchInsert");
        RequestParam requestParam = createRequest(electronicTransaction);
        System.out.println(JSON.toJSONString(requestParam));
        HttpResponse execute = HttpUtil.createPost(API_ENDPOINT).addHeaders(headers).body(JSON.toJSONString(requestParam)).execute();
        // 上传成功后修改明细表数据
        if (execute.body().toLowerCase().contains("success")){
            try {
                rpaTransactionVerifyMapper.updateBySerialNumber(collectDetailSqlNo);
            }catch (Exception e){
                return AjaxResult.error("上传司库成功,本地化修改数据失败，请刷新数据");
            }
        }else {
            return AjaxResult.error("批量上传司库回单详情失败");
        }
        return AjaxResult.success();
    }

    /**
     * 电子回单文件推送接口
     *
     * @return
     */
    @Override
    public Object electronicFile(ElectronicFileDto electronicFileDto) throws IOException {
        String electronFile = electronicFileDto.getFile();
        // 电子回单压缩包读取
        File file = new File(electronFile);
        InputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(file);
            byte[] fileContent = new byte[(int) file.length()];
            fileInputStream.read(fileContent);
            // Base64转换
            String encodedString = Base64.getEncoder().encodeToString(fileContent);
            ElectronicFileData electronicFileData = new ElectronicFileData();
            electronicFileData.setData(new ElectronicFileData.Data() {{
                setFile(encodedString);
            }});
            // 创建 HTTP 请求
            HashMap<String, String> headers = createHead("/access/treasury/account/electronic/batchInsertFile");
            RequestParam requestParam = createRequest(electronicFileData);
            // 发起 HTTP 请求
            HttpResponse execute = HttpUtil.createPost(API_ENDPOINT).addHeaders(headers).body(JSON.toJSONString(requestParam)).execute();
            System.out.println(execute.body());
            // 关流
            fileInputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            fileInputStream.close();
        }
        return null;
    }


    private String bantchTransactionPush(List<StandardTransaction> standardTransactionList) throws IOException {
        HashMap<String, String> headers = createHead("/access/treasury/account/transaction/batchInsert");
        TradeAccountData tradeAccountData = createTransactionReq(standardTransactionList);
        RequestParam requestParam = createRequest(tradeAccountData);

        log.info(JSON.toJSONString(requestParam));
        System.out.println(JSON.toJSONString(requestParam));
        log.info(requestParam.getReqSignData());
        System.out.println(requestParam.getReqSignData());
        String res;
        // 发送 HTTP 请求
        try {
            HttpResponse execute = HttpUtil.createPost(API_ENDPOINT).addHeaders(headers).body(JSON.toJSONString(requestParam)).execute();
            // 校验 HTTP 响应状态码
            log.info(execute.body());
            res = execute.body();
        } catch (Exception e) {
            // 处理 HttpRuntimeException
            throw new IOException("HTTP请求失败：" + e.getMessage(), e);
        }
        return res;
    }

    private HashMap<String, String> createHead(String accessApiCode) {
        HashMap<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        // 民航电子: CAACET-OPENAPI 宜宾天原: YB-TIANYUAN
        headers.put("Access-Channel-Code", "CAACET-OPENAPI");
        headers.put("Access-Api-Code", accessApiCode);
        return headers;
    }

    private TradeAccountData createTransactionReq(List<StandardTransaction> standardTransactionList) {
        TradeAccountData tradeAccountData = new TradeAccountData();
        tradeAccountData.setData(new TradeAccountData.Data() {{
            setDetailRequestList(standardTransactionList);
        }});
        return tradeAccountData;
    }

    private RequestParam createRequest(Object obj) {
        String json = JSON.toJSONString(obj);
        ClassLoader classLoader = getClass().getClassLoader();
        String sign;
        RequestParam requestParam = new RequestParam();

        try (InputStream inputStream = classLoader.getResourceAsStream(PRIVATE_KEY_PATH)) {
            if (inputStream != null) {
                log.info("处理私钥信息");
                // 处理文件内容
                sign = SignVerifyUtil.sign(json, inputStream);
                requestParam.setReqSignData(sign);
                requestParam.setReqData(obj);
            }
        } catch (Exception e) {
            // 处理 IOException
            throw new RuntimeException("处理私钥异常" + e);
        }
        return requestParam;
    }

    /**
     * 通过账户详情页数据传输对象获取账户详情页信息。
     * @param accountDetailsPageDto 账户详情页的数据传输对象，包含账户详情页的必要信息。
     * @return 返回从API端获取的账户交易详情的响应体字符串。
     */
    public Object accountDetailsPage(AccountDetailsPageDto accountDetailsPageDto)
    {
        // 根据账户详情页数据传输对象获取账户详情页实例
        AccountDetailsPage accountDetailsPage = getAccountDetailsPage(accountDetailsPageDto);

        // 创建请求头
        HashMap headers = createHead("/access/treasury/account/transactionDetails/listByPage");

        // 根据账户详情页创建请求参数
        RequestParam requestParam = createRequest(accountDetailsPage);

        // 打印请求参数的JSON字符串
        System.out.println(JSONUtil.toJsonStr(requestParam));

        // 发起POST请求并执行
        HttpResponse execute = HttpUtil.createPost(API_ENDPOINT).addHeaders(headers).body(JSONUtil.toJsonStr(requestParam)).execute();

        // 打印响应体
        String body = execute.body();
        System.out.println(body);

        // 返回响应体字符串
        return body;
    }


    /**
     * 根据账户详情页面数据传输对象生成账户详情页面对象。
     *
     * @param accountDetailsPageDto 账户详情页面数据传输对象，包含分页信息和查询条件。
     * @return AccountDetailsPage 账户详情页面对象，包含根据查询条件检索到的账户详情信息。
     * @throws IllegalArgumentException 如果账户详情页面数据传输对象为null，或者包含的页码、页面大小为null或非正数，将抛出此异常。
     */
    private static AccountDetailsPage getAccountDetailsPage(AccountDetailsPageDto accountDetailsPageDto)
    {
        // 验证输入参数是否合法
        if (accountDetailsPageDto == null) {
            throw new IllegalArgumentException("accountDetailsPageDto cannot be null");
        }
        Number pageNum = accountDetailsPageDto.getPageNum();
        Number pageSize = accountDetailsPageDto.getPageSize();
        // 确保页码和页面大小既不为null也是正数
        if ((pageNum == null) || (pageSize == null)) {
            throw new IllegalArgumentException("PageNum and PageSize cannot be null");
        }
        if ((pageNum.intValue() <= 0) || (pageSize.intValue() <= 0)) {
            throw new IllegalArgumentException("PageNum and PageSize must be positive");
        }

        // 提取并校验查询条件
        String companyCode = accountDetailsPageDto.getCompanyCode();
        String transactionTimeStart = accountDetailsPageDto.getStartTransactionDate();
        String transactionTimeEnd = accountDetailsPageDto.getEndTransactionDate();
        String transactionType = accountDetailsPageDto.getTransactionType();
        String balanceVerifyResult = accountDetailsPageDto.getBalanceVerifyResult();
        String tradeAccountNum = accountDetailsPageDto.getTradeAccountNum();
        BigDecimal lowBalance = accountDetailsPageDto.getLowBalance();
        BigDecimal highBalance = accountDetailsPageDto.getHighBalance();
        Boolean electronicFlag = accountDetailsPageDto.getElectronicFlag();

        // 创建账户详情页面对象，并设置分页信息和查询条件
        AccountDetailsPage accountDetailsPage = new AccountDetailsPage();
        accountDetailsPage.setPageSize(pageSize);
        accountDetailsPage.setPageNum(pageNum);
        accountDetailsPage.setData(new AccountDetailsPage.Data() {{
            // 使用双重嵌套初始化账户详情页面的数据字段
            setCompanyCode(companyCode);
            setEndTransactionDate(transactionTimeEnd);
            setStartTransactionDate(transactionTimeStart);
            setTransactionType(transactionType);
            setBalanceVerifyResult(balanceVerifyResult);
            setTradeAccountNum(tradeAccountNum);
            setLowBalance(lowBalance);
            setHighBalance(highBalance);
            setElectronicFlag(electronicFlag);
        }
        });
        return accountDetailsPage;
    }
}
