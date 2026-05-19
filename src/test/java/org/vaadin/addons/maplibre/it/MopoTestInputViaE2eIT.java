package org.vaadin.addons.maplibre.it;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import in.virit.mopo.Mopo;
import jakarta.annotation.PostConstruct;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.util.Arrays;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/**
 * Tests that fields can input data via E2E tests
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class MopoTestInputViaE2eIT {

    @LocalServerPort
    private int port;

    private String rootUrl;

    static Playwright playwright = Playwright.create();

    private Browser browser;
    private Page page;
    private Mopo mopo;

    // fake location for Playwright, close enough so animation is shorter :-)
    double lat = 60.452;
    double lon = 22.290;

    @PostConstruct
    public void init() {
        rootUrl = "http://localhost:" + port + "/";
    }

    @BeforeEach
    public void setup() {
        browser = playwright.chromium()
                .launch(new BrowserType.LaunchOptions()
                        .setHeadless(false)
//                        .setDevtools(true)
                );


        // Define geolocation and permissions for Playwright to use
        BrowserContext context = browser.newContext(new Browser.NewContextOptions()
                .setGeolocation(lat, lon)
                .setPermissions(Arrays.asList("geolocation")));
        page = context.newPage();
        mopo = new Mopo(page);
    }

    @AfterEach
    public  void closePlaywright() {
        page.close();
        browser.close();
    }

    @Test
    public void osmViaMapTiler() {
        page.navigate(rootUrl + "bindercompatiblefields");

        Locator polygoninput = page.locator("vaadin-custom-field").first();

        // now simulate user input of polygon via "JS API"
        String geojson = """
                {
                  "type": "Polygon",
                  "coordinates": [
                    [
                      [
                        -2.63671875,
                        31.203404950917374
                      ],
                      [
                        -1.23046875,
                        1.58183026396064
                      ],
                      [
                        38.84765625,
                        1.9332268264771244
                      ],
                      [
                        35.33203125,
                        35.88905007936091
                      ],
                      [
                        14.58984375,
                        38.41055825094608
                      ],
                      [
                        -2.63671875,
                        31.203404950917374
                      ]
                    ]
                  ]
                }
                """;

        polygoninput.evaluate("""
                (el, geojson) => {
                    el.$server.inputGeoJson(geojson);
                }
                """, geojson);

        page.getByText("Display DTO value").click();

        assertThat(page.getByText("polygon:POLYGON ((-2.63671875 31.203404950917374")).isVisible();


    }

}
