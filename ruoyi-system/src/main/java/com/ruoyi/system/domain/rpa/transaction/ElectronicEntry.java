package com.ruoyi.system.domain.rpa.transaction;

import lombok.Data;

@Data
public class ElectronicEntry {
    private String counterpartyAccountName;

    private String counterpartyAccountNum;

    private String counterpartyBankName;

    private String currency;

    private String detailSqNo;

    private String remark;

    private String serialNumber;

    private String tradeAccountName;

    private String tradeAccountNum;

    private String tradeBankName;

    private String transNo;

    private String transactionAmount;

    private String transactionTime;

    private String transactionType;
}
