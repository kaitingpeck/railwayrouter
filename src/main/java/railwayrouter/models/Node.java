package railwayrouter.models;

import java.time.LocalDateTime;
import lombok.Getter;

@Getter
public class Node implements Comparable<Node> {
  Station station;
  LocalDateTime arrivalDateTime;

  public Node(Station station, LocalDateTime arrivalDateTime) {
    this.station = station;
    this.arrivalDateTime = arrivalDateTime;
  }

  @Override
  public int compareTo(Node o) {
    return this.arrivalDateTime.compareTo(o.arrivalDateTime);
  }
}
