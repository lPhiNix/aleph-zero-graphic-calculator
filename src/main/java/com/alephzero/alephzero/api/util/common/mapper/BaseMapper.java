package com.alephzero.alephzero.api.util.common.mapper;


/**
 * A basic interface for MapStruct mappers. Specially useful to avoid code duplication when mapping basic
 * responses.
 * @param <E> Entity.
 * @param <D> Response DTO, normaly a general one from {@link com.alephzero.alephzero.db.basicdto}
 */
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
