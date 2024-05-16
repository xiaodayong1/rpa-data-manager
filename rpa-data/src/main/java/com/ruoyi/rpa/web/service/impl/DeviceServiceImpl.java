package com.ruoyi.rpa.web.service.impl;

import com.alibaba.fastjson2.JSON;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.rpa.socketClient.WebSocketClient;
import com.ruoyi.rpa.util.ServerIpUtil;
import com.ruoyi.rpa.web.service.IDeviceService;
import com.ruoyi.system.domain.rpa.cabinet.*;
import com.ruoyi.system.domain.rpa.transaction.JsonRootBean;
import com.ruoyi.system.domain.rpa.transaction.Senddata;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 开关机柜对应Ukey
 */
@Service
@Slf4j
public class DeviceServiceImpl implements IDeviceService {

    @Override
    public AjaxResult openDevice(Long id, String cabinetCode) {
        if (!ServerIpUtil.cabinetCodeList.contains(cabinetCode)){
            return AjaxResult.error("没有对应的机柜");
        }
        return AjaxResult.success(operateDevice(id, "open_new", ServerIpUtil.cabinetCodeList.indexOf(cabinetCode),"usbip.dll","usbip"));
    }

    //068330001082机柜开
    @Override
    public String openDevice(Long id,Integer serverIpIndex) {
        return operateDevice(id, "open_new", serverIpIndex,"usbip.dll","usbip");
    }

    //068330001082机柜关
    @Override
    public String closeDevice(Long id,Integer serverIpIndex) {
        return operateDevice(id, "close_new", serverIpIndex,"usbip.dll","usbip");
    }

    @Override
    public AjaxResult closeDevice(Long id, String cabinetCode) {
        if (!ServerIpUtil.cabinetCodeList.contains(cabinetCode)){
            return AjaxResult.error("没有对应的机柜");
        }
        return AjaxResult.success(operateDevice(id, "close_new", ServerIpUtil.cabinetCodeList.indexOf(cabinetCode),"usbip.dll","usbip"));
    }

    @Override
    public String batchCloseDevice(List<CloseCabinetInfo> closeCabinetInfoList) {
        final StringBuilder message = new StringBuilder();
        if (CollectionUtils.isEmpty(closeCabinetInfoList)){
            message.append("关闭机柜信息不能为空");
        }
        final StringBuilder stringBuilder = new StringBuilder();
        // 获取机柜编码下标
        final List<String> cabinetCodeList = ServerIpUtil.cabinetCodeList;
        closeCabinetInfoList.stream().forEach(item -> {
            if (cabinetCodeList.contains(item.getTerId())){
                item.setServerIndex(cabinetCodeList.indexOf(item.getTerId()));
            }
            if (Objects.nonNull(item.getServerIndex()) && Objects.nonNull(item.getUsbPort())){
                stringBuilder.append(operateDevice(item.getUsbPort(),"close_new",item.getServerIndex(),"usbip.dll","usbip") + System.lineSeparator());
            } else {
                // 无法获取对应机柜信息
                stringBuilder.append("无法获取ter_id信息为" + item.getTerId() + "机柜的" + item.getUsbPort() + "端口信息" + System.lineSeparator());
            }
        });
        message.append(stringBuilder);
        return message.toString();
    }

    @Override
    public List<CloseCabinetInfo> getCabinetInfo(List<CabinCode> cabinCodes) {
        if (CollectionUtils.isEmpty(cabinCodes)){
            return Collections.EMPTY_LIST;
        }
        List<CloseCabinetInfo> closeCabinetInfoList = new ArrayList<>();
        final ObjectMapper objectMapper = new ObjectMapper();
        cabinCodes.forEach(item -> {
            String message = operateDevice(null, "get_ter_status", ServerIpUtil.cabinetCodeList.indexOf(item.getTerId()), "dev_info.dll", "dev_oper");
            try {
                CabinResInfo cabinResInfo = objectMapper.readValue(message, CabinResInfo.class);
                final List<CabinInfo> data = cabinResInfo.getData().getData();
                if (!CollectionUtils.isEmpty(data)){
                    data.stream().forEach(portManager -> {
                        final List<UsbPortInfo> usbPortInfo = portManager.getUsbPortManage().getUsbPortInfo();
                        // 拿所有开启的设备
                        usbPortInfo.stream().forEach(port -> {
                            if (port.getDev_exists().equals("1") && port.getPort_power().equals("1")){
                                final CloseCabinetInfo cabinCode = new CloseCabinetInfo();
                                cabinCode.setUsbPort(Long.parseLong(port.getUsb_port()));
                                cabinCode.setTerId(portManager.getTer_id());
                                closeCabinetInfoList.add(cabinCode);
                            }
                        });
                    });
                }
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        });
        return closeCabinetInfoList;
    }

    @Override
    public AjaxResult compareCloseDevice(List<CloseCabinetInfo> closeCabinetInfoList) {
        if (CollectionUtils.isEmpty(closeCabinetInfoList)){
            return AjaxResult.error("集合不能为空");
        }
        List<CabinCode> codeList = closeCabinetInfoList.stream().map(cabinet -> {
            return new CabinCode(cabinet.getTerId());
        }).distinct().collect(Collectors.toList());
        // 获取到这些机柜所有已经开启的端口
        List<CloseCabinetInfo> cabinetInfo = getCabinetInfo(codeList);
        if (!CollectionUtils.isEmpty(cabinetInfo)){
            closeCabinetInfoList.removeIf(info -> {
                return !cabinetInfo.contains(info);
            });
        }
        String message = batchCloseDevice(closeCabinetInfoList);
        if (message.contains("无法获取ter_id信息") || message.contains("关闭机柜信息不能为空")){
            return AjaxResult.error("关闭信息有误，已关闭所有可关闭的端口");
        }
        return AjaxResult.success(message);
    }


    private String operateDevice(Long id, String cmd, Integer serverIpIndex,String moduledll,String dllName) {
        final WebSocketClient instance = WebSocketClient.getInstance();
        JsonRootBean jsonRootBean = new JsonRootBean();
        jsonRootBean.setCmd("sendcmd");
        jsonRootBean.setModuledll(moduledll);
        Senddata senddata = new Senddata();
        senddata.setCmd(cmd);
        senddata.setServer_ip(ServerIpUtil.openCloseIpList.get(serverIpIndex));
        senddata.setServer_port(ServerIpUtil.port);
        senddata.setDll_name(dllName);
        if (Objects.nonNull(id)){
            senddata.setDev_port(id.toString());
            senddata.setTer_id(ServerIpUtil.cabinetCodeList.get(serverIpIndex));
            senddata.setUser("test");
        }
        jsonRootBean.setSenddata(senddata);
        String json = JSON.toJSONString(jsonRootBean);
        String message = instance.sendStr(json);
        System.out.println(message);
        System.out.println("Ukey已操作");
        return message;
    }

}
