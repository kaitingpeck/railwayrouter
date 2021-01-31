package railwayrouter.utils;

import java.time.LocalDateTime;
import railwayrouter.models.TimeOfDay;

public class TimeOfDayUtil {
  /**
   * PEAK: Mon-Fri, 0600-0900, 1800-2100 NIGHT: 2200-0559 NONPEAK: all other hours
   *
   * @param arrivalTime
   * @return TimeOfDay enum
   */
  public static TimeOfDay getTimeOfDay(LocalDateTime arrivalTime) {
    int dayOfWeek = arrivalTime.getDayOfWeek().getValue();
    int hour = arrivalTime.getHour();
    int minute = arrivalTime.getMinute();
    if (dayOfWeek >= 1
        && dayOfWeek <= 5
        && ((hour >= 6 && hour <= 8)
            || (hour == 9 && minute == 0)
            || (hour >= 18 && hour <= 20)
            || (hour == 21 && minute == 0))) {
      return TimeOfDay.PEAK;
    } else if (hour >= 22 || hour <= 5) {
      return TimeOfDay.NIGHT;
    } else {
      return TimeOfDay.NONPEAK;
    }
  }
}
