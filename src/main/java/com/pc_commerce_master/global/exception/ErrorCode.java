package com.pc_commerce_master.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // 공통
    INVALID_INPUT(HttpStatus.BAD_REQUEST, "잘못된 입력값입니다."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다."),

    // 관리자
    ADMIN_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 관리자입니다."),
    ADMIN_INVALID_PASSWORD(HttpStatus.UNAUTHORIZED, "이메일 또는 비밀번호가 일치하지 않습니다."),

    // 고객
    CUSTOMER_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 고객입니다."),

    // 상품
    PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 상품입니다."),
    PRODUCT_DISCONTINUED(HttpStatus.BAD_REQUEST, "단종된 상품입니다."),
    PRODUCT_SOLD_OUT(HttpStatus.BAD_REQUEST, "품절된 상품입니다."),

    // 주문
    ORDER_CANCEL_NOT_ALLOWED(HttpStatus.BAD_REQUEST, "준비중 상태의 주문만 취소할 수 있습니다.");

    private final HttpStatus status;
    private final String message;
}
