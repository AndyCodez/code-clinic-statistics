package com.andycodez.statistics;

import com.andycodez.statistics.data.WeatherEntry;
import com.andycodez.statistics.exceptions.WeatherEntryNotFoundException;

import java.io.File;
import java.io.FileNotFoundException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Scanner;

public class StatisticsApp {
    public static void main(String[] args) {
        StatisticsApp statisticsApp = new StatisticsApp();

        LocalDateTime startDateTime = LocalDateTime.of(2012, 01, 01, 00, 46,01);
        LocalDateTime endDateTime = LocalDateTime.of(2015, 06, 04, 01, 9,21);

        getForecast(statisticsApp, startDateTime, endDateTime);
    }

    private static void getForecast(StatisticsApp statisticsApp, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        if (startDateTime.compareTo(endDateTime) >= 0) {
            System.out.println("Beginning time cannot be after end time");
            return;
        }

        ArrayList<WeatherEntry> weatherEntries;

        try {
            weatherEntries = statisticsApp.loadWeatherEntriesIntoTempStore(startDateTime, endDateTime);
        } catch (FileNotFoundException e) {
            System.out.println("Unable to read file: " + e.getLocalizedMessage());
            return;
        }

        var startTimePressure = getPressureAt(startDateTime, weatherEntries);
        var endTimePressure = getPressureAt(endDateTime, weatherEntries);

        displayOutput(startTimePressure, endTimePressure);
    }

    private static void displayOutput(float startTimePressure, float endTimePressure) {
        System.out.println("endTimePressure: " + endTimePressure);
        System.out.println("startTimePressure: " + startTimePressure);
        System.out.println("\r");

        if ((endTimePressure - startTimePressure) > 0) {
            System.out.println("Forecast: Fair and sunny");
        }

        if ((endTimePressure - startTimePressure) == 0) {
            System.out.println("Forecast: Same conditions");
        }

        if ((endTimePressure - startTimePressure) < 0) {
            System.out.println("Forecast: Stormy weather");
        }
    }

    private static float getPressureAt(LocalDateTime localDateTime, ArrayList<WeatherEntry> weatherEntries) {
        WeatherEntry weatherEntry = new WeatherEntry();
        try {
            weatherEntry = searchForEntry(localDateTime,weatherEntries);
        } catch (WeatherEntryNotFoundException e) {
            System.out.println(e.getLocalizedMessage());
            System.exit(0);
        }
        return weatherEntry.getBarometricPressure();
    }

    private static WeatherEntry searchForEntry(LocalDateTime targetDateTime, ArrayList<WeatherEntry> weatherEntries) {
        var lowIdx = 0;
        var upperIdx = weatherEntries.size() - 1;

        while (lowIdx <= upperIdx) {
            int midpoint = (upperIdx + lowIdx)/2;

            if (weatherEntries.get(midpoint).getLocalDateTime().equals(targetDateTime)){
                return weatherEntries.get(midpoint);
            } else if (weatherEntries.get(midpoint).getLocalDateTime().compareTo(targetDateTime) >= 0) {
                upperIdx = midpoint - 1;
            } else if (weatherEntries.get(midpoint).getLocalDateTime().compareTo(targetDateTime) < 0) {
                lowIdx = midpoint + 1;
            }
        }
        throw new WeatherEntryNotFoundException("Weather Entry not found for " + targetDateTime);
    }

    private ArrayList<WeatherEntry> loadWeatherEntriesIntoTempStore(LocalDateTime startDateTime, LocalDateTime endDateTime) throws FileNotFoundException {
        DateTimeFormatter yearFormatter = DateTimeFormatter.ofPattern("yyyy");

        var startYear = startDateTime.format(yearFormatter);
        var endYear = endDateTime.format(yearFormatter);

        ArrayList<String> years = new ArrayList<>();
        years.add(startYear);
        if (!startYear.equals(endYear))  years.add(endYear);

        ArrayList<WeatherEntry> tempDataStore = new ArrayList<>();

        for(String year: years) {
            String filepath = "src/main/java/resources/Environmental_Data_Deep_Moor_" + year + ".txt";

            File file = new File(filepath);
            Scanner scanner = new Scanner(file);
            // Ignore header
            scanner.nextLine();

            System.out.println("Loading data for year " + year + " ...");


            while (scanner.hasNextLine()) {
                String data = scanner.nextLine();
                String[] entries = data.split("\t");
                String[] dateTime = entries[0].split(" ");
                var date = dateTime[0];
                var time = dateTime[1].split(":");


                LocalDateTime localDateTime = LocalDateTime.of(Integer.parseInt(date.substring(0, 4)), Integer.parseInt(date.substring(5, 7)), Integer.parseInt(date.substring(8, 10)),
                        Integer.parseInt(time[0]), Integer.parseInt(time[1]), Integer.parseInt(time[2]));

                WeatherEntry weatherEntry = new WeatherEntry(localDateTime, Float.parseFloat(entries[3]));
                tempDataStore.add(weatherEntry);
            }

            scanner.close();
            System.out.println("Finished loading data for year " + year + ".");
            System.out.println("\r");
        }
        return tempDataStore;
    }

}
