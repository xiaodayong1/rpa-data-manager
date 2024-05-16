package com.ruoyi.rpa.web.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ruoyi.rpa.web.service.RpaCalculateService;
import com.ruoyi.system.domain.rpa.RpaBalanceCalculate;
import com.ruoyi.system.mapper.rpa.RpaBalanceCalculateMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class RpaCalculateServiceImpl implements RpaCalculateService {

    @Autowired
    private RpaBalanceCalculateMapper rpaBalanceCalculateMapper;

    @Override
    public List<RpaBalanceCalculate> selectCalculateList(RpaBalanceCalculate rpaBalanceCalculate) {
        final QueryWrapper<RpaBalanceCalculate> rpaBalanceCalculateQueryWrapper = new QueryWrapper<>();
        rpaBalanceCalculateQueryWrapper.orderByDesc("batch_no");
        rpaBalanceCalculateQueryWrapper.eq(Objects.nonNull(rpaBalanceCalculate.getTradeAccountNum()),"trade_account_num",rpaBalanceCalculate.getTradeAccountNum());
        rpaBalanceCalculateQueryWrapper.eq(Objects.nonNull(rpaBalanceCalculate.getCalculateDate()),"calculate_date",rpaBalanceCalculate.getCalculateDate());
        return rpaBalanceCalculateMapper.selectList(rpaBalanceCalculateQueryWrapper);
    }
}
