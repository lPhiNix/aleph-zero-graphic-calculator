package com.alephzero.alephzero.api.v1.math;

import com.alephzero.alephzero.api.v1.math.dto.request.history.UserHistoryCreationDto;
import com.alephzero.alephzero.api.v1.math.dto.SimpleUserHistoryDto;
import com.alephzero.alephzero.core.services.math.persistence.MathUserHistoryService;
import com.alephzero.alephzero.commons.messages.UriHelperBuilder;
import com.alephzero.alephzero.api.v1.math.dto.UserHistoryDto;
import com.alephzero.alephzero.api.v1.math.mapper.UserHistoryMapper;
import com.alephzero.alephzero.core.models.UserHistory;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/math/history")
public class MathUserHistoryController {
	private final MathUserHistoryService mathUserHistoryService;
	private final UserHistoryMapper userHistoryMapper;

	@GetMapping("/summary")
	public ResponseEntity<List<SimpleUserHistoryDto>> getHistorySummary() {
		List<UserHistory> elements = mathUserHistoryService.findAllByCurrentUserReadOnly();
		List<SimpleUserHistoryDto> dto = userHistoryMapper.toSimpleResponseDtoListFromEntityList(elements);
		return ResponseEntity.ok(dto);

	}

	@PostMapping
	public ResponseEntity<Void> addEntryToUserHistory(@RequestBody UserHistoryCreationDto request) {
		UserHistory history = mathUserHistoryService.createUserHistory(request);
		URI location = UriHelperBuilder.buildUriFromCurrentRequest(history.getId());
		return ResponseEntity.created(location).build();
	}

	@PreAuthorize("@userHistorySecurity.hasAccessTo(#id, authentication)")
	@DeleteMapping("/{id:\\d+}")
	public ResponseEntity<Void> deleteUserHistory(@PathVariable Integer id) {
		mathUserHistoryService.deleteById(id);
		return ResponseEntity.noContent().build();
	}

	@PreAuthorize("@userHistorySecurity.hasAccessTo(#id, authentication)")
	@GetMapping("/{id:\\d+}")
	public ResponseEntity<UserHistoryDto> getUserHistory(@PathVariable Integer id) {
		UserHistory entity = mathUserHistoryService.findByIdReadOnly(id);
		UserHistoryDto dto = userHistoryMapper.toResponseDtoFromEntity(entity);
		return ResponseEntity.ok(dto);
	}

	@PreAuthorize("@userHistorySecurity.hasAccessTo(#id, authentication)")
	@GetMapping("/summary/{id:\\d+}")
	public ResponseEntity<SimpleUserHistoryDto> getHistorySummaryById(@PathVariable Integer id) {
		UserHistory history = mathUserHistoryService.findByIdReadOnly(id);
		SimpleUserHistoryDto dto = userHistoryMapper.toSimpleResponseDtoFromEntity(history);
		return ResponseEntity.ok(dto);
	}
}
