package com.ruoyi.system.mapper.rpa;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.system.domain.rpa.RpaTransactionVerify;
import com.ruoyi.system.domain.rpa.transaction.Balance;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

public interface RpaTransactionVerifyMapper extends BaseMapper<RpaTransactionVerify> {

    @Select("SELECT DISTINCT trade_account_num FROM rpa_transaction_verify")
    List<String> selectDistinctTradeAccountNum();

    @Insert({
            "<script>",
            "INSERT INTO rpa_transaction_verify (serial_number, trade_account_num, trade_account_name, trade_bank_name, " +
                    "counterparty_account_num, counterparty_account_name, counterparty_bank_name, transaction_amount, " +
                    "transaction_time, transaction_type, remark, currency, balance, detail_sql_no, sync_type, create_time, last_modify_time) VALUES ",
            "<foreach collection='list' item='item' index='index' separator=','>",
            "(#{item.serialNumber}, #{item.tradeAccountNum}, #{item.tradeAccountName}, #{item.tradeBankName}, " +
                    "#{item.counterpartyAccountNum}, #{item.counterpartyAccountName}, #{item.counterpartyBankName}, " +
                    "#{item.transactionAmount}, #{item.transactionTime}, #{item.transactionType}, #{item.remark}, " +
                    "#{item.currency}, #{item.balance}, #{item.detailSqlNo}, #{item.syncType}, #{item.createTime}, #{item.lastModifyTime})",
            "</foreach>",
            "ON DUPLICATE KEY UPDATE",
            "`serial_number` = VALUES(`serial_number`),",
            "`trade_account_name` = VALUES(`trade_account_name`),",
            "`trade_bank_name` = VALUES(`trade_bank_name`),",
            "`counterparty_account_num` = VALUES(`counterparty_account_num`),",
            "`counterparty_account_name` = VALUES(`counterparty_account_name`),",
            "`counterparty_bank_name` = VALUES(`counterparty_bank_name`),",
            "`transaction_amount` = VALUES(`transaction_amount`),",
            "`transaction_time` = VALUES(`transaction_time`),",
            "`transaction_type` = VALUES(`transaction_type`),",
            "`remark` = VALUES(`remark`),",
            "`currency` = VALUES(`currency`),",
            "`balance` = VALUES(`balance`),",
            "`last_modify_time` = VALUES(`last_modify_time`)",
            "</script>"
    })
    void batchInsert(@Param("list") List<RpaTransactionVerify> rpaVerifyList);

    @Update({
            "<script>",
            "UPDATE rpa_transaction_verify SET sync_type = 1 WHERE id IN ",
            "<foreach item='item' index='index' collection='ids' open='(' separator=',' close=')'>",
            "#{item}",
            "</foreach>",
            "</script>"
    })
    int updateSyncTypeById(@Param("ids") List<Long> ids);

    @Update({
            "<script>",
            "UPDATE rpa_transaction_verify SET sync_type = 0 WHERE id &lt;= #{id} and trade_account_num = #{tradeAccountNum}",
            "</script>"
    })
    int updateSyncTypeLtId(@Param("id") Long id,@Param("tradeAccountNum") String tradeAccountNum);

    @Select("SELECT * FROM rpa_transaction_verify " +
            "WHERE trade_account_num = #{tradeAccountNum} " +
            "AND id <= (SELECT verify_id FROM rpa_transaction_baseline WHERE trade_account_num = #{tradeAccountNum}) " +
            "AND DATE(transaction_time) = #{date} " +
            "ORDER BY transaction_time DESC, id DESC")
    List<RpaTransactionVerify> selectByTradeAccountNumTransactionJoinBaseLine(@Param("tradeAccountNum") String tradeAccountNum, @Param("date") String date);

    @Select({
            "<script>",
            "SELECT * ",
            "FROM (",
            "    SELECT #{serialNumberList[0]} as id ",
            "    <foreach item='item' index='index' collection='serialNumberList' open='UNION ALL' separator='UNION ALL' close='' >",
            "       SELECT #{item} ",
            "    </foreach>",
            ") AS main_table ",
            "WHERE main_table.id NOT IN (SELECT serial_number FROM rpa_transaction_verify)",
            "</script>"
    })
    List<String> lookupPresentSerialNumber(@Param("serialNumberList") List<String> serialNumberList);

    @Update({
            "<script>",
            "UPDATE rpa_transaction_verify SET detail_sql_no = serial_number WHERE serial_number IN ",
            "<foreach item='item' index='index' collection='serialNumberList' open='(' separator=',' close=')'>",
            "#{item}",
            "</foreach>",
            "</script>"
    })
    void updateBySerialNumber(@Param("serialNumberList") List<String> serialNumberList);

    @Insert({
            "<script>",
            "INSERT INTO rpa_balance_history (account_num, account_name, account_bank_name, balance, balance_date, currency) ",
            "VALUES ",
            "<foreach collection='list' item='item' separator=','>",
            "(#{item.accountNum}, #{item.accountName}, #{item.accountBankName}, #{item.balance}, #{item.balanceDate}, #{item.currency})",
            "</foreach>",
            "</script>"
    })
    int insertHistoryBalance(@Param("list") List<Balance> balances);
}
