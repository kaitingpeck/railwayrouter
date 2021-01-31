package railwayrouter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import railwayrouter.services.QueryHandler;
import railwayrouter.services.RouteFinder;

/**
 * Based on user input of source, destination station and travel start date time, generate the
 * fastest possible route. The algorithm only considers the stations currently open at the time. If
 * any of the inputs are invalid, a response will be returned to inform that to the user. Source and
 * destination inputs are case-insensitive, but space-sensitive.
 *
 * <p>Assume that a station can be ignored if it's built only after the query date time, since it is
 * not known whether it has to pass through the unbuilt station at the time, depending on how the
 * station map is planned and constructed at the time.
 *
 * <p>The time of day (peak, non-peak, night hours) in the response refers to the start date time.
 * During the duration of travel, the time of day may change (e.g. peak to non-peak). The result
 * takes this into consideration.
 */
public class App {

  private final QueryHandler queryHandler;
  protected final String ERROR_MESSAGE = "Unable to process request. Terminating application now.";

  public App(QueryHandler queryHandler) {
    this.queryHandler = queryHandler;
  }

  public static void main(String[] args) throws IOException {
    RouteFinder routeFinder = new RouteFinder();
    QueryHandler queryHandler = new QueryHandler(routeFinder);
    App app = new App(queryHandler);
    app.run();
  }

  public void run() throws IOException {
    BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

    System.out.print("Enter starting station: ");
    String source = reader.readLine();
    System.out.print("Enter destination station: ");
    String destination = reader.readLine();
    System.out.print("Enter travel date time (YYYY-MM-DDTHH:mm e.g. 2021-01-28T07:00): ");
    String startDateTimeStr = reader.readLine();

    try {
      String response = queryHandler.handleQuery(source, destination, startDateTimeStr);
      System.out.println(response);
    } catch (Exception e) {
      System.out.println(ERROR_MESSAGE);
    }
  }
}
