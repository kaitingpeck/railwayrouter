package railwayrouter.utils;

import static org.junit.Assert.assertEquals;
import static railwayrouter.models.TimeOfDay.*;
import static railwayrouter.utils.TimeOfDayUtil.getTimeOfDay;

import java.time.LocalDateTime;
import org.junit.Test;

public class TimeOfDayUtilTest {
  @Test
  public void testGetTimeOfDayPeak() {
    LocalDateTime dateTime = LocalDateTime.of(2021, 01, 28, 7, 0);
    assertEquals(PEAK, getTimeOfDay(dateTime));
  }

  @Test
  public void testGetTimeOfDayPeak2() {
    LocalDateTime dateTime = LocalDateTime.of(2021, 01, 28, 20, 0);
    assertEquals(PEAK, getTimeOfDay(dateTime));
  }

  @Test
  public void testGetTimeOfDayPeakFirstEndpoint() {
    LocalDateTime dateTime = LocalDateTime.of(2021, 01, 28, 6, 0);
    assertEquals(PEAK, getTimeOfDay(dateTime));
  }

  @Test
  public void testGetTimeOfDayPeakHourSecondEndpoint() {
    LocalDateTime dateTime = LocalDateTime.of(2021, 01, 28, 9, 0);
    assertEquals(PEAK, getTimeOfDay(dateTime));
  }

  @Test
  public void testGetTimeOfDayPeakHourThirdEndpoint() {
    LocalDateTime dateTime = LocalDateTime.of(2021, 01, 28, 18, 0);
    assertEquals(PEAK, getTimeOfDay(dateTime));
  }

  @Test
  public void testGetTimeOfDayPeakHourFourthEndpoint() {
    LocalDateTime dateTime = LocalDateTime.of(2021, 01, 28, 21, 0);
    assertEquals(PEAK, getTimeOfDay(dateTime));
  }

  @Test
  public void testGetTimeOfDayPeakMonday() {
    LocalDateTime dateTime = LocalDateTime.of(2021, 01, 25, 7, 0);
    assertEquals(PEAK, getTimeOfDay(dateTime));
  }

  @Test
  public void testGetTimeOfDayPeakFriday() {
    LocalDateTime dateTime = LocalDateTime.of(2021, 01, 29, 7, 0);
    assertEquals(PEAK, getTimeOfDay(dateTime));
  }

  @Test
  public void testGetTimeOfDayNightHour1() {
    LocalDateTime dateTime = LocalDateTime.of(2021, 01, 28, 23, 0);
    assertEquals(NIGHT, getTimeOfDay(dateTime));
  }

  @Test
  public void testGetTimeOfDayNightHour2() {
    LocalDateTime dateTime = LocalDateTime.of(2021, 01, 28, 5, 0);
    assertEquals(NIGHT, getTimeOfDay(dateTime));
  }

  @Test
  public void testGetTimeOfDayNightHourFirstEndpoint() {
    LocalDateTime dateTime = LocalDateTime.of(2021, 01, 28, 22, 0);
    assertEquals(NIGHT, getTimeOfDay(dateTime));
  }

  @Test
  public void testGetTimeOfDayNightHourSecondEndpoint() {
    LocalDateTime dateTime = LocalDateTime.of(2021, 01, 28, 5, 59);
    assertEquals(NIGHT, getTimeOfDay(dateTime));
  }

  @Test
  public void testGetTimeOfDayNonPeakSun() {
    LocalDateTime dateTime = LocalDateTime.of(2021, 01, 31, 15, 0);
    assertEquals(NONPEAK, getTimeOfDay(dateTime));
  }

  @Test
  public void testGetTimeOfDayNonPeakSat() {
    LocalDateTime dateTime = LocalDateTime.of(2021, 01, 30, 15, 0);
    assertEquals(NONPEAK, getTimeOfDay(dateTime));
  }

  @Test
  public void testGetTimeOfDayNonPeakHourFirstEndpoint() {
    LocalDateTime dateTime = LocalDateTime.of(2021, 01, 28, 9, 1);
    assertEquals(NONPEAK, getTimeOfDay(dateTime));
  }

  @Test
  public void testGetTimeOfDayNonPeakHourSecondEndpoint() {
    LocalDateTime dateTime = LocalDateTime.of(2021, 01, 28, 17, 59);
    assertEquals(NONPEAK, getTimeOfDay(dateTime));
  }

  @Test
  public void testGetTimeOfDayNonPeakHourThirdEndpoint() {
    LocalDateTime dateTime = LocalDateTime.of(2021, 01, 28, 21, 1);
    assertEquals(NONPEAK, getTimeOfDay(dateTime));
  }

  @Test
  public void testGetTimeOfDayNonPeakHourFourthEndpoint() {
    LocalDateTime dateTime = LocalDateTime.of(2021, 01, 28, 21, 59);
    assertEquals(NONPEAK, getTimeOfDay(dateTime));
  }

  @Test
  public void testGetTimeOfDayNonPeakHourBetween1() {
    LocalDateTime dateTime = LocalDateTime.of(2021, 01, 28, 21, 30);
    assertEquals(NONPEAK, getTimeOfDay(dateTime));
  }

  @Test
  public void testGetTimeOfDayNonPeakHourBetween3() {
    LocalDateTime dateTime = LocalDateTime.of(2021, 01, 28, 15, 0);
    assertEquals(NONPEAK, getTimeOfDay(dateTime));
  }
}
