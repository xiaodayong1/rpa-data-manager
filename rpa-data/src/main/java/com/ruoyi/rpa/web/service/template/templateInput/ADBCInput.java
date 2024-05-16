package com.ruoyi.rpa.web.service.template.templateInput;


import com.ruoyi.common.annotation.ExcelIndexAnno;

public class ADBCInput {

    @ExcelIndexAnno(fieldName = "tradeAccountName",rowIndex = 3,cellIndex = 5)
    private String tradeAccountName;

    @ExcelIndexAnno(fieldName = "tradeAccountNum",rowIndex = 2,cellIndex = 5)
    private String tradeAccountNum;


}
