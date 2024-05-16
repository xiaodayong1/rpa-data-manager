package com.ruoyi.rpa.web.service.template.bankTemp;

import com.ruoyi.rpa.util.ExcelUtil;
import com.ruoyi.rpa.web.service.template.HandleTemplate;
import com.ruoyi.rpa.web.service.template.templateInput.CMBInput;
import com.ruoyi.system.domain.rpa.transaction.StandardTransaction;
import com.ruoyi.system.domain.rpa.transaction.Transaction;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.*;

@Component
@Slf4j
public class CMB extends HandleTemplate {

    @Override
    protected HashMap<String, Object> handleExcelHead(Workbook xwb) {
        log.info("处理招商银行表头数据");
        final CMBInput cmbInput = new CMBInput();
        return ExcelUtil.readExcelIndexData(xwb, cmbInput.getClass(), 4, 10);
    }

    @Override
    protected List<LinkedHashMap<String, Object>> handleExcelData(Workbook xwb) {
        log.info("处理招商银行表格数据");
        return ExcelUtil.importExcelData(xwb, 12);
    }

    @Override
    protected Class<? extends Transaction> getTransactionClass() {
        return StandardTransaction.class;
    }

    @Override
    protected <T extends Transaction> List<T> fillData(HashMap<String, Object> headMap, List<LinkedHashMap<String, Object>> dataMap) {
        log.info("招商银行数据填充");
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
            outJsonBean.setSerialNumber(data.get("流水号").toString());
            // "K2 - 摘要"
            outJsonBean.setRemark(data.get("摘要").toString());
            // "K3 - 交易时间 这个字段比较特殊，需要针对处理"
            if (!Objects.isNull(data.get("交易日")) && data.get("交易日").toString() != "-" && !Objects.isNull(data.get("交易时间")) && data.get("交易时间").toString() != "-") {
                outJsonBean.setTransactionTime(data.get("交易日").toString() + " " + data.get("交易时间").toString());
            }
            // "K4 - 交易类型"
            String stringValue = (String) data.get("借方金额");
            if (stringValue != null && !stringValue.isEmpty()) {
                if (stringValue.equals("-")) {
                    outJsonBean.setTransactionType("C");
                    outJsonBean.setTransactionAmount(data.get("贷方金额").toString());
                } else {
                    outJsonBean.setTransactionType("D");
                    outJsonBean.setTransactionAmount(data.get("借方金额").toString());
                }
            }
            if (headMap.containsKey("tradeAccountName")) {
                outJsonBean.setTradeAccountName(headMap.get("tradeAccountName").toString());
            }
            if (headMap.containsKey("tradeAccountNum")) {
                outJsonBean.setTradeAccountNum(headMap.get("tradeAccountNum").toString());
            }
            outJsonBean.setCounterpartyAccountNum(data.get("收(付)方账号").toString());
            outJsonBean.setCounterpartyAccountName(data.get("收(付)方名称").toString());
            outJsonBean.setCurrency("CNY");
            outJsonBean.setCounterpartyBankName(data.get("收(付)方开户行名").toString());
            outJsonBean.setBalance(data.get("余额").toString().replaceAll(",",""));
            outJsonBeans.add(outJsonBean);
        });
        return outJsonBeans;
    }
}
