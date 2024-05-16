package com.ruoyi.rpa.web.service.template.templateInput;

import com.ruoyi.common.annotation.ExcelIndexAnno;
import lombok.Data;

@Data
public class BOCInput {
    @ExcelIndexAnno(fieldName = "tradeAccountNum", rowIndex = 1, cellIndex = 2)
    private String tradeAccountNum;
}
