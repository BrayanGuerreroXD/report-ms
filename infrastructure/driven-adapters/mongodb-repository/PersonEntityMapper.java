package co.com.report.infrastructure.drivenadapters.mongodb.repository;

import co.com.report.model.person.Person;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface PersonEntityMapper {
    Person toModel(PersonEntity entity);
    PersonEntity toEntity(Person model);
}
