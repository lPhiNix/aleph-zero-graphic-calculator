package com.alephzero.alephzero.infrastructure.config;

import org.mapstruct.MapperConfig;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.MappingConstants;
import org.mapstruct.NullValuePropertyMappingStrategy;

@MapperConfig(componentModel = MappingConstants.ComponentModel.SPRING, unmappedTargetPolicy = ReportingPolicy.ERROR, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface GlobalMapperConfig {
}
