package co.com.report.infrastructure.entrypoints.reactiveweb;

import co.com.report.model.technology.Technology;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface TechnologyResponseMapper {
    Technology toModel(TechnologyResponse event);
}
