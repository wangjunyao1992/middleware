package com.wangjunyao.middleware.server.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Objects;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PhoneUser implements Serializable {

    private String phone;

    private Double fare;

    //手机号相同，代表充值记录重复（只适用于特殊的排名需要），所以需要重写equals和hashcode

    @Override
    public boolean equals(Object obj){
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        PhoneUser phoneUser = (PhoneUser) obj;
        return Objects.equals(phone, phoneUser.phone);
    }

    @Override
    public int hashCode(){
        return phone != null ? phone.hashCode() : 0;
    }

}
