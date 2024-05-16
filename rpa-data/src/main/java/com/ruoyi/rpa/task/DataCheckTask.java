package com.ruoyi.rpa.task;

import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.rpa.web.service.CheckRpaService;
import com.ruoyi.system.domain.rpa.RpaBalanceCalculate;
import com.ruoyi.system.domain.rpa.RpaTransactionBaseLine;
import com.ruoyi.system.mapper.rpa.RpaBalanceCalculateMapper;
import com.ruoyi.system.mapper.rpa.RpaTransactionBaseLineMapper;
import com.ruoyi.system.mapper.rpa.RpaTransactionVerifyMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class DataCheckTask {

//    @Value("${scheduled.cron.expression}")
//    private String cronExpression;

    @Autowired
    private RpaTransactionVerifyMapper rpaTransactionVerifyMapper;

    @Autowired
    private RpaTransactionBaseLineMapper rpaTransactionBaseLineMapper;

    @Autowired
    private RpaBalanceCalculateMapper rpaBalanceCalculateMapper;

    @Autowired
    private CheckRpaService checkRpaService;

//    @Scheduled(cron = "${scheduled.cron.expression}")
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult checkDetailTask() {
        final List<String> strings = rpaTransactionVerifyMapper.selectDistinctTradeAccountNum();
        final StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < strings.size(); i++) {
            try {
                checkRpaService.checkTransaction(strings.get(i));
            } catch (Exception e) {
                stringBuilder.append("处理账号 " + strings.get(i) + " 时出错");
                // 打印异常信息 // 后续写表里面去
                System.out.println("处理账号 " + strings.get(i) + " 时出错：" + e.getMessage());
                continue;
            }
        }
        generateCurrentDayBalance(null);
        if (stringBuilder.length() > 0){
            return AjaxResult.error(String.valueOf(stringBuilder));
        }
        return AjaxResult.success("基线数据同步成功");
    }

    // 计算明细余额 （有限制 ，只能计算出基线是明细的最后一条数据的余额，明细完整的情况下可以计算）
    @Transactional(rollbackFor = Exception.class)
    public void generateCurrentDayBalance(String tradeAccountNum){
        final List<RpaTransactionBaseLine> rpaTransactionBaseLines =  rpaTransactionBaseLineMapper.selectBalanceList(Objects.isNull(tradeAccountNum)?null:tradeAccountNum);
//        final List<RpaTransactionBaseLine> rpaTransactionBaseLines = rpaTransactionBaseLineMapper.selectList(new QueryWrapper<RpaTransactionBaseLine>().eq(Objects.nonNull(tradeAccountNum),"trade_account_num",tradeAccountNum));
        if (CollectionUtils.isEmpty(rpaTransactionBaseLines)){
            return;
        }
        String batchNo = String.valueOf(System.currentTimeMillis());
        LocalDate currentDate = LocalDate.now();
        List<RpaBalanceCalculate> collect = rpaTransactionBaseLines.stream().map(item -> {
            if (item.getIsSyncType() == 1) {
                RpaBalanceCalculate rpaBalanceCalculate = new RpaBalanceCalculate();
                rpaBalanceCalculate.setBalance(item.getBalance());
                rpaBalanceCalculate.setTradeAccountNum(item.getTradeAccountNum());
                rpaBalanceCalculate.setBatchNo(batchNo);
                rpaBalanceCalculate.setCalculateDate(String.valueOf(currentDate));
                return rpaBalanceCalculate;
            }
            return null;
        }).filter(Objects::nonNull).collect(Collectors.toList());
        rpaBalanceCalculateMapper.batchInsert(collect);
    }
}
