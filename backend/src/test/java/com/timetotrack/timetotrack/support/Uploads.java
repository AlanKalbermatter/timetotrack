package com.timetotrack.timetotrack.support;

import io.vertx.ext.web.handler.BodyHandler;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.stream.Stream;

/** Multipart bodies and BodyHandler's default upload directory, for tests proving uploads never hit disk. */
public final class Uploads {

    public static final Path DIRECTORY = Path.of(BodyHandler.DEFAULT_UPLOADS_DIRECTORY);
    public static final String CONTENT_TYPE = "multipart/form-data; boundary=X";
    public static final String BODY = "--X\r\n"
            + "Content-Disposition: form-data; name=\"f\"; filename=\"blob.bin\"\r\n"
            + "Content-Type: application/octet-stream\r\n\r\n"
            + "a".repeat(1000) + "\r\n--X--\r\n";

    private Uploads() {
    }

    public static void deleteDirectory() {
        if (!Files.exists(DIRECTORY)) {
            return;
        }
        try (Stream<Path> paths = Files.walk(DIRECTORY)) {
            paths.sorted(Comparator.reverseOrder()).forEach(path -> path.toFile().delete());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
