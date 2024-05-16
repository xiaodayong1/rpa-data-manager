package com.ruoyi.system.domain.rpa.cabinet;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class RobotarmManage {
    private String robotarm_num;
    @JsonProperty("robotarm_info")
    private List<RobotarmInfo> robotarmInfo;
}

