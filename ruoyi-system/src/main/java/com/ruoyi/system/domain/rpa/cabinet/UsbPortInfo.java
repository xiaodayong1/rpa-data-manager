package com.ruoyi.system.domain.rpa.cabinet;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class UsbPortInfo {
    private String usb_port;
    private String dev_exists;
    private String port_power;
    private String usb_vid;
    private String usb_pid;
    private String usb_app;
    private String usb_share;
    private String usb_share_mode;
    private String client_ip;
    @JsonProperty("ca_infos")
    private CaInfos caInfos;
}
