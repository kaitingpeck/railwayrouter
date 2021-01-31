package railwayrouter.services;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;
import static railwayrouter.testutils.TestUtils.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import railwayrouter.models.Constants;
import railwayrouter.models.Route;
import railwayrouter.models.Station;
import railwayrouter.models.TimeOfDay;

@RunWith(PowerMockRunner.class)
@PrepareForTest(Constants.class)
public class RouteFinderTest {

  private RouteFinder routeFinder;
  private RouteFinder spiedRouteFinder;

  private static final Station BUANGKOK =
      new Station(0, "NE2", "Buangkok", LocalDate.of(2021, 2, 21).atStartOfDay());
  private static final Station HOUGANG =
      new Station(1, "NE3", "Hougang", LocalDate.of(2021, 2, 21).atStartOfDay());
  private static final Station KOVAN =
      new Station(2, "NE4", "Kovan", LocalDate.of(2021, 2, 21).atStartOfDay());
  private static final Station SERANGOON =
      new Station(3, "NE5", "Serangoon", LocalDate.of(2021, 2, 21).atStartOfDay());
  private static final Station SERANGOON_2 =
      new Station(4, "CC16", "Serangoon", LocalDate.of(2021, 2, 21).atStartOfDay());
  private static final Station LORONG_CHUAN =
      new Station(5, "CC17", "Lorong Chuan", LocalDate.of(2021, 2, 21).atStartOfDay());
  private static final Station LORONG_CHUAN_2 =
      new Station(6, "AA1", "Lorong Chuan", LocalDate.of(2021, 2, 21).atStartOfDay());

  @Before
  public void setUp() {
    String mockLineChangeFieldName = "lineChange";

    PowerMockito.mockStatic(Constants.class);
    when(Constants.getLineChangeFieldName()).thenReturn(mockLineChangeFieldName);
    when(Constants.getFrequencyMap())
        .thenReturn(
            Map.of(
                TimeOfDay.PEAK,
                    Map.of("NE", 1, "CC", 2, "NS", 8, "CG", 7, mockLineChangeFieldName, 1),
                TimeOfDay.NIGHT,
                    Map.of(
                        "NE",
                        4,
                        "CC",
                        5,
                        "NS",
                        2,
                        "CG",
                        Integer.MAX_VALUE,
                        mockLineChangeFieldName,
                        2),
                TimeOfDay.NONPEAK,
                    Map.of(
                        "NE",
                        7,
                        "CC",
                        8,
                        "NS",
                        7,
                        "CG",
                        Integer.MAX_VALUE,
                        mockLineChangeFieldName,
                        3)));

    routeFinder = new RouteFinder();
    spiedRouteFinder = spy(routeFinder);
  }

  @Test
  public void testFindRouteOneRoute() {
    Route route = new Route(100, List.of(BUANGKOK, HOUGANG, KOVAN), TimeOfDay.PEAK);
    List<Station> srcStations = List.of(BUANGKOK);
    List<Station> destStations = List.of(KOVAN);
    LocalDateTime startDateTime = VALID_PEAK_DATE_TIME;
    List<List<Station>> adjList =
        List.of(List.of(HOUGANG), List.of(BUANGKOK, KOVAN), List.of(HOUGANG));

    doReturn(route).when(spiedRouteFinder).dijkstra(BUANGKOK, KOVAN, startDateTime, adjList);
    assertEquals(
        Optional.of(route),
        spiedRouteFinder.findRoute(srcStations, destStations, startDateTime, adjList));
  }

  @Test
  public void testFindRouteNoRouteReturnsEmpty() {
    List<Station> srcStations = List.of(BUANGKOK);
    List<Station> destStations = List.of(KOVAN);
    LocalDateTime startDateTime = VALID_PEAK_DATE_TIME;
    List<List<Station>> adjList =
        List.of(List.of(HOUGANG), List.of(BUANGKOK, KOVAN), List.of(HOUGANG));

    doReturn(null).when(spiedRouteFinder).dijkstra(BUANGKOK, KOVAN, startDateTime, adjList);
    assertEquals(
        Optional.empty(),
        spiedRouteFinder.findRoute(srcStations, destStations, startDateTime, adjList));
  }

