package com.placeholder.placeholder.api.math.service.persistence;

import com.placeholder.placeholder.api.auth.service.SquipUserDetailService;
import com.placeholder.placeholder.api.math.dto.request.MathExpressionCreationDto;
import com.placeholder.placeholder.api.user.service.UserService;
import com.placeholder.placeholder.api.util.common.service.AbstractCrudService;
import com.placeholder.placeholder.db.mappers.MathExpressionMapper;
import com.placeholder.placeholder.db.models.MathExpression;
import com.placeholder.placeholder.db.models.User;
import com.placeholder.placeholder.db.repositories.MathExpressionRepository;

import org.slf4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class MathExpressionPersistenceService extends AbstractCrudService<MathExpression, Integer, MathExpressionRepository> {
    private static final Logger logger = org.slf4j.LoggerFactory.getLogger(MathExpressionPersistenceService.class);

    private final MathExpressionMapper mathExpressionMapper;
    private final UserService userService;
    private final SquipUserDetailService squipUserDetailService;

    public MathExpressionPersistenceService(MathExpressionRepository repository, MathExpressionMapper mathExpressionMapper, UserService userService, SquipUserDetailService squipUserDetailService) {
        super(repository);
        this.mathExpressionMapper = mathExpressionMapper;
        this.userService = userService;
        this.squipUserDetailService = squipUserDetailService;
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
        User owner = squipUserDetailService.getCurrentUser();

        String imageHash = UUID.randomUUID().toString();
        logger.info("User {} is creating a new math expression with image hash: {}", owner.getUsername(), imageHash);

        MathExpression mathExpression = mathExpressionMapper.toEntityFromCreationDto(request, owner, imageHash);

        // save the snapshot to a file with the specified hash
        SnapshotUtils.saveSnapshotToFile(request.snapshot(), imageHash);
        return save(mathExpression);
    }
}
