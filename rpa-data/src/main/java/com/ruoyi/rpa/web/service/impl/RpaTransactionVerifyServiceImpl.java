package com.ruoyi.rpa.web.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.rpa.web.service.RpaTransactionVerifyService;
import com.ruoyi.system.domain.rpa.RpaTransactionVerify;
import com.ruoyi.system.domain.rpa.transaction.Balance;
import com.ruoyi.system.mapper.rpa.RpaBalanceHistoryMapper;
import com.ruoyi.system.mapper.rpa.RpaTransactionVerifyMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class RpaTransactionVerifyServiceImpl implements RpaTransactionVerifyService {

    @Autowired
    private RpaTransactionVerifyMapper rpaTransactionVerifyMapper;

    @Autowired
    private RpaBalanceHistoryMapper rpaBalanceHistoryMapper;

    @Override
    public List<String> selectListAccount() {
        final QueryWrapper<RpaTransactionVerify> wrapper = new QueryWrapper<>();
        wrapper.select("distinct(trade_account_num) as trade_account_num").isNotNull("balance");
        final List<RpaTransactionVerify> rpaTransactionVerifies = rpaTransactionVerifyMapper.selectList(wrapper);
        if (CollectionUtils.isEmpty(rpaTransactionVerifies)){
            return Collections.EMPTY_LIST;
        }
        return rpaTransactionVerifies.stream().map(item -> item.getTradeAccountNum()).collect(Collectors.toList());
    }

    @Override
    public List<String> selectListBank() {
        final QueryWrapper<RpaTransactionVerify> wrapper = new QueryWrapper<>();
        wrapper.select("distinct(trade_bank_name) as trade_bank_name").isNotNull("balance");
        final List<RpaTransactionVerify> rpaTransactionVerifies = rpaTransactionVerifyMapper.selectList(wrapper);
        if (CollectionUtils.isEmpty(rpaTransactionVerifies)){
            return Collections.EMPTY_LIST;
        }
        return rpaTransactionVerifies.stream().map(item -> item.getTradeBankName()).collect(Collectors.toList());
    }

    @Override
    public AjaxResult updateTransactionVerify(RpaTransactionVerify rpaTransactionVerify) {
        final UpdateWrapper<RpaTransactionVerify> rpaTransactionVerifyUpdateWrapper = new UpdateWrapper<>();
        try {
            rpaTransactionVerifyMapper.updateById(rpaTransactionVerify);
        }catch (Exception e){
            return AjaxResult.error("修改数据失败" + rpaTransactionVerify);
        }
        return AjaxResult.success();
    }

    @Override
    public RpaTransactionVerify getVerifyByid(Long id) {
        return rpaTransactionVerifyMapper.selectById(id);
    }

    @Override
    public List<Balance> historyBalance(Balance balance) {
        final QueryWrapper<Balance> balanceQueryWrapper = new QueryWrapper<>();
        balanceQueryWrapper.eq(Objects.nonNull(balance.getAccountNum()),"account_num",balance.getAccountNum());
        balanceQueryWrapper.like(Objects.nonNull(balance.getBalanceDate()),"balance_date",balance.getBalanceDate());
        return rpaBalanceHistoryMapper.selectList(balanceQueryWrapper);
    }


}
