package railwayrouter.services;

import static railwayrouter.models.Constants.getFrequencyMap;
import static railwayrouter.models.Constants.getLineChangeFieldName;
import static railwayrouter.utils.TimeOfDayUtil.getTimeOfDay;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import railwayrouter.models.Node;
import railwayrouter.models.Route;
import railwayrouter.models.Station;
import railwayrouter.models.TimeOfDay;

public class RouteFinder {
  private static final Map<TimeOfDay, Map<String, Integer>> FREQUENCY_MAP =
      Collections.unmodifiableMap(getFrequencyMap());
  private static final String LINE_CHANGE_FIELD_NAME = getLineChangeFieldName();

  /**
   * Returns the shortest route from stations in srcStations to stations in destStations. e.g. if
   * srcStations = [NE1, CC2], destStations = [CC3, NS2], returns the shortest route from either NE1
   * -> CC3, NE1 -> NS2, CC2 -> CC3 or CC2 -> NS2
   *
   * @param srcStations
   * @param destStations
   * @param startDateTime
   * @param adjList
   * @return shortest {@code Route} from a station in srcStations to a station in destStations
   */
  public Optional<Route> findRoute(
      List<Station> srcStations,
      List<Station> destStations,
      LocalDateTime startDateTime,
      List<List<Station>> adjList) {
    Route route = null;
    for (Station src : srcStations) {
      for (Station dest : destStations) {
        Route cur = dijkstra(src, dest, startDateTime, adjList);
        if (route == null || (cur != null && cur.getMinutes() <= route.getMinutes())) {
          route = cur;
        }
      }
    }
    return Optional.ofNullable(route);
  }

  /**
   * Runs dijkstra algorithm from src, and returns the shortest route from src to dest
   *
   * @param src
   * @param dest
   * @param startDateTime
   * @param adjList
   * @return shortest {@code Route} from src to dest
   */
  protected Route dijkstra(
      Station src, Station dest, LocalDateTime startDateTime, List<List<Station>> adjList) {
    int numStations = adjList.size();
    LocalDateTime[] arrivalDateTimes = new LocalDateTime[numStations];
    Station[] prec = new Station[numStations]; // store preceding station in shortest path
    Set<Integer> solved = new HashSet<>();
    PriorityQueue<Node> pq = new PriorityQueue<>(numStations);

    for (int i = 0; i < numStations; i++) {
      arrivalDateTimes[i] = LocalDateTime.MAX;
    }

    pq.add(new Node(src, startDateTime));
    arrivalDateTimes[src.getId()] = startDateTime;

    while (!pq.isEmpty() && solved.size() < numStations) {
      Station cur = pq.remove().getStation();
      solved.add(cur.getId());

      // relax outgoing edges
      List<Station> neighbours = adjList.get(cur.getId());
      for (int i = 0; i < neighbours.size(); i++) {
        Station v = neighbours.get(i);
        if (!solved.contains(v.getId())) {
          LocalDateTime arrivalDateTimeCur = arrivalDateTimes[cur.getId()];
          int cost = getCost(cur, v, arrivalDateTimeCur); // cost of travelling from cur -> v
          if (cost != Integer.MAX_VALUE) { // if cost == Integer.MAX_VALUE, line is not operating
            LocalDateTime arrivalDateTimeV = arrivalDateTimes[cur.getId()].plusMinutes(cost);

            if (arrivalDateTimeV.isBefore(arrivalDateTimes[v.getId()])) {
              arrivalDateTimes[v.getId()] = arrivalDateTimeV;
              prec[v.getId()] = cur;
            }
          }
          pq.add(new Node(v, arrivalDateTimes[v.getId()]));
        }
      }
    }
    return getRoute(dest, startDateTime, arrivalDateTimes, prec);
  }

  /**
   * Gets the route to {@code dest}, from a list of arrival date times {@code arrivalDateTimes}
   *
   * @param dest
   * @param startDateTime
   * @param arrivalDateTimes
   * @param prec
   * @return shortest {@code Route} to dest
   */
  protected Route getRoute(
      Station dest, LocalDateTime startDateTime, LocalDateTime[] arrivalDateTimes, Station[] prec) {
    LocalDateTime arrivalDateTime = arrivalDateTimes[dest.getId()];
    if (arrivalDateTime.equals(LocalDateTime.MAX))
      return null; // no possible route from src to dest

    long minutes = Duration.between(startDateTime, arrivalDateTime).toMinutes();

    TimeOfDay timeOfDay = getTimeOfDay(startDateTime);
    Stack<Station> stack = new Stack<>();
    Station cur = dest;
    while (cur != null) {
      stack.push(cur);
      cur = prec[cur.getId()];
    }
    List<Station> stations = new ArrayList<>();
    while (!stack.isEmpty()) {
      stations.add(stack.pop());
    }
    return new Route(minutes, stations, timeOfDay);
  }

  /**
   * Computes time required to travel from cur -> next. If unable to get there, return
   * Integer.MAX_VALUE Assumes that cur and next are either valid adjacent stations (next is after
   * cur in the currently open stations), or a line transfer.
   *
   * @param cur
   * @param next
   * @param arrivalTime
   * @return time taken to travel from cur -> next
   */
  protected int getCost(Station cur, Station next, LocalDateTime arrivalTime) {
    TimeOfDay timeOfDay = getTimeOfDay(arrivalTime);
    Map<String, Integer> frequency = FREQUENCY_MAP.get(timeOfDay);
    String curLine = cur.getLineCode();
    String nextLine = next.getLineCode();
    if (curLine.equals(nextLine)) {
      return frequency.get(curLine);
    } else {
      // line transfer
      return frequency.get(LINE_CHANGE_FIELD_NAME);
    }
  }
}
