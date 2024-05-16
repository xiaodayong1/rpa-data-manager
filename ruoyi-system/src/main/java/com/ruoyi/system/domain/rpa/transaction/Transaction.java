package com.ruoyi.system.domain.rpa.transaction;

import lombok.Data;

@Data
public class Transaction {
    private String serialNumber = "-";
    private String tradeAccountNum = "-";
    //交易账户名
    private String tradeAccountName = "-";
    private String tradeBankName = "-";
    private String counterpartyAccountNum = "-";
    private String counterpartyAccountName = "-";
    private String counterpartyBankName = "-";
    private String transactionAmount = "-";
    private String transactionTime = "-";
    private String transactionType = "-";
    private String remark = "-";
    private String currency = "-";

    public void setBalance(String balance) {
    }
}

