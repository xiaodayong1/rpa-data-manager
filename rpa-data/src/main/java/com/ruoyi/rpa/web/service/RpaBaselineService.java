package com.ruoyi.rpa.web.service;

import com.ruoyi.system.domain.rpa.RpaTransactionBaseLine;
import com.ruoyi.system.domain.rpa.RpaTransactionVerify;

import java.util.List;

public interface RpaBaselineService {

    List<RpaTransactionBaseLine> selectBaselineList(RpaTransactionBaseLine rpaTransactionBaseLine);

    void deleteIds(Long[] ids);
}
