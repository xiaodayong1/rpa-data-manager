package com.ruoyi.rpa.web.service.template.templateInput;

import com.ruoyi.common.annotation.ExcelIndexAnno;
import lombok.Data;

@Data
public class CITICInput {
    // 对方账号
    @ExcelIndexAnno(fieldName = "counterpartyAccountNum", rowIndex = 9, cellIndex = 2)
    private String tradeBankName;

    // 对方账户名称
    @ExcelIndexAnno(fieldName = "counterpartyAccountName", rowIndex = 10, cellIndex = 2)
    private String tradeAccountName;

    // 账号
    @ExcelIndexAnno(fieldName = "tradeAccountNum", rowIndex = 4, cellIndex = 2)
    private String tradeAccountNum;
}
