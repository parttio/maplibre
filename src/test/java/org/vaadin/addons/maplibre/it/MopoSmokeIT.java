package org.vaadin.addons.maplibre.it;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import in.virit.mopo.Mopo;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class MopoSmokeIT {

    private final int port = 8080;

    private final String rootUrl = "http://localhost:" + port + "/";

    static Playwright playwright = Playwright.create();

    static {
    }

    private Browser browser;
    private Page page;
    private Mopo mopo;

    @BeforeEach
    public void setup() {
        browser = playwright.chromium()
                .launch(new BrowserType.LaunchOptions()
//                        .setHeadless(false)
//                        .setDevtools(true)
                );

        page = browser.newPage();
        page.setDefaultTimeout(5000); // die faster if needed
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
    public void osmViaMapTilerCheckInternalElementsExist() {
        page.navigate(rootUrl + "osmviamaptiler");

        // Assert that OpenLibre GL has initialized certain internal parts
        assertThat(page.locator("canvas")).isVisible();
        // PW checks aria-labels
        assertThat(page.getByLabel("Map marker")).isVisible();
        assertThat(page.getByLabel("Zoom in")).isVisible();
    }

}
