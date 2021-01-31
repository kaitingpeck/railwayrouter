package railwayrouter.services;

import static java.lang.String.CASE_INSENSITIVE_ORDER;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static railwayrouter.services.QueryHandler.INVALID_DATE_TIME_STRING;
import static railwayrouter.services.QueryHandler.INVALID_STATION_STRING;
import static railwayrouter.testutils.TestUtils.*;

import com.opencsv.exceptions.CsvValidationException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import railwayrouter.models.Route;
import railwayrouter.models.Station;
import railwayrouter.models.TimeOfDay;
import railwayrouter.utils.DataProcessor;
import railwayrouter.utils.ResponseBuilder;

@RunWith(PowerMockRunner.class)
@PrepareForTest({DataProcessor.class, ResponseBuilder.class})
public class QueryHandlerTest {

  @Mock private RouteFinder mockRouteFinder;

  private QueryHandler queryHandler;

  private QueryHandler spiedQueryHandler;

  private String TEST_RESULT_STRING = "test result";

  private static final Station HOUGANG =
      new Station(1, "NE3", "Hougang", LocalDate.of(2021, 2, 21).atStartOfDay());
  private static final Station KOVAN =
      new Station(2, "NE4", "Kovan", LocalDate.of(2021, 2, 21).atStartOfDay());
  private static final Station SERANGOON =
      new Station(3, "NE5", "Serangoon", LocalDate.of(2021, 2, 21).atStartOfDay());
  private static final Station SERANGOON_2 =
      new Station(4, "CC16", "Serangoon", LocalDate.of(2021, 2, 21).atStartOfDay());

  @Before
  public void setUp() throws CsvValidationException {
    queryHandler = new QueryHandler(mockRouteFinder);
    spiedQueryHandler = spy(queryHandler);

    PowerMockito.mockStatic(DataProcessor.class);
    when(DataProcessor.getOpenStations(any())).thenReturn(List.of());
    when(DataProcessor.generateLineCodeToStationsMap(any())).thenReturn(Map.of());
    when(DataProcessor.generateNameToStationsMap(any())).thenReturn(Map.of());
    when(DataProcessor.generateAdjList(any(), any())).thenReturn(List.of());

    PowerMockito.mockStatic(ResponseBuilder.class);
    when(ResponseBuilder.buildResponse(any())).thenReturn(TEST_RESULT_STRING);
  }

  @Test
  public void testGetStations() {
    Map<String, List<Station>> treeMap = new TreeMap<>(CASE_INSENSITIVE_ORDER);
    treeMap.put("Kovan", List.of(KOVAN));
    Map<String, List<Station>> mockNameToStationsMap = Collections.unmodifiableMap(treeMap);

    List<Station> expected = List.of(KOVAN);
    assertEquals(expected, spiedQueryHandler.getStations("Kovan", mockNameToStationsMap));
  }

  @Test
  public void testGetStationsMultipleResults() {
    Map<String, List<Station>> treeMap = new TreeMap<>(CASE_INSENSITIVE_ORDER);
    treeMap.put("Kovan", List.of(KOVAN));
    treeMap.put("Serangoon", List.of(SERANGOON, SERANGOON_2));
    Map<String, List<Station>> mockNameToStationsMap = Collections.unmodifiableMap(treeMap);

    List<Station> expected = List.of(SERANGOON, SERANGOON_2);
    assertEquals(expected, spiedQueryHandler.getStations("Serangoon", mockNameToStationsMap));
  }

  @Test
  public void testGetStationsMixedCase() {
    Map<String, List<Station>> treeMap = new TreeMap<>(CASE_INSENSITIVE_ORDER);
    treeMap.put("Kovan", List.of(KOVAN));
    treeMap.put("Serangoon", List.of(SERANGOON, SERANGOON_2));
    Map<String, List<Station>> mockNameToStationsMap = Collections.unmodifiableMap(treeMap);

    List<Station> expected = List.of(SERANGOON, SERANGOON_2);
    assertEquals(expected, spiedQueryHandler.getStations("seraNgOon", mockNameToStationsMap));
  }

  @Test
  public void testGetStationsEmpty() {
    Map<String, List<Station>> treeMap = new TreeMap<>(CASE_INSENSITIVE_ORDER);
    treeMap.put("Kovan", List.of(KOVAN));
    Map<String, List<Station>> mockNameToStationsMap = Collections.unmodifiableMap(treeMap);

    List<Station> expected = List.of();
    assertEquals(expected, spiedQueryHandler.getStations("invalidStation", mockNameToStationsMap));
  }

  @Test
  public void testGetDateTime() {
    String str = "2020-01-20T07:00";
    LocalDateTime dateTime = LocalDateTime.of(2020, 1, 20, 7, 0);
    assertEquals(Optional.of(dateTime), spiedQueryHandler.getDateTime(str));
  }

  @Test
  public void testGetInvalidDateTime() {
    String str = "2020-01-20 07:00";
    assertEquals(Optional.empty(), spiedQueryHandler.getDateTime(str));
  }

