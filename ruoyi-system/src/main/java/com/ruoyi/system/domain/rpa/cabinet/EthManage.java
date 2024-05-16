package com.ruoyi.system.domain.rpa.cabinet;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class EthManage {
    private String intranet_delay;
    private String external_delay;
    private String ip_type;
    private String ter_mac;
    private String ip_address;
    private String netmask;
    private String gateway;
    private String dns;

}
