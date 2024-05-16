package com.ruoyi.system.domain.rpa;


import lombok.Data;

@Data
public class ConvertData {

    private String filePath;

    private String tradeBankName;

    private String tradeAccountName;

    private String tradeAccountNum;

    private String handleType;

    private Boolean isPushFlag;

//    private boolean isUploadFlag;

    private Boolean isFillTemplateFlag;

//    private ConvertCashire convertCashire;

    private String resJsonType;

    // 时间排序是正序还是倒序
    private Boolean isDesc;

    public ConvertData(){
        System.out.println("Constructed ConvertData: " + this);
    }
}
