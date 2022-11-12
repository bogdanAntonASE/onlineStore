package service;

import exceptions.ExportException;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public final class ExportService {

    public static void exportPayloadsForPeriod(LocalDate from, LocalDate to) throws ExportException {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        String exportFileName = "purchases" + from.format(dateTimeFormatter) + "to" + to.format(dateTimeFormatter) + ".txt";

        try (BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(
                        new FileInputStream(DatabaseService.PURCHASES_DB)));
             FileWriter fileWriter = new FileWriter(exportFileName)) {
            String currentLine;

            while ((currentLine = bufferedReader.readLine()) != null) {
                String[] splitData = currentLine.split("; ");
                LocalDate parse = LocalDate.parse(splitData[2].split("T")[0]);
                if ((parse.isEqual(from) || parse.isAfter(from)) && (parse.isBefore(to) || parse.isEqual(to))) {
                    fileWriter.append(currentLine).append("\n");
                }
            }
        } catch (IOException exception) {
            throw new ExportException(exception.getMessage());
        }
    }

    private ExportService() {}
}
