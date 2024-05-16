package com.ruoyi.rpa.web.service.impl;

import cn.hutool.json.JSONUtil;
import com.ruoyi.rpa.socketClient.WebSocketClient;
import com.ruoyi.rpa.util.ServerIpUtil;
import com.ruoyi.rpa.web.service.IMechanicalArmService;
import com.ruoyi.system.domain.rpa.transaction.JsonRootBean;
import com.ruoyi.system.domain.rpa.transaction.Senddata;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 对应Ukey机械臂按压
 *
 *
 * */
@Service
@Slf4j
public class MechanicalArmServiceImpl implements IMechanicalArmService {

    //1082机械臂按压
    @Override
    public String mechanicalArm(Long id,Integer serverIpIndex) {
        return performMechanicalArm(id, serverIpIndex);
    }

    private String performMechanicalArm(Long id, Integer serverIp) {
        WebSocketClient wsClient = WebSocketClient.getInstance();
        JsonRootBean jsonRootBean = createJsonRootBean(id, serverIp);
        String json = JSONUtil.toJsonStr(jsonRootBean);
        String message = wsClient.sendStr(json);
        log.info(message);
        log.info("机械臂已按压");
        return message;
    }

    private JsonRootBean createJsonRootBean(Long id, Integer serverIpIndex) {
        JsonRootBean jsonRootBean = new JsonRootBean();
        jsonRootBean.setCmd("sendcmd");
        jsonRootBean.setModuledll("dev_oper.dll");
        Senddata senddata = new Senddata();
        senddata.setCmd("start_robot_arm");
        senddata.setServer_ip(ServerIpUtil.cabinetIpList.get(serverIpIndex));
        senddata.setDll_name("dev_oper");
        senddata.setServer_port(ServerIpUtil.port);
        senddata.setOper_port(id.toString());
        jsonRootBean.setSenddata(senddata);
        return jsonRootBean;
    }
}