  @Test
  public void testFindRouteMultiSourceDest() {
    Route route = new Route(100, List.of(SERANGOON, LORONG_CHUAN), TimeOfDay.PEAK);
    Route fasterRoute = new Route(80, List.of(SERANGOON_2, LORONG_CHUAN_2), TimeOfDay.PEAK);
    List<Station> srcStations = List.of(SERANGOON, SERANGOON_2);
    List<Station> destStations = List.of(LORONG_CHUAN, LORONG_CHUAN_2);
    LocalDateTime startDateTime = VALID_PEAK_DATE_TIME;
    List<List<Station>> adjList = List.of(); // placeholder argument

    doReturn(route)
        .when(spiedRouteFinder)
        .dijkstra(SERANGOON, LORONG_CHUAN, startDateTime, adjList);
    doReturn(null)
        .when(spiedRouteFinder)
        .dijkstra(SERANGOON, LORONG_CHUAN_2, startDateTime, adjList);
    doReturn(fasterRoute)
        .when(spiedRouteFinder)
        .dijkstra(SERANGOON_2, LORONG_CHUAN, startDateTime, adjList);
    doReturn(null)
        .when(spiedRouteFinder)
        .dijkstra(SERANGOON_2, LORONG_CHUAN_2, startDateTime, adjList);
    assertEquals(
        Optional.of(fasterRoute),
        spiedRouteFinder.findRoute(srcStations, destStations, startDateTime, adjList));
  }

  @Test
  public void testFindRouteEmptySource() {
    List<Station> srcStations = List.of();
    List<Station> destStations = List.of(LORONG_CHUAN);
    LocalDateTime startDateTime = VALID_PEAK_DATE_TIME;
    List<List<Station>> adjList = List.of(); // placeholder argument

    assertEquals(
        Optional.empty(),
        spiedRouteFinder.findRoute(srcStations, destStations, startDateTime, adjList));
  }

  @Test
  public void testFindRouteEmptyDest() {
    List<Station> srcStations = List.of(BUANGKOK);
    List<Station> destStations = List.of();
    LocalDateTime startDateTime = VALID_PEAK_DATE_TIME;
    List<List<Station>> adjList = List.of(); // placeholder argument

    assertEquals(
        Optional.empty(),
        spiedRouteFinder.findRoute(srcStations, destStations, startDateTime, adjList));
  }

  @Test
  public void testGetRoute() {
    Station dest = KOVAN;
    int destId = dest.getId();

    LocalDateTime[] arrivalDateTimes = new LocalDateTime[destId + 1];
    LocalDateTime startDateTime = VALID_PEAK_DATE_TIME;
    int minutesTaken = 20;
    LocalDateTime arrivalDateTime = VALID_PEAK_DATE_TIME.plusMinutes(minutesTaken);
    arrivalDateTimes[destId] = arrivalDateTime;

    Station[] prec = new Station[destId + 1];
    prec[destId] = HOUGANG;

    Route expected = new Route(minutesTaken, List.of(HOUGANG, KOVAN), TimeOfDay.PEAK);
    assertEquals(expected, spiedRouteFinder.getRoute(dest, startDateTime, arrivalDateTimes, prec));
  }

  @Test
  public void testGetRouteNoRoute() {
    Station dest = KOVAN;
    int destId = dest.getId();

    LocalDateTime[] arrivalDateTimes = new LocalDateTime[destId + 1];
    LocalDateTime startDateTime = VALID_PEAK_DATE_TIME;
    arrivalDateTimes[destId] = LocalDateTime.MAX;

    Station[] prec = new Station[destId + 1];

    Route expected = null;
    assertEquals(expected, spiedRouteFinder.getRoute(dest, startDateTime, arrivalDateTimes, prec));
  }

  @Test
  public void testGetCostNotTransfer() {
    assertEquals(1, spiedRouteFinder.getCost(BUANGKOK, HOUGANG, VALID_PEAK_DATE_TIME));
  }

  @Test
  public void testGetCostTransfer() {
    assertEquals(3, spiedRouteFinder.getCost(SERANGOON, SERANGOON_2, VALID_NONPEAK_DATE_TIME));
  }

