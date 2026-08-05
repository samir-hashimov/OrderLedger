package com.orderledger.exception;

public class CouponAlreadyExistsException extends RuntimeException {
    public CouponAlreadyExistsException(String code) {
        super(String.format("'%s' kupon kodu artıq bazada mövcuddur!", code));
    }
}