package railwayrouter.utils;

import static org.junit.Assert.assertEquals;

import java.time.LocalDate;
import java.util.List;
import org.junit.Test;
import railwayrouter.models.Route;
import railwayrouter.models.Station;
import railwayrouter.models.TimeOfDay;

public class ResponseBuilderTest {

  @Test
  public void testBuildResponseWithoutLineChange() {
    Station src = new Station(0, "EW1", "Pasir Ris", LocalDate.of(2021, 2, 21).atStartOfDay());
    Station between = new Station(1, "EW2", "Tampines", LocalDate.of(2021, 2, 21).atStartOfDay());
    Station dest = new Station(2, "EW4", "Simei", LocalDate.of(2021, 2, 21).atStartOfDay());
    Route route = new Route(100, List.of(src, between, dest), TimeOfDay.PEAK);
    assertEquals(
        "Travel from Pasir Ris to Simei during peak hours\n"
            + "Time: 100 minutes\n"
            + "Route: [EW1, EW2, EW4]\n"
            + "\n"
            + "Take EW line from Pasir Ris to Tampines\n"
            + "Take EW line from Tampines to Simei",
        ResponseBuilder.buildResponse(route));
  }

  @Test
  public void testBuildResponseWithLineChange() {
    Station src = new Station(0, "EW1", "Pasir Ris", LocalDate.of(2021, 2, 21).atStartOfDay());
    Station between = new Station(1, "EW2", "Tampines", LocalDate.of(2021, 2, 21).atStartOfDay());
    Station between2 = new Station(2, "NS4", "Tampines", LocalDate.of(2021, 2, 21).atStartOfDay());
    Station dest = new Station(3, "NS5", "Simei", LocalDate.of(2021, 2, 21).atStartOfDay());
    Route route = new Route(100, List.of(src, between, between2, dest), TimeOfDay.PEAK);
    assertEquals(
        "Travel from Pasir Ris to Simei during peak hours\n"
            + "Time: 100 minutes\n"
            + "Route: [EW1, EW2, NS4, NS5]\n"
            + "\n"
            + "Take EW line from Pasir Ris to Tampines\n"
            + "Change from EW line to NS line\n"
            + "Take NS line from Tampines to Simei",
        ResponseBuilder.buildResponse(route));
  }
}
