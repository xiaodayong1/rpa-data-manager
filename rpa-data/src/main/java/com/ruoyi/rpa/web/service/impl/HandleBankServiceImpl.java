package com.ruoyi.rpa.web.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson2.JSON;
import com.ruoyi.common.constant.RPAConstant;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.rpa.web.service.HandleBankService;
import com.ruoyi.rpa.web.service.IHistoryBalanceService;
import com.ruoyi.rpa.web.service.template.HandleTemplate;
import com.ruoyi.system.domain.rpa.ConvertData;
import com.ruoyi.system.domain.rpa.ConvertRes;
import com.ruoyi.system.domain.rpa.transaction.BatchStrandTransaction;
import com.ruoyi.system.domain.rpa.transaction.ElectronicEntry;
import com.ruoyi.system.domain.rpa.transaction.StandardTransaction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

@Service
@Slf4j
public class HandleBankServiceImpl implements HandleBankService {

    @Autowired
    private IHistoryBalanceService iHistoryBalanceService;

    private static final HashMap<String, HandleTemplate> bankMappingMap = initBankMappingMap();

    private static HashMap<String, HandleTemplate> initBankMappingMap() {
        HashMap<String, HandleTemplate> map = new HashMap<>();

        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        Resource[] resources;
        try {
            resources = resolver.getResources("classpath*:com/ruoyi/rpa/web/service/template/bankTemp/*.class");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        CachingMetadataReaderFactory readerFactory = new CachingMetadataReaderFactory();
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

        for (Resource resource : resources) {
            String className;
            try {
                className = readerFactory.getMetadataReader(resource).getClassMetadata().getClassName();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            Class<?> aClass;
            try {
                aClass = classLoader.loadClass(className);
                if (HandleTemplate.class.isAssignableFrom(aClass)) {
                    map.put(aClass.getSimpleName(), (HandleTemplate) aClass.newInstance());
                } else {
                    log.warn("Class {} is not a subclass or implementation of HandleTemplate.", aClass.getName());
                }
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            } catch (InstantiationException e) {
                throw new RuntimeException(e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        System.out.println(JSON.toJSONString(map));
        return map;
    }

    @Override
    public AjaxResult convertExcelToJson(ConvertData convertData) {
        String handleType = convertData.getHandleType();
        if (!bankMappingMap.containsKey(handleType)) {
            return AjaxResult.error("没有对应的处理类模板");
        }
        final HandleTemplate template = bankMappingMap.get(handleType);
        final ConvertRes convertRes = template.handleProcess(convertData);
        // 数据推送不要卸载模板类里面 ，抽象方法注入会有很多隐藏问题
        if (CollectionUtil.isNotEmpty(convertRes.getTransationList()) && convertData.getIsPushFlag()) {
            log.info("调用数据推送流程-开始");
            try {
                iHistoryBalanceService.transactionList(new BatchStrandTransaction(convertRes.getTransationList(),convertData.getIsDesc()));
            } catch (IOException e) {
                log.error("调用数据推送流程发生异常" + e.getMessage());
                return AjaxResult.error("推送数据失败" + e.getMessage(), convertRes);
            }
        }
        checkResJsonType(convertData.getResJsonType(),convertRes);
        return AjaxResult.success(convertRes);
    }

    private void checkResJsonType(String resJsonType,ConvertRes convertRes) {
        if (StringUtils.isEmpty(resJsonType) || RPAConstant.DETAIL_JSON.equals(resJsonType)){
            return;
        }
        // 根据明细生成回单json
        if (RPAConstant.RECEIPT_JSON.equals(resJsonType)){
            final ArrayList<ElectronicEntry> electronicEntries = new ArrayList<>();
            if (CollectionUtil.isNotEmpty(convertRes.getTransationList())){
                convertRes.getTransationList().stream().forEach(item -> {
                    final ElectronicEntry electronicEntry = new ElectronicEntry();
                    final StandardTransaction transaction = (StandardTransaction)item;
                    BeanUtils.copyProperties(item,electronicEntry,"serialNumber");
                    electronicEntry.setDetailSqNo(transaction.getSerialNumber());
                    electronicEntry.setSerialNumber(String.valueOf(System.nanoTime()));
                    electronicEntry.setTransNo("");
                    electronicEntries.add(electronicEntry);
                });
            }
            convertRes.setElectronicList(electronicEntries);
        }
    }

    @Override
    public AjaxResult uploadDetail(@RequestParam("file") MultipartFile file, ConvertData convertData) {
        String handleType = convertData.getHandleType();
        if (!bankMappingMap.containsKey(handleType)) {
            return AjaxResult.error("没有对应的处理类模板");
        }
        final HandleTemplate template = bankMappingMap.get(handleType);
        if (file.isEmpty()){
            return AjaxResult.error("请上传文件进行处理");
        }
        final ConvertRes convertRes;
        try {
            convertRes = template.handleProcess(file, convertData);
        }catch (Exception e){
            return AjaxResult.error("解析失败" + e.getMessage());
        }
        // 数据推送不要卸载模板类里面 ，抽象方法注入会有很多隐藏问题
        if (CollectionUtil.isNotEmpty(convertRes.getTransationList()) && convertData.getIsPushFlag()) {
            log.info("调用数据推送流程-开始");
            try {
                iHistoryBalanceService.transactionList(new BatchStrandTransaction(convertRes.getTransationList(),convertData.getIsDesc()));
            } catch (IOException e) {
                log.error("调用数据推送流程发生异常" + e.getMessage());
                return AjaxResult.error("推送数据失败" + e.getMessage(), convertRes);
            }
        }
        checkResJsonType(convertData.getResJsonType(),convertRes);
        return AjaxResult.success(convertRes);
    }
}
