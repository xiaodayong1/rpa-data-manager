package com.ruoyi.rpa.web.service.template.templateInput;

import com.ruoyi.common.annotation.ExcelIndexAnno;
import lombok.Data;

@Data
public class CMBInput {
    // 账号名称
    @ExcelIndexAnno(fieldName = "tradeAccountName", rowIndex = 4, cellIndex = 4)
    private String tradeAccountName;

    // 账号
    @ExcelIndexAnno(fieldName = "tradeAccountNum", rowIndex = 5, cellIndex = 7)
    private String tradeAccountNum;
}
