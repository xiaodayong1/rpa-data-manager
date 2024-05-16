package com.ruoyi.rpa.web.service;

import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.system.domain.rpa.RpaTransactionVerify;

import java.util.List;

public interface CheckRpaService {
    AjaxResult checkTransaction(String tradeAccountNum);

    AjaxResult getAccurateBalance(String tradeAccountNum, String endTime);

    AjaxResult promoteBaseLine(String tradeAccountNum, Long id);

    List<RpaTransactionVerify> selectRpaTransactionVerifyList(RpaTransactionVerify rpaTransactionVerify1);
}
