package com.ruoyi.system.domain.rpa.transaction;

import lombok.Data;

import java.util.List;

@Data
public class ElectronicTransactionDto {
    private List<ElectronicEntry> electronicList;
}