  @Test
  /** 0 - 1 - 2 - 3 */
  public void testDijkstraOnePathNoTransfer() {
    LocalDateTime startDateTime = VALID_PEAK_DATE_TIME;
    LocalDateTime stationOpenDateTime = VALID_PEAK_DATE_TIME.minusMonths(3);

    Station station0 = new Station(0, "NE1", "Station A", stationOpenDateTime);
    Station station1 = new Station(1, "NE2", "Station B", stationOpenDateTime);
    Station station2 = new Station(2, "NE3", "Station C", stationOpenDateTime);
    Station station3 = new Station(3, "NE4", "Station D", stationOpenDateTime);

    List<List<Station>> adjList =
        List.of(
            List.of(station1),
            List.of(station0, station2),
            List.of(station1, station3),
            List.of(station2));

    Route expected = new Route(2, List.of(station1, station2, station3), TimeOfDay.PEAK);
    assertEquals(expected, spiedRouteFinder.dijkstra(station1, station3, startDateTime, adjList));
  }

  @Test
  /** 0 - 1 | 2 - 3 */
  public void testDijkstraOnePathTransfer() {
    LocalDateTime startDateTime = VALID_PEAK_DATE_TIME;
    LocalDateTime stationOpenDateTime = VALID_PEAK_DATE_TIME.minusMonths(3);

    Station station0 = new Station(0, "NE1", "Station A", stationOpenDateTime);
    Station station1 = new Station(1, "NE2", "Station B", stationOpenDateTime);
    Station station2 = new Station(2, "CC1", "Station B", stationOpenDateTime);
    Station station3 = new Station(3, "CC2", "Station D", stationOpenDateTime);

    List<List<Station>> adjList =
        List.of(
            List.of(station1),
            List.of(station0, station2),
            List.of(station1, station3),
            List.of(station2));

    Route expected = new Route(4, List.of(station0, station1, station2, station3), TimeOfDay.PEAK);
    assertEquals(expected, spiedRouteFinder.dijkstra(station0, station3, startDateTime, adjList));
  }

  @Test
  /** 0 - 1 - 2 - 3 | 4 - 5 - 3 */
  public void testDijkstraMultiplePaths() {
    LocalDateTime startDateTime = VALID_PEAK_DATE_TIME;
    LocalDateTime stationOpenDateTime = VALID_PEAK_DATE_TIME.minusMonths(3);

    Station station0 = new Station(0, "CC1", "Station A", stationOpenDateTime);
    Station station1 = new Station(1, "CC2", "Station B", stationOpenDateTime);
    Station station2 = new Station(2, "CC3", "Station C", stationOpenDateTime);
    Station station3 = new Station(3, "CC4", "Station D", stationOpenDateTime);
    Station station4 = new Station(4, "NE1", "Station B", stationOpenDateTime);
    Station station5 = new Station(5, "NE2", "Station F", stationOpenDateTime);

    List<List<Station>> adjList =
        List.of(
            List.of(station1),
            List.of(station0, station2, station4),
            List.of(station1, station3),
            List.of(station2, station5),
            List.of(station1, station5),
            List.of(station4, station3));

    Route expected =
        new Route(5, List.of(station0, station1, station4, station5, station3), TimeOfDay.PEAK);
    assertEquals(expected, spiedRouteFinder.dijkstra(station0, station3, startDateTime, adjList));
  }

