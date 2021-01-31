package railwayrouter.testutils;

import java.time.LocalDateTime;

public class TestUtils {
  public static LocalDateTime VALID_PEAK_DATE_TIME = LocalDateTime.of(2020, 12, 28, 7, 0);
  public static LocalDateTime VALID_NONPEAK_DATE_TIME = LocalDateTime.of(2020, 12, 28, 12, 0);
  public static LocalDateTime VALID_NIGHT_DATE_TIME = LocalDateTime.of(2020, 12, 28, 4, 0);
  public static LocalDateTime VALID_10_MINS_BEFORE_NONPEAK_DATE_TIME =
      LocalDateTime.of(2021, 1, 25, 20, 50);
  public static LocalDateTime VALID_10_MINS_BEFORE_NIGHT_DATE_TIME =
      LocalDateTime.of(2021, 1, 25, 21, 50);
  public static LocalDateTime VALID_3_MINS_BEFORE_PEAK_DATE_TIME =
      LocalDateTime.of(2021, 1, 25, 5, 57);
  public static LocalDateTime VALID_10_MINS_NONPEAK_TO_PEAK_DATE_TIME =
      LocalDateTime.of(2021, 1, 25, 17, 50);
}
