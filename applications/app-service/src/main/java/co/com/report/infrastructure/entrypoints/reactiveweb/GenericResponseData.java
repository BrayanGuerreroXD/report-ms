package co.com.report.infrastructure.entrypoints.reactiveweb;

import lombok.*;

@Getter
@AllArgsConstructor
public class GenericResponseData<T> {
    private final T data;

    public static <T> GenericResponseData<T> of(T data) {
        return new GenericResponseData<>(data);
    }
}