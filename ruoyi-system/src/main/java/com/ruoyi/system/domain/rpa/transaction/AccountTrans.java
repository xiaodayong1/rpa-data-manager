package com.ruoyi.system.domain.rpa.transaction;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class AccountTrans {
    private String companyCode;
    private String tradeAccountNum;
    private String startTransactionDate;
    private String endTransactionDate;
    private BigDecimal highBalance;
    private BigDecimal lowBalance;
    private String transactionType;
    private Boolean electronicFlag;
    private String balanceVerifyResult;





}
