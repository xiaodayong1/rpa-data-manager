package com.ruoyi.rpa.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConfigurationProperties(prefix = "server-ip")
@Data
public class ServerIpProperties {
    private List<String> openClose;
    private List<String> cabinetIp;
    private List<String> cabinetCode;
    private String serverPort;
}
