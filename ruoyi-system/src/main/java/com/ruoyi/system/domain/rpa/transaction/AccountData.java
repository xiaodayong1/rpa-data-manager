package com.ruoyi.system.domain.rpa.transaction;


import lombok.Data;

import java.util.List;

/*
* 账户信息
* */
@Data
public class AccountData {
    private Data data;

    @lombok.Data
    public static class Data {
        private List<Balance> balanceList;
    }
}
