package com.placeholder.placeholder.api.math.service;

import com.placeholder.placeholder.api.math.dto.request.MathExpressionCreationDto;
import com.placeholder.placeholder.api.user.service.UserService;
import com.placeholder.placeholder.api.util.common.service.AbstractCrudService;
import com.placeholder.placeholder.db.mappers.MathExpressionMapper;
import com.placeholder.placeholder.db.models.MathExpression;
import com.placeholder.placeholder.db.models.User;
import com.placeholder.placeholder.db.repositories.MathExpressionRepository;

import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.UUID;

@Service
public class MathExpressionPersistenceService extends AbstractCrudService<MathExpression, Integer, MathExpressionRepository> {
    private final MathExpressionMapper mathExpressionMapper;
    private final UserService userService;

    public MathExpressionPersistenceService(MathExpressionRepository repository, MathExpressionMapper mathExpressionMapper, UserService userService) {
        super(repository);
        this.mathExpressionMapper = mathExpressionMapper;
        this.userService = userService;
    }

    public MathExpression createNewExpression(MathExpressionCreationDto request) {
        User owner = userService.findUserByIdentifier(request.userIdentifier()); // If user not found, nuclear exception.

        String base64Snapshot = request.snapshot();
        String base64Data = base64Snapshot.split(",").length > 1
                ? base64Snapshot.split(",")[1]
                : base64Snapshot;

        byte[] imageBytes = Base64.getDecoder().decode(base64Data);
        UUID hash = UUID.randomUUID();

        Path imagePath = Paths.get("snapshots", hash + ".png"); // o .svg, .jpg, etc.
        Files.write(imagePath, imageBytes);

// En la entidad solo guardas el hash
        mathExpression.setSnapshot(hash);


    }
}
