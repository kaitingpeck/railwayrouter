package railwayrouter.utils;

import static java.lang.String.CASE_INSENSITIVE_ORDER;
import static org.junit.Assert.assertEquals;

import com.opencsv.exceptions.CsvValidationException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.junit.Before;
import org.junit.Test;
import railwayrouter.models.Station;

public class DataProcessorTest {
  private static final Station KOVAN =
      new Station(0, "NE4", "Kovan", LocalDate.of(2021, 2, 21).atStartOfDay());
  private static final Station SERANGOON =
      new Station(1, "NE5", "Serangoon", LocalDate.of(2021, 2, 21).atStartOfDay());
  private static final Station SERANGOON_2 =
      new Station(2, "CC16", "Serangoon", LocalDate.of(2021, 2, 21).atStartOfDay());
  private static final Station LORONG_CHUAN =
      new Station(3, "CC17", "Lorong Chuan", LocalDate.of(2021, 2, 21).atStartOfDay());

  private static final List<Station> DEFAULT_STATIONS =
      List.of(KOVAN, SERANGOON, SERANGOON_2, LORONG_CHUAN);
  private static Map<String, List<Station>> DEFAULT_NAME_TO_STATIONS_MAP;
  private static final Map<String, List<Station>> DEFAULT_LINE_CODE_TO_STATIONS_MAP =
      Map.of(
          "NE", List.of(KOVAN, SERANGOON),
          "CC", List.of(SERANGOON_2, LORONG_CHUAN));

  @Before
  public void setUp() {
    Map<String, List<Station>> treeMap = new TreeMap<>(CASE_INSENSITIVE_ORDER);
    treeMap.put("Kovan", List.of(KOVAN));
    treeMap.put("Serangoon", List.of(SERANGOON, SERANGOON_2));
    treeMap.put("Lorong Chuan", List.of(LORONG_CHUAN));
    DEFAULT_NAME_TO_STATIONS_MAP = Collections.unmodifiableMap(treeMap);
  }

  @Test
  public void testGetOpenStations() throws CsvValidationException {
    List<Station> expected =
        List.of(
            new Station(0, "NE2", "Buangkok", LocalDate.of(2006, 1, 7).atStartOfDay()),
            new Station(1, "NE3", "Hougang", LocalDate.of(2003, 6, 1).atStartOfDay()),
            new Station(2, "NE4", "Kovan", LocalDate.of(2003, 6, 20).atStartOfDay()));
    assertEquals(expected, DataProcessor.getOpenStations(LocalDateTime.of(2020, 10, 21, 7, 0)));
  }

  @Test
  public void testGetOpenStationsSomeNotOpen() throws CsvValidationException {
    List<Station> expected =
        List.of(
            new Station(0, "NE3", "Hougang", LocalDate.of(2003, 6, 1).atStartOfDay()),
            new Station(1, "NE4", "Kovan", LocalDate.of(2003, 6, 20).atStartOfDay()));
    assertEquals(expected, DataProcessor.getOpenStations(LocalDateTime.of(2003, 10, 21, 7, 0)));
  }

  @Test
  public void testGetOpenStationsAllNotOpen() throws CsvValidationException {
    List<Station> expected = List.of();
    assertEquals(expected, DataProcessor.getOpenStations(LocalDateTime.of(1999, 10, 21, 7, 0)));
  }

  @Test
  public void testGenerateLineCodeToStationsMap() {
    Map<String, List<Station>> expected =
        Map.of(
            "NE", List.of(KOVAN, SERANGOON),
            "CC", List.of(SERANGOON_2, LORONG_CHUAN));
    assertEquals(expected, DataProcessor.generateLineCodeToStationsMap(DEFAULT_STATIONS));
  }

  @Test
  public void testGenerateLineCodeToStationsMapEmptyList() {
    Map<String, List<Station>> expected = Map.of();
    assertEquals(expected, DataProcessor.generateLineCodeToStationsMap(List.of()));
  }

  @Test
  public void testGetAdjList() {
    List<List<Station>> expected =
        List.of(
            List.of(SERANGOON),
            List.of(KOVAN, SERANGOON_2),
            List.of(LORONG_CHUAN, SERANGOON),
            List.of(SERANGOON_2));
    assertEquals(
        expected,
        DataProcessor.generateAdjList(
            DEFAULT_NAME_TO_STATIONS_MAP, DEFAULT_LINE_CODE_TO_STATIONS_MAP));
  }

  @Test
  public void testGetAdjListEmptyList() {
    List<List<Station>> expected = List.of();
    assertEquals(expected, DataProcessor.generateAdjList(Map.of(), Map.of()));
  }

  @Test
  public void testGenerateNameToStationsMap() {
    Map<String, List<Station>> expected =
        Map.of(
            "Kovan", List.of(KOVAN),
            "Serangoon", List.of(SERANGOON, SERANGOON_2),
            "Lorong Chuan", List.of(LORONG_CHUAN));
    assertEquals(expected, DataProcessor.generateNameToStationsMap(DEFAULT_STATIONS));
  }

  @Test
  public void testGenerateNameToStationsMapEmptyList() {
    Map<String, List<Station>> expected = Map.of();
    assertEquals(expected, DataProcessor.generateNameToStationsMap(List.of()));
  }
}
