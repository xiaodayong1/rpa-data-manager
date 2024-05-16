package com.ruoyi.web.controller.rpa;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.rpa.web.service.RpaBaselineService;
import com.ruoyi.system.domain.rpa.RpaTransactionBaseLine;
import com.ruoyi.system.domain.rpa.RpaTransactionVerify;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController()
@RequestMapping(value = "/baseline")
public class rpaBaselineController extends BaseController {

    @Autowired
    private RpaBaselineService rpaBaselineService;

    @GetMapping("/list")
    public TableDataInfo list(RpaTransactionBaseLine rpaTransactionBaseLine)
    {
        startPage();
        List<RpaTransactionBaseLine> list = rpaBaselineService.selectBaselineList(rpaTransactionBaseLine);
        return getDataTable(list);
    }

    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        rpaBaselineService.deleteIds(ids);
        return success();
    }

}
