package com.mask.api.global.common;

import com.mask.api.global.exception.CustomException;
import com.mask.api.global.exception.ErrorCode;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
public class Response {

    @Getter
    @Setter
    @Builder
    public static class Body{
        private Object result;
        private String message;
    }


    public ResponseEntity<?> success(Object data,HttpStatus status){

        Body body = Body.builder()
                .result(data)
                .message("Success")
                .build();

        return ResponseEntity.status(status).body(body);
    }

    public ResponseEntity<?> fail(CustomException e){//String errorMessage, HttpStatus status){
        ErrorCode errorCode = e.getErrorCode();
        Body body = Body.builder()
                .message(errorCode.getDetail())
                .build();

        return ResponseEntity.status(errorCode.getHttpStatus()).body(body);
    }


}
