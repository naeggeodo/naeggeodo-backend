package com.naeggeodo.exception;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
	
	//400
	INVALID_FORMAT(HttpStatus.BAD_REQUEST,"올바르지 않은 요청입니다."),
	UNAUTHORZED(HttpStatus.UNAUTHORIZED,"인증 정보가 올바르지 않습니다"),
	UNKNOWN_ERROR(HttpStatus.BAD_REQUEST,"알 수 없는 오류 발생! 관리자에게 문의해주세요"),
	// 404
	RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND,"리소스를 찾을수 없습니다."),

	UPLOAD_FAIL(HttpStatus.UNSUPPORTED_MEDIA_TYPE,"업로드실패")


	;
	private final HttpStatus httpStatus;
    private final String detail;
}
