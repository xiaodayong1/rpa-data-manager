package com.ruoyi.rpa.web.service.template.bankTemp;

import com.ruoyi.rpa.util.DataUtil;
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
public class CCB2 extends HandleTemplate {

    @Override
    public HashMap<String, Object> handleExcelHead(Workbook xwb) {
        System.out.printf("建设银行2模板处理表头" + "此模板没有表头 不需处理");
        return null;
    }

    @Override
    public List<LinkedHashMap<String, Object>> handleExcelData(Workbook xwb) {
        System.out.printf("建设银行2模板处理表数据");
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
        dataMap.stream().forEach(item -> {
            T outJsonBean;
            try {
                outJsonBean = (T) getTransactionClass().newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                throw new RuntimeException("Error creating instance of Transaction class", e);
            }
            outJsonBean.setSerialNumber(item.get("账户明细编号-交易流水号").toString());
            outJsonBean.setRemark(item.get("摘要").toString());
            if (!Objects.isNull(item.get("交易时间")) && item.get("交易时间").toString() != "-") {
                final String outputDateString = DataUtil.formatToStandardDateTime("yyyyMMdd HH:mm:ss", item.get("交易时间").toString());
                outJsonBean.setTransactionTime(outputDateString);
            }
            // "K4 - 交易类型"
            String stringValue = (String) item.get("借方发生额（支取）");
            if (stringValue != null && !stringValue.isEmpty() && stringValue != "-") {
                try {
                    Double doubleValue = Double.parseDouble(stringValue);
                    if (doubleValue == 0) {
                        outJsonBean.setTransactionType("C");
                        outJsonBean.setTransactionAmount(item.get("贷方发生额（收入）").toString());
                    } else {
                        outJsonBean.setTransactionType("D");
                        outJsonBean.setTransactionAmount(item.get("借方发生额（支取）").toString());
                    }
                } catch (NumberFormatException e) {
                    log.error("建设银行模板二解析借方发生额（支取）发生异常" + item.get("借方发生额（支取）"));
                    // 处理字符串无法解析为 Long 的情况
                    e.printStackTrace();
                }
            }
            // 没有开户行
            outJsonBean.setTradeAccountName(item.get("账户名称").toString());
            outJsonBean.setTradeAccountNum(item.get("账号").toString());
            outJsonBean.setCounterpartyAccountNum(item.get("对方账号").toString());
            outJsonBean.setCounterpartyBankName(item.get("对方开户机构").toString());
            outJsonBean.setCounterpartyAccountName(item.get("对方户名").toString());
            outJsonBean.setCurrency("CNY");
            outJsonBean.setBalance(item.get("余额").toString().replaceAll(",",""));
            outJsonBeans.add(outJsonBean);
        });
        return outJsonBeans;
    }
}
