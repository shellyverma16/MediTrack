package com.airtribe.meditrack.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Minimal CSV reader/writer. Deliberately uses String.split(",") as required
 * by the assignment; fields must not themselves contain commas (multi-value
 * fields such as medical history use ';' as an inner separator instead).
 */
public final class CSVUtil {

    private CSVUtil() {
    }

    public static void writeLines(String filePath, List<String> lines) throws IOException {
        File file = new File(filePath);
        File parent = file.getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (String line : lines) {
                writer.write(line);
                writer.newLine();
            }
        }
    }

    public static List<String> readLines(String filePath) throws IOException {
        List<String> lines = new ArrayList<>();
        File file = new File(filePath);
        if (!file.exists()) {
            return lines;
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    lines.add(line);
                }
            }
        }
        return lines;
    }

    public static String[] parseLine(String line) {
        return line.split(",", -1);
    }
}
