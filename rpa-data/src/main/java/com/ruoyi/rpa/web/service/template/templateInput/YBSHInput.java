package com.ruoyi.rpa.web.service.template.templateInput;

import com.ruoyi.common.annotation.ExcelIndexAnno;
import lombok.Data;

@Data
public class YBSHInput {

    @ExcelIndexAnno(fieldName = "tradeAccountName",rowIndex = 1,cellIndex = 1)
    private String tradeAccountName;

    @ExcelIndexAnno(fieldName = "tradeAccountNum",rowIndex = 0,cellIndex = 1)
    private String tradeAccountNum;

}
