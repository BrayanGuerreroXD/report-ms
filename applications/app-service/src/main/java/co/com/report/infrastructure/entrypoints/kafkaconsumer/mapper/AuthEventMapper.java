package co.com.report.infrastructure.entrypoints.kafkaconsumer.mapper;

import co.com.report.infrastructure.entrypoints.kafkaconsumer.dto.AuthLoginEvent;
import co.com.report.model.auth.Auth;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AuthEventMapper {
    Auth toAuth(AuthLoginEvent event);
}