package com.ruoyi.system.domain.rpa;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class AccountDetailsPageDto {
    private Number pageNum;
    private Number pageSize;
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
