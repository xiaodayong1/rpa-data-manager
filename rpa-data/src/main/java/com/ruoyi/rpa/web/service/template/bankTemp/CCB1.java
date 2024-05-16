package com.ruoyi.rpa.web.service.template.bankTemp;

import com.ruoyi.rpa.util.DataUtil;
import com.ruoyi.rpa.util.ExcelUtil;
import com.ruoyi.rpa.web.service.template.HandleTemplate;
import com.ruoyi.rpa.web.service.template.templateInput.CCBInput;
import com.ruoyi.system.domain.rpa.transaction.StandardTransaction;
import com.ruoyi.system.domain.rpa.transaction.Transaction;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.*;

@Component
@Slf4j
public class CCB1 extends HandleTemplate {

    @Override
    public HashMap<String, Object> handleExcelHead(Workbook xwb) {
        System.out.println("建设银行1模板处理表头");
        final CCBInput ccbInput = new CCBInput();
        return ExcelUtil.readExcelIndexData(xwb, ccbInput.getClass(), 3, 7);
    }

    @Override
    public List<LinkedHashMap<String, Object>> handleExcelData(Workbook xwb) {
        System.out.println("建设银行1模板处理表数据");
        return ExcelUtil.importExcelData(xwb, 9);
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
            outJsonBean.setSerialNumber(data.get("账户明细编号-交易流水号").toString());
            // "K2 - 摘要"
            outJsonBean.setRemark(data.get("摘要").toString());
            // "K3 - 交易时间 这个字段比较特殊，需要针对处理"
            if (!Objects.isNull(data.get("交易时间")) && data.get("交易时间").toString() != "-") {
                final String outputDateString = DataUtil.formatToStandardDateTime("yyyyMMdd HH:mm:ss", data.get("交易时间").toString());
                outJsonBean.setTransactionTime(outputDateString);
            }
            // "K4 - 交易类型"
            String stringValue = (String) data.get("借方发生额/元(支取)");
            if (stringValue != null && !stringValue.isEmpty() && stringValue != "-") {
                try {
                    Double doubleValue = Double.parseDouble(stringValue);
                    if (doubleValue == 0) {
                        outJsonBean.setTransactionType("C");
                        outJsonBean.setTransactionAmount(data.get("贷方发生额/元(收入)").toString());
                    } else {
                        outJsonBean.setTransactionType("D");
                        outJsonBean.setTransactionAmount(data.get("借方发生额/元(支取)").toString());
                    }
                } catch (NumberFormatException e) {
                    // 处理字符串无法解析为 Long 的情况
                    e.printStackTrace();
                }
            }
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
            outJsonBean.setCounterpartyAccountName(data.get("对方户名").toString());
            outJsonBean.setCurrency("CNY");
            outJsonBean.setCounterpartyBankName(data.get("对方开户机构").toString());
            outJsonBean.setBalance(data.get("余额").toString());
            outJsonBeans.add(outJsonBean);
        });
        return outJsonBeans;
    }

}
