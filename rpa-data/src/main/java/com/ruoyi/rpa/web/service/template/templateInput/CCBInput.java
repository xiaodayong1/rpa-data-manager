package com.ruoyi.rpa.web.service.template.templateInput;

import com.ruoyi.common.annotation.ExcelIndexAnno;
import lombok.Data;

@Data
public class CCBInput {
    // 开户行
    @ExcelIndexAnno(fieldName = "tradeBankName", rowIndex = 5, cellIndex = 1)
    private String tradeBankName;

    // 账号名称
    @ExcelIndexAnno(fieldName = "tradeAccountName", rowIndex = 3, cellIndex = 3)
    private String tradeAccountName;

    // 账号
    @ExcelIndexAnno(fieldName = "tradeAccountNum", rowIndex = 3, cellIndex = 1)
    private String tradeAccountNum;
}
