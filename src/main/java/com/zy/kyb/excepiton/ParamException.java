package com.zy.kyb.excepiton;



import com.zy.kyb.enums.ExceptionEnum;

public class ParamException extends BasicException {
    public ParamException(ExceptionEnum exceptionEnum) {
        super(exceptionEnum);
    }
}
