package com.ruoyi.rpa.web.service.template.bankTemp;

import com.ruoyi.rpa.util.DataUtil;
import com.ruoyi.rpa.util.ExcelUtil;
import com.ruoyi.rpa.web.service.template.HandleTemplate;
import com.ruoyi.rpa.web.service.template.templateInput.ADBCInput;
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
public class ADBC extends HandleTemplate {


    @Override
    protected HashMap<String, Object> handleExcelHead(Workbook xwb) {
        System.out.println("中国农业发展银行模板处理表头");
        final ADBCInput ADBCInput = new ADBCInput();
        return ExcelUtil.readExcelIndexData(xwb, ADBCInput.getClass(), 0, 4);
    }

    @Override
    protected List<LinkedHashMap<String, Object>> handleExcelData(Workbook xwb) {
        log.info("中国农业发展银行读取表数据");
        return ExcelUtil.importExcelData(xwb, 4);
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
            outJsonBean.setTransactionTime(DataUtil.formatToStandardDateTime("yyyy-MM-ddHH:mm:ss",item.get("交易时间").toString().replaceAll("\n","").replaceAll(" ","")));

            String stringValue = (String) item.get("借方发生额");
            stringValue.replaceAll(",", "");
            if (stringValue != null && !stringValue.isEmpty()) {
                if (!stringValue.equals("-") ) {
                    outJsonBean.setTransactionType("D");
                    // K5
                    outJsonBean.setTransactionAmount(item.get("借方发生额").toString().replaceAll(",", ""));
                } else {
                    outJsonBean.setTransactionType("C");
                    outJsonBean.setTransactionAmount(item.get("贷方发生额").toString());
                }
            }
            outJsonBean.setCurrency("CNY");
            outJsonBean.setCounterpartyAccountName(item.get("交易对手名称").toString());
            outJsonBean.setCounterpartyAccountNum(item.get("交易对手账户").toString());
            outJsonBean.setCounterpartyBankName(item.get("交易对手行").toString());
            outJsonBean.setBalance(item.get("账户余额").toString().replaceAll(",", ""));
            outJsonBean.setTradeAccountName(headMap.get("tradeAccountName").toString());
            outJsonBean.setTradeAccountNum(headMap.get("tradeAccountNum").toString());
            outJsonBeans.add(outJsonBean);
        });
        return outJsonBeans;
    }
}
