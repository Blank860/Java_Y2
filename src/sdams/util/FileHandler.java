package sdams.util;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public final class FileHandler {
    public static final Path DATA_DIR = Paths.get("data");

    private FileHandler() { }

    public static List<String> readLines(String fileName) throws IOException {
        Path p = DATA_DIR.resolve(fileName);
        List<String> result = new ArrayList<>();
        if (!Files.exists(p)) return result;
        for (String line : Files.readAllLines(p, StandardCharsets.UTF_8)) {
            if (!line.trim().isEmpty()) result.add(line);
        }
        return result;
    }

    public static void writeLines(String fileName, List<String> lines) throws IOException {
        Files.createDirectories(DATA_DIR);
        Files.write(DATA_DIR.resolve(fileName), lines, StandardCharsets.UTF_8);
    }
}