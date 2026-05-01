package co.com.report.infrastructure.entrypoints.reactiveweb;

import lombok.*;

@Getter
@AllArgsConstructor
public class ErrorData {
    private final String errorCode;
    private final String message;
    private final String description;

    public static ErrorData of(co.com.report.domain.exception.GlobalExceptionEnum error) {
        return new ErrorData(error.name(), error.getMessage(), error.getDescription());
    }
}