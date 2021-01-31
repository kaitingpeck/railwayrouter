package railwayrouter.models;

import lombok.Getter;

@Getter
public enum TimeOfDay {
  PEAK("peak"),
  NIGHT("night"),
  NONPEAK("non-peak");

  private final String name;

  TimeOfDay(String name) {
    this.name = name;
  }
}
