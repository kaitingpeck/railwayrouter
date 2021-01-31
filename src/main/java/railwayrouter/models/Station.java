package railwayrouter.models;

import java.time.LocalDateTime;
import lombok.EqualsAndHashCode;
import lombok.Getter;

// immutable class
@Getter
@EqualsAndHashCode
public final class Station {
  final int id;
  final String lineCode;
  final int number;
  final String name;
  final LocalDateTime openingDate;

  public Station(int id, String code, String name, LocalDateTime openingDate) {
    this.id = id;
    this.lineCode = code.substring(0, 2);
    this.number = Integer.parseInt(code.substring(2));
    this.name = name;
    this.openingDate = openingDate;
  }

  public String getCode() {
    return lineCode + number;
  }

  @Override
  public String toString() {
    return "Station{"
        + "id='"
        + id
        + '\''
        + ", code="
        + lineCode
        + number
        + ", name='"
        + name
        + '\''
        + '}';
  }
}
