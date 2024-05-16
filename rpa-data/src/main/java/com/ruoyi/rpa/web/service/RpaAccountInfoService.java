package com.ruoyi.rpa.web.service;

import com.ruoyi.system.domain.rpa.RpaAccountInfo;

import java.util.List;

public interface RpaAccountInfoService {
    List<RpaAccountInfo> selectRpaAccountInfoList(RpaAccountInfo rpaAccountInfo);

    int insertAccountInfo(RpaAccountInfo rpaAccountInfo) throws Exception;

    String decrypt(String password) throws Exception;

    void deleteAccount(Long[] configIds);

    RpaAccountInfo selectAccountById(Long id);

    int editAccountInfo(RpaAccountInfo info) throws Exception;
}