  @Test
  /** 0 (NS) - 1 - 2 - 3 | 4 (CC) - 5 | 6 (NE) - 7 - 8 - 3 */
  public void testDijkstraMultipleTransfer() {
    LocalDateTime startDateTime = VALID_PEAK_DATE_TIME;
    LocalDateTime stationOpenDateTime = VALID_PEAK_DATE_TIME.minusMonths(3);

    Station station0 = new Station(0, "NS1", "Station A", stationOpenDateTime);
    Station station1 = new Station(1, "NS2", "Station B", stationOpenDateTime);
    Station station2 = new Station(2, "NS3", "Station C", stationOpenDateTime);
    Station station3 = new Station(3, "NS4", "Station D", stationOpenDateTime);
    Station station4 = new Station(4, "CC1", "Station B", stationOpenDateTime);
    Station station5 = new Station(5, "CC2", "Station E", stationOpenDateTime);
    Station station6 = new Station(6, "NE1", "Station E", stationOpenDateTime);
    Station station7 = new Station(7, "NE2", "Station F", stationOpenDateTime);
    Station station8 = new Station(8, "NE3", "Station G", stationOpenDateTime);

    List<List<Station>> adjList =
        List.of(
            List.of(station1),
            List.of(station0, station2, station4),
            List.of(station1, station3),
            List.of(station2, station8),
            List.of(station1, station5),
            List.of(station4, station6),
            List.of(station5, station7),
            List.of(station6, station8),
            List.of(station3, station7));

    Route expected =
        new Route(
            15,
            List.of(station0, station1, station4, station5, station6, station7, station8, station3),
            TimeOfDay.PEAK);
    assertEquals(expected, spiedRouteFinder.dijkstra(station0, station3, startDateTime, adjList));
  }

  @Test
  /** 0 - 1 2 - 3 */
  public void testDijkstraNoRoute() {
    LocalDateTime startDateTime = VALID_PEAK_DATE_TIME;
    LocalDateTime stationOpenDateTime = VALID_PEAK_DATE_TIME.minusMonths(3);

    Station station0 = new Station(0, "NE1", "Station A", stationOpenDateTime);
    Station station1 = new Station(1, "NE2", "Station B", stationOpenDateTime);
    Station station2 = new Station(2, "CC1", "Station C", stationOpenDateTime);
    Station station3 = new Station(3, "CC2", "Station D", stationOpenDateTime);

    List<List<Station>> adjList =
        List.of(List.of(station1), List.of(station0), List.of(station3), List.of(station2));

    Route expected = null;
    assertEquals(expected, spiedRouteFinder.dijkstra(station1, station3, startDateTime, adjList));
  }

  @Test
  /**
   * 2050 start (10 mins before peak) Each station takes 7 mins 0 - 1 - 2 (closed on arrival) - 3
   */
  public void testDijkstraNoRouteLineClosed() {
    LocalDateTime startDateTime = VALID_10_MINS_BEFORE_NONPEAK_DATE_TIME;
    LocalDateTime stationOpenDateTime = VALID_PEAK_DATE_TIME.minusMonths(3);

    Station station0 = new Station(0, "CG1", "Station A", stationOpenDateTime);
    Station station1 = new Station(1, "CG2", "Station B", stationOpenDateTime);
    Station station2 = new Station(2, "CG3", "Station C", stationOpenDateTime);
    Station station3 = new Station(3, "CG4", "Station D", stationOpenDateTime);

    List<List<Station>> adjList =
        List.of(
            List.of(station1),
            List.of(station0, station2),
            List.of(station1, station3),
            List.of(station2));

    Route expected = null;
    assertEquals(expected, spiedRouteFinder.dijkstra(station0, station3, startDateTime, adjList));
  }

  @Test
  /**
   * 2050 start (10 mins before peak) Each station takes 7 mins 0 - 1 | 2 (closed on arrival) - 3
   */
  public void testDijkstraNoRouteTransferLineClosed() {
    LocalDateTime startDateTime = VALID_NIGHT_DATE_TIME;
    LocalDateTime stationOpenDateTime = VALID_PEAK_DATE_TIME.minusMonths(3);

    Station station0 = new Station(0, "NE1", "Station A", stationOpenDateTime);
    Station station1 = new Station(1, "NE2", "Station B", stationOpenDateTime);
    Station station2 = new Station(2, "CG3", "Station B", stationOpenDateTime);
    Station station3 = new Station(3, "CG4", "Station D", stationOpenDateTime);

    List<List<Station>> adjList =
        List.of(
            List.of(station1),
            List.of(station0, station2),
            List.of(station1, station3),
            List.of(station2));

    Route expected = null;
    assertEquals(expected, spiedRouteFinder.dijkstra(station0, station3, startDateTime, adjList));
  }

