package com.ruoyi.system.domain.rpa.transaction;

import lombok.Data;

/**
 * 标准版司库准换模板类 比生态版司库多了余额
 */
@Data
public class StandardTransaction extends Transaction{
    private String balance = "-";

    @Override
    public void setBalance(String balance) {
        // 子类方法的实现，可以调用父类的实现
        super.setBalance(balance);

        // 子类特有的实现
        this.balance = balance;
    }

}
