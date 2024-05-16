package com.ruoyi.rpa.web.service.template.bankTemp;

import com.ruoyi.rpa.util.DataUtil;
import com.ruoyi.rpa.util.ExcelUtil;
import com.ruoyi.rpa.web.service.template.HandleTemplate;
import com.ruoyi.rpa.web.service.template.templateInput.CITICInput;
import com.ruoyi.system.domain.rpa.transaction.StandardTransaction;
import com.ruoyi.system.domain.rpa.transaction.Transaction;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.*;

@Component
public class CITIC extends HandleTemplate {
    @Override
    protected HashMap<String, Object> handleExcelHead(Workbook xwb) {
        System.out.println("中信银行模板处理表头");
        final CITICInput citicInput = new CITICInput();
        return ExcelUtil.readExcelIndexData(xwb, citicInput.getClass(), 4, 12);
    }

    @Override
    protected List<LinkedHashMap<String, Object>> handleExcelData(Workbook xwb) {
        System.out.println("中信银行模板处理表数据");
        return ExcelUtil.importExcelData(xwb, 13);
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
            T outJsonBean;
            try {
                outJsonBean = (T) getTransactionClass().newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                throw new RuntimeException("Error creating instance of Transaction class", e);
            }
            // "K1 - 流水"

            outJsonBean.setSerialNumber(data.get("发起方流水号").toString().equals("-")?data.get("柜员交易号").toString():data.get("发起方流水号").toString());
            // "K2 - 摘要"
            outJsonBean.setRemark(data.get("摘要").toString());
            // "K3 - 交易时间 这个字段比较特殊，需要针对处理"

            if (!Objects.isNull(data.get("交易时间")) && data.get("交易时间").toString() != "-" && !Objects.isNull(data.get("交易日期")) && data.get("交易日期").toString() != "-") {
                final String outputDateString = DataUtil.formatToStandardDateTime("yyyy-MM-dd HH:mm:ss", data.get("交易日期").toString() + " " + data.get("交易时间").toString());
                outJsonBean.setTransactionTime(outputDateString);
            }
            // "K4 - 交易类型"
            String stringValue = (String) data.get("借方发生额");
            stringValue.replaceAll(",", "");
            if (stringValue != null && !stringValue.isEmpty()) {
                if (!stringValue.equals("-")) {
                    outJsonBean.setTransactionType("D");
                    // K5
                    outJsonBean.setTransactionAmount(data.get("借方发生额").toString().replaceAll(",", ""));
                } else {
                    outJsonBean.setTransactionType("C");
                    outJsonBean.setTransactionAmount(data.get("贷方发生额").toString().replaceAll(",", ""));
                }
            }
            // k6
            if (headMap.containsKey("tradeBankName")) {
                outJsonBean.setTradeBankName(headMap.get("tradeBankName").toString());
            }
            if (headMap.containsKey("tradeAccountName")) {
                outJsonBean.setTradeAccountName(headMap.get("tradeAccountName").toString());
            }
            if (headMap.containsKey("tradeAccountNum")) {
                outJsonBean.setTradeAccountNum(headMap.get("tradeAccountNum").toString());
            }
            outJsonBean.setCounterpartyAccountNum(data.get("对方账号").toString());
            outJsonBean.setCounterpartyAccountName(data.get("对方账户名称").toString());
            outJsonBean.setCurrency("CNY");
            outJsonBean.setCounterpartyBankName(data.get("对方账号开户网点名称").toString());
            outJsonBean.setBalance(data.get("账户余额").toString().replaceAll(",",""));
            outJsonBeans.add(outJsonBean);
        });
        return outJsonBeans;
    }
}