  @Test
  public void testGetDateTimeEmptyString() {
    String str = "";
    assertEquals(Optional.empty(), spiedQueryHandler.getDateTime(str));
  }

  @Test
  public void testHandleQuery() throws CsvValidationException {
    String src = "Hougang";
    String dest = "Kovan";
    String startDateTimeStr = "2020-01-20T07:00";
    LocalDateTime startDateTime = LocalDateTime.of(2020, 1, 20, 7, 0);
    Route route = new Route(100, List.of(HOUGANG, KOVAN), TimeOfDay.PEAK);

    doReturn(Optional.of(startDateTime)).when(spiedQueryHandler).getDateTime(startDateTimeStr);
    doReturn(List.of(HOUGANG), List.of(KOVAN)).when(spiedQueryHandler).getStations(any(), any());
    doReturn(Optional.of(route)).when(mockRouteFinder).findRoute(any(), any(), any(), any());

    assertEquals(TEST_RESULT_STRING, spiedQueryHandler.handleQuery(src, dest, startDateTimeStr));
  }

  @Test
  public void testHandleQueryInvalidSrc() throws CsvValidationException {
    String invalidSrc = "invalid string";
    String dest = "Kovan";
    String startDateTimeStr = "2020-01-20T07:00";
    LocalDateTime startDateTime = LocalDateTime.of(2020, 1, 20, 7, 0);

    doReturn(Optional.of(startDateTime)).when(spiedQueryHandler).getDateTime(startDateTimeStr);
    doReturn(List.of(), List.of(KOVAN)).when(spiedQueryHandler).getStations(any(), any());

    assertEquals(
        INVALID_STATION_STRING, spiedQueryHandler.handleQuery(invalidSrc, dest, startDateTimeStr));
  }

  @Test
  public void testHandleQueryInvalidDest() throws CsvValidationException {
    String src = "Hougang";
    String invalidDest = "invalid string";
    String startDateTimeStr = "2020-01-20T07:00";
    LocalDateTime startDateTime = LocalDateTime.of(2020, 1, 20, 7, 0);

    doReturn(Optional.of(startDateTime)).when(spiedQueryHandler).getDateTime(startDateTimeStr);
    doReturn(List.of(HOUGANG), List.of()).when(spiedQueryHandler).getStations(any(), any());

    assertEquals(
        INVALID_STATION_STRING, spiedQueryHandler.handleQuery(src, invalidDest, startDateTimeStr));
  }

  @Test
  public void testHandleQueryInvalidStartDateTime() throws CsvValidationException {
    String src = "Hougang";
    String dest = "Kovan";
    String startDateTimeStr = "2020-01-20 07:00";

    doReturn(Optional.empty()).when(spiedQueryHandler).getDateTime(startDateTimeStr);
    doReturn(List.of(HOUGANG), List.of(KOVAN)).when(spiedQueryHandler).getStations(any(), any());

    assertEquals(
        INVALID_DATE_TIME_STRING, spiedQueryHandler.handleQuery(src, dest, startDateTimeStr));
  }

  @Test
  public void testHandleQueryEmptySrc() throws CsvValidationException {
    String emptySrc = "";
    String dest = "Kovan";
    String startDateTimeStr = "2020-01-20T07:00";
    LocalDateTime startDateTime = LocalDateTime.of(2020, 1, 20, 7, 0);

    doReturn(Optional.of(startDateTime)).when(spiedQueryHandler).getDateTime(startDateTimeStr);
    doReturn(List.of(), List.of(KOVAN)).when(spiedQueryHandler).getStations(any(), any());

    assertEquals(
        INVALID_STATION_STRING, spiedQueryHandler.handleQuery(emptySrc, dest, startDateTimeStr));
  }

  @Test
  public void testHandleQueryEmptyDest() throws CsvValidationException {
    String src = "Hougang";
    String emptyDest = "";
    String startDateTimeStr = "2020-01-20T07:00";
    LocalDateTime startDateTime = LocalDateTime.of(2020, 1, 20, 7, 0);

    doReturn(Optional.of(startDateTime)).when(spiedQueryHandler).getDateTime(startDateTimeStr);
    doReturn(List.of(HOUGANG), List.of()).when(spiedQueryHandler).getStations(any(), any());

    assertEquals(
        INVALID_STATION_STRING, spiedQueryHandler.handleQuery(src, emptyDest, startDateTimeStr));
  }

  @Test
  public void testHandleQueryEmptyStartDateTime() throws CsvValidationException {
    String src = "Hougang";
    String dest = "Kovan";
    String emptyStartDateTimeStr = "";

    doReturn(Optional.empty()).when(spiedQueryHandler).getDateTime(emptyStartDateTimeStr);
    doReturn(List.of(HOUGANG), List.of(KOVAN)).when(spiedQueryHandler).getStations(any(), any());

    assertEquals(
        INVALID_DATE_TIME_STRING, spiedQueryHandler.handleQuery(src, dest, emptyStartDateTimeStr));
  }
}
