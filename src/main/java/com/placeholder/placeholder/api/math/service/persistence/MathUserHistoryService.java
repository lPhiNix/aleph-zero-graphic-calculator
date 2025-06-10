package com.placeholder.placeholder.api.math.service.persistence;

import com.placeholder.placeholder.api.auth.service.SquipUserDetailService;
import com.placeholder.placeholder.api.math.dto.request.UserHistoryCreationDto;
import com.placeholder.placeholder.api.util.common.mapper.MappingContext;
import com.placeholder.placeholder.api.util.common.service.AbstractCrudService;
import com.placeholder.placeholder.db.mappers.MathExpressionMapper;
import com.placeholder.placeholder.db.mappers.UserHistoryMapper;
import com.placeholder.placeholder.db.models.User;
import com.placeholder.placeholder.db.models.UserHistory;
import com.placeholder.placeholder.db.repositories.UserHistoryRepository;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class MathUserHistoryService extends AbstractCrudService<UserHistory, Integer, UserHistoryRepository> {
    private static final Logger logger = org.slf4j.LoggerFactory.getLogger(MathUserHistoryService.class);

    private final UserHistoryMapper userHistoryMapper;
    private final MathExpressionMapper mathExpressionMapper;
    private final SquipUserDetailService squipUserDetailService;

    public MathUserHistoryService(UserHistoryRepository repository, UserHistoryMapper userHistoryMapper, MathExpressionMapper mathExpressionMapper, SquipUserDetailService squipUserDetailService) {
        super(repository);
        this.userHistoryMapper = userHistoryMapper;
        this.mathExpressionMapper = mathExpressionMapper;
        this.squipUserDetailService = squipUserDetailService;
    }

    @Transactional
    public UserHistory createUserHistory(UserHistoryCreationDto request) {
        User owner = squipUserDetailService.getCurrentUser();
        String snapshotUUid = UUID.randomUUID().toString();
        MappingContext context = MappingContext.builder().withAny(owner, snapshotUUid, mathExpressionMapper).build();

        logger.info("Creating user history for user: {}, image snapshot UUID: {}", owner.getUsername(), snapshotUUid);

        UserHistory history = userHistoryMapper.toEntityFromCreationDto(request, context);
        UserHistory persisted = repository.save(history);
        SnapshotUtils.saveSnapshotToFile(request.snapshot(), snapshotUUid);

        return persisted;
    }

    @Transactional(readOnly = true)
    public List<UserHistory> findAllByCurrentUserReadOnly() {
        User currentUser = squipUserDetailService.getCurrentUser();
        List<UserHistory> elements = repository.findAllByUser(currentUser);
        logger.info("Finding all user history for user: {}, found {} elements", currentUser.getUsername(), elements.size());
        return elements;
    }
}
