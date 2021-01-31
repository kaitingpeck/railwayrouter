package railwayrouter.models;

import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
public class Route {
  private long minutes;
  private List<Station> stations;
  private TimeOfDay timeOfDay;

  public Route(long minutes, List<Station> stations, TimeOfDay timeOfDay) {
    this.minutes = minutes;
    this.stations = stations;
    this.timeOfDay = timeOfDay;
  }
}
