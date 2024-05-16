package com.ruoyi.system.domain.rpa.cabinet;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CabinInfo {

    private String product_code;
    private String product_name;
    private String product_rename;
    private String ter_id;
    private String ter_version;
    private String ter_version_detail;
    private String ter_type;
    @JsonProperty("eth_manage")
    private EthManage ethManage;
    @JsonProperty("robotarm_manage")
    private RobotarmManage robotarmManage;
    @JsonProperty("usb_port_manage")
    private UsbPortManage usbPortManage;

}
