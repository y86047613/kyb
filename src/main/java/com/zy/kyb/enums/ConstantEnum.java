package com.zy.kyb.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ConstantEnum {
    DEFAULT_ROLE("USER"), AUTHORIZATION("Authorization");

    private String value;

}
