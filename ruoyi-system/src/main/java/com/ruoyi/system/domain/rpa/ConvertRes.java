package com.ruoyi.system.domain.rpa;

import com.ruoyi.system.domain.rpa.transaction.ElectronicEntry;
import lombok.Data;

import java.util.List;

@Data
public class ConvertRes {

    private List transationList;

    private List<ElectronicEntry> electronicList;


    public ConvertRes(List transationList, List electronicList) {
        this.transationList = transationList;
        this.electronicList = electronicList;
    }
}
