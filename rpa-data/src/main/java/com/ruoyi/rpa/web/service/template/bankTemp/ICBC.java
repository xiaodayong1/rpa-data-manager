package com.ruoyi.rpa.web.service.template.bankTemp;

import com.ruoyi.rpa.util.ExcelUtil;
import com.ruoyi.rpa.web.service.template.HandleTemplate;
import com.ruoyi.system.domain.rpa.transaction.StandardTransaction;
import com.ruoyi.system.domain.rpa.transaction.Transaction;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

@Component
@Slf4j
public class ICBC extends HandleTemplate {

    @Override
    protected HashMap<String, Object> handleExcelHead(Workbook xwb) {
        log.info("工商银行无需读取表头");
        return null;
    }

    @Override
    protected List<LinkedHashMap<String, Object>> handleExcelData(Workbook xwb) {
        log.info("工商银行读取表数据");
        return ExcelUtil.importExcelData(xwb, 1);
    }

    @Override
    protected Class<? extends Transaction> getTransactionClass() {
        return StandardTransaction.class;
    }

    @Override
    protected <T extends Transaction> List<T> fillData(HashMap<String, Object> headMap, List<LinkedHashMap<String, Object>> dataMap) {
        final ArrayList<T> outJsonBeans = new ArrayList<>();
        if (CollectionUtils.isEmpty(dataMap)) {
            return outJsonBeans;
        }
        dataMap.stream().forEach(item -> {
            T outJsonBean;
            try {
                outJsonBean = (T) getTransactionClass().newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                throw new RuntimeException("Error creating instance of Transaction class", e);
            }
            outJsonBean.setSerialNumber(item.get("顺序号").toString());
            outJsonBean.setRemark(item.get("摘要").toString());
            outJsonBean.setTransactionTime(item.get("交易时间").toString());
            if (item.containsKey("余额")){
                outJsonBean.setBalance(item.get("余额").toString().replaceAll(",",""));
            }
            if (item.get("借贷标志").toString().equals("借")) {
                outJsonBean.setTransactionType("D");
                outJsonBean.setTransactionAmount(item.get("转出金额").toString().replaceAll(",", ""));
            } else if (item.get("借贷标志").toString().equals("贷")) {
                outJsonBean.setTransactionType("C");
                outJsonBean.setTransactionAmount(item.get("转入金额").toString().replaceAll(",", ""));
            }
            outJsonBean.setCurrency("CNY");
            outJsonBean.setCounterpartyAccountName(item.get("对方单位").toString());
            outJsonBean.setCounterpartyAccountNum(item.get("对方账号").toString());
            outJsonBeans.add(outJsonBean);
        });
        return outJsonBeans;
    }
}
