package railwayrouter.models;

import static org.junit.Assert.assertEquals;

import java.time.LocalDate;
import java.time.LocalDateTime;
import org.junit.Test;

public class NodeTest {

  @Test
  public void testCompareToNodeLessThan() {
    Station station = new Station(0, "EW1", "Pasir Ris", LocalDate.of(2021, 2, 21).atStartOfDay());
    LocalDateTime arrivalDateTimeEarlier = LocalDateTime.of(2021, 2, 21, 14, 01);
    LocalDateTime arrivalDateTimeLater = LocalDateTime.of(2021, 2, 21, 16, 01);
    Node nodeLess = new Node(station, arrivalDateTimeEarlier);
    Node nodeMore = new Node(station, arrivalDateTimeLater);
    assert (nodeLess.compareTo(nodeMore) < 0);
  }

  @Test
  public void testCompareToNodeMoreThan() {
    Station station = new Station(0, "EW1", "Pasir Ris", LocalDate.of(2021, 2, 21).atStartOfDay());
    LocalDateTime arrivalDateTimeEarlier = LocalDateTime.of(2021, 2, 21, 14, 01);
    LocalDateTime arrivalDateTimeLater = LocalDateTime.of(2021, 2, 21, 16, 01);
    Node nodeLess = new Node(station, arrivalDateTimeEarlier);
    Node nodeMore = new Node(station, arrivalDateTimeLater);
    assert (nodeMore.compareTo(nodeLess) > 0);
  }

  @Test
  public void testCompareToNodeEquals() {
    Station station = new Station(0, "EW1", "Pasir Ris", LocalDate.of(2021, 2, 21).atStartOfDay());
    Node node1 = new Node(station, LocalDateTime.of(2021, 2, 21, 14, 01));
    Node node2 = new Node(station, LocalDateTime.of(2021, 2, 21, 14, 01));
    assertEquals(0, node1.compareTo(node2));
  }

  @Test
  // different station should not affect comparison of nodes with equal arrival date times
  public void testCompareToNodeDifferentStationResultEquals() {
    Station station1 = new Station(0, "EW1", "Pasir Ris", LocalDate.of(2021, 2, 21).atStartOfDay());
    Station station2 = new Station(0, "EW2", "Tampines", LocalDate.of(2021, 1, 11).atStartOfDay());
    Node node1 = new Node(station1, LocalDateTime.of(2021, 2, 21, 14, 01));
    Node node2 = new Node(station2, LocalDateTime.of(2021, 2, 21, 14, 01));
    assertEquals(0, node1.compareTo(node2));
  }
}
