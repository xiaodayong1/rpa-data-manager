package com.ruoyi.rpa.web.service.template.templateInput;

import com.ruoyi.common.annotation.ExcelIndexAnno;
import lombok.Data;

@Data
public class ABCInput {

    // 账号名称
    @ExcelIndexAnno(fieldName = "tradeAccountName", rowIndex = 1, cellIndex = 12)
    private String tradeAccountName;

    // 账号
    @ExcelIndexAnno(fieldName = "tradeAccountNum", rowIndex = 1, cellIndex = 2)
    private String tradeAccountNum;

}
