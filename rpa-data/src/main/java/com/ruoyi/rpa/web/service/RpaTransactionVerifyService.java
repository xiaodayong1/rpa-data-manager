package com.ruoyi.rpa.web.service;

import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.system.domain.rpa.RpaTransactionVerify;
import com.ruoyi.system.domain.rpa.transaction.Balance;

import java.util.List;

public interface RpaTransactionVerifyService {
    List<String> selectListAccount();

    List<String> selectListBank();

    AjaxResult updateTransactionVerify(RpaTransactionVerify rpaTransactionVerify);

    RpaTransactionVerify getVerifyByid(Long id);

    List<Balance> historyBalance(Balance balance);
}
