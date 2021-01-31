package railwayrouter;

import static org.junit.Assert.assertEquals;

import java.io.*;
import java.nio.charset.StandardCharsets;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import railwayrouter.services.QueryHandler;
import railwayrouter.services.RouteFinder;

@RunWith(MockitoJUnitRunner.class)
public class AppIntTest {

  private final InputStream ORIGINAL_STD_IN = System.in;
  private final PrintStream ORIGINAL_STD_OUT = System.out;

  private App app;

  @Before
  public void setUp() {
    RouteFinder routeFinder = new RouteFinder();
    QueryHandler queryHandler = new QueryHandler(routeFinder);
    app = new App(queryHandler);
  }

  @Test
  public void testRun() throws IOException {
    String input = "Buangkok\nKovan\n2021-01-28T07:00\n";
    InputStream stream = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
    OutputStream outContent = new ByteArrayOutputStream();

    System.setIn(stream);
    System.setOut(new PrintStream(outContent));

    app.run();
    String expected =
        "Enter starting station: Enter destination station: Enter travel date time "
            + "(YYYY-MM-DDTHH:mm e.g. 2021-01-28T07:00): "
            + "Travel from Buangkok to Kovan during peak hours\n"
            + "Time: 24 minutes\n"
            + "Route: [NE2, NE3, NE4]\n"
            + "\n"
            + "Take NE line from Buangkok to Hougang\n"
            + "Take NE line from Hougang to Kovan\n";

    assertEquals(expected, outContent.toString());
  }

  @After
  public void tearDown() {
    System.setIn(ORIGINAL_STD_IN);
    System.setOut(ORIGINAL_STD_OUT);
  }
}
