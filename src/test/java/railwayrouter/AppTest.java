package railwayrouter;

import static org.junit.Assert.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;

import com.opencsv.exceptions.CsvValidationException;
import java.io.*;
import java.nio.charset.StandardCharsets;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import railwayrouter.services.QueryHandler;

@RunWith(MockitoJUnitRunner.class)
public class AppTest {

  private InputStream ORIGINAL_STD_IN = System.in;
  private PrintStream ORIGINAL_STD_OUT = System.out;

  private App app;
  @Mock private QueryHandler mockQueryHandler;

  @Before
  public void setUp() {
    app = new App(mockQueryHandler);
  }

  @Test
  public void testRun() throws IOException, CsvValidationException {
    String input = "Buangkok\nKovan\n2021-01-28T07:00\n";
    InputStream stream = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
    OutputStream outContent = new ByteArrayOutputStream();

    System.setIn(stream);
    System.setOut(new PrintStream(outContent));
    String testResponse = "Test response";
    String expected =
        "Enter starting station: "
            + "Enter destination station: "
            + "Enter travel date time (YYYY-MM-DDTHH:mm e.g. 2021-01-28T07:00): "
            + testResponse
            + "\n";

    doReturn(testResponse)
        .when(mockQueryHandler)
        .handleQuery("Buangkok", "Kovan", "2021-01-28T07:00");

    app.run();

    assertEquals(expected, outContent.toString());
  }

  @Test
  public void testRunExceptionThrownErrorStringReturned()
      throws IOException, CsvValidationException {
    String input = "Buangkok\nKovan\n2021-01-28T07:00\n";
    InputStream stream = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
    OutputStream outContent = new ByteArrayOutputStream();

    System.setIn(stream);
    System.setOut(new PrintStream(outContent));

    doThrow(new CsvValidationException("test exception"))
        .when(mockQueryHandler)
        .handleQuery("Buangkok", "Kovan", "2021-01-28T07:00");

    app.run();
    String expected =
        "Enter starting station: "
            + "Enter destination station: "
            + "Enter travel date time (YYYY-MM-DDTHH:mm e.g. 2021-01-28T07:00): "
            + "Unable to process request. Terminating application now.\n";
    assertEquals(expected, outContent.toString());
  }

  @After
  public void tearDown() {
    System.setIn(ORIGINAL_STD_IN);
    System.setOut(ORIGINAL_STD_OUT);
  }
}
