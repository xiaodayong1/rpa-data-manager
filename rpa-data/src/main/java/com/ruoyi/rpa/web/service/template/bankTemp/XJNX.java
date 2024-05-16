package com.ruoyi.rpa.web.service.template.bankTemp;

import com.ruoyi.rpa.util.DataUtil;
import com.ruoyi.rpa.util.ExcelUtil;
import com.ruoyi.rpa.web.service.template.HandleTemplate;
import com.ruoyi.system.domain.rpa.transaction.StandardTransaction;
import com.ruoyi.system.domain.rpa.transaction.Transaction;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.*;

@Component
public class XJNX extends HandleTemplate {
    @Override
    protected HashMap<String, Object> handleExcelHead(Workbook xwb) {
        System.out.println("新疆农信社无需表格处理");

        return null;
    }

    @Override
    protected List<LinkedHashMap<String, Object>> handleExcelData(Workbook xwb) {
        System.out.println("新疆农信社模板处理表数据");
        return ExcelUtil.importExcelData(xwb, 0);
    }

    @Override
    protected Class<? extends Transaction> getTransactionClass() {
        return StandardTransaction.class;
    }

    @Override
    public <T extends Transaction> List<T> fillData(HashMap<String, Object> headMap, List<LinkedHashMap<String, Object>> dataMap) {
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
            outJsonBean.setSerialNumber(String.valueOf(System.currentTimeMillis()));
            // "K2 - 摘要"
            outJsonBean.setRemark(data.get("业务种类").toString());
            // "K3 - 交易时间 这个字段比较特殊，需要针对处理"

            if (!Objects.isNull(data.get("交易日期")) && data.get("交易日期").toString() != "-") {
                final String outputDateString = DataUtil.formatToStandardDateTime("yyyy-MM-dd HH:mm:ss", data.get("交易日期").toString() + " " + "08:00:00");
                outJsonBean.setTransactionTime(outputDateString);
            }
            // "K4 - 交易类型"
            String stringValue = (String) data.get("收入金额");
            stringValue.replaceAll(",", "");
            if (!stringValue.equals("--") && !stringValue.isEmpty()) {

                outJsonBean.setTransactionType("C");
                // K5
                outJsonBean.setTransactionAmount(data.get("收入金额").toString());
            } else {
                outJsonBean.setTransactionType("D");
                outJsonBean.setTransactionAmount(data.get("支出金额").toString());
            }

            // k6


            outJsonBean.setCounterpartyAccountNum(data.get("对方帐号").toString());
            outJsonBean.setCounterpartyAccountName(data.get("对方户名").toString());
            outJsonBean.setCurrency("CNY");
            outJsonBean.setCounterpartyBankName(data.get("对方行名").toString());
            outJsonBeans.add(outJsonBean);
        });
        return outJsonBeans;
    }
}
