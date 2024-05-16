package com.ruoyi.rpa.web.service;

import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.system.domain.rpa.ConvertData;
import org.springframework.web.multipart.MultipartFile;

public interface HandleBankService {
    AjaxResult convertExcelToJson(ConvertData convertData) throws InstantiationException, IllegalAccessException;

    AjaxResult uploadDetail(MultipartFile file, ConvertData convertData);
}
