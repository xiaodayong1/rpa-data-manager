package com.ruoyi.system.mapper.rpa;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.system.domain.rpa.RpaTransactionBaseLine;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface RpaTransactionBaseLineMapper extends BaseMapper<RpaTransactionBaseLine> {

    @Insert({
            "INSERT INTO rpa_transaction_baseline (id, trade_account_num, verify_id, balance, is_sync_type)",
            "VALUES (#{id}, #{tradeAccountNum}, #{verifyId}, #{balance}, #{isSyncType})",
            "ON DUPLICATE KEY UPDATE",
            "verify_id = VALUES(verify_id),",
            "balance = VALUES(balance),",
            "is_sync_type = VALUES(is_sync_type)"
    })
    int insertOrUpdate(RpaTransactionBaseLine rpaTransactionBaseLine);

    @Select({
            "<script>",
            "SELECT b.*",
            "FROM rpa_transaction_baseline b",
            "JOIN (",
            "SELECT trade_account_num, MAX(id) AS max_verify_id",
            "FROM rpa_transaction_verify",
            "GROUP BY trade_account_num",
            ") v ON b.trade_account_num = v.trade_account_num",
            "WHERE b.verify_id = v.max_verify_id",
            "<if test='tradeAccountNum != null'> AND b.trade_account_num = #{tradeAccountNum}</if>",
            "</script>",
    })
    List<RpaTransactionBaseLine> selectBalanceList(@Param("tradeAccountNum") String tradeAccountNum);
}
