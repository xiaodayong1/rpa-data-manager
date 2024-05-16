package com.ruoyi.system.domain.rpa.transaction;

import lombok.Data;

import java.util.List;

@Data
public class ElectronicTransaction {
    private Data data;

    @lombok.Data
    public static class Data {
        private List<ElectronicEntry> electronicList;
    }

}

