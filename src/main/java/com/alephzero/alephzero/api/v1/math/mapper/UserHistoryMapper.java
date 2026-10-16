package com.alephzero.alephzero.api.v1.math.mapper;

import java.util.Comparator;
import java.util.List;
import java.util.Set;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

import com.alephzero.alephzero.api.v1.math.dto.SimpleUserHistoryDto;
import com.alephzero.alephzero.api.v1.math.dto.UserHistoryDto;
import com.alephzero.alephzero.api.v1.math.dto.request.history.UserHistoryCreationDto;
import com.alephzero.alephzero.api.v1.user.mapper.UserMapper;
import com.alephzero.alephzero.core.models.HistoryExpression;
import com.alephzero.alephzero.core.models.User;
import com.alephzero.alephzero.core.models.UserHistory;
import com.alephzero.alephzero.core.services.math.persistence.SnapshotUtils;
import com.alephzero.alephzero.infrastructure.config.GlobalMapperConfig;

/**
 * MapStruct mapper for {@link UserHistory} ==> DTOs.
 */
@Mapper(config = GlobalMapperConfig.class, uses = { HistoryExpressionMapper.class, UserMapper.class })
public interface UserHistoryMapper {

	/**
	 * Converts a {@link UserHistory} into a response {@link UserHistoryDto} with a
	 * transformed snapshot field.
	 *
	 * @param entity the {@link UserHistory} entity to convert
	 * @return a mapped {@link UserHistoryDto}
	 */
	@Mapping(source = "snapshot", target = "snapshot", qualifiedByName = "parseSnapshotToUrl")
	UserHistoryDto toResponseDtoFromEntity(UserHistory entity);

	/**
	 * Maps a {@link UserHistoryCreationDto} into a {@link UserHistory} entity.
	 * <p>
	 * The owning {@link User} and the generated snapshot UUID are provided as extra
	 * source parameters,
	 * so no mapping context is required. The {@code snapshot} of the DTO is
	 * intentionally ignored:
	 * the entity stores the generated {@code snapshotUuid} instead.
	 *
	 * @param creationDto  the incoming DTO for creation
	 * @param user         the owner of the history entry
	 * @param snapshotUuid the generated snapshot identifier to persist
	 * @return a mapped {@link UserHistory} entity
	 */
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "updatedAt", ignore = true)
	@Mapping(target = "user", source = "user")
	@Mapping(target = "snapshot", source = "snapshotUuid")
	@Mapping(target = "historyExpressions", source = "creationDto.mathExpressions")
	UserHistory toEntityFromCreationDto(UserHistoryCreationDto creationDto, User user, String snapshotUuid);

	/**
	 * Maps a {@link UserHistory} into a simplified version
	 * {@link SimpleUserHistoryDto},
	 * including only the first math expression as the description.
	 *
	 * @param entity the entity to convert
	 * @return a simplified DTO
	 */
	@Mapping(source = "historyExpressions", target = "description", qualifiedByName = "getFirst")
	@Mapping(source = "snapshot", target = "snapshot", qualifiedByName = "parseSnapshotToUrl")
	SimpleUserHistoryDto toSimpleResponseDtoFromEntity(UserHistory entity);

	/**
	 * Maps a list of {@link UserHistory} entities into a list of simplified DTOs.
	 *
	 * @param entities list of user history entities
	 * @return list of simplified DTOs
	 */
	List<SimpleUserHistoryDto> toSimpleResponseDtoListFromEntityList(List<UserHistory> entities);

	/**
	 * Links each mapped {@link HistoryExpression} back to its owning
	 * {@link UserHistory}.
	 *
	 * @param entity the freshly mapped user history
	 */
	@AfterMapping
	default void linkBackReferences(@MappingTarget UserHistory entity) {
		entity.getHistoryExpressions().forEach(expression -> expression.setUserHistory(entity));
	}

	/**
	 * Extracts the expression string of the {@link HistoryExpression} with the
	 * lowest {@code indexOrder}.
	 *
	 * @param historyExpressions a set of history expressions
	 * @return the expression string of the first entry, or null if empty
	 */
	@Named("getFirst")
	default String getFirst(Set<HistoryExpression> historyExpressions) {
		return historyExpressions.stream()
				.min(Comparator.comparing(HistoryExpression::getIndexOrder,
						Comparator.nullsLast(Comparator.naturalOrder())))
				.map(expression -> expression.getMathExpression().getExpression())
				.orElse(null);
	}

	/**
	 * Transforms a snapshot UUID into a complete URL.
	 *
	 * @param snapshot the snapshot UUID
	 * @return the full URL string, or null if input is null or empty
	 */
	@Named("parseSnapshotToUrl")
	default String parseSnapshotToUrl(String snapshot) {
		if (snapshot == null || snapshot.isEmpty()) {
			return null;
		}
		return SnapshotUtils.getSnapshotUrl(snapshot);
	}
}
