package com.ruoyi.system.domain.rpa;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("rpa_transaction_verify")
public class RpaTransactionVerify {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String serialNumber;

    private String tradeAccountNum;

    private String tradeAccountName;

    private String tradeBankName;

    private String counterpartyAccountNum;

    private String counterpartyAccountName;

    private String counterpartyBankName;

    private BigDecimal transactionAmount;

    private String transactionTime;

    private String transactionType;

    private String remark;

    private String currency;

    private BigDecimal balance;

    private String detailSqlNo;

    @TableField(fill = FieldFill.INSERT)
    private Integer syncType;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.UPDATE)
    private LocalDateTime lastModifyTime;

    @TableField("is_active")
    private Boolean isActive;
}
