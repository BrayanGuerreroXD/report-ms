package co.com.report.domain.exception;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum GlobalExceptionEnum {
    BOOTCAMP_NOT_FOUND("Bootcamp not found", "No bootcamp found with the provided identifier"),
    BOOTCAMP_ALREADY_EXISTS("Bootcamp already exists", "A bootcamp with the provided externalId already exists"),
    UNAUTHORIZED("Unauthorized", "Token is missing or invalid"),
    TOKEN_EXPIRED("Token expired", "The provided token has expired");

    private final String message;
    private final String description;
}