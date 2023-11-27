package com.miracle.memberservice.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDate;

@Getter
@ToString
public class UserBaseInfoResponseDto {

    private final String email;
    private final String name;
    private final String phone;
    private final LocalDate birth;
    private final String address;

    @Builder
    public UserBaseInfoResponseDto(Object email, Object name, Object phone, Object birth
    , Object address){
        this.email = String.valueOf(email);
        this.name = String.valueOf(name);
        this.phone = String.valueOf(phone);
        this.birth = LocalDate.parse(String.valueOf(birth));
        this.address = String.valueOf(address);
    }
}
