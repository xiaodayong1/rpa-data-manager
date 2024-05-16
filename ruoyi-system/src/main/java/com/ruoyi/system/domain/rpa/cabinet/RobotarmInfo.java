package com.ruoyi.system.domain.rpa.cabinet;

import lombok.Data;

@Data
public class RobotarmInfo {
    private String port_num;
    private String robot_arm_version;
    private String robotarm_status;
    private String robotarm_with_dev_ports;
}
