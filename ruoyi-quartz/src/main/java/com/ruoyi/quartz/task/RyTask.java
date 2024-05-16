package com.ruoyi.quartz.task;

import com.ruoyi.rpa.task.DataCheckTask;
import com.ruoyi.rpa.web.service.CheckRpaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * 定时任务调度测试
 * 
 * @author ruoyi
 */
@Component("ryTask")
public class RyTask
{
    @Autowired
    private CheckRpaService checkRpaService;

    @Autowired
    private DataCheckTask dataCheckTask;
    public void syncBaseLineTask(String accountNum){
        System.out.println("同步基线开始" + accountNum);
        // 单账号同步
        if (StringUtils.hasLength(accountNum)) {
            checkRpaService.checkTransaction(accountNum);
            dataCheckTask.generateCurrentDayBalance(accountNum);
        } else {
            dataCheckTask.checkDetailTask();
        }
    }
}
