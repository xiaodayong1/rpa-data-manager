package com.ruoyi.system.domain.rpa.transaction;


import lombok.Data;

import java.util.List;

@Data
public class TradeAccountData {
    private Data data;

    @lombok.Data
    public static class Data {
        private List<StandardTransaction> detailRequestList;
    }
}
