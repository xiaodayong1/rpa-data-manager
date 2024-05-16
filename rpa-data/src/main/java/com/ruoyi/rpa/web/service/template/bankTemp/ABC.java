package com.ruoyi.rpa.web.service.template.bankTemp;

import cn.hutool.core.collection.CollectionUtil;
import com.ruoyi.rpa.util.ExcelUtil;
import com.ruoyi.rpa.web.service.template.HandleTemplate;
import com.ruoyi.rpa.web.service.template.templateInput.ABCInput;
import com.ruoyi.system.domain.rpa.transaction.StandardTransaction;
import com.ruoyi.system.domain.rpa.transaction.Transaction;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Component
@Slf4j
public class ABC extends HandleTemplate {

    @Override
    protected HashMap<String, Object> handleExcelHead(Workbook xwb) {
        log.info("处理农业银行表头");
        final ABCInput abcInput = new ABCInput();
        return ExcelUtil.readExcelIndexData(xwb,abcInput.getClass(),1,2);
    }

    @Override
    protected List<LinkedHashMap<String, Object>> handleExcelData(Workbook xwb) {
        log.info("处理农业银行表数据");
        return ExcelUtil.importExcelData(xwb, 2);
    }

    @Override
    protected Class<? extends Transaction> getTransactionClass() {
        return StandardTransaction.class;
    }

    @Override
    protected <T extends Transaction> List<T> fillData(HashMap<String, Object> headMap, List<LinkedHashMap<String, Object>> dataMap) {
        if (CollectionUtil.isEmpty(dataMap)){
            return Collections.EMPTY_LIST;
        }
        final ArrayList<T> transactions = new ArrayList<>();


        for (Map<String, Object> item : dataMap) {
            if (item.get("交易日期").toString().equals("汇总收入金额")) {
                break ;
            }
            T transaction;
            try {
                transaction = (T) getTransactionClass().newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                throw new RuntimeException("Error creating instance of Transaction class", e);
            }
            transaction.setSerialNumber(item.get("交易日志号").toString());
            transaction.setRemark(item.get("交易摘要").toString() + item.get("交易附言").toString());
            transaction.setTradeAccountName(headMap.get("tradeAccountName").toString());
            transaction.setTradeAccountNum(headMap.get("tradeAccountNum").toString().replaceAll("-",""));
            transaction.setCurrency("CNY");
            transaction.setCounterpartyAccountName(StringUtils.isEmpty(item.get("对方户名").toString()) ? "-" : item.get("对方户名").toString());
            transaction.setCounterpartyAccountNum(item.get("对方账号").toString());
            transaction.setCounterpartyBankName(item.get("对方账户开户行").toString());

            final String getAmount = item.get("收入金额").toString();
            if (getAmount.equals("0")) {
                // 证明是支出
                transaction.setTransactionAmount(item.get("支出金额").toString());
                transaction.setTransactionType("D");
            } else {
                transaction.setTransactionAmount(getAmount);
                transaction.setTransactionType("C");
            }

            String timestampString = item.get("交易时间戳").toString();
            try {
                if (StringUtils.hasLength(timestampString)) {
                    int year = Integer.parseInt(timestampString.substring(0, 4));
                    int month = Integer.parseInt(timestampString.substring(4, 6));
                    int day = Integer.parseInt(timestampString.substring(6, 8));
                    int hour = Integer.parseInt(timestampString.substring(8, 10));
                    int minute = Integer.parseInt(timestampString.substring(10, 12));
                    int second = Integer.parseInt(timestampString.substring(12, 14));
                    Instant instant = Instant.ofEpochSecond(LocalDateTime.of(year, month, day, hour, minute, second).atZone(ZoneId.systemDefault()).toInstant().getEpochSecond());
                    LocalDateTime dateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                    transaction.setTransactionTime(dateTime.format(formatter));
                }
            } catch (Exception e) {
                log.error("转换日期格式异常");
            }
            transaction.setBalance(item.get("本次余额").toString().replaceAll(",",""));
            transactions.add(transaction);
        }

        return transactions;
    }
}
