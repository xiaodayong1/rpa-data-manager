package com.ruoyi.system.domain.rpa;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;

@Data
@TableName("rpa_transaction_baseline")
public class RpaTransactionBaseLine {

    @TableId(type = IdType.AUTO)
    private Long id;
    private String tradeAccountNum;
    private Long verifyId;
    private BigDecimal balance;
    private Integer isSyncType;
}
