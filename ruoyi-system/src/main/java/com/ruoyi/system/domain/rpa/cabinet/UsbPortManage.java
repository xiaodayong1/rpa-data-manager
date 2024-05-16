package com.ruoyi.system.domain.rpa.cabinet;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class UsbPortManage {
    private String usb_port_num;
    private String without_dev_port_num;
    private String without_dev_ports;
    @JsonProperty("usb_port_info")
    private List<UsbPortInfo> usbPortInfo;
}

