package com.mask.api.global.custom;

import com.mask.api.global.custom.Error.CustomError;
import com.mask.api.global.custom.Error.CustomException;
import com.mask.api.global.custom.Error.ErrorCode;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
public class CustomResponse {
    @Setter
    @Getter
    @Builder
    private static class Body{
        private Object data;
        private Object error;
    }

    public ResponseEntity<?> success(Object data, HttpStatus status){

        Body body = Body.builder()
                .data(data)
                .error(null)
                .build();
        return ResponseEntity.status(status).body(body);
    }

    public ResponseEntity<?> fail(CustomException e){
        ErrorCode errorCode = e.getErrorCode();

        Body body = Body.builder()
                .data(null)
                .error(CustomError.builder()
                        .error(errorCode.getHttpStatus().name())
                        .code(errorCode.name())
                        .build())
                .build();
        return ResponseEntity.status(errorCode.getHttpStatus()).body(body);
    }
}
