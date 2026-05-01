package co.com.report.infrastructure.drivenadapters.mongodb.repository;

import co.com.report.model.bootcamp.Bootcamp;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", uses = {CapacityEntityMapper.class, PersonEntityMapper.class})
public interface BootcampEntityMapper {
    Bootcamp toModel(BootcampEntity entity);
    BootcampEntity toEntity(Bootcamp model);
}
