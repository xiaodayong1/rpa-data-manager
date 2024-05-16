package com.ruoyi.rpa.web.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ruoyi.rpa.web.service.RpaBaselineService;
import com.ruoyi.system.domain.rpa.RpaTransactionBaseLine;
import com.ruoyi.system.domain.rpa.RpaTransactionVerify;
import com.ruoyi.system.mapper.rpa.RpaTransactionBaseLineMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class RpaBaselineServiceImpl implements RpaBaselineService {

    @Autowired
    private RpaTransactionBaseLineMapper rpaTransactionBaseLineMapper;

    @Override
    public List<RpaTransactionBaseLine> selectBaselineList(RpaTransactionBaseLine rpaTransactionBaseLine) {
        final QueryWrapper<RpaTransactionBaseLine> rpaTransactionBaseLineQueryWrapper = new QueryWrapper<>();
        rpaTransactionBaseLineQueryWrapper.eq(Objects.nonNull(rpaTransactionBaseLine.getTradeAccountNum()),"trade_account_num",rpaTransactionBaseLine.getTradeAccountNum());
        return rpaTransactionBaseLineMapper.selectList(rpaTransactionBaseLineQueryWrapper);
    }

    @Override
    public void deleteIds(Long[] ids) {
        if (ids.length == 0){
            return;
        }
        rpaTransactionBaseLineMapper.deleteBatchIds(Arrays.asList(ids));
    }
}
