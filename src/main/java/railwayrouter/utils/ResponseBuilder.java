package railwayrouter.utils;

import java.util.List;
import java.util.stream.Collectors;
import railwayrouter.models.Route;
import railwayrouter.models.Station;

public class ResponseBuilder {
  public static String buildResponse(Route route) {
    long minutes = route.getMinutes();
    List<Station> stations = route.getStations();
    List<String> stationCodes =
        stations.stream().map(Station::getCode).collect(Collectors.toList());
    String timeOfDay = route.getTimeOfDay().getName();
    int numStations = stations.size();

    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder
        .append(
            String.format(
                "Travel from %s to %s during %s hours",
                stations.get(0).getName(), stations.get(numStations - 1).getName(), timeOfDay))
        .append(String.format("\nTime: %s minutes", minutes))
        .append(String.format("\nRoute: %s\n", stationCodes));

    for (int i = 0; i < numStations; i++) {
      Station cur = stations.get(i);
      if (i < numStations - 1) {
        Station next = stations.get(i + 1);
        if (cur.getName().equals(next.getName()))
          stringBuilder.append(
              String.format(
                  "\nChange from %s line to %s line", cur.getLineCode(), next.getLineCode()));
        else
          stringBuilder.append(
              String.format(
                  "\nTake %s line from %s to %s",
                  cur.getLineCode(), cur.getName(), next.getName()));
      }
    }

    return stringBuilder.toString();
  }
}
