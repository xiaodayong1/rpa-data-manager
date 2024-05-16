package com.ruoyi.web.controller.rpa;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.rpa.web.service.RpaCalculateService;
import com.ruoyi.system.domain.rpa.RpaBalanceCalculate;
import com.ruoyi.system.domain.rpa.RpaTransactionBaseLine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController()
@RequestMapping(value = "/calculate")
public class rpaCalculateController extends BaseController {

    @Autowired
    private RpaCalculateService rpaCalculateService;

    @GetMapping("/list")
    public TableDataInfo list(RpaBalanceCalculate rpaBalanceCalculate)
    {
        startPage();
        List<RpaBalanceCalculate> list = rpaCalculateService.selectCalculateList(rpaBalanceCalculate);
        return getDataTable(list);
    }

}
