package com.ruoyi.rpa.util;

import com.ruoyi.rpa.properties.ServerIpProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

@Component
@Slf4j
public final class ServerIpUtil {

    private static ServerIpProperties serverIpProperties;

    public static List<String> openCloseIpList = Collections.emptyList();
    public static List<String> cabinetIpList = Collections.emptyList();

    public static List<String> cabinetCodeList = Collections.emptyList();

    public static String port;

    @Autowired
    public void setServerIpProperties(ServerIpProperties serverIpProperties) {
        ServerIpUtil.serverIpProperties = serverIpProperties;
    }

    @PostConstruct
    public static void init() {
        if (Objects.nonNull(serverIpProperties)) {
            List<String> openClose  = new LinkedList<>(serverIpProperties.getOpenClose());
            List<String>  cabinetIp  = new LinkedList<>(serverIpProperties.getCabinetIp());
            List<String>  cabinetCode = new LinkedList<>(serverIpProperties.getCabinetCode());
            openCloseIpList = Collections.unmodifiableList(openClose);
            cabinetIpList = Collections.unmodifiableList(cabinetIp);
            cabinetCodeList = Collections.unmodifiableList(cabinetCode);
            port = serverIpProperties.getServerPort();

            log.info("init ip info ");
            openCloseIpList.forEach(System.out::println);
            cabinetIpList.forEach(System.out::println);
        }
    }
}
