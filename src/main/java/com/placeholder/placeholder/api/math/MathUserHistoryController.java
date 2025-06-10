package com.placeholder.placeholder.api.math;

import com.placeholder.placeholder.api.auth.service.SquipUserDetailService;
import com.placeholder.placeholder.api.math.dto.request.UserHistoryCreationDto;
import com.placeholder.placeholder.api.math.dto.request.UserHistoryUpddateDto;
import com.placeholder.placeholder.api.math.service.persistence.MathExpressionPersistenceService;
import com.placeholder.placeholder.api.math.service.persistence.MathUserHistoryService;
import com.placeholder.placeholder.api.util.common.messages.ApiMessageFactory;
import com.placeholder.placeholder.api.util.common.messages.UriHelperBuilder;
import com.placeholder.placeholder.api.util.common.messages.dto.ApiResponse;
import com.placeholder.placeholder.db.basicdto.UserHistoryDto;
import com.placeholder.placeholder.db.mappers.UserHistoryMapper;
import com.placeholder.placeholder.db.models.User;
import com.placeholder.placeholder.db.models.UserHistory;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RequiredArgsConstructor
@RestController("api/v1/math/user-history")
public class MathUserHistoryController {
    private final MathExpressionPersistenceService persistenceService;
    private final MathUserHistoryService mathUserHistoryService;
    private final ApiMessageFactory apiMessageFactory;
    private final UserHistoryMapper userHistoryMapper;
    private final SquipUserDetailService squipUserDetailService;

    @PostMapping
    public ResponseEntity<ApiResponse<Void>> addEntryToUserHistory(@RequestBody UserHistoryCreationDto request) {
        UserHistory history = mathUserHistoryService.createUserHistory(request);
        URI location = UriHelperBuilder.buildUriFromCurrentRequest(history.getId());
        return apiMessageFactory.response().created(location).build();
    }

    @PreAuthorize("@userHistorySecurity.hasAccessTo(#id, authentication)")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserHistoryDto>> getUserHistory(@PathVariable Integer id) {
        UserHistory entity = mathUserHistoryService.findByIdReadOnly(id);
        User owner = squipUserDetailService.getCurrentUser();

        UserHistoryDto dto =  userHistoryMapper.toResponseDtoFromEntity(entity);
        return apiMessageFactory.response(dto).ok().build();
    }
}
