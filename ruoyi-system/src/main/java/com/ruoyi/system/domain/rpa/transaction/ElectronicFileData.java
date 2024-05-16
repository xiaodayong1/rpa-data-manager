package com.ruoyi.system.domain.rpa.transaction;

import lombok.Data;

@Data
public class ElectronicFileData {
    private Data data;
    @lombok.Data
    public static class Data {
        private String file;
    }
}
