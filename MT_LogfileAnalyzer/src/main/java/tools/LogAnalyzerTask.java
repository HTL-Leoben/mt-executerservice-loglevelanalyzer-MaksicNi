package tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.EnumMap;
import java.util.Map;
import java.util.concurrent.Callable;

public class LogAnalyzerTask implements Callable<Map<LogAnalyzerTask.LogLevel, Integer>> {

    public enum LogLevel {
        TRACE, DEBUG, INFO, WARN, ERROR
    }

    private final File file;

    public LogAnalyzerTask(File file) {
        this.file = file;
    }

    @Override
    public Map<LogLevel, Integer> call() throws Exception {
        Map<LogLevel, Integer> counts = new EnumMap<>(LogLevel.class);
        for (LogLevel level : LogLevel.values()) {
            counts.put(level, 0);
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                for (LogLevel level : LogLevel.values()) {
                    if (line.matches(".*\\b" + level.name() + "\\b\\s{2,}.*")) {
                        counts.put(level, counts.get(level) + 1);
                        break;
                    }
                }
            }
        }

        return counts;
    }

    public File getFile() {
        return file;
    }
}
