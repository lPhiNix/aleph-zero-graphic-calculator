package com.placeholder.placeholder.api.math;

import com.placeholder.placeholder.api.math.dto.request.MathEvaluationRequest;
import com.placeholder.placeholder.api.math.dto.request.MathExpressionCreationDto;
import com.placeholder.placeholder.api.math.dto.response.MathEvaluationResultResponse;
import com.placeholder.placeholder.api.math.service.persistence.MathExpressionPersistenceService;
import com.placeholder.placeholder.api.math.service.core.MathExpressionService;
import com.placeholder.placeholder.api.math.service.persistence.SnapshotUtils;
import com.placeholder.placeholder.api.util.common.messages.ApiMessageFactory;
import com.placeholder.placeholder.api.util.common.messages.UriHelperBuilder;
import com.placeholder.placeholder.api.util.common.messages.dto.ApiResponse;
import com.placeholder.placeholder.db.basicdto.MathExpressionResponseDto;
import com.placeholder.placeholder.db.mappers.MathExpressionMapper;
import com.placeholder.placeholder.db.models.MathExpression;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

/**
 * REST controller that exposes endpoints related to mathematical expression processing.
 * <p>
 * Handles operations such as evaluation of mathematical expressions using a service layer.
 *
 * </p>
 */
@RestController
@RequestMapping("api/v1/math")
@RequiredArgsConstructor
public class MathExpressionController {
    private final MathExpressionService service;
    private final MathExpressionPersistenceService persistenceService;
    private final ApiMessageFactory messageFactory;
    private final MathExpressionMapper mathExpressionMapper;
    private final ApiMessageFactory apiMessageFactory;

    /**
     * Evaluates one or more mathematical expressions with optional formatting settings.
     *
     * @param mathExpressionRequest A request object containing a list of expressions to evaluate and optional evaluation data
     * @return A {@link ResponseEntity} containing an {@link ApiResponse} with the evaluation result
     */
    @PostMapping("/evaluation")
    public ResponseEntity<ApiResponse<MathEvaluationResultResponse>> evaluation(
            @RequestBody @Valid MathEvaluationRequest mathExpressionRequest
    ) {
        MathEvaluationResultResponse response = service.evaluation(mathExpressionRequest);
        return messageFactory.response(response).ok().build();
    }

    /**
     * Retrieves a mathematical expression by its ID.
     * @param id the ID of the mathematical expression to retrieve
     * @param includePoints whether to include points in the response
     * @return a {@link ResponseEntity} containing an {@link ApiResponse} with the mathematical expression details
     */
    @GetMapping("expression/{id}")
    public ResponseEntity<ApiResponse<MathExpressionResponseDto>> getById(
            @PathVariable Integer id,
            @RequestParam(required = false, defaultValue = "false") boolean includePoints) {

        MathExpression expression = persistenceService.findByIdReadOnly(id);
        MathExpressionResponseDto dto = mathExpressionMapper.toResponseDtoFromEntity(expression, includePoints);

        return apiMessageFactory.response(dto).ok().build();
    }

    /**
     * Persists a new mathematical expression.
     *
     * @param request the request containing the details of the expression to be created
     * @return a {@link ResponseEntity} with an {@link ApiResponse} indicating the result of the operation
     */
    @PostMapping("/expression")
    public  ResponseEntity<ApiResponse<Void>> persistNewExpression(
            @RequestBody @Valid MathExpressionCreationDto request
    ) {
        MathExpression expression = persistenceService.createNewExpression(request);
        URI location = UriHelperBuilder.buildUriFromCurrentRequest(expression.getId());
        return apiMessageFactory.response().created(location).build();
    }
}