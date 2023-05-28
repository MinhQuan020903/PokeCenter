package com.example.pokecenter.admin.Quan.AdminTab.Model.User;

import com.example.pokecenter.customer.lam.Model.address.Address;

public class Vender extends User {

    @Override
    public String toString() {
        StringBuilder ret = new StringBuilder("Vender {addresses=");
        if (addresses != null) {
            for (Address a : addresses) {
                ret.append(a.getAddress2()).append(", ");
            }
        }
        ret.append("address='").append(address).append('\'')
                .append(", avatar='").append(avatar).append('\'')
                .append(", gender='").append(gender).append('\'')
                .append(", phoneNumber='").append(phoneNumber).append('\'')
                .append(", registrationDate='").append(registrationDate).append('\'')
                .append(", role=").append(role)
                .append(", username='").append(username).append('\'')
                .append(", email='").append(email).append('\'')
                .append('}');
        return ret.toString();
    }
}
