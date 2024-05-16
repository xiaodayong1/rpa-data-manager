package com.ruoyi.rpa.web.service.template.bankTemp;

import cn.hutool.core.collection.CollectionUtil;
import com.ruoyi.rpa.util.ExcelUtil;
import com.ruoyi.rpa.web.service.template.HandleTemplate;
import com.ruoyi.rpa.web.service.template.templateInput.YBSHInput;
import com.ruoyi.system.domain.rpa.transaction.StandardTransaction;
import com.ruoyi.system.domain.rpa.transaction.Transaction;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class YBSY extends HandleTemplate {
    @Override
    protected HashMap<String, Object> handleExcelHead(Workbook xwb) {
        final YBSHInput ybshInput = new YBSHInput();
        return ExcelUtil.readExcelIndexData(xwb,ybshInput.getClass(),0,6);
    }

    @Override
    protected List<LinkedHashMap<String, Object>> handleExcelData(Workbook xwb) {
        return ExcelUtil.importExcelData(xwb,6);
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
        dataMap.stream().forEach(item -> {
            T transaction;
            try {
                transaction = (T) getTransactionClass().newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                throw new RuntimeException("Error creating instance of Transaction class", e);
            }
            transaction.setSerialNumber(item.get("流水号").toString());
            transaction.setRemark(item.get("附言").toString());
            transaction.setTransactionTime(item.get("交易时间").toString());
            final Object tranAccount = item.get("收入");
            if (Objects.nonNull(tranAccount) && tranAccount.toString() != "-"){
                transaction.setTransactionType("C");
                transaction.setTransactionAmount(tranAccount.toString());
            } else {
                transaction.setTransactionType("D");
                transaction.setTransactionAmount(item.get("支出").toString());
            }
            transaction.setTradeAccountNum(headMap.get("tradeAccountNum").toString());
            transaction.setTradeAccountName(headMap.get("tradeAccountName").toString());
            transaction.setCurrency("CNY");
            transaction.setCounterpartyBankName(item.get("对方银行").toString());
            transaction.setCounterpartyAccountName(item.get("对方名称").toString());
            transaction.setCounterpartyAccountNum(item.get("对方账号").toString());
            transactions.add(transaction);
        });
        return transactions;
    }
}
