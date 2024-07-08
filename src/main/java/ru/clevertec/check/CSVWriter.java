package ru.clevertec.check;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
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

}


