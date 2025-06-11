package com.alephzero.alephzero.api.math;

import com.alephzero.alephzero.api.math.dto.request.history.UserHistoryCreationDto;
import com.alephzero.alephzero.db.basicdto.SimpleUserHistoryDto;
import com.alephzero.alephzero.api.math.service.persistence.MathUserHistoryService;
import com.alephzero.alephzero.api.util.common.messages.ApiMessageFactory;
import com.alephzero.alephzero.api.util.common.messages.UriHelperBuilder;
import com.alephzero.alephzero.api.util.common.messages.dto.ApiResponse;
import com.alephzero.alephzero.db.basicdto.UserHistoryDto;
import com.alephzero.alephzero.db.mappers.UserHistoryMapper;
import com.alephzero.alephzero.db.models.UserHistory;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RequiredArgsConstructor
@RestController("api/v1/math/history")
public class MathUserHistoryController {
    private final MathUserHistoryService mathUserHistoryService;
    private final ApiMessageFactory apiMessageFactory;
    private final UserHistoryMapper userHistoryMapper;

    @PostMapping
    public ResponseEntity<ApiResponse<Void>> addEntryToUserHistory(@RequestBody UserHistoryCreationDto request) {
        UserHistory history = mathUserHistoryService.createUserHistory(request);
        URI location = UriHelperBuilder.buildUriFromCurrentRequest(history.getId());
        return apiMessageFactory.response().created(location).build();
    }

    @PreAuthorize("@userHistorySecurity.hasAccessTo(#id, authentication)")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteUserHistory(@PathVariable Integer id) {
        mathUserHistoryService.deleteById(id);
        return apiMessageFactory.response().noContent().build();
    }

    @PreAuthorize("@userHistorySecurity.hasAccessTo(#id, authentication)")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserHistoryDto>> getUserHistory(@PathVariable Integer id) {
        UserHistory entity = mathUserHistoryService.findByIdReadOnly(id);
        UserHistoryDto dto =  userHistoryMapper.toResponseDtoFromEntity(entity);
        return apiMessageFactory.response(dto).ok().build();
    }

    @PreAuthorize("@userHistorySecurity.hasAccessTo(#id, authentication)")
    @GetMapping("summary/{id}")
    public ResponseEntity<ApiResponse<SimpleUserHistoryDto>> getHistorySummaryById(@PathVariable Integer id){
        UserHistory history = mathUserHistoryService.findByIdReadOnly(id);
        SimpleUserHistoryDto dto = userHistoryMapper.toSimpleResponseDtoFromEntity(history);
        return apiMessageFactory.response(dto).ok().build();
    }

    @GetMapping("summary/")
    public ResponseEntity<ApiResponse<List<SimpleUserHistoryDto>>> getHistorySummary(){
        List<UserHistory> elements = mathUserHistoryService.findAllByCurrentUserReadOnly();
        List<SimpleUserHistoryDto> dto = userHistoryMapper.toSimpleResponseDtoListFromEntityList(elements);
        return apiMessageFactory.response(dto).ok().build();
    }
}
