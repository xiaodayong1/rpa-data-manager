package com.ruoyi.system.domain.rpa.transaction;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;

@Data
public class ReceiptDto {

    private String bank;

    private String type;

    @Size(min = 0)
    private int topMargin;

    @Size(min = 0)
    private int bottomMargin;

    private String[] topText;

    private String bottomText;

    @NotBlank
    private String filePath;

    @NotBlank
    private String savePath;

    @JsonProperty("isUpload")
    private boolean isUpload;

    private String order;

    private List<ElectronicEntry> electronicList;

}
