package co.com.report.infrastructure.entrypoints.reactiveweb;

import co.com.report.model.capacity.Capacity;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", uses = {TechnologyEventMapper.class})
public interface CapacityEventMapper {
    Capacity toModel(CapacityEvent event);
}
