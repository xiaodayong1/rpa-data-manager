package com.ruoyi.system.domain.rpa.cabinet;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class resData {
    private List<CabinInfo> data;
}
