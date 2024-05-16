package com.ruoyi.rpa.web.service.template.bankTemp;

import com.ruoyi.rpa.util.DataUtil;
import com.ruoyi.rpa.util.ExcelUtil;
import com.ruoyi.rpa.web.service.template.HandleTemplate;
import com.ruoyi.system.domain.rpa.transaction.StandardTransaction;
import com.ruoyi.system.domain.rpa.transaction.Transaction;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
@Component
@Slf4j
public class LSSY extends HandleTemplate {
    @Override
    protected HashMap<String, Object> handleExcelHead(Workbook xwb) {
        log.info("乐山商业银行无需读取表头");
        return null;
    }
    @Override
    protected List<LinkedHashMap<String, Object>> handleExcelData(Workbook xwb) {
        log.info("乐山商业银行读取表数据");
        return ExcelUtil.importExcelData(xwb, 0);
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
            outJsonBean.setSerialNumber(RandomStringUtils.randomAlphanumeric(18));
            outJsonBean.setRemark(item.get("摘要").toString());
            outJsonBean.setTransactionTime(DataUtil.formatToStandardDateTime("yyyyMMddHHmmss",item.get("交易日期").toString()));

            if (item.get("借贷标记").toString().equals("借")) {
                outJsonBean.setTransactionType("D");
            } else if (item.get("借贷标记").toString().equals("贷")) {
                outJsonBean.setTransactionType("C");
            }
            outJsonBean.setTransactionAmount(item.get("交易金额").toString().replaceAll(",", ""));
            outJsonBean.setCurrency("CNY");
            outJsonBean.setCounterpartyAccountName(item.get("对方户名").toString());
            outJsonBean.setCounterpartyAccountNum(item.get("对方账号").toString());
            outJsonBean.setCounterpartyBankName(item.get("对方账户开户行").toString());
            outJsonBean.setBalance(item.get("本次余额").toString().replaceAll(",", ""));
            outJsonBeans.add(outJsonBean);
        });
        return outJsonBeans;
    }

}
