package co.com.report.infrastructure.drivenadapters.mongodb.repository;

import co.com.report.model.capacity.Capacity;
import co.com.report.model.technology.Technology;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring", uses = {TechnologyEntityMapper.class})
public interface CapacityEntityMapper {
    Capacity toModel(CapacityEntity entity);
    CapacityEntity toEntity(Capacity model);
}
