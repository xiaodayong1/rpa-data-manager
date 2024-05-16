package com.ruoyi.web.controller.rpa;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.rpa.web.service.HandleBankService;
import com.ruoyi.system.domain.rpa.ConvertData;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping(value = "/convert")
public class ConvertExcelDetailToJsonController {

    public ConvertExcelDetailToJsonController() {
        System.out.println("ConvertExcelDetailToJsonController instantiated.");
    }

    @Autowired
    private HandleBankService handleBankService;


    @PostMapping("/detail/toJson")
    public AjaxResult convertExcel(@RequestBody ConvertData convertData) throws InstantiationException, IllegalAccessException {
        return handleBankService.convertExcelToJson(convertData);
    }

    @Log(title = "明细管理-上传", businessType = BusinessType.IMPORT)
    @PostMapping(value = "/detail/upload",consumes = "multipart/form-data")
    public AjaxResult uploadDetail(MultipartFile file,ConvertData convertData) {
        return handleBankService.uploadDetail(file,convertData);
    }

}
