package com.ruoyi.rpa.web.service.template.bankTemp;

import com.ruoyi.rpa.util.DataUtil;
import com.ruoyi.rpa.util.ExcelUtil;
import com.ruoyi.rpa.web.service.template.HandleTemplate;
import com.ruoyi.rpa.web.service.template.templateInput.BCMInput;
import com.ruoyi.system.domain.rpa.transaction.StandardTransaction;
import com.ruoyi.system.domain.rpa.transaction.Transaction;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.*;

@Component
public class BCM extends HandleTemplate {
    @Override
    protected HashMap<String, Object> handleExcelHead(Workbook xwb) {
        System.out.println("交通银行模板处理表头");
        final BCMInput bcmInput = new BCMInput();
        return ExcelUtil.readExcelIndexData(xwb, bcmInput.getClass(), 0, 2);
    }

    @Override
    protected List<LinkedHashMap<String, Object>> handleExcelData(Workbook xwb) {
        System.out.println("交通银行模板处理表数据");
        return ExcelUtil.importExcelData(xwb, 1);
    }

    @Override
    protected Class<? extends Transaction> getTransactionClass() {
        return StandardTransaction.class;
    }

    @Override
    public <T extends Transaction> List<T> fillData(HashMap<String, Object> headMap, List<LinkedHashMap<String, Object>> dataMap) {
        final ArrayList<T> outJsonBeans = new ArrayList<>();
        if (CollectionUtils.isEmpty(dataMap)) {
            return Collections.EMPTY_LIST;
        }
        dataMap.stream().forEach(data -> {


            if (!data.get("交易时间").toString().equals("借方交易笔数")) {
                T outJsonBean;
                try {
                    outJsonBean = (T) getTransactionClass().newInstance();
                } catch (InstantiationException | IllegalAccessException e) {
                    throw new RuntimeException("Error creating instance of Transaction class", e);
                }
//                outJsonBean.setSerialNumber("UUID" + UUID.randomUUID());
                outJsonBean.setSerialNumber(headMap.get("tradeAccountNum").toString() + System.nanoTime());
                outJsonBean.setBalance(data.get("账户余额").toString().replaceAll(",",""));
                // "K2 - 摘要"
                if (!data.get("摘要").equals("贷方交易金额")) {
                    outJsonBean.setRemark(data.get("摘要").toString());
                }


                if (!Objects.isNull(data.get("交易时间")) && data.get("交易时间").toString() != "-" && data.get("交易时间").toString() != "借方交易笔数") {
                    final String outputDateString = DataUtil.formatToStandardDateTime("yyyy-MM-dd HH:mm:ss", data.get("交易时间").toString());
                    outJsonBean.setTransactionTime(outputDateString);
                }
                // "K4 - 交易类型"
                String stringValue = (String) data.get("借方发生额（支出）");
                if (stringValue != null && !stringValue.isEmpty() && !stringValue.equals("0.00") && data.get("交易时间").toString() != "借方交易笔数") {
                    outJsonBean.setTransactionType("D");
                    // K5
                    outJsonBean.setTransactionAmount(stringValue);
                } else {
                    outJsonBean.setTransactionType("C");
                    outJsonBean.setTransactionAmount(data.get("贷方发生额（收入）").toString());
                }
                // k6

                if (headMap.containsKey("tradeAccountNum")) {
                    outJsonBean.setTradeAccountNum(headMap.get("tradeAccountNum").toString());
                }
                if (headMap.containsKey("tradeAccountName")) {
                    outJsonBean.setTradeAccountName(headMap.get("tradeAccountName").toString());
                }
                if (data.get("交易时间").toString() != "借方交易笔数") {
                    outJsonBean.setCounterpartyAccountNum(data.get("对方账号").toString());
                }
                if (data.get("交易时间").toString() != "借方交易笔数") {
                    outJsonBean.setCounterpartyAccountName(data.get("对方户名").toString());
                }

                outJsonBean.setCurrency("CNY");

                outJsonBeans.add(outJsonBean);
            }

        });
        return outJsonBeans;
    }
}
