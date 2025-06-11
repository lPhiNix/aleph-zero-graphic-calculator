package com.alephzero.alephzero.api.math.service.persistence;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;

@RequiredArgsConstructor
public class SnapshotUtils {
    private static final Logger logger = LoggerFactory.getLogger(SnapshotUtils.class);
    private static final Path SNAPSHOT_DIR = Paths.get("snapshots");
    private static final String PLACEHOLDER_FILENAME = "placeholder.jpg";

    private static byte[] getSnapshotBytes(String base64Snapshot) {
        String[] parts = base64Snapshot.split(",", 2);
        String base64Data = parts.length > 1 ? parts[1] : parts[0];
        return Base64.getDecoder().decode(base64Data);
    }

    public static void saveSnapshotToFile(String base64Snapshot, String hash) {
        // Asegurarse de que el directorio existe
        try {
            if (!Files.exists(SNAPSHOT_DIR)) {
                Files.createDirectories(SNAPSHOT_DIR);
                logger.info("Created snapshots directory at {}", SNAPSHOT_DIR.toAbsolutePath());
            }
        } catch (IOException e) {
            logger.error("Failed to create snapshots directory", e);
            return;
        }

        Path imagePath = SNAPSHOT_DIR.resolve(hash + ".jpg"); // Si quieres flexibilidad, que te pasen extensión
        try {
            byte[] imageBytes = getSnapshotBytes(base64Snapshot);
            Files.write(imagePath, imageBytes);
            logger.info("Saved image to {}", imagePath.toAbsolutePath());
        } catch (IOException e) {
            logger.error("Failed to save snapshot to file {}", imagePath.toAbsolutePath(), e);
        }
    }

    public static String getSnapshotUrl(String hash) {
        String filename = hash + ".jpg";
        Path snapshotPath = Paths.get(SNAPSHOT_DIR.toString(), filename);

        String finalFilename = Files.exists(snapshotPath) ? filename : PLACEHOLDER_FILENAME;

        return ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/snapshots/")
                .path(finalFilename)
                .toUriString();
    }

}
