package co.com.report.infrastructure.entrypoints.reactiveweb;

import co.com.report.model.person.Person;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface PersonEventMapper {
    Person toModel(PersonEvent event);
}
