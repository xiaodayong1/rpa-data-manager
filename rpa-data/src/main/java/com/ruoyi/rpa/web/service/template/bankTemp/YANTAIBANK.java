package com.ruoyi.rpa.web.service.template.bankTemp;

import com.ruoyi.rpa.util.ExcelUtil;
import com.ruoyi.rpa.web.service.template.HandleTemplate;
import com.ruoyi.system.domain.rpa.transaction.StandardTransaction;
import com.ruoyi.system.domain.rpa.transaction.Transaction;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.*;

@Component
@Slf4j
public class YANTAIBANK extends HandleTemplate {
    @Override
    protected HashMap<String, Object> handleExcelHead(Workbook xwb) {
        log.info("烟台银行无需读取表头");
        return null;
    }

    @Override
    protected List<LinkedHashMap<String, Object>> handleExcelData(Workbook xwb) {
        log.info("烟台银行无需读取表头");
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
            return Collections.EMPTY_LIST;
        }
        dataMap.stream().forEach(data -> {
            T outJsonBean;
            try {
                outJsonBean = (T) getTransactionClass().newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                throw new RuntimeException("Error creating instance of Transaction class", e);
            }
            outJsonBean.setSerialNumber(data.get("核心流水号").toString());
            // "K2 - 摘要"
            outJsonBean.setRemark(data.get("摘要").toString());
            // "K3 - 交易时间 这个字段比较特殊，需要针对处理"
            outJsonBean.setTransactionTime(data.get("交易日期").toString() + " " + data.get("交易时间").toString());

            // "K4 - 交易类型"
            String stringValue = (String) data.get("收入");
            stringValue.replaceAll(",", "");
            if (!stringValue.equals("--") && !stringValue.isEmpty() && Double.parseDouble(stringValue) > 0) {
                outJsonBean.setTransactionType("C");
                outJsonBean.setTransactionAmount(data.get("收入").toString());
            } else {
                outJsonBean.setTransactionType("D");
                outJsonBean.setTransactionAmount(data.get("支出").toString());
            }
            outJsonBean.setCounterpartyAccountNum(data.get("对方账号").toString());
            outJsonBean.setCounterpartyAccountName(data.get("对方户名").toString());
            outJsonBean.setCurrency("CNY");
            outJsonBean.setCounterpartyBankName(data.get("对方开户机构").toString());
            outJsonBeans.add(outJsonBean);
        });
        return outJsonBeans;
    }
}
