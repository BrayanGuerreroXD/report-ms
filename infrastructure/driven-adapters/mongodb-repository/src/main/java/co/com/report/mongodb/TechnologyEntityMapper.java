package co.com.report.mongodb;

import co.com.report.model.technology.Technology;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface TechnologyEntityMapper {
    Technology toModel(TechnologyEntity entity);
    TechnologyEntity toEntity(Technology model);
}
