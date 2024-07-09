package ru.clevertec.check;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CSVWriter {

    public static void writeToCSV(String csvFilePath, List<String[]> data) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(csvFilePath))) {
            // Запись заголовка
            bw.write(String.join(";", data.get(0)));
            bw.newLine();

            // Запись данных
            for (int i = 1; i < data.size(); i++) {
                bw.write(String.join(";", data.get(i)));
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void writeError(String message) {
        System.out.println(message);
        List<String[]> error = new ArrayList<>();
        error.add(new String[]{"ERROR"});
        error.add(new String[]{message});
        writeToCSV("src/main/resources/out.csv", error);
    }

}


