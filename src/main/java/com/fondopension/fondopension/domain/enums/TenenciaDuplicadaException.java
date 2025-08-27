package com.fondopension.fondopension.domain.enums;
import com.fondopension.fondopension.domain.exception.BusinessException;

public class TenenciaDuplicadaException extends BusinessException {
    public TenenciaDuplicadaException(String msg) { super(msg); }
}
