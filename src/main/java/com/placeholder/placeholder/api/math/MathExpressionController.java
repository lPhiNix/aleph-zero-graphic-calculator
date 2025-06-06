package com.placeholder.placeholder.api.math;

import com.placeholder.placeholder.api.math.dto.request.MathEvaluationRequest;
import com.placeholder.placeholder.api.math.dto.response.MathEvaluationResultResponse;
import com.placeholder.placeholder.api.math.service.core.MathExpressionService;
import com.placeholder.placeholder.api.util.common.messages.ApiResponseFactory;
import com.placeholder.placeholder.api.util.common.messages.dto.ApiResponse;
import jakarta.validation.Valid;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller that exposes endpoints related to mathematical expression processing.
 * <p>
 * Handles operations such as evaluation of mathematical expressions using a service layer.
 * </p>
 */
@RestController
@RequestMapping("api/v1/math")
public class MathExpressionController {

    private static final Logger logger = LogManager.getLogger(MathExpressionController.class);

    private final MathExpressionService service;
    private final ApiResponseFactory apiResponseFactory;

    /**
     * Constructor for {@link MathExpressionController}.
     *
     * @param service            The service responsible for evaluating mathematical expressions
     * @param apiResponseFactory A factory for creating standardized API responses
     */
    public MathExpressionController(MathExpressionService service, ApiResponseFactory apiResponseFactory) {
        this.service = service;
        this.apiResponseFactory = apiResponseFactory;
        logger.info("MathExpressionController initialized with MathExpressionService and ApiResponseFactory");
    }

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
        logger.info("Entering evaluation endpoint");
        logger.debug("Request payload: {}", mathExpressionRequest);

        MathEvaluationResultResponse response;
        try {
            response = service.evaluation(mathExpressionRequest);
            logger.debug("Service returned evaluation result: {}", response);
        } catch (Exception ex) {
            logger.error("Exception occurred while evaluating expressions: {}", ex.getMessage(), ex);
            // In case of exception, you might want to return a custom error response, but here we propagate.
            throw ex;
        }

        logger.info("Successfully evaluated expressions, returning response");
        return apiResponseFactory.ok(response);
    }
}
