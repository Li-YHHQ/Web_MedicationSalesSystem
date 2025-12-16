package com.neusoft.coursemgr.domain;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class UpdateCreditRequest {
    @NotNull
    @Min(0)
    private Integer credit;

    public Integer getCredit() {
        return credit;
    }

    public void setCredit(Integer credit) {
        this.credit = credit;
    }
}
