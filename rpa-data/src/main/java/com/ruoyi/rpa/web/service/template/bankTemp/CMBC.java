package com.ruoyi.rpa.web.service.template.bankTemp;

import com.ruoyi.rpa.util.DataUtil;
import com.ruoyi.rpa.util.ExcelUtil;
import com.ruoyi.rpa.web.service.template.HandleTemplate;
import com.ruoyi.rpa.web.service.template.templateInput.CMBCInput;
import com.ruoyi.system.domain.rpa.transaction.StandardTransaction;
import com.ruoyi.system.domain.rpa.transaction.Transaction;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.*;

@Component
@Slf4j
public class CMBC extends HandleTemplate {

    @Override
    public HashMap<String, Object> handleExcelHead(Workbook xwb) {
        System.out.println("民生银行模板处理表头");
        final CMBCInput cmbcInput = new CMBCInput();
        return ExcelUtil.readExcelIndexData(xwb, cmbcInput.getClass(), 0, 12);
    }

    @Override
    public List<LinkedHashMap<String, Object>> handleExcelData(Workbook xwb) {
        System.out.println("民生银行模板处理表数据");
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
            outJsonBean.setSerialNumber(headMap.get("tradeAccountNum").toString() + "-" + System.nanoTime());
            // "K2 - 摘要"
            outJsonBean.setRemark(data.get("客户附言").toString());
            // "K3 - 交易时间 这个字段比较特殊，需要针对处理"
            if (!Objects.isNull(data.get("交易时间")) && data.get("交易时间").toString() != "-") {
                final String outputDateString = DataUtil.formatToStandardDateTime("yyyy-MM-dd HH:mm:ss", data.get("交易时间").toString());
                outJsonBean.setTransactionTime(outputDateString);
            }
            // "K4 - 交易类型"
            String stringValue = (String) data.get("借方发生额");
            if (stringValue != null && !stringValue.isEmpty() && stringValue != "-") {
                try {
                    Double doubleValue = Double.parseDouble(stringValue);
                    if (doubleValue == 0) {
                        outJsonBean.setTransactionType("C");
                        outJsonBean.setTransactionAmount(data.get("贷方发生额").toString());
                    } else {
                        outJsonBean.setTransactionType("D");
                        outJsonBean.setTransactionAmount(data.get("借方发生额").toString());
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
            outJsonBean.setCounterpartyAccountName(data.get("对方账号名称").toString());
            outJsonBean.setCurrency("CNY");
            outJsonBean.setCounterpartyBankName(data.get("对方开户行").toString());
            outJsonBean.setBalance(data.get("账户余额").toString());
            outJsonBeans.add(outJsonBean);
        });
        return outJsonBeans;
    }

}
