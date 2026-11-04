package ca.bc.gov.educ.api.gradbusiness.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Locale;

@Service
@ConditionalOnProperty(name = "pdf-cleanup.enabled", havingValue = "true", matchIfMissing = true)
public class PdfCleanupService {
    private static final Logger logger = LoggerFactory.getLogger(PdfCleanupService.class);

    private final Path directory;
    private final Duration retention;
    private final Clock clock;

    @Autowired
    public PdfCleanupService(@Value("${pdf-cleanup.directory}") String directory,
                             @Value("${pdf-cleanup.retention-days}") long retentionDays) {
        this(Path.of(directory), retentionDays, Clock.systemUTC());
    }

    PdfCleanupService(Path directory, long retentionDays, Clock clock) {
        if (retentionDays < 1) {
            throw new IllegalArgumentException("PDF retention must be at least one day");
        }
        this.directory = directory;
        this.retention = Duration.ofDays(retentionDays);
        this.clock = clock;
    }

    @Scheduled(cron = "${pdf-cleanup.cron}", zone = "${pdf-cleanup.zone}")
    public void cleanUp() {
        Instant cutoff = clock.instant().minus(retention);
        long deleted = 0;
        long bytes = 0;
        try (DirectoryStream<Path> files = Files.newDirectoryStream(directory,
                path -> path.getFileName().toString().toLowerCase(Locale.ROOT).endsWith(".pdf"))) {
            for (Path file : files) {
                long removedBytes = deleteExpiredPdf(file, cutoff);
                if (removedBytes >= 0) {
                    deleted++;
                    bytes += removedBytes;
                }
            }
            logger.info("PDF cleanup completed: directory={}, cutoff={}, deleted={}, bytes={}",
                    directory, cutoff, deleted, bytes);
        } catch (IOException e) {
            logger.error("Unable to scan PDF cleanup directory {}", directory, e);
        }
    }

    private long deleteExpiredPdf(Path file, Instant cutoff) {
        try {
            BasicFileAttributes attributes = Files.readAttributes(file, BasicFileAttributes.class,
                    LinkOption.NOFOLLOW_LINKS);
            if (attributes.isRegularFile() && attributes.lastModifiedTime().toInstant().isBefore(cutoff)
                    && Files.deleteIfExists(file)) {
                return attributes.size();
            }
        } catch (NoSuchFileException e) {
            // Another replica may have already removed this file from the shared volume.
            logger.debug("PDF already removed: {}", file);
        } catch (IOException e) {
            logger.warn("Unable to delete expired PDF {}", file, e);
        }
        return -1;
    }
}
