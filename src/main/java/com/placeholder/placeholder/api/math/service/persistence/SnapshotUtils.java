package com.placeholder.placeholder.api.math.service.persistence;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;

@Component
@RequiredArgsConstructor
public class SnapshotUtils {
    private static final Logger logger = LoggerFactory.getLogger(SnapshotUtils.class);
    private static final Path SNAPSHOT_DIR = Paths.get("snapshots");

    private byte[] getSnapshotBytes(String base64Snapshot) {
        String[] parts = base64Snapshot.split(",", 2);
        String base64Data = parts.length > 1 ? parts[1] : parts[0];
        return Base64.getDecoder().decode(base64Data);
    }

    public Path saveSnapshotToFile(String base64Snapshot, String hash) {
        // Asegurarse de que el directorio existe
        try {
            if (!Files.exists(SNAPSHOT_DIR)) {
                Files.createDirectories(SNAPSHOT_DIR);
                logger.info("Created snapshots directory at {}", SNAPSHOT_DIR.toAbsolutePath());
            }
        } catch (IOException e) {
            logger.error("Failed to create snapshots directory", e);
            return null;
        }

        Path imagePath = SNAPSHOT_DIR.resolve(hash + ".jpg"); // Si quieres flexibilidad, que te pasen extensión
        try {
            byte[] imageBytes = getSnapshotBytes(base64Snapshot);
            Files.write(imagePath, imageBytes);
            logger.info("Saved image to {}", imagePath.toAbsolutePath());
            return imagePath;
        } catch (IOException e) {
            logger.error("Failed to save snapshot to file {}", imagePath.toAbsolutePath(), e);
            return null;
        }
    }

    public String getSnapshotUrl(String hash) {
        return ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/snapshots/")
                .path(hash + ".jpg")
                .toUriString();
    }

}
