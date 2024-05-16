package com.ruoyi.rpa.web.service.template.templateInput;

import com.ruoyi.common.annotation.ExcelIndexAnno;
import lombok.Data;

@Data
public class BOBInput {
    // 账号
    @ExcelIndexAnno(fieldName = "tradeAccountNum", rowIndex = 1, cellIndex = 1)
    private String tradeAccountNum;

}
