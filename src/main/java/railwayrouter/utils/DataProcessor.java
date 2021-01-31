package railwayrouter.utils;

import static java.lang.String.CASE_INSENSITIVE_ORDER;
import static java.time.temporal.ChronoField.DAY_OF_MONTH;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import railwayrouter.models.Station;

public class DataProcessor {

  private static final Logger LOGGER = Logger.getLogger(DataProcessor.class.getName());

  /**
   * Parses resources/StationMap.csv to get the list of open stations at {@code startDateTime}. If
   * no day of month is specified in csv file, default station opening date to first day of month.
   *
   * @param startDateTime
   * @return list of open stations
   */
  public static List<Station> getOpenStations(LocalDateTime startDateTime)
      throws CsvValidationException {
    InputStream in = DataProcessor.class.getResourceAsStream("/StationMap.csv");
    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));

    List<Station> stations = new ArrayList<>();
    try (CSVReader reader = new CSVReader(bufferedReader)) {
      String[] line = reader.readNext(); // skip header
      int id = 0;
      List<DateTimeFormatter> formatters =
          List.of(
              DateTimeFormatter.ofPattern("dd MMMM yyyy"),
              DateTimeFormatter.ofPattern("d MMMM yyyy"),
              new DateTimeFormatterBuilder()
                  .appendPattern("MMMM yyyy")
                  .parseDefaulting(DAY_OF_MONTH, 1)
                  .toFormatter());
      while (line != null) {
        line = reader.readNext();
        for (DateTimeFormatter formatter : formatters) {
          try {
            // try the different parsing formats - there's more than one format in the csv file
            LocalDateTime openingDate = LocalDate.parse(line[2], formatter).atStartOfDay();
            if (openingDate.isBefore(startDateTime)) {
              Station station = new Station(id++, line[0], line[1], openingDate);
              stations.add(station);
            }
            break;
          } catch (Exception e) {
            // do nothing
          }
        }
      }
    } catch (IOException | CsvValidationException e) {
      LOGGER.severe("Stations.csv cannot be read");
      throw new CsvValidationException(e.getMessage());
    }
    return stations;
  }

  /**
   * Generates a mapping from line code to stations. e.g. "CC": [Serangoon station object, Bishan
   * station object]
   *
   * @param stations
   * @return mapping from line code to stations in that line
   */
  public static Map<String, List<Station>> generateLineCodeToStationsMap(List<Station> stations) {
    // create a copy of stations, sorted by lineCode, then number
    List<Station> stationsCopy =
        stations.stream()
            .sorted(Comparator.comparing(Station::getLineCode).thenComparing(Station::getNumber))
            .collect(Collectors.toList());

    Map<String, List<Station>> map = new HashMap<>();
    for (Station station : stationsCopy) {
      List<Station> stationList =
          map.computeIfAbsent(station.getLineCode(), k -> new ArrayList<>());
      stationList.add(station);
    }
    return map;
  }

  /**
   * Generates adjacency list based on all the current open stations.
   *
   * @param nameToStationsMap
   * @param lineCodeToStationsMap
   * @return adjacency list for all the open stations in the network
   */
  public static List<List<Station>> generateAdjList(
      Map<String, List<Station>> nameToStationsMap,
      Map<String, List<Station>> lineCodeToStationsMap) {
    Collection<List<Station>> stationsByLineCode = lineCodeToStationsMap.values();
    int numStations = stationsByLineCode.stream().map(List::size).reduce(0, Integer::sum);
    List<List<Station>> adjList = new ArrayList<>();
    IntStream.range(0, numStations).forEach(i -> adjList.add(new ArrayList<>()));

    for (List<Station> stationList : stationsByLineCode) {
      for (int i = 0; i < stationList.size(); i++) {
        Station cur = stationList.get(i);
        int idx = cur.getId();
        List<Station> adjStations = adjList.get(idx);

        if (i > 0) adjStations.add(stationList.get(i - 1)); // add prev station
        if (i < stationList.size() - 1) adjStations.add(stationList.get(i + 1)); // add next station

        // get all the stations with the same name, but different code e.g. Buona Vista is CC22 &
        // EW21
        List<Station> sameNameStations = nameToStationsMap.get(cur.getName());
        for (Station sameNameStation : sameNameStations) {
          // compare by id for simplicity
          if (sameNameStation.getId() != cur.getId()) {
            adjStations.add(sameNameStation);
          }
        }
      }
    }
    return adjList;
  }

  /**
   * Generates mapping of station names to list of {@code Station} objects e.g. "Serangoon":
   * [Station representing Serangoon in NE line, Station representing Serangoon in CC line]
   *
   * @param stations
   * @return mapping of station name to stations with that name
   */
  public static Map<String, List<Station>> generateNameToStationsMap(List<Station> stations) {
    Map<String, List<Station>> map = new TreeMap<>(CASE_INSENSITIVE_ORDER);
    for (Station station : stations) {
      List<Station> stationsWithName = map.getOrDefault(station.getName(), new ArrayList<>());
      stationsWithName.add(station);
      map.put(station.getName(), stationsWithName);
    }
    return map;
  }
}
