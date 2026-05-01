package co.com.report.infrastructure.drivenadapters.mongodb.repository.auth;

import co.com.report.model.auth.Auth;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface AuthEntityMapper {
    Auth toModel(AuthEntity entity);
    AuthEntity toEntity(Auth model);
}
