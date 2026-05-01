package co.com.report.infrastructure.entrypoints.reactiveweb;

import co.com.report.model.bootcamp.Bootcamp;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", uses = {CapacityResponseMapper.class, PersonResponseMapper.class})
public interface BootcampDTOMapper {
    Bootcamp toModel(BootcampEvent event);
    BootcampResponse toResponse(Bootcamp bootcamp);
}
