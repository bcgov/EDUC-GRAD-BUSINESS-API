package ca.bc.gov.educ.api.gradbusiness.service;

import ca.bc.gov.educ.api.gradbusiness.util.IOUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.*;

class PdfCleanupServiceTest {
    private static final Instant NOW = Instant.parse("2026-11-04T12:00:00Z");
    private static final Clock CLOCK = Clock.fixed(NOW, ZoneOffset.UTC);

    @TempDir
    Path directory;

    @Test
    void regeneratingReportNeverReplacesExpiredCleanupCandidate() throws IOException {
        String reportName = "09292007_202600_TVRGRAD.pdf";
        Path expired = file(reportName, NOW.minusSeconds(172800));
        byte[] firstContent = "first generation".getBytes(java.nio.charset.StandardCharsets.UTF_8);
        byte[] secondContent = "second generation".getBytes(java.nio.charset.StandardCharsets.UTF_8);

        Path first = IOUtils.savePdfReport(directory, firstContent);
        Path second = IOUtils.savePdfReport(directory, secondContent);
        Files.setLastModifiedTime(first, FileTime.from(NOW));
        Files.setLastModifiedTime(second, FileTime.from(NOW));

        assertNotEquals(first, second);
        assertNotEquals(expired, first);
        assertNotEquals(expired, second);
        assertEquals(directory, first.getParent());
        assertTrue(first.getFileName().toString().matches("report_[0-9a-f-]{36}\\.pdf"));
        assertEquals("report", Files.readString(expired));
        new PdfCleanupService(directory, 1, CLOCK).cleanUp();

        assertFalse(Files.exists(expired));
        assertArrayEquals(firstContent, Files.readAllBytes(first));
        assertArrayEquals(secondContent, Files.readAllBytes(second));
    }

    @Test
    void deletesOnlyPdfsStrictlyOlderThanRetention() throws IOException {
        Path old = file("old.pdf", NOW.minusSeconds(86401));
        Path upperCase = file("uppercase.PDF", NOW.minusSeconds(86401));
        Path boundary = file("boundary.pdf", NOW.minusSeconds(86400));
        Path recent = file("recent.pdf", NOW.minusSeconds(3600));
        Path other = file("old.txt", NOW.minusSeconds(172800));

        new PdfCleanupService(directory, 1, CLOCK).cleanUp();

        assertFalse(Files.exists(old));
        assertFalse(Files.exists(upperCase));
        assertTrue(Files.exists(boundary));
        assertTrue(Files.exists(recent));
        assertTrue(Files.exists(other));
    }

    @Test
    void preservesDirectoriesNestedFilesAndSymbolicLinks() throws IOException {
        Path nested = Files.createDirectory(directory.resolve("nested.pdf"));
        Path nestedPdf = Files.writeString(nested.resolve("old.pdf"), "report");
        Files.setLastModifiedTime(nestedPdf, FileTime.from(NOW.minusSeconds(172800)));
        Files.setLastModifiedTime(nested, FileTime.from(NOW.minusSeconds(172800)));
        Path target = file("target.txt", NOW.minusSeconds(172800));
        Path link = Files.createSymbolicLink(directory.resolve("link.pdf"), target);

        new PdfCleanupService(directory, 1, CLOCK).cleanUp();

        assertTrue(Files.isDirectory(nested));
        assertTrue(Files.exists(nestedPdf));
        assertTrue(Files.isSymbolicLink(link));
        assertTrue(Files.exists(target));
    }

    @Test
    void supportsConfiguredRetentionAndRepeatedCleanup() throws IOException {
        Path report = file("report.pdf", NOW.minusSeconds(172800));
        new PdfCleanupService(directory, 3, CLOCK).cleanUp();
        assertTrue(Files.exists(report));

        PdfCleanupService service = new PdfCleanupService(directory, 1, CLOCK);
        service.cleanUp();
        assertDoesNotThrow(service::cleanUp);
        assertFalse(Files.exists(report));
    }

    @Test
    void handlesMissingDirectoryWithoutThrowing() {
        PdfCleanupService service = new PdfCleanupService(directory.resolve("missing"), 1, CLOCK);
        assertDoesNotThrow(service::cleanUp);
    }

    @Test
    void rejectsRetentionShorterThanOneDay() {
        assertThrows(IllegalArgumentException.class, () -> new PdfCleanupService(directory, 0, CLOCK));
        assertThrows(IllegalArgumentException.class, () -> new PdfCleanupService(directory, -1, CLOCK));
    }

    private Path file(String name, Instant modified) throws IOException {
        Path file = Files.writeString(directory.resolve(name), "report");
        Files.setLastModifiedTime(file, FileTime.from(modified));
        return file;
    }
}
