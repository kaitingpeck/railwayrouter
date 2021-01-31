package railwayrouter.testutils;

import java.time.LocalDate;
import railwayrouter.models.Station;

public class IntTestUtils {
  public static final Station BUANGKOK =
      new Station(0, "NE2", "Buangkok", LocalDate.of(2021, 2, 21).atStartOfDay());
  public static final Station HOUGANG =
      new Station(1, "NE3", "Hougang", LocalDate.of(2021, 2, 21).atStartOfDay());
  public static final Station KOVAN =
      new Station(2, "NE4", "Kovan", LocalDate.of(2021, 2, 21).atStartOfDay());
}
