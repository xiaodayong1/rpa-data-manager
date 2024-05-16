package com.ruoyi.rpa.web.service.impl;

import com.ruoyi.rpa.task.DataCheckTask;
import com.ruoyi.rpa.util.LinkedListUtil;
import com.ruoyi.rpa.util.ListNode;
import com.ruoyi.rpa.util.ListNodeWrapper;
import com.ruoyi.rpa.web.service.CheckRpaService;
import com.ruoyi.common.core.domain.AjaxResult;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ruoyi.system.domain.rpa.RpaBalanceCalculate;
import com.ruoyi.system.domain.rpa.RpaTransactionBaseLine;
import com.ruoyi.system.domain.rpa.RpaTransactionVerify;
import com.ruoyi.system.mapper.rpa.RpaBalanceCalculateMapper;
import com.ruoyi.system.mapper.rpa.RpaTransactionBaseLineMapper;
import com.ruoyi.system.mapper.rpa.RpaTransactionVerifyMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class CheckRpaServiceImpl implements CheckRpaService {

    private static final Logger log = LoggerFactory.getLogger(CheckRpaServiceImpl.class);

    @Autowired
    private RpaTransactionVerifyMapper rpaTransactionVerifyMapper;

    @Autowired
    private RpaTransactionBaseLineMapper rpaTransactionBaseLineMapper;

    @Autowired
    private RpaBalanceCalculateMapper rpaBalanceCalculateMapper;

    @Autowired
    private DataCheckTask dataCheckTask;

    private final LinkedListUtil<RpaTransactionVerify> linkedListUtil = new LinkedListUtil<>();

    @Override
    public AjaxResult checkTransaction(String tradeAccountNum) {
        // 查询数据水位线
        QueryWrapper<RpaTransactionBaseLine> baselineWrapper = new QueryWrapper<>();
        RpaTransactionBaseLine rpaTransactionBaseLine = rpaTransactionBaseLineMapper.selectOne(
                baselineWrapper.eq("trade_account_num", tradeAccountNum));
        // 更新水位线下的数据错误状态
        if (Objects.nonNull(rpaTransactionBaseLine)){
            rpaTransactionVerifyMapper.updateSyncTypeLtId(rpaTransactionBaseLine.getVerifyId(),rpaTransactionBaseLine.getTradeAccountNum());
        }
        // todo 改造成不查全量数据
        QueryWrapper<RpaTransactionVerify> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("trade_account_num", tradeAccountNum)
//                .lt(Objects.nonNull(endTime), "transaction_time", LocalDateTime.parse(endTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .isNotNull("balance")
                .orderByAsc("transaction_time").orderByAsc("id");
        List<RpaTransactionVerify> checkList = rpaTransactionVerifyMapper.selectList(queryWrapper);

        if (CollectionUtils.isEmpty(checkList)) {
            return AjaxResult.error("没有需要校验的数据,请检查参数条件");
        }

        if (checkList.size() == 1 && Objects.isNull(rpaTransactionBaseLine)) {
            return AjaxResult.success("只有一条数据，无法校验");
        }
        ListNodeWrapper tailWrapper = new ListNodeWrapper(null);
        RpaTransactionBaseLine newBaseLine = new RpaTransactionBaseLine();
        ListNode<RpaTransactionVerify> baseLineNode;
        try {
            if (Objects.isNull(rpaTransactionBaseLine) || rpaTransactionBaseLine.getIsSyncType() == 0) {
                baseLineNode = linkedListUtil.toLinkedList(checkList);
            } else if (rpaTransactionBaseLine.getIsSyncType() == 1) {
                List<RpaTransactionVerify> collect = checkList.stream()
                        .filter(data -> data.getId() >= rpaTransactionBaseLine.getVerifyId())
                        .collect(Collectors.toList());
                baseLineNode = linkedListUtil.toLinkedList(collect);
            } else {
                return AjaxResult.error("无法识别的基线数据，请检查:" + rpaTransactionBaseLine);
            }

            List<Long> lists = checkDetailNodeList(baseLineNode, tailWrapper, newBaseLine);

            if (Objects.isNull(tailWrapper.node)) {
                return AjaxResult.success("无需识别");
            }

            if (CollectionUtils.isEmpty(lists)) {
                // 创建基线
                RpaTransactionVerify data = tailWrapper.node.data;
                if (Objects.nonNull(data.getTradeAccountNum())){
                    RpaTransactionBaseLine tailBaseLine = new RpaTransactionBaseLine();
                    tailBaseLine.setIsSyncType(1);
                    tailBaseLine.setVerifyId(data.getId());
                    tailBaseLine.setTradeAccountNum(data.getTradeAccountNum());
                    tailBaseLine.setBalance(data.getBalance());
                    rpaTransactionBaseLineMapper.insertOrUpdate(tailBaseLine);
                }
            } else {
                // 将错误数据打标记
                rpaTransactionVerifyMapper.updateSyncTypeById(lists);
                if (Objects.nonNull(newBaseLine.getBalance()) && Objects.nonNull(newBaseLine.getTradeAccountNum()) && Objects.nonNull(newBaseLine.getVerifyId())) {
                    // 看是否能够基线上移
                    if (Objects.isNull(rpaTransactionBaseLine) || (Objects.nonNull(rpaTransactionBaseLine) && newBaseLine.getVerifyId() > rpaTransactionBaseLine.getVerifyId())) {
                        rpaTransactionBaseLineMapper.insertOrUpdate(newBaseLine);
                    }
                }
            }
            return AjaxResult.success("数据校验完成");
        } catch (Exception e) {
            log.error("检查交易出错：", e);
            return AjaxResult.error("检查交易出错：" + e.getMessage());
        }
    }

    public List<Long> checkDetailNodeList(ListNode<RpaTransactionVerify> listNode, ListNodeWrapper tail, RpaTransactionBaseLine baseline) {
        ArrayList<Long> problemIdList = new ArrayList<>();
        BigDecimal currentBalance = null;
        boolean confirmBaseLine = false;

        while (listNode != null) {
            RpaTransactionVerify data = listNode.data;

            if (Objects.isNull(listNode.prev)) {
                currentBalance = data.getBalance();
            } else {
                BigDecimal calculatedBalance = getCalculatedBalance(data);
                if (calculatedBalance.compareTo(currentBalance) != 0) {
                    if (!confirmBaseLine && Objects.nonNull(listNode.prev)) {
                        confirmBaseLine = true;
                        baseline.setTradeAccountNum(listNode.prev.data.getTradeAccountNum());
                        baseline.setVerifyId(listNode.prev.data.getId());
                        baseline.setIsSyncType(0);
                        baseline.setBalance(listNode.prev.data.getBalance());
                    }
                    problemIdList.add(listNode.prev.data.getId());
                    problemIdList.add(data.getId());
                }
                currentBalance = data.getBalance();
            }
            if (Objects.isNull(listNode.next)) {
                tail.node = listNode;
            }
            listNode = listNode.next;
        }

        return problemIdList.stream().distinct().collect(Collectors.toList());
    }

    private BigDecimal getCalculatedBalance(RpaTransactionVerify data) {
        if ("D".equals(data.getTransactionType())) {
            return data.getBalance().add(data.getTransactionAmount());
        } else if ("C".equals(data.getTransactionType())) {
            return data.getBalance().subtract(data.getTransactionAmount());
        } else {
            throw new RuntimeException("数据错误");
        }
    }

    @Override
    public AjaxResult getAccurateBalance(String tradeAccountNum, String endTime) {
        if (Objects.isNull(tradeAccountNum) || Objects.isNull(endTime)){
            return AjaxResult.error("缺少账号或者查询时间参数");
        }
        LocalDate date = LocalDate.parse(endTime, DateTimeFormatter.BASIC_ISO_DATE);

        // 格式化为yyyy-MM-dd字符串
        String formattedDate = date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        QueryWrapper<RpaBalanceCalculate> rpaBalanceCalculateQueryWrapper = new QueryWrapper<>();
        List<RpaBalanceCalculate> rpaBalanceCalculates = rpaBalanceCalculateMapper.selectList(
                rpaBalanceCalculateQueryWrapper.eq("trade_account_num", tradeAccountNum)
                        .eq("calculate_date", formattedDate)
                        .orderByDesc("batch_no")
        );
        if (!CollectionUtils.isEmpty(rpaBalanceCalculates)){
            return AjaxResult.success(rpaBalanceCalculates.get(0).getBalance());
        }
        // 如果为空的话，查一下时间在及线下面的当天数据
        List<RpaTransactionVerify> rpaTransactionVerifies = rpaTransactionVerifyMapper.selectByTradeAccountNumTransactionJoinBaseLine(tradeAccountNum, formattedDate);
        if (!CollectionUtils.isEmpty(rpaTransactionVerifies)){
            return AjaxResult.success(rpaTransactionVerifies.get(0).getBalance());
        }
        return AjaxResult.error("查询不到当天的余额，请补齐数据并进行校验");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AjaxResult promoteBaseLine(String tradeAccountNum, Long id) {
        if (Objects.isNull(tradeAccountNum)){
            return AjaxResult.error("缺少推动的数据账号");
        }
        final QueryWrapper<RpaTransactionBaseLine> rpaTransactionBaseLineQueryWrapper = new QueryWrapper<>();
        rpaTransactionBaseLineQueryWrapper.eq("trade_account_num", tradeAccountNum);
        final List<RpaTransactionBaseLine> rpaTransactionBaseLines = rpaTransactionBaseLineMapper.selectList(rpaTransactionBaseLineQueryWrapper);
        final QueryWrapper<RpaTransactionVerify> rpaTransactionVerifyQueryWrapper = new QueryWrapper<>();
        rpaTransactionVerifyQueryWrapper.eq(Objects.nonNull(id),"id",id)
                .eq("trade_account_num",tradeAccountNum)
                .orderByAsc("transaction_time")
                .orderByAsc("id")
                .isNotNull("balance");
        final List<RpaTransactionVerify> rpaTransactionVerifies = rpaTransactionVerifyMapper.selectList(rpaTransactionVerifyQueryWrapper);
        if (CollectionUtils.isEmpty(rpaTransactionVerifies)){
            return AjaxResult.error("没有对应的明细数据，无法设置基线");
        }
        // 基线
        RpaTransactionVerify rpaTransactionVerify = rpaTransactionVerifies.get(0);
        if (!CollectionUtils.isEmpty(rpaTransactionBaseLines) && rpaTransactionBaseLines.get(0).getVerifyId() >= rpaTransactionVerify.getId()){
            return AjaxResult.error("请选择当前基线之后的数据设置为基线");
        }
        rpaTransactionVerifyMapper.updateSyncTypeLtId(rpaTransactionVerify.getId(),tradeAccountNum);

        // 查询最新的基线数据
//        final QueryWrapper<RpaTransactionVerify> rpaTransactionVerifyQueryWrapper1 = new QueryWrapper<>();
//        rpaTransactionVerifyQueryWrapper1.eq("trade_account_num",tradeAccountNum)
//                .orderByAsc("transaction_time")
//                .orderByAsc("id")
//                .isNotNull("balance")
//                .eq("is_sync",1)
//                .last("limit 1");
//        final List<RpaTransactionVerify> rpaTransactionVerifies1 = rpaTransactionVerifyMapper.selectList(rpaTransactionVerifyQueryWrapper1);
//        if (CollectionUtils.isEmpty(rpaTransactionVerifies1)){
//            // 如果为空，则基线是最后一条数据
//
//        }
        RpaTransactionBaseLine rpaTransactionBaseLine = new RpaTransactionBaseLine();
        rpaTransactionBaseLine.setBalance(rpaTransactionVerify.getBalance());
        rpaTransactionBaseLine.setVerifyId(rpaTransactionVerify.getId());
        rpaTransactionBaseLine.setIsSyncType(1);
        rpaTransactionBaseLine.setTradeAccountNum(tradeAccountNum);
        rpaTransactionBaseLineMapper.insertOrUpdate(rpaTransactionBaseLine);
        // 刷新当天的余额记录
        dataCheckTask.generateCurrentDayBalance(tradeAccountNum);
        return AjaxResult.success("设置基线成功");
    }

    @Override
    public List<RpaTransactionVerify> selectRpaTransactionVerifyList(RpaTransactionVerify rpaTransactionVerify) {
        QueryWrapper<RpaTransactionVerify> wrapper = new QueryWrapper<>();
        wrapper.likeRight(Objects.nonNull(rpaTransactionVerify.getSerialNumber()),"serial_number",rpaTransactionVerify.getSerialNumber());
        wrapper.eq(Objects.nonNull(rpaTransactionVerify.getTradeAccountNum()),"trade_account_num",rpaTransactionVerify.getTradeAccountNum());
        wrapper.eq(Objects.nonNull(rpaTransactionVerify.getTradeBankName()),"trade_bank_name",rpaTransactionVerify.getTradeBankName());
        wrapper.likeRight(Objects.nonNull(rpaTransactionVerify.getTransactionTime()),"transaction_time",rpaTransactionVerify.getTransactionTime());
        wrapper.eq(Objects.nonNull(rpaTransactionVerify.getSyncType()),"sync_type",rpaTransactionVerify.getSyncType());
        wrapper.orderByAsc("transaction_time");
        wrapper.orderByAsc("id");
        final List<RpaTransactionVerify> rpaTransactionVerifies = rpaTransactionVerifyMapper.selectList(wrapper);
        return rpaTransactionVerifies;
    }
}
