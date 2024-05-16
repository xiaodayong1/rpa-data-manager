package com.ruoyi.rpa.web.service.template;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.ruoyi.system.domain.rpa.ConvertData;
import com.ruoyi.system.domain.rpa.ConvertRes;
import com.ruoyi.system.domain.rpa.transaction.Transaction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.*;

@Slf4j
@Service
public abstract class HandleTemplate {

    // 1.通过路径读取文件
    // 2.解析excel
    // 3.将excel转换为json
    // 4.比对数据，填充默认值
    public final <T extends Transaction> ConvertRes handleProcess(ConvertData convertData) {
        return process(preparationData(convertData),convertData);
    }

    public final <T extends Transaction> ConvertRes handleProcess(MultipartFile file, ConvertData convertData) throws IOException {
        return process(file.getInputStream(),convertData);
    }

    public final <T extends Transaction> ConvertRes process(InputStream in, ConvertData convertData) {
        HashMap<String, Object> headMap;
        List<LinkedHashMap<String, Object>> dataMap;
        Workbook workbook;
        try  {
            workbook = WorkbookFactory.create(in);
            headMap = this.handleExcelHead(workbook);
            dataMap = this.handleExcelData(workbook);
        } catch (IOException e) {
            log.error("解析 Excel 时发生 IO 异常", e);
            return new ConvertRes(Collections.EMPTY_LIST,Collections.EMPTY_LIST) ;
        } catch (Exception e) {
            log.error("解析 Excel 时发生异常", e);
            return new ConvertRes(Collections.EMPTY_LIST,Collections.EMPTY_LIST);
        } finally {
            if(in!=null){
                try {
                    in.close();
                } catch(IOException e) {
                    log.error(e.getMessage());
                }
            }
        }
        // 填充数据
        List<T> outList = this.fillData(headMap, dataMap);
        log.info("data:{}", JSON.toJSONString(outList));
        // 数据填充完毕后对照默认值
        this.compareAndFillDefault(convertData, outList);
        return new ConvertRes(outList,Collections.EMPTY_LIST);
    }

    protected abstract HashMap<String, Object> handleExcelHead(Workbook xwb);

    protected abstract List<LinkedHashMap<String, Object>> handleExcelData(Workbook xwb);

    /**
     *  实现类中返回指定的类型，方便后续拓展多个类型 ？ extends Transaction ,目前标准版多一个余额的参数
     * @return
     */
    protected abstract Class<? extends Transaction> getTransactionClass();

    protected abstract <T extends Transaction> List<T> fillData(HashMap<String, Object> headMap, List<LinkedHashMap<String, Object>> dataMap);

    private <T extends Transaction> void compareAndFillDefault(ConvertData convertData, List<T> outJsonBeans) {
        if (CollectionUtils.isEmpty(outJsonBeans)) {
            return;
        }

        Iterator<T> iterator = outJsonBeans.iterator();
        while (iterator.hasNext()) {
            T item = iterator.next();

            if ("-".equals(item.getTransactionAmount()) && "-".equals(item.getSerialNumber())){
                iterator.remove();
            }

            if (!StringUtils.isEmpty(convertData.getTradeBankName()) && !item.getTradeBankName().equals(convertData.getTradeBankName())) {
                item.setTradeBankName(convertData.getTradeBankName());
            }
            if (!StringUtils.isEmpty(convertData.getTradeAccountName()) && !item.getTradeAccountName().equals(convertData.getTradeAccountName())) {
                item.setTradeAccountName(convertData.getTradeAccountName());
            }
            if (!StringUtils.isEmpty(convertData.getTradeAccountNum()) && !item.getTradeAccountNum().equals(convertData.getTradeAccountNum())) {
                item.setTradeAccountNum(convertData.getTradeAccountNum());
            }
        }
    }

    private InputStream preparationData(ConvertData convertData){
        File file;
        InputStream in = null;
        if (!StringUtils.isEmpty(convertData.getFilePath())){
            file = new File(convertData.getFilePath());
            try {
                in = new FileInputStream(file);
            } catch (FileNotFoundException e) {
                log.error(JSON.toJSONString(convertData) + ":" + e.getMessage() );
                throw new RuntimeException("解析文件路径不存在");
            }
        }
        if (Objects.isNull(in)){
            throw new RuntimeException("无法获取文件信息" + JSONObject.toJSONString(convertData));
        }
        return in;
    }
}
