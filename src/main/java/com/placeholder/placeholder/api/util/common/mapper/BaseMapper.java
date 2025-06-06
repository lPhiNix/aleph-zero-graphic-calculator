package com.placeholder.placeholder.api.util.common.mapper;


public interface BaseMapper<E, D> {

    /**
     * Converts an entity to a DTO.
     *
     * @param entity the entity to convert
     * @return the converted DTO
     */
    D toResponseDtoFromEntity(E entity);

    /**
     * Converts a DTO to an entity.
     *
     * @param dto the DTO to convert
     * @return the converted entity
     */
    E toEntityFromResponseDto(D dto);
}
