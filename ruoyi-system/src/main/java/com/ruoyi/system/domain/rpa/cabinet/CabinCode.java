package com.ruoyi.system.domain.rpa.cabinet;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.Objects;

@Data
@AllArgsConstructor
public class CabinCode {
    @NotBlank
    private String terId;

    public CabinCode(){

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CabinCode cabinCode = (CabinCode) o;

        return Objects.equals(terId, cabinCode.terId);
    }

    @Override
    public int hashCode() {
        return terId != null ? terId.hashCode() : 0;
    }
}
