package co.com.report.infrastructure.entrypoints.reactiveweb;

import org.mapstruct.*;
import co.com.report.domain.model.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface BootcampDTOMapper {
    Bootcamp toModel(BootcampEvent event);
    BootcampResponse toResponse(Bootcamp bootcamp);
}