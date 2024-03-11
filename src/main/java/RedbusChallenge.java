import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RedbusChallenge {

    //    public static String holidayCounter = "//div[@id='onwardCal']//div[contains(@class,'DayNavigator__CalendarHeader')]//div[@class='holiday_count']";
    //    public static String currentMonthName = "//div[@id='onwardCal']//div[contains(@class,'DayNavigator__CalendarHeader')]//div[not(contains(@style,'cursor')) and not(contains(@class,'holiday'))]";
    //    public static String weekendDates = "//div[contains(@class,'DayTiles__CalendarDaysBlock')]//span[contains(@class,'DayTiles__CalendarDaysSpan') and contains(@class,'bwoYtA')]";
    //    public static String arrowKey = "//div[@id='onwardCal']//div[contains(@class,'DayNavigator__CalendarHeader')]//div[not(contains(@style,'cursor'))]/following::div//*[name()='svg']";

    public static String holidayCounter = ".holiday_count";
    public static String currentMonthName = "div[class*='DayNavigator__CalendarHeader'] > div:nth-child(2)";
    public static String weekendDates = "span[class*='bwoYtA'], span[class*='fgdqFw']";
    public static String arrowKey = "div[class*='DayNavigator__CalendarHeader'] > div:nth-child(3) #Layer_1";

    public static void main(String[] args) {

        String targetMonth = "Dec 2025";
        List<String> weekendDateList = getWeekendDates(targetMonth);
        if(!weekendDateList.isEmpty())
            System.out.println("Weekend List ==> "+weekendDateList);

    }

    private static List<String> getWeekendDates(String targetMonth) {

        List<String> weekendDateList = new ArrayList<>();
        boolean status = checkIfTargetMonthIsBehindCurrentMonth(targetMonth);
        if(status) {

            WebDriver driver = getDriver();
            driver.get("https://www.redbus.in/");
//        driver.findElement(By.xpath("//div[@id='onwardCal']")).click();
            driver.findElement(By.cssSelector("#onwardCal")).click();
            WebElement currentMonthElement = driver.findElement(By.cssSelector(currentMonthName));

            String currentMonth = currentMonthElement.getText();
            while(!(currentMonth.contains(targetMonth))) {
//            driver.findElement(By.xpath(arrowKey)).click();
                driver.findElement(By.cssSelector(arrowKey)).click();
                currentMonth = currentMonthElement.getText();
            }
            System.out.println("Current Month ==> "+currentMonth.split("\\r?\\n")[0]);
            List<WebElement> holidayElements = driver.findElements(By.cssSelector(holidayCounter));
            if(!holidayElements.isEmpty()) {
                System.out.println("Holidays ==> "+holidayElements.get(0).getText());
            } else {
                System.out.println("Holidays ==> No holidays");
            }

//        List<WebElement> weekendDateElements = driver.findElements(By.xpath(weekendDates));
            List<WebElement> weekendDateElements = driver.findElements(By.cssSelector(weekendDates));

            for(WebElement element:weekendDateElements) {
                weekendDateList.add(element.getText().trim());
            }

            driver.quit();
        } else {
            System.out.println("Provided month is behind current month");
        }

        return weekendDateList;
    }

    private static WebDriver getDriver() {
        WebDriverManager.chromedriver().setup();
        ChromeOptions chromeOptions = getChromeOptions();
        WebDriver driver = new ChromeDriver(chromeOptions);
        driver.manage().window().maximize();
        driver.manage().deleteAllCookies();

        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(60));
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        return driver;
    }

    private static ChromeOptions getChromeOptions() {
        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.addArguments("--start-maximized");
        chromeOptions.addArguments("--window-size=1920, 1080");
        chromeOptions.addArguments("--disable-infobars");
        Map<String,Object> prefs = new HashMap<>();
        Map<String,Object> profile = new HashMap<>();
        Map<String,Object> contentSettings = new HashMap<>();
        contentSettings.put("geolocation", 2);
        contentSettings.put("notifications", 2);
        profile.put("managed_default_content_settings", contentSettings);
        prefs.put("profile", profile);
        chromeOptions.setExperimentalOption("prefs", prefs);
        return chromeOptions;
    }

    private static boolean checkIfTargetMonthIsBehindCurrentMonth(String targetMonth) {
        DateTimeFormatter dateTimeFormatter = new DateTimeFormatterBuilder().appendPattern("MMM uuuu")
                .parseDefaulting(ChronoField.DAY_OF_MONTH,1).toFormatter();
        LocalDate localDate = LocalDate.parse(targetMonth,dateTimeFormatter);
        LocalDate today = LocalDate.of(LocalDate.now().getYear(),LocalDate.now().getMonth(),1);
        return today.isBefore(localDate) || today.isEqual(localDate);
    }
}