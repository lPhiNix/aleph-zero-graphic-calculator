package com.alephzero.alephzero.api.math.service.persistence;

import com.alephzero.alephzero.api.auth.service.SquipUserDetailService;
import com.alephzero.alephzero.api.math.dto.request.history.UserHistoryCreationDto;
import com.alephzero.alephzero.api.util.common.mapper.MappingContext;
import com.alephzero.alephzero.api.util.common.service.AbstractCrudService;
import com.alephzero.alephzero.db.mappers.MathExpressionMapper;
import com.alephzero.alephzero.db.mappers.UserHistoryMapper;
import com.alephzero.alephzero.db.models.User;
import com.alephzero.alephzero.db.models.UserHistory;
import com.alephzero.alephzero.db.repositories.UserHistoryRepository;
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
