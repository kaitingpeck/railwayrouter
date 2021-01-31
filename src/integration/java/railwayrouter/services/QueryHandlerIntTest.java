package railwayrouter.services;

import static org.junit.Assert.assertEquals;

import com.opencsv.exceptions.CsvValidationException;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import railwayrouter.models.Route;
import railwayrouter.models.TimeOfDay;
import railwayrouter.testutils.IntTestUtils;

@RunWith(MockitoJUnitRunner.class)
public class QueryHandlerIntTest {

  private QueryHandler queryHandler;

  @Before
  public void setUp() {
    queryHandler = new QueryHandler(new RouteFinder());
  }

  @Test
  public void testHandleQuery() throws CsvValidationException {
    String src = "Buangkok";
    String dest = "Kovan";
    String startDateTimeStr = "2020-01-20T07:00";
    Route route =
        new Route(
            100,
            List.of(IntTestUtils.BUANGKOK, IntTestUtils.HOUGANG, IntTestUtils.KOVAN),
            TimeOfDay.PEAK);

    String expected =
        "Travel from Buangkok to Kovan during peak hours\n"
            + "Time: 24 minutes\n"
            + "Route: [NE2, NE3, NE4]\n"
            + "\n"
            + "Take NE line from Buangkok to Hougang\n"
            + "Take NE line from Hougang to Kovan";

    assertEquals(expected, queryHandler.handleQuery(src, dest, startDateTimeStr));
  }

  @Test
  public void testHandleQuerySrcMixedCase() throws CsvValidationException {
    String src = "buaNgkok";
    String dest = "Kovan";
    String startDateTimeStr = "2020-01-20T07:00";
    Route route =
        new Route(
            100,
            List.of(IntTestUtils.BUANGKOK, IntTestUtils.HOUGANG, IntTestUtils.KOVAN),
            TimeOfDay.PEAK);

    String expected =
        "Travel from Buangkok to Kovan during peak hours\n"
            + "Time: 24 minutes\n"
            + "Route: [NE2, NE3, NE4]\n"
            + "\n"
            + "Take NE line from Buangkok to Hougang\n"
            + "Take NE line from Hougang to Kovan";

    assertEquals(expected, queryHandler.handleQuery(src, dest, startDateTimeStr));
  }

  @Test
  public void testHandleQueryDestMixedCase() throws CsvValidationException {
    String src = "Buangkok";
    String dest = "koVaN";
    String startDateTimeStr = "2020-01-20T07:00";
    Route route =
        new Route(
            100,
            List.of(IntTestUtils.BUANGKOK, IntTestUtils.HOUGANG, IntTestUtils.KOVAN),
            TimeOfDay.PEAK);

    String expected =
        "Travel from Buangkok to Kovan during peak hours\n"
            + "Time: 24 minutes\n"
            + "Route: [NE2, NE3, NE4]\n"
            + "\n"
            + "Take NE line from Buangkok to Hougang\n"
            + "Take NE line from Hougang to Kovan";

    assertEquals(expected, queryHandler.handleQuery(src, dest, startDateTimeStr));
  }

  @Test
  public void testHandleQueryInvalidSrc() throws CsvValidationException {
    String src = "invalid source";
    String dest = "Kovan";
    String startDateTimeStr = "2020-01-20T07:00";
    Route route =
        new Route(
            100,
            List.of(IntTestUtils.BUANGKOK, IntTestUtils.HOUGANG, IntTestUtils.KOVAN),
            TimeOfDay.PEAK);

    String expected = QueryHandler.INVALID_STATION_STRING;

    assertEquals(expected, queryHandler.handleQuery(src, dest, startDateTimeStr));
  }

  @Test
  public void testHandleQueryInvalidDest() throws CsvValidationException {
    String src = "Buangkok";
    String dest = "invalid dest";
    String startDateTimeStr = "2020-01-20T07:00";
    Route route =
        new Route(
            100,
            List.of(IntTestUtils.BUANGKOK, IntTestUtils.HOUGANG, IntTestUtils.KOVAN),
            TimeOfDay.PEAK);

    String expected = QueryHandler.INVALID_STATION_STRING;

    assertEquals(expected, queryHandler.handleQuery(src, dest, startDateTimeStr));
  }

  @Test
  public void testHandleQueryInvalidDate() throws CsvValidationException {
    String src = "Buangkok";
    String dest = "Kovan";
    String startDateTimeStr = "2020-01-20 07:00";
    Route route =
        new Route(
            100,
            List.of(IntTestUtils.BUANGKOK, IntTestUtils.HOUGANG, IntTestUtils.KOVAN),
            TimeOfDay.PEAK);

    String expected = QueryHandler.INVALID_DATE_TIME_STRING;

    assertEquals(expected, queryHandler.handleQuery(src, dest, startDateTimeStr));
  }

  @Test
  public void testHandleQueryEmptySrc() throws CsvValidationException {
    String src = "";
    String dest = "Kovan";
    String startDateTimeStr = "2020-01-20T07:00";
    Route route =
        new Route(
            100,
            List.of(IntTestUtils.BUANGKOK, IntTestUtils.HOUGANG, IntTestUtils.KOVAN),
            TimeOfDay.PEAK);

    String expected = QueryHandler.INVALID_STATION_STRING;

    assertEquals(expected, queryHandler.handleQuery(src, dest, startDateTimeStr));
  }

  @Test
  public void testHandleQueryEmptyDest() throws CsvValidationException {
    String src = "Buangkok";
    String dest = "";
    String startDateTimeStr = "2020-01-20T07:00";
    Route route =
        new Route(
            100,
            List.of(IntTestUtils.BUANGKOK, IntTestUtils.HOUGANG, IntTestUtils.KOVAN),
            TimeOfDay.PEAK);

    String expected = QueryHandler.INVALID_STATION_STRING;

    assertEquals(expected, queryHandler.handleQuery(src, dest, startDateTimeStr));
  }

  @Test
  public void testHandleQueryEmptyDate() throws CsvValidationException {
    String src = "Buangkok";
    String dest = "Kovan";
    String startDateTimeStr = "";
    Route route =
        new Route(
            100,
            List.of(IntTestUtils.BUANGKOK, IntTestUtils.HOUGANG, IntTestUtils.KOVAN),
            TimeOfDay.PEAK);

    String expected = QueryHandler.INVALID_DATE_TIME_STRING;

    assertEquals(expected, queryHandler.handleQuery(src, dest, startDateTimeStr));
  }
}
