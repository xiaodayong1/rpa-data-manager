package com.ruoyi.web.controller.rpa;

import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.rpa.web.service.ReceiptService;
import com.ruoyi.system.domain.rpa.transaction.ReceiptDto;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/receipt")
@Api(tags = "回单解析控制类", description = "回单解析并上传")
public class ReceiptController {

    @Autowired
    private ReceiptService receiptService;

    @PostMapping("/push")
    @ApiOperation(value = "获取回单解析推送响应值", response = AjaxResult.class)
    public AjaxResult pushReceipt(@RequestBody ReceiptDto receiptDto) throws IOException {
       receiptService.pushRecepit(receiptDto);
       return AjaxResult.success();
    }

    @PostMapping("/splitReceiptPDF")
    @ApiOperation(value = "拆分回单pdf", response = AjaxResult.class)
    public AjaxResult splitReceiptPDF(@RequestBody ReceiptDto receiptDto) throws Exception {
        receiptService.splitReceiptPDF(receiptDto);
        return AjaxResult.success();
    }
}
