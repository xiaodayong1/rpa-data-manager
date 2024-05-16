package com.ruoyi.rpa.web.service;


import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.system.domain.rpa.cabinet.CabinCode;
import com.ruoyi.system.domain.rpa.cabinet.CloseCabinetInfo;

import java.util.List;

public interface IDeviceService {

    AjaxResult openDevice(Long id,String cabinetCode);
    String openDevice(Long id,Integer serverIpIndex);

    String closeDevice(Long id,Integer serverIpIndex);

    AjaxResult closeDevice(Long id,String cabinetCode);

    String batchCloseDevice(List<CloseCabinetInfo> closeCabinetInfoList);

    List<CloseCabinetInfo> getCabinetInfo(List<CabinCode> cabinCodes);

    AjaxResult compareCloseDevice(List<CloseCabinetInfo> closeCabinetInfoList);
}

