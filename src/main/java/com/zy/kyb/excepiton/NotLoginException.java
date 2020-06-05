package com.zy.kyb.excepiton;



import com.zy.kyb.enums.ExceptionEnum;

public class NotLoginException extends BasicException {
    public NotLoginException(ExceptionEnum exceptionEnum) {
        super(exceptionEnum);
    }
}
