package com.chaottic.template;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.*;
import java.util.Objects;

import static org.lwjgl.system.MemoryUtil.memCalloc;
import static org.lwjgl.system.MemoryUtil.memRealloc;

public final class Resources {

    private Resources() {}

    public static FileSystem getFileSystem() throws IOException {
        var location = Resources.class.getProtectionDomain().getCodeSource().getLocation().toString();

        return FileSystems.newFileSystem(Paths.get(location.substring(location.lastIndexOf(':') + 1)), Resources.class.getClassLoader());
    }

    public static URL getResource(String path) {
        return Objects.requireNonNull(Resources.class.getClassLoader().getResource(path));
    }

    public static String readFile(String path) throws IOException, URISyntaxException {
        var uri = getResource(path).toURI();

        if (isWithinJar(uri)) {
            try (FileSystem fileSystem = getFileSystem()) {
                return readFile(fileSystem.getPath(path));
            }
        } else {
            return readFile(Paths.get(uri));
        }
    }

    public static ByteBuffer readImage(String path) throws IOException, URISyntaxException {
        var uri = getResource(path).toURI();

        if (isWithinJar(uri)) {
            try (FileSystem fileSystem = getFileSystem()) {
                return readImage(fileSystem.getPath(path));
            }
        } else {
            return readImage(Paths.get(uri));
        }
    }

    public static String readFile(Path path) throws IOException {
        try (BufferedReader reader = Files.newBufferedReader(path)) {
            var builder = new StringBuilder();

            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line).append("\n");
            }

            return builder.toString();
        }
    }

    public static ByteBuffer readImage(Path path) throws IOException {
        try (ReadableByteChannel channel = Files.newByteChannel(path)) {
            var buffer = memCalloc(8192);

            while (channel.read(buffer) != -1) {
                if (buffer.remaining() == 0) {
                    buffer = memRealloc(buffer, buffer.capacity() * 2);
                }
            }

            return buffer.flip();
        }
    }

    public static boolean isWithinJar(URI uri) {
        return uri.getScheme().contains("jar");
    }
}
