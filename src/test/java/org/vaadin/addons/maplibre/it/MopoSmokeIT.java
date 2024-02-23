package org.vaadin.addons.maplibre.it;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import in.virit.mopo.Mopo;
import jakarta.annotation.PostConstruct;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.util.Arrays;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class MopoSmokeIT {

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
//                        .setHeadless(false)
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
    public void smokeTest() {
        mopo.getViewsReportedByDevMode(browser, rootUrl).forEach(viewName -> {
            String url = rootUrl + viewName;
            page.navigate(url);
            mopo.assertNoJsErrors();
            System.out.println("Checked %s and it contained no JS errors.".formatted(viewName));
        });
    }

    @Test
    public void osmViaMapTiler() {
        page.navigate(rootUrl + "osmviamaptiler");

        // Assert that OpenLibre GL has initialized certain internal parts
        assertThat(page.locator("canvas")).isVisible();
        // PW checks aria-labels
        assertThat(page.getByLabel("Map marker")).isVisible();
        assertThat(page.getByLabel("Zoom in")).isVisible();

        mopo.click(page.getByText("Plot yourself"));

        page.getByLabel("Map marker").last().click();

        assertThat(page.locator("vaadin-notification-card").first())
                .containsText("That's you!");

    }

}
