package com.example.pokecenter.customer.lam.Model.voucher;

import java.util.Date;

public class VoucherInfo {

    private boolean status;
    private Date startDate;
    private Date endDate;
    private int value;

    public VoucherInfo() {

    }

    public VoucherInfo(boolean status, Date startDate, Date endDate, int value) {
        this.status = status;
        this.startDate = startDate;
        this.endDate = endDate;
        this.value = value;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
