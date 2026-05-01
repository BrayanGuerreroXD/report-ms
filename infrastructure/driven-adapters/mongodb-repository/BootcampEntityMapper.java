package co.com.report.infrastructure.drivenadapters.mongodb.repository;

import org.mapstruct.*;
import co.com.report.domain.model.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface BootcampEntityMapper {
    Bootcamp toModel(BootcampEntity entity);
    BootcampEntity toEntity(Bootcamp model);
}
