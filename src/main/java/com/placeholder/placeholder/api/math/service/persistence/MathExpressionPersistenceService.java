package com.placeholder.placeholder.api.math.service.persistence;

import com.placeholder.placeholder.api.math.dto.request.history.MathExpressionCreationDto;
import com.placeholder.placeholder.api.util.common.service.AbstractCrudService;
import com.placeholder.placeholder.db.mappers.MathExpressionMapper;
import com.placeholder.placeholder.db.mappers.UserHistoryMapper;
import com.placeholder.placeholder.db.models.MathExpression;
import com.placeholder.placeholder.db.repositories.MathExpressionRepository;

import org.slf4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
@Service
public class MathExpressionPersistenceService extends AbstractCrudService<MathExpression, Integer, MathExpressionRepository> {
    private static final Logger logger = org.slf4j.LoggerFactory.getLogger(MathExpressionPersistenceService.class);

    private final MathExpressionMapper mathExpressionMapper;
    private final UserHistoryMapper userHistoryMapper;

    public MathExpressionPersistenceService(MathExpressionRepository repository, MathExpressionMapper mathExpressionMapper, UserHistoryMapper userHistoryMapper) {
        super(repository);
        this.mathExpressionMapper = mathExpressionMapper;
        this.userHistoryMapper = userHistoryMapper;
    }

    /**
     * Creates a new MathExpression entity from the provided request DTO.
     * It saves the snapshot to a file and returns the created MathExpression.
     *
     * @param request The DTO containing the data for creating a new MathExpression.
     * @return The created MathExpression entity.
     */
    @Transactional
    public MathExpression createNewExpression(MathExpressionCreationDto request) {
        MathExpression mathExpression = mathExpressionMapper.toEntityFromCreationDto(request);
        return save(mathExpression);
    }

}
