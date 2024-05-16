package com.ruoyi.web.controller.rpa;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.rpa.web.service.IDeviceService;
import com.ruoyi.rpa.web.service.RpaAccountInfoService;
import com.ruoyi.system.domain.rpa.RpaAccountInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/accountInfo")
public class RpaAccountInfoController extends BaseController {

    @Autowired
    private RpaAccountInfoService rpaAccountInfoService;

    @Autowired
    private IDeviceService deviceService;

    @GetMapping("/list")
    public TableDataInfo list(RpaAccountInfo rpaAccountInfo)
    {
        startPage();
        List<RpaAccountInfo> list = rpaAccountInfoService.selectRpaAccountInfoList(rpaAccountInfo);
        return getDataTable(list);
    }

    @PostMapping("/add")
    public AjaxResult add(@Validated @RequestBody RpaAccountInfo rpaAccountInfo) throws Exception {
        return toAjax(rpaAccountInfoService.insertAccountInfo(rpaAccountInfo));
    }

    @GetMapping("/decrypt")
    public AjaxResult decrypt(@RequestParam("password") String password) throws Exception {
        return AjaxResult.success(rpaAccountInfoService.decrypt(password));
    }

    @DeleteMapping("/{id}")
    public AjaxResult deleteAccount(@PathVariable Long[] id){
        if (id.length == 0){
            return AjaxResult.error("请选择删除的数据");
        }
        rpaAccountInfoService.deleteAccount(id);
        return success();
    }


    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable Long id)
    {
        return success(rpaAccountInfoService.selectAccountById(id));
    }


    @PostMapping("/edit")
    public AjaxResult edit(@Validated @RequestBody RpaAccountInfo info) throws Exception {
        return toAjax(rpaAccountInfoService.editAccountInfo(info));
    }


}
