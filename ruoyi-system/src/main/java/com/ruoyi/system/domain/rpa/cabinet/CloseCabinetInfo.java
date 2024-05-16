package com.ruoyi.system.domain.rpa.cabinet;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.Objects;

@Data
public class CloseCabinetInfo extends CabinCode{

    @NotBlank
    private Long usbPort;

    @JsonIgnore
    private Integer serverIndex;

    public CloseCabinetInfo(String terId, Long usbPort, Integer serverIndex) {
        super(terId);  // 调用父类的构造函数
        this.usbPort = usbPort;
        this.serverIndex = serverIndex;
    }

    public CloseCabinetInfo() {
        super();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        CloseCabinetInfo that = (CloseCabinetInfo) o;

        return Objects.equals(usbPort, that.usbPort);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (usbPort != null ? usbPort.hashCode() : 0);
        return result;
    }
}
