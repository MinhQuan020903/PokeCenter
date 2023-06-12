package com.example.pokecenter.admin.AdminTab.Model.AdminBlockVoucher;

import java.io.Serializable;

public class AdminVoucher implements Serializable {
    private String blockVoucherId;
    private String code;
    private boolean status;

    public AdminVoucher(String blockVoucherId, String code, boolean status) {
        this.blockVoucherId = blockVoucherId;
        this.code = code;
        this.status = status;
    }

    public String getBlockVoucherId() {
        return blockVoucherId;
    }

    public void setBlockVoucherId(String blockVoucherId) {
        this.blockVoucherId = blockVoucherId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
}
