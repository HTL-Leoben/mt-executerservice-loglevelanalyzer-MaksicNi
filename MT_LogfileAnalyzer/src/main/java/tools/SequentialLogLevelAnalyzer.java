package tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.EnumMap;
import java.util.Map;

public class SequentialLogLevelAnalyzer {

    enum LogLevel {
        TRACE, DEBUG, INFO, WARN, ERROR
    }

    public static void main(String[] args) {
        File logDir = new File(".");
        if (!logDir.exists() || !logDir.isDirectory()) {
            System.out.println("Logverzeichnis nicht gefunden.");
            return;
        }

        Map<LogLevel, Integer> totalCounts = new EnumMap<>(LogLevel.class);
        for (LogLevel level : LogLevel.values()) {
            totalCounts.put(level, 0);
        }

        long startTime = System.currentTimeMillis();

        for (File file : logDir.listFiles()) {
            if (!file.getName().endsWith(".log")) continue;

            Map<LogLevel, Integer> fileCounts = new EnumMap<>(LogLevel.class);
            for (LogLevel level : LogLevel.values()) {
                fileCounts.put(level, 0);
            }

            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    for (LogLevel level : LogLevel.values()) {
                        if (line.contains("[" + level.name() + "]")) {
                            fileCounts.put(level, fileCounts.get(level) + 1);
                            totalCounts.put(level, totalCounts.get(level) + 1);
                            break;
                        }
                    }
                }
            } catch (Exception e) {
                System.out.println("Fehler beim Lesen von Datei " + file.getName() + ": " + e.getMessage());
            }

            System.out.println("LogLevel-Zählung für Datei: " + file.getName());
            fileCounts.forEach((level, count) -> System.out.printf("  %s: %d%n", level, count));
        }

        long endTime = System.currentTimeMillis();

        System.out.println("\nLogLevel-Zusammenfassung:");
        totalCounts.forEach((level, count) -> System.out.printf("  %s: %d%n", level, count));
        System.out.println("Sequentielle Analyse dauerte: " + (endTime - startTime) + " ms");
    }
}
