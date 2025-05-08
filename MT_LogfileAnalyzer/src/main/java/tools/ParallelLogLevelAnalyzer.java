package tools;

import java.io.File;
import java.util.*;
import java.util.concurrent.*;

public class ParallelLogLevelAnalyzer {

    public static void main(String[] args) {
        File logDir = new File(".");
        if (!logDir.exists() || !logDir.isDirectory()) {
            System.out.println("Logverzeichnis nicht gefunden.");
            return;
        }

        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        List<Future<Map<LogAnalyzerTask.LogLevel, Integer>>> futures = new ArrayList<>();
        List<File> files = new ArrayList<>();

        long startTime = System.currentTimeMillis();

        for (File file : logDir.listFiles()) {
            if (!file.getName().endsWith(".log")) continue;
            LogAnalyzerTask task = new LogAnalyzerTask(file);
            futures.add(executor.submit(task));
            files.add(file);
        }

        Map<LogAnalyzerTask.LogLevel, Integer> totalCounts = new EnumMap<>(LogAnalyzerTask.LogLevel.class);
        for (LogAnalyzerTask.LogLevel level : LogAnalyzerTask.LogLevel.values()) {
            totalCounts.put(level, 0);
        }

        for (int i = 0; i < futures.size(); i++) {
            try {
                Map<LogAnalyzerTask.LogLevel, Integer> result = futures.get(i).get();
                System.out.println("LogLevel-Zählung für Datei: " + files.get(i).getName());
                for (var entry : result.entrySet()) {
                    System.out.printf("  %s: %d%n", entry.getKey(), entry.getValue());
                    totalCounts.put(entry.getKey(), totalCounts.get(entry.getKey()) + entry.getValue());
                }
            } catch (Exception e) {
                System.out.println("Fehler bei Datei " + files.get(i).getName() + ": " + e.getMessage());
            }
        }

        long endTime = System.currentTimeMillis();

        executor.shutdown();

        System.out.println("\nGesamte LogLevel-Zusammenfassung:");
        totalCounts.forEach((level, count) -> System.out.printf("  %s: %d%n", level, count));
        System.out.println("Parallele Analyse dauerte: " + (endTime - startTime) + " ms");
    }
}
