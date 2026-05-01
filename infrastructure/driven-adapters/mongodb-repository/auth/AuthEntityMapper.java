package co.com.report.infrastructure.drivenadapters.mongodb.repository.auth;

import org.mapstruct.*;
import co.com.report.domain.model.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AuthEntityMapper {
    Auth toModel(AuthEntity entity);
    AuthEntity toEntity(Auth model);
}
