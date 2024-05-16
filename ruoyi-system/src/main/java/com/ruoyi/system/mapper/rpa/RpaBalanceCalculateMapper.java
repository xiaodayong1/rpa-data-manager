package com.ruoyi.system.mapper.rpa;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.system.domain.rpa.RpaBalanceCalculate;
import org.apache.ibatis.annotations.Insert;

import java.util.List;

public interface RpaBalanceCalculateMapper extends BaseMapper<RpaBalanceCalculate> {
    @Insert("<script>" +
            "INSERT INTO rpa_balance_calculate (balance, trade_account_num, batch_no, calculate_date) VALUES " +
            "<foreach collection='list' item='item' separator=','>" +
            "(#{item.balance}, #{item.tradeAccountNum}, #{item.batchNo}, #{item.calculateDate})" +
            "</foreach>" +
            "</script>")
    void batchInsert(List<RpaBalanceCalculate> collect);
}
