/**
 * Copyright 2023 json.cn
 */
package com.ruoyi.system.domain.rpa.transaction;

import lombok.Data;

/*
* HUB设备请求参数
* */
@Data
public class JsonRootBean {
    private String cmd;
    private String moduledll;
    private Senddata senddata;

}