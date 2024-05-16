package com.ruoyi.rpa.web.service.template.templateInput;

import com.ruoyi.common.annotation.ExcelIndexAnno;
import lombok.Data;

@Data
public class CMBCInput {
    // 开户行
    @ExcelIndexAnno(fieldName = "tradeBankName", rowIndex = 2, cellIndex = 1)
    private String tradeBankName;

    // 账号名称
    @ExcelIndexAnno(fieldName = "tradeAccountName", rowIndex = 0, cellIndex = 1)
    private String tradeAccountName;

    // 账号
    @ExcelIndexAnno(fieldName = "tradeAccountNum", rowIndex = 1, cellIndex = 1)
    private String tradeAccountNum;
}
