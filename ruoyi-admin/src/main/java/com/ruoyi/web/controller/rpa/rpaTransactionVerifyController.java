package com.ruoyi.web.controller.rpa;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.rpa.web.service.CheckRpaService;
import com.ruoyi.rpa.web.service.RpaTransactionVerifyService;
import com.ruoyi.system.domain.rpa.RpaTransactionVerify;
import com.ruoyi.system.domain.rpa.transaction.Balance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController()
@RequestMapping(value = "/transaction")
public class rpaTransactionVerifyController extends BaseController {

    @Autowired
    private CheckRpaService checkRpaService;

    @Autowired
    private RpaTransactionVerifyService rpaTransactionVerifyService;

    /**
     * 查询明细详情校验宽列表
     */
    @GetMapping("/list")
    public TableDataInfo list(RpaTransactionVerify rpaTransactionVerify)
    {
        startPage();
        List<RpaTransactionVerify> list = checkRpaService.selectRpaTransactionVerifyList(rpaTransactionVerify);
        return getDataTable(list);
    }
    /**
     * 查询明细账号列表
     */
    @GetMapping("/listAccount")
    public AjaxResult listAccount() {
        return AjaxResult.success(rpaTransactionVerifyService.selectListAccount());
    }
    @GetMapping("/listBank")
    public AjaxResult listBank() {
        return AjaxResult.success(rpaTransactionVerifyService.selectListBank());
    }

    @PostMapping("/updateTransactionVerify")
    public AjaxResult updateTransactionVerify(@RequestBody RpaTransactionVerify rpaTransactionVerify){
        return rpaTransactionVerifyService.updateTransactionVerify(rpaTransactionVerify);
    }

    @GetMapping("/getVerifyByid/{id}")
    public AjaxResult getVerifyByid(@PathVariable Long id){
        return AjaxResult.success(rpaTransactionVerifyService.getVerifyByid(id));
    }

    @GetMapping("/history")
    public TableDataInfo historyBalance(Balance balance){
        startPage();
        List<Balance> list = rpaTransactionVerifyService.historyBalance(balance);
        return getDataTable(list);
    }
}
