package com.ruoyi.rpa.web.service.template.templateInput;

import com.ruoyi.common.annotation.ExcelIndexAnno;
import lombok.Data;

@Data
public class BCMInput {
    // 账号
    @ExcelIndexAnno(fieldName = "tradeAccountNum", rowIndex = 0, cellIndex = 1)
    private String tradeAccountNum;

    // 账号名称
    @ExcelIndexAnno(fieldName = "tradeAccountName", rowIndex = 0, cellIndex = 3)
    private String tradeAccountName;

}
