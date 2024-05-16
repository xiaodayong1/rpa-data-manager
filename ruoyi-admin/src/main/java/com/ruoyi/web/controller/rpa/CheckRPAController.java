package com.ruoyi.web.controller.rpa;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.rpa.task.DataCheckTask;
import com.ruoyi.rpa.web.service.CheckRpaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController()
@RequestMapping(value = "/check")
public class CheckRPAController extends BaseController {

    @Autowired
    private CheckRpaService checkRpaService;

    @Autowired
    private DataCheckTask dataCheckTask;



    @GetMapping("/checkTransaction/{tradeAccountNum}")
    public AjaxResult checkTransaction(@PathVariable String tradeAccountNum){
        return checkRpaService.checkTransaction(tradeAccountNum);
    }

    @GetMapping("/checkTask")
    public AjaxResult checkTask(){
        return dataCheckTask.checkDetailTask();
    }

    @GetMapping("/getAccurateBalance")
    public AjaxResult getAccurateBalance(String tradeAccountNum,String endTime){
        return checkRpaService.getAccurateBalance(tradeAccountNum,endTime);
    }

    /**
     *  推动账号的明细基线
     * @return
     */
    @GetMapping("/promoteBaseLine")
    public AjaxResult promoteBaseLine(@RequestParam(required = false) String tradeAccountNum,@RequestParam(required = false) Long id){
        return checkRpaService.promoteBaseLine(tradeAccountNum,id);
    }

}
