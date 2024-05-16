package com.ruoyi.rpa.web.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ruoyi.common.utils.AESEncryption;
import com.ruoyi.rpa.web.service.IDeviceService;
import com.ruoyi.rpa.web.service.RpaAccountInfoService;
import com.ruoyi.system.domain.rpa.RpaAccountInfo;
import com.ruoyi.system.domain.rpa.cabinet.CabinCode;
import com.ruoyi.system.domain.rpa.cabinet.CloseCabinetInfo;
import com.ruoyi.system.mapper.rpa.RpaAccountInfoMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class RpaAccountInfoServiceImpl implements RpaAccountInfoService {

    @Value("${key}")
    private String key;

    @Autowired
    private RpaAccountInfoMapper rpaAccountInfoMapper;

    @Autowired
    private IDeviceService deviceService;

    @Override
    public List<RpaAccountInfo> selectRpaAccountInfoList(RpaAccountInfo rpaAccountInfo) {
        final QueryWrapper<RpaAccountInfo> rpaAccountInfoQueryWrapper = new QueryWrapper<>();
        rpaAccountInfoQueryWrapper.like(Objects.nonNull(rpaAccountInfo.getAccountNum()),"account_num",rpaAccountInfo.getAccountNum());
        rpaAccountInfoQueryWrapper.like(Objects.nonNull(rpaAccountInfo.getCabinetCode()),"cabinet_code",rpaAccountInfo.getCabinetCode());
        rpaAccountInfoQueryWrapper.like(Objects.nonNull(rpaAccountInfo.getFinancialInstitution()),"financial_institution",rpaAccountInfo.getFinancialInstitution());
        rpaAccountInfoQueryWrapper.like(Objects.nonNull(rpaAccountInfo.getAccountName()),"account_name",rpaAccountInfo.getAccountName());
        final List<RpaAccountInfo> list = rpaAccountInfoMapper.selectList(rpaAccountInfoQueryWrapper);
        // 展示已经开启的端口
        if (!CollectionUtils.isEmpty(list)){
            final List<CabinCode> codeList = list.stream().map(item -> {
                return new CabinCode(item.getCabinetCode());
            }).distinct().collect(Collectors.toList());
            List<CloseCabinetInfo> cabinetInfo = deviceService.getCabinetInfo(codeList);
            list.forEach(account -> {
                account.setIsOpenDevice(cabinetInfo.stream().anyMatch(
                        closeCabinetInfo -> {
                            return account.getCabinetCode().equals(closeCabinetInfo.getTerId()) &&
                                    account.getUsbPort().equals(closeCabinetInfo.getUsbPort().intValue());
                        }
                ));
            });
        }
        return list;
    }

    @Override
    public int insertAccountInfo(RpaAccountInfo rpaAccountInfo) throws Exception {
        // 密码加密存储
        rpaAccountInfo.setLoginPassword(AESEncryption.encrypt(key, rpaAccountInfo.getLoginPassword()));
        rpaAccountInfo.setKeyPassword(AESEncryption.encrypt(key,rpaAccountInfo.getKeyPassword()));
        return rpaAccountInfoMapper.insert(rpaAccountInfo);
    }

    @Override
    public String decrypt(String password) throws Exception {
        return AESEncryption.decrypt(key, password);
    }

    @Override
    public void deleteAccount(Long[] configIds) {
        rpaAccountInfoMapper.deleteBatchIds(Arrays.stream(configIds).collect(Collectors.toList()));
    }

    @Override
    public RpaAccountInfo selectAccountById(Long id) {
        if (Objects.isNull(id) || id == 0){
            return null;
        }
        return rpaAccountInfoMapper.selectById(id);
    }

    @Override
    public int editAccountInfo(RpaAccountInfo info) throws Exception {
        final RpaAccountInfo rpaAccountInfo = rpaAccountInfoMapper.selectById(info.getId());
        if (!StringUtils.equals(info.getLoginPassword(),rpaAccountInfo.getLoginPassword())){
            info.setLoginPassword(AESEncryption.encrypt(key,info.getLoginPassword()));
        }
        if (!StringUtils.equals(info.getKeyPassword(),rpaAccountInfo.getKeyPassword())){
            info.setKeyPassword(AESEncryption.encrypt(key,info.getKeyPassword()));
        }
        return rpaAccountInfoMapper.updateById(info);
    }
}
