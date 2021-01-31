package railwayrouter.services;

import static railwayrouter.utils.DataProcessor.*;

import com.opencsv.exceptions.CsvValidationException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;
import railwayrouter.models.Route;
import railwayrouter.models.Station;
import railwayrouter.utils.ResponseBuilder;

/** Handles the query given the query parameters and returns the response */
public class QueryHandler {
  private static final Logger LOGGER = Logger.getLogger(QueryHandler.class.getName());

  protected static final String INVALID_DATE_TIME_STRING = "Invalid date time entered.";
  protected static final String INVALID_STATION_STRING =
      "Source or destination station(s) does not exist at this point in time";
  protected static final String ROUTE_NOT_FOUND_STRING = "No route found";
  private final RouteFinder routeFinder;

  public QueryHandler(RouteFinder routeFinder) {
    this.routeFinder = routeFinder;
  }

  /**
   * Given string inputs, process in the inputs and generate the response in required format
   *
   * @param src
   * @param dest
   * @param startDateTimeStr
   * @return response string
   */
  public String handleQuery(String src, String dest, String startDateTimeStr)
      throws CsvValidationException {
    Optional<LocalDateTime> startDateTime = getDateTime(startDateTimeStr);
    if (startDateTime.isEmpty()) {
      LOGGER.info(String.format("Start date time %s is invalid", startDateTimeStr));
      return INVALID_DATE_TIME_STRING;
    }

    List<Station> stations = getOpenStations(startDateTime.get());
    Map<String, List<Station>> lineCodeToStationsMap = generateLineCodeToStationsMap(stations);
    Map<String, List<Station>> nameToStationsMap = generateNameToStationsMap(stations);
    List<List<Station>> adjList = generateAdjList(nameToStationsMap, lineCodeToStationsMap);

    List<Station> srcStations = getStations(src, nameToStationsMap);
    List<Station> destStations = getStations(dest, nameToStationsMap);

    // validate stations input
    if (srcStations.isEmpty() || destStations.isEmpty()) {
      LOGGER.info(String.format("Station %s or %s is invalid", src, dest));
      return INVALID_STATION_STRING;
    }

    Optional<Route> route =
        routeFinder.findRoute(srcStations, destStations, startDateTime.get(), adjList);
    if (route.isEmpty()) {
      LOGGER.info(String.format("Route is not found for %s to %s", src, dest));
      return ROUTE_NOT_FOUND_STRING;
    }

    return ResponseBuilder.buildResponse(route.get());
  }

  /**
   * Retrieves the stations with the name {@code source}. If doesn't exist, returns an empty list.
   *
   * @param source
   * @param nameToStationsMap
   * @return list of stations named {@code source}, else returns an empty list
   */
  protected List<Station> getStations(String source, Map<String, List<Station>> nameToStationsMap) {
    return nameToStationsMap.getOrDefault(source, new ArrayList<>());
  }

  /**
   * Returns an Optional object containing the date time, if it's present. Else returns an empty
   * optional
   *
   * @param dateTimeStr
   */
  protected Optional<LocalDateTime> getDateTime(String dateTimeStr) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
    try {
      return Optional.of(LocalDateTime.parse(dateTimeStr, formatter));
    } catch (DateTimeParseException e) {
      LOGGER.info(String.format("Datetime string %s cannot be parsed", dateTimeStr));
      return Optional.empty();
    }
  }
}
