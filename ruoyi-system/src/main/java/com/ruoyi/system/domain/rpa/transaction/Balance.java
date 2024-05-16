package com.ruoyi.system.domain.rpa.transaction;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;

@Data
@TableName("rpa_balance_history")
public class Balance {
    //开户行名称
    private String accountBankName;
    //账户名
    private String accountName;
    //账户号
    private String accountNum;
    //余额
    private BigDecimal balance;
    //余额时间
    private String balanceDate;
    //币种
    private String currency;
}

