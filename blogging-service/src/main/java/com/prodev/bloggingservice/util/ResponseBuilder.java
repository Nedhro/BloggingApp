package com.prodev.bloggingservice.util;


import com.prodev.bloggingservice.model.dto.ErrorResponse;
import com.prodev.bloggingservice.model.dto.Response;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;

import java.util.*;

public final class ResponseBuilder {
    private ResponseBuilder() {
    }

    private static List<ErrorResponse> getCustomError(BindingResult result) {
        List<ErrorResponse> dtoList = new ArrayList<>();
        result.getFieldErrors().forEach(fieldError -> {
            ErrorResponse errorResponse = ErrorResponse.builder()
                    .field(fieldError.getField())
                    .message(fieldError.getDefaultMessage())
                    .build();
            dtoList.add(errorResponse);
        });
        return dtoList;
    }

    public static Response getFailureResponse(BindingResult result, String message) {
        return Response.builder()
                .message(message)
                .errors(getCustomError(result))
                .status(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .timeStamp(new Date().getTime())
                .build();
    }

    public static Response getFailureResponse(HttpStatus status, String message) {
        return Response.builder()
                .message(message)
                .status(status.getReasonPhrase())
                .status(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .timeStamp(new Date().getTime())
                .build();
    }

    public static Response getSuccessResponse(HttpStatus status, String message, Object content) {
        return Response.builder()
                .message(message)
                .status(status.getReasonPhrase())
                .statusCode(status.value())
                .content(content)
                .timeStamp(new Date().getTime())
                .build();
    }

    public static Response getSuccessResponse(HttpStatus status, String message, Object content, int numberOfElement) {
        return Response.builder()
                .message(message)
                .status(status.getReasonPhrase())
                .statusCode(status.value())
                .content(content)
                .timeStamp(new Date().getTime())
                .numberOfElement(numberOfElement)
                .build();
    }

    public static Response getSuccessResponse(HttpStatus status, String message, Object content, int numberOfElement, Long rowCount) {
        return Response.builder()
                .message(message)
                .status(status.getReasonPhrase())
                .statusCode(status.value())
                .content(content)
                .timeStamp(new Date().getTime())
                .numberOfElement(numberOfElement)
                .rowCount(rowCount)
                .build();
    }
}
