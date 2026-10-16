package com.alephzero.alephzero.core.services.math.persistence;

import com.alephzero.alephzero.core.services.auth.AlephzeroUserDetailService;
import com.alephzero.alephzero.api.v1.math.dto.request.history.UserHistoryCreationDto;
import com.alephzero.alephzero.core.services.common.AbstractCrudService;
import com.alephzero.alephzero.api.v1.math.mapper.UserHistoryMapper;
import com.alephzero.alephzero.core.models.User;
import com.alephzero.alephzero.core.models.UserHistory;
import com.alephzero.alephzero.core.repositories.UserHistoryRepository;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class MathUserHistoryService extends AbstractCrudService<UserHistory, Integer, UserHistoryRepository> {
    private static final Logger logger = org.slf4j.LoggerFactory.getLogger(MathUserHistoryService.class);

    private final UserHistoryMapper userHistoryMapper;
    private final AlephzeroUserDetailService alephzeroUserDetailService;

    public MathUserHistoryService(UserHistoryRepository repository, UserHistoryMapper userHistoryMapper, AlephzeroUserDetailService alephzeroUserDetailService) {
        super(repository);
        this.userHistoryMapper = userHistoryMapper;
        this.alephzeroUserDetailService = alephzeroUserDetailService;
    }

    @Transactional
    public UserHistory createUserHistory(UserHistoryCreationDto request) {
        User owner = alephzeroUserDetailService.getCurrentUser();
        String snapshotUUid = UUID.randomUUID().toString();

        logger.info("Creating user history for user: {}, image snapshot UUID: {}", owner.getUsername(), snapshotUUid);

        UserHistory history = userHistoryMapper.toEntityFromCreationDto(request, owner, snapshotUUid);
        UserHistory persisted = repository.save(history);
        SnapshotUtils.saveSnapshotToFile(request.snapshot(), snapshotUUid);

        return persisted;
    }

    @Transactional(readOnly = true)
    public List<UserHistory> findAllByCurrentUserReadOnly() {
        User currentUser = alephzeroUserDetailService.getCurrentUser();
        List<UserHistory> elements = repository.findAllByUser(currentUser);
        logger.info("Finding all user history for user: {}, found {} elements", currentUser.getUsername(), elements.size());
        return elements;
    }
}
