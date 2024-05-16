package com.ruoyi.rpa.web.service.template.bankTemp;

import com.ruoyi.rpa.util.DataUtil;
import com.ruoyi.rpa.util.ExcelUtil;
import com.ruoyi.rpa.web.service.template.HandleTemplate;
import com.ruoyi.rpa.web.service.template.templateInput.BOCInput;
import com.ruoyi.system.domain.rpa.transaction.StandardTransaction;
import com.ruoyi.system.domain.rpa.transaction.Transaction;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

@Slf4j
@Component
public class BOC extends HandleTemplate {

    @Override
    protected HashMap<String, Object> handleExcelHead(Workbook xwb) {
        log.info("处理中国银行表头数据");
        final BOCInput bocInput = new BOCInput();
        ExcelUtil.readExcelIndexData(xwb, bocInput.getClass(), 0, 1);
        return null;
    }

    @Override
    protected List<LinkedHashMap<String, Object>> handleExcelData(Workbook xwb) {
        return ExcelUtil.importExcelData(xwb, 8);
    }

    @Override
    protected Class<? extends Transaction> getTransactionClass() {
        return StandardTransaction.class;
    }

    @Override
    protected <T extends Transaction> List<T> fillData(HashMap<String, Object> headMap, List<LinkedHashMap<String, Object>> dataMap) {
        final ArrayList<T> outJsonBeans = new ArrayList<>();
        if (CollectionUtils.isEmpty(dataMap)) {
            return outJsonBeans;
        }
        dataMap.stream().forEach(item -> {
            T outJsonBean;
            try {
                outJsonBean = (T) getTransactionClass().newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                throw new RuntimeException("Error creating instance of Transaction class", e);
            }
            if (item.get("交易流水号[Transactionreferencenumber]").toString().equals("-")) {
                return;
            }
            outJsonBean.setSerialNumber(item.get("交易流水号[Transactionreferencenumber]").toString());
            outJsonBean.setRemark(item.get("摘要[Reference]").toString());
            outJsonBean.setCurrency("CNY");
            final String tranDate = item.get("交易日期[TransactionDate]").toString() + " " + item.get("交易时间[Transactiontime]").toString();
            outJsonBean.setTransactionTime(DataUtil.formatToStandardDateTime("yyyyMMdd HH:mm:ss", tranDate));
            final String tranAmount = item.get("交易金额[TradeAmount]").toString();
            if (tranAmount.contains("-")) {
                // 含有-号 证明是借 支出
                outJsonBean.setTransactionType("D");
                outJsonBean.setTransactionAmount(item.get("交易金额[TradeAmount]").toString().replaceAll("-", ""));

                outJsonBean.setCounterpartyAccountNum(item.get("收款人账号[Payee'sAccountNumber]").toString());
                outJsonBean.setCounterpartyAccountName(item.get("收款人名称[Payee'sName]").toString());
                outJsonBean.setCounterpartyBankName(item.get("收款人名称[Payee'sName]").toString());
                outJsonBean.setTradeAccountName("付款人名称[Payer'sName]");
                outJsonBean.setTradeAccountNum(item.get("付款人账号[DebitAccountNo.]").toString());
                outJsonBean.setTradeBankName(item.get("付款人开户行名[Payeraccountbank]").toString());
            } else {
                outJsonBean.setTransactionType("C");
                outJsonBean.setTransactionAmount(item.get("交易金额[TradeAmount]").toString().replaceAll("-", ""));
                outJsonBean.setCounterpartyAccountNum(item.get("付款人账号[DebitAccountNo.]").toString());
                outJsonBean.setCounterpartyAccountName(item.get("付款人名称[Payer'sName]").toString());
                outJsonBean.setCounterpartyBankName(item.get("付款人开户行名[Payeraccountbank]").toString());
                outJsonBean.setTradeAccountName(item.get("收款人名称[Payee'sName]").toString());
                outJsonBean.setTradeAccountNum(item.get("收款人账号[Payee'sAccountNumber]").toString());
                outJsonBean.setTradeBankName(item.get("收款人开户行名[Beneficiaryaccountbank]").toString());
            }
            outJsonBean.setBalance(item.get("交易后余额[After-transactionbalance]").toString().replaceAll(",",""));
            outJsonBeans.add(outJsonBean);
        });
        return outJsonBeans;
    }
}
