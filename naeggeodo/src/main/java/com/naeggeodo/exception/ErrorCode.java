package com.naeggeodo.exception;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
	
	//400
	INVALID_FORMAT(HttpStatus.BAD_REQUEST,"올바르지 않은 요청입니다."),
	
	// 404
	RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND,"리소스를 찾을수 없습니다.")
	
	
	;
	private final HttpStatus httpStatus;
    private final String detail;
}
