package test;

import java.util.List;
import static org.hamcrest.CoreMatchers.is;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class CarsTest1 {

    private static final int WAIT_MAX = 10;
    static WebDriver driver;

    @BeforeClass
    public static void setup() {
        /*########################### IMPORTANT ######################*/
 /*## Change this, according to your own OS and location of driver(s) ##*/
 /*############################################################*/
        System.setProperty("webdriver.gecko.driver", "D:\\Java\\SeleniumExerciseGettingStarted-master\\geckodriver.exe");
        System.setProperty("webdriver.chrome.driver", "D:\\Java\\SeleniumExerciseGettingStarted-master\\chromedriver.exe");

        //Reset Database
        com.jayway.restassured.RestAssured.given().get("http://localhost:3000/reset");

        //select driver
        driver = new ChromeDriver();
        driver.get("http://localhost:3000");
    }

    @AfterClass
    public static void tearDown() {
        driver.quit();
        //Reset Database 
        com.jayway.restassured.RestAssured.given().get("http://localhost:3000/reset");
    }

    @Test
    //Verify that page is loaded and all expected data are visible
    public void test1() throws Exception {
        (new WebDriverWait(driver, WAIT_MAX)).until((ExpectedCondition<Boolean>) (WebDriver d) -> {
            WebElement e = d.findElement(By.tagName("tbody"));
            List<WebElement> rows = e.findElements(By.tagName("tr"));
            Assert.assertThat(rows.size(), is(5));
            return true;
        });
    }

    @Test
    //Verify the filter functionality part 1
    public void test2() throws Exception {
        //No need to WAIT, since we are running test in a fixed order, we know the DOM is ready (because of the wait in test1)
        WebElement element = driver.findElement(By.id("filter"));
        element.sendKeys("2002");
        (new WebDriverWait(driver, WAIT_MAX)).until((ExpectedCondition<Boolean>) (WebDriver d) -> {
            WebElement e = d.findElement(By.tagName("tbody"));
            List<WebElement> rows = e.findElements(By.tagName("tr"));
            Assert.assertThat(rows.size(), is(2));
            return true;
        });
    }

    @Test
    //Verify the filter functionality part 2
    public void test3() throws Exception {
        WebElement element = driver.findElement(By.id("filter"));
        element.clear(); // clear the elements
        element.sendKeys(" "); // send space to ensure that angulars apply function is triggered
        (new WebDriverWait(driver, WAIT_MAX)).until((ExpectedCondition<Boolean>) (WebDriver d) -> {
            WebElement e = d.findElement(By.tagName("tbody"));
            List<WebElement> rows = e.findElements(By.tagName("tr"));
            Assert.assertThat(rows.size(), is(5));
            return true;
        });
    }

    @Test
    //Verify the sort by year button
    public void test4() throws Exception {
        WebElement element = driver.findElement(By.id("h_year"));
        element.click();
        (new WebDriverWait(driver, WAIT_MAX)).until((ExpectedCondition<Boolean>) (WebDriver d) -> {
            WebElement e = d.findElement(By.tagName("tbody"));
            List<WebElement> rows = e.findElements(By.tagName("tr"));

            WebElement tdFirst = rows.get(0).findElements(By.tagName("td")).get(0);
            WebElement tdLast = rows.get(rows.size() - 1).findElements(By.tagName("td")).get(0);

            Assert.assertThat(tdFirst.getText(), is("938"));
            Assert.assertThat(tdLast.getText(), is("940"));
            return true;
        });
    }

    @Test
    //Verify the edit element function
    public void test5() throws Exception {
        WebElement e = driver.findElement(By.tagName("tbody"));
        List<WebElement> rows = e.findElements(By.tagName("tr"));
        WebElement carToEdit = null;

        for (WebElement row : rows) {
            if (row.findElements(By.tagName("td")).get(0).getText().equals("938")) {
                carToEdit = row;
            }
        }
        List<WebElement> tds = carToEdit.findElements(By.tagName("td"));
        WebElement editBtn = tds.get(tds.size() - 1).findElements(By.tagName("a")).get(0);
        editBtn.click();

        (new WebDriverWait(driver, WAIT_MAX)).until((ExpectedCondition<Boolean>) (WebDriver d) -> {
            WebElement desc = d.findElement(By.id("description"));
            desc.clear();
            desc.sendKeys("Cool car");

            WebElement saveBtn = d.findElement(By.id("save"));
            saveBtn.click();
            return true;
        });

        (new WebDriverWait(driver, WAIT_MAX)).until((ExpectedCondition<Boolean>) (WebDriver d) -> {
            List<WebElement> tdata = d.findElement(By.id("tbodycars")).findElements(By.tagName("tr"));
            WebElement carEdited = null;
            for (WebElement webElement : tdata) {
                if (webElement.findElements(By.tagName("td")).get(0).getText().equals("938")) {
                    carEdited = webElement;
                }
            }
            WebElement carEditedDesc = carEdited.findElements(By.tagName("td")).get(5);
            Assert.assertThat(carEditedDesc.getText(), is("Cool car"));
            return true;
        });
    }

    @Test
    //Verify error message on trying to save a car with no data
    public void test6() throws Exception {
        WebElement newBtn = driver.findElement(By.id("new"));
        newBtn.click();
        (new WebDriverWait(driver, WAIT_MAX)).until((ExpectedCondition<Boolean>) (WebDriver d) -> {
            WebElement saveBtn = driver.findElement(By.id("save"));
            saveBtn.click();

            WebElement errorMsg = driver.findElement(By.id("submiterr"));

            Assert.assertThat(errorMsg.getText(), is("All fields are required"));

            WebElement e = d.findElement(By.tagName("tbody"));
            List<WebElement> rows = e.findElements(By.tagName("tr"));
            Assert.assertThat(rows.size(), is(5));
            return true;
        });
    }

    @Test
    //Verify error message on trying to save a car with no data
    public void test7() throws Exception {
        WebElement newBtn = driver.findElement(By.id("new"));
        newBtn.click();
        (new WebDriverWait(driver, WAIT_MAX)).until((ExpectedCondition<Boolean>) (WebDriver d) -> {
            WebElement year = driver.findElement(By.id("year"));
            WebElement registered = driver.findElement(By.id("registered"));
            WebElement make = driver.findElement(By.id("make"));
            WebElement model = driver.findElement(By.id("model"));
            WebElement description = driver.findElement(By.id("description"));
            WebElement price = driver.findElement(By.id("price"));

            year.sendKeys("2008");
            registered.sendKeys("2002-05-05");
            make.sendKeys("Kia");
            model.sendKeys("Rio");
            description.sendKeys("As new");
            price.sendKeys("31000");

            WebElement saveBtn = driver.findElement(By.id("save"));
            saveBtn.click();

            return true;
        });

        (new WebDriverWait(driver, WAIT_MAX)).until((ExpectedCondition<Boolean>) (WebDriver d) -> {
            WebElement e = d.findElement(By.tagName("tbody"));
            List<WebElement> rows = e.findElements(By.tagName("tr"));
            Assert.assertThat(rows.size(), is(6));
            return true;
        });
    }

}
