package com.ruoyi.system.domain.rpa;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@TableName("rpa_balance_calculate")
@Data
public class RpaBalanceCalculate {
    @TableId(type = IdType.AUTO)
    private Long id;

    private BigDecimal balance;

    private String tradeAccountNum;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    private String batchNo;

    private String calculateDate;
}
