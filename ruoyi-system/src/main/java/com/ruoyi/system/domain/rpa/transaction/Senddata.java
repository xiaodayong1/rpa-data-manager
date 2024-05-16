/**
 * Copyright 2023 json.cn
 */
package com.ruoyi.system.domain.rpa.transaction;

import lombok.Data;

@Data
public class Senddata {
    private String cmd;
    private String dll_name;
    private String server_ip;
    private String server_port;
    private String dev_port;
    private String ter_id;
    private String user;
    private String oper_port;
}