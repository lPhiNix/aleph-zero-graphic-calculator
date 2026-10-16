package com.alephzero.alephzero.core.services.math.persistence;

import com.alephzero.alephzero.api.v1.math.dto.request.history.MathExpressionCreationDto;
import com.alephzero.alephzero.core.services.common.AbstractCrudService;
import com.alephzero.alephzero.api.v1.math.mapper.MathExpressionMapper;
import com.alephzero.alephzero.core.models.MathExpression;
import com.alephzero.alephzero.core.repositories.MathExpressionRepository;

import org.slf4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
@Service
public class MathExpressionPersistenceService extends AbstractCrudService<MathExpression, Integer, MathExpressionRepository> {
    private static final Logger logger = org.slf4j.LoggerFactory.getLogger(MathExpressionPersistenceService.class);

    private final MathExpressionMapper mathExpressionMapper;

    public MathExpressionPersistenceService(MathExpressionRepository repository, MathExpressionMapper mathExpressionMapper) {
        super(repository);
        this.mathExpressionMapper = mathExpressionMapper;
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
