package Base;

import com.thoughtworks.gauge.AfterScenario;
import com.thoughtworks.gauge.BeforeScenario;

import org.monte.screenrecorder.ScreenRecorder;
import org.openqa.selenium.Platform;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import org.monte.media.math.Rational;
import org.monte.media.Format;

import static org.monte.media.AudioFormatKeys.*;
import static org.monte.media.VideoFormatKeys.*;

import java.awt.*;
import java.io.File;
import java.io.IOException;


public class BaseTest {
    protected static WebDriver driver;
    public static String baseUrl = "http://automationpractice.com/";
    public static String Path = "web_driver/chromedriver";
    private static ScreenRecorder screenRecorder;

    @BeforeScenario
    public void setUp() throws IOException, AWTException {
        ChromeOptions options = new ChromeOptions();
        File targetFolder = new File(System.getProperty("user.dir") + "\\ScreenRecord");

        GraphicsConfiguration gc = GraphicsEnvironment
                .getLocalGraphicsEnvironment()
                .getDefaultScreenDevice()
                .getDefaultConfiguration();

        screenRecorder = new ScreenRecorder(gc,
                gc.getBounds(),
                new Format(MediaTypeKey, MediaType.FILE, MimeTypeKey, MIME_AVI),
                new Format(MediaTypeKey, MediaType.VIDEO, EncodingKey, ENCODING_AVI_TECHSMITH_SCREEN_CAPTURE,
                        CompressorNameKey, ENCODING_AVI_TECHSMITH_SCREEN_CAPTURE,
                        DepthKey, 24, FrameRateKey, Rational.valueOf(15),
                        QualityKey, 1.0f,
                        KeyFrameIntervalKey, 15 * 60),
                new Format(MediaTypeKey, MediaType.VIDEO, EncodingKey, "black", FrameRateKey, Rational.valueOf(30)),
                null,
                new File(String.valueOf(targetFolder)));

        screenRecorder.start();

        if (Platform.getCurrent().toString().contains("WIN")) {
            Path = "web_driver/chromedriver.exe";
        }
        System.setProperty("webdriver.chrome.driver", Path);
        driver = new ChromeDriver(options);
        driver.get(baseUrl);
        driver.manage().window().maximize();
    }

    @AfterScenario
    public void tearDown() throws IOException {
        screenRecorder.stop();

        driver.quit();
    }
}
