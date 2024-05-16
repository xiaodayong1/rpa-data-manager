package com.ruoyi.system.domain.rpa.transaction;

import lombok.Data;

@Data
public class BalanceDto {
    private String accountBankName;
    private String accountName;
    private String accountNum;
    private String balanceStr;
    private String balanceDate;
    private String currency;
}