  @Test
  /** 0 (peak) - 1 - 2 (non-peak) - 3 */
  public void testDijkstraPeakToNonPeak() {
    LocalDateTime startDateTime = VALID_10_MINS_BEFORE_NONPEAK_DATE_TIME;
    LocalDateTime stationOpenDateTime = VALID_PEAK_DATE_TIME.minusMonths(3);

    Station station0 = new Station(0, "NS1", "Station A", stationOpenDateTime);
    Station station1 = new Station(1, "NS2", "Station B", stationOpenDateTime);
    Station station2 = new Station(2, "NS3", "Station C", stationOpenDateTime);
    Station station3 = new Station(3, "NS4", "Station D", stationOpenDateTime);

    List<List<Station>> adjList =
        List.of(
            List.of(station1),
            List.of(station0, station2),
            List.of(station1, station3),
            List.of(station2));

    Route expected = new Route(23, List.of(station0, station1, station2, station3), TimeOfDay.PEAK);
    assertEquals(expected, spiedRouteFinder.dijkstra(station0, station3, startDateTime, adjList));
  }

  @Test
  /** 0 (non-peak) - 1 - 2 (night) - 3 */
  public void testDijkstraNonPeakToNight() {
    LocalDateTime startDateTime = VALID_10_MINS_BEFORE_NIGHT_DATE_TIME;
    LocalDateTime stationOpenDateTime = VALID_PEAK_DATE_TIME.minusMonths(3);

    Station station0 = new Station(0, "NS1", "Station A", stationOpenDateTime);
    Station station1 = new Station(1, "NS2", "Station B", stationOpenDateTime);
    Station station2 = new Station(2, "NS3", "Station C", stationOpenDateTime);
    Station station3 = new Station(3, "NS4", "Station D", stationOpenDateTime);

    List<List<Station>> adjList =
        List.of(
            List.of(station1),
            List.of(station0, station2),
            List.of(station1, station3),
            List.of(station2));

    Route expected =
        new Route(16, List.of(station0, station1, station2, station3), TimeOfDay.NONPEAK);
    assertEquals(expected, spiedRouteFinder.dijkstra(station0, station3, startDateTime, adjList));
  }

  @Test
  /** 0 (night) - 1 - 2 (peak) - 3 */
  public void testDijkstraNightToPeak() {
    LocalDateTime startDateTime = VALID_3_MINS_BEFORE_PEAK_DATE_TIME;
    LocalDateTime stationOpenDateTime = VALID_PEAK_DATE_TIME.minusMonths(3);

    Station station0 = new Station(0, "NS1", "Station A", stationOpenDateTime);
    Station station1 = new Station(1, "NS2", "Station B", stationOpenDateTime);
    Station station2 = new Station(2, "NS3", "Station C", stationOpenDateTime);
    Station station3 = new Station(3, "NS4", "Station D", stationOpenDateTime);

    List<List<Station>> adjList =
        List.of(
            List.of(station1),
            List.of(station0, station2),
            List.of(station1, station3),
            List.of(station2));

    Route expected =
        new Route(12, List.of(station0, station1, station2, station3), TimeOfDay.NIGHT);
    assertEquals(expected, spiedRouteFinder.dijkstra(station0, station3, startDateTime, adjList));
  }

  @Test
  /** 0 (night) - 1 - 2 (peak) - 3 */
  public void testDijkstraNonPeakToPeak() {
    LocalDateTime startDateTime = VALID_10_MINS_NONPEAK_TO_PEAK_DATE_TIME;
    LocalDateTime stationOpenDateTime = VALID_PEAK_DATE_TIME.minusMonths(3);

    Station station0 = new Station(0, "NS1", "Station A", stationOpenDateTime);
    Station station1 = new Station(1, "NS2", "Station B", stationOpenDateTime);
    Station station2 = new Station(2, "NS3", "Station C", stationOpenDateTime);
    Station station3 = new Station(3, "NS4", "Station D", stationOpenDateTime);

    List<List<Station>> adjList =
        List.of(
            List.of(station1),
            List.of(station0, station2),
            List.of(station1, station3),
            List.of(station2));

    Route expected =
        new Route(22, List.of(station0, station1, station2, station3), TimeOfDay.NONPEAK);
    assertEquals(expected, spiedRouteFinder.dijkstra(station0, station3, startDateTime, adjList));
  }
}
