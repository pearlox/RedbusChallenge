import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.time.Duration;
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
    public static String weekendDates = "span[class*='bwoYtA']";
    public static String arrowKey = "div[class*='DayNavigator__CalendarHeader'] > div:nth-child(3) #Layer_1";

    public static void main(String[] args) {

        WebDriverManager.chromedriver().setup();
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
        WebDriver driver = new ChromeDriver(chromeOptions);
        driver.manage().window().maximize();
        driver.manage().deleteAllCookies();

        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(60));
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        String targetMonth = "Dec 2024";

        List<String> weekendDateList = getWeekendDates(driver, targetMonth);
        System.out.println("Weekend List ==> "+weekendDateList);

        driver.quit();
    }

    private static List<String> getWeekendDates(WebDriver driver, String targetMonth) {
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
        List<String> weekendDateList = new ArrayList<>();
        for(WebElement element:weekendDateElements) {
            weekendDateList.add(element.getText().trim());
        }

        return weekendDateList;
    }
}