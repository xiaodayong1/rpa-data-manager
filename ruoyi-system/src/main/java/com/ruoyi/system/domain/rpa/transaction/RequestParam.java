package com.ruoyi.system.domain.rpa.transaction;

import lombok.Data;

@Data
public class RequestParam {
    /**
     * 被加签对象 根据接口传不同的值
     */
    Object reqData;
    /**
     * 加签后的值
     */
    String  reqSignData;
}
