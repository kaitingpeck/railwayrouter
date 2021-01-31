package railwayrouter.models;

import java.util.Map;

public class Constants {
  private static final String LINE_CHANGE_FIELD_NAME = "lineChange";
  private static final Map<TimeOfDay, Map<String, Integer>> FREQUENCY_MAP =
      Map.of(
          TimeOfDay.PEAK,
              Map.of(
                  "NS",
                  12,
                  "NE",
                  12,
                  "EW",
                  10,
                  "CG",
                  10,
                  "CC",
                  10,
                  "CE",
                  10,
                  "DT",
                  10,
                  "TE",
                  10,
                  LINE_CHANGE_FIELD_NAME,
                  15),
          TimeOfDay.NIGHT,
              Map.of(
                  "NS",
                  10,
                  "NE",
                  10,
                  "EW",
                  10,
                  "CG",
                  Integer.MAX_VALUE,
                  "CC",
                  10,
                  "CE",
                  Integer.MAX_VALUE,
                  "DT",
                  Integer.MAX_VALUE,
                  "TE",
                  8,
                  LINE_CHANGE_FIELD_NAME,
                  10),
          TimeOfDay.NONPEAK,
              Map.of(
                  "NS",
                  10,
                  "NE",
                  10,
                  "EW",
                  10,
                  "CG",
                  10,
                  "CC",
                  10,
                  "CE",
                  Integer.MAX_VALUE,
                  "DT",
                  8,
                  "TE",
                  8,
                  LINE_CHANGE_FIELD_NAME,
                  10));

  public static String getLineChangeFieldName() {
    return LINE_CHANGE_FIELD_NAME;
  }

  public static Map<TimeOfDay, Map<String, Integer>> getFrequencyMap() {
    return FREQUENCY_MAP;
  }
}
