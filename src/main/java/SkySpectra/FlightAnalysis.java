package SkySpectra;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FlightAnalysis {
	private static HashMap<Integer, HashMap<String, Object>> flightInfoKayak = new HashMap<>();
	private static HashMap<Integer, HashMap<String, Object>> flightInfoCheapFlights = new HashMap<>();
	private static HashMap<Integer, HashMap<String, Object>> flightInfoMomondo = new HashMap<>();

	private static void scrapeData(String from, String to, String startDate, String endDate, String flightWebsite) {
		WebDriver driver = new FirefoxDriver();

		try {

			driver.get(flightWebsite);

			WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(20));

			// Remove the existing Airports selected
			wait.until(ExpectedConditions.elementToBeClickable(By.className("vvTc-item-close")));
			List<WebElement> elemList = driver.findElements(By.className("vvTc-item-close"));
			try {
				for (WebElement elem : elemList) {
					elem.click();
				}
			} catch (Exception e) {
				System.out.println("ERROR");
			}

			// Enter Values in the Input box
			elemList = driver.findElements(By.className("k_my-input"));
			elemList.get(0).sendKeys(from);
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			elemList.get(1).click();

			elemList.get(1).sendKeys(to);
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			elemList.get(0).click();

			// Get the value from that input box
			elemList = driver.findElements(By.className("vvTc-item-value"));
			String[] travelInfo = new String[2];
			for (int i = 0; i < 2; i++) {
				String[] cityCodeList = elemList.get(i).getText().split(" ");
				travelInfo[i] = cityCodeList[cityCodeList.length - 1].replaceAll("\\(", "").replaceAll("\\)", "");
			}

			String url = "";
			String extension = flightWebsite.contains("kayak") ? "flights/" : "flight-search/";
			if (endDate == "") {
				url = flightWebsite + extension + travelInfo[0] + "-" + travelInfo[1] + "/" + startDate
						+ "?sort=bestflight_a";
			} else {
				url = flightWebsite + extension + travelInfo[0] + "-" + travelInfo[1] + "/" + startDate + "/" + endDate
						+ "?sort=bestflight_a";
			}

			driver.get(url);

			try {
				wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.className("nrc6")));
				elemList = driver.findElements(By.className("nrc6"));
			} catch (Exception e) {
				System.out.println("There are no flights from " + from + " - " + to + "." );
			}

			for (int i = 0; i < elemList.size(); i++) {
				String data = elemList.get(i).getText();
//			System.out.println(data);
//			System.out.println("--------------------------");
				if (flightWebsite.contains("kayak")) {
					FlightAnalysis.flightInfoKayak.put(i, extractData(data));
				} else if (flightWebsite.contains("cheap")) {
					FlightAnalysis.flightInfoCheapFlights.put(i, extractData(data));
				} else {
					FlightAnalysis.flightInfoMomondo.put(i, extractData(data));
				}
				// remove this
//			if (i == 5)
//				break;
			}
		} catch (Exception e) {
			System.out.println("Exception in scraping the website: " + flightWebsite);
		} finally {
			driver.close();
		}
	}

	private static HashMap<String, Object> extractData(String data) {
		HashMap<String, Object> innerHashMap = new HashMap<>() {
			{
				put("flight", "");
				put("price", "");
				put("airports", "");
				put("time", "");
				put("days", "");
				put("hours", "");
			}
		};

		HashMap<String, String> regexMap = new HashMap<>() {
			{
				put("time", "\\d{1,2}:\\d{2} [apmAPM]{2}");
				put("days", "[+-]\\d");
				put("airports", "[A-Z]{3}[a-zA-Z]*\\s?[a-zA-Z]+");
				put("hours", "\\b\\d+h \\d+m\\b");
				put("price", "C\\$\\s\\d+(\\,\\d+)?");
			}
		};

		for (String key : regexMap.keySet()) {
			try {
				Pattern pattern = Pattern.compile(regexMap.get(key));
				Matcher matcher = pattern.matcher(data);
				ArrayList<String> lst = new ArrayList<>();
				while (matcher.find()) {
					String match = matcher.group();
					lst.add(match);
				}
				innerHashMap.put(key, lst);
			} catch (Exception e) {
				System.out.println("Exception in Extracting Data via Regex");
			}
		}

		try {
			ArrayList<String> a = (ArrayList<String>) innerHashMap.get("hours");
			int index1 = data.indexOf(a.get(a.size() > 1 ? 1 : 0)); // 220
			String temp = data.substring(index1);
			index1 = index1 + temp.indexOf("\n") + 1;
			temp = data.substring(index1);
			int index2 = index1 + temp.indexOf("\n");
			innerHashMap.put("flight", data.substring(index1, index2));
		} catch (Exception e) {
			System.out.println("Exception in fetching flight name !");
		}

//		for(String key: innerHashMap.keySet()) {
//			System.out.println(key + "-->" + innerHashMap.get(key));
//		}

		return innerHashMap;
	}

	public static void getHeaders() {
		String[] headers = {"Flight", "Hours", "Price", "Days", "Time", "Airports"};
		System.out.printf("%-55s", headers[0]);
		System.out.printf("%-25s", headers[1]);
		System.out.printf("%-15s", headers[2]);
		System.out.printf("%-15s", headers[3]);
		System.out.printf("%-45s", headers[4]);
		System.out.printf("%-25s", headers[5]);
		System.out.println("\n" + "=".repeat(250));
	}

	public static void flightAnalysis(String from, String to, String startDate, String endDate) {
//		System.setProperty("webdriver.chrome.driver", "/Users/yashsashani/Downloads/selenium-java-4/chromedriver");

		String[] flightWebsites = { "https://www.ca.kayak.com/", "https://www.cheapflights.ca/",
				"https://www.momondo.ca/" };

		for (String website : flightWebsites) {
			try {
				scrapeData(from, to, startDate, endDate, website);
			} catch (Exception e) {
				System.out.println("Something went wrong at flightAnalysis, please try again !!!");
			}
		}

		System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! KAYAK !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");

		HashMap<Integer, HashMap<String, Object>> hm = FlightAnalysis.flightInfoKayak;


		getHeaders();

		for (int i : hm.keySet()) {
			ArrayList<String> price  = (ArrayList<String>) hm.get(i).get("price");
			System.out.printf("%-55s", hm.get(i).get("flight"));
			System.out.printf("%-25s", hm.get(i).get("hours"));
			System.out.printf("%-15s", price.get(0));
			System.out.printf("%-15s", hm.get(i).get("days"));
			System.out.printf("%-45s", hm.get(i).get("time"));
			System.out.printf("%-25s", hm.get(i).get("airports"));
			System.out.println();
		}

		System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! Cheap Flights !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");

		hm = FlightAnalysis.flightInfoCheapFlights;

		getHeaders();
		for (int i : hm.keySet()) {
			System.out.printf("%-55s", hm.get(i).get("flight"));
			System.out.printf("%-25s", hm.get(i).get("hours"));
			System.out.printf("%-15s", hm.get(i).get("price"));
			System.out.printf("%-15s", hm.get(i).get("days"));
			System.out.printf("%-45s", hm.get(i).get("time"));
			System.out.printf("%-25s", hm.get(i).get("airports"));
			System.out.println();
		}

		System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! Momondo !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");

		getHeaders();
		for (int i : hm.keySet()) {
			System.out.printf("%-55s", hm.get(i).get("flight"));
			System.out.printf("%-25s", hm.get(i).get("hours"));
			System.out.printf("%-15s", hm.get(i).get("price"));
			System.out.printf("%-15s", hm.get(i).get("days"));
			System.out.printf("%-45s", hm.get(i).get("time"));
			System.out.printf("%-25s", hm.get(i).get("airports"));
			System.out.println();
		}
	}


}

/*
 * Sample URL:
 * //https://www.ca.kayak.com/flights/YYZ-AMD/2024-02-01/2024-03-30?sort=
 * bestflight_a
 * 
 * 
 * // Pattern pattern = Pattern.compile("\\d{1,2}:\\d{2} [apmAPM]{2}"); // time
 * // Pattern pattern = Pattern.compile("[+-]\\d"); // number of days // Pattern
 * pattern = Pattern.compile("[A-Z]{3}[a-zA-Z]*\\s?[a-zA-Z]+"); // Airport //
 * Pattern pattern = Pattern.compile("\\b\\d+\\s+stop\\b"); // number of stops
 * // Pattern pattern = Pattern.compile("\\b[A-Z]{3}\\b"); // Stop airport //
 * Pattern pattern = Pattern.compile("\\b\\d+h \\d+m\\b"); // Time // Pattern
 * pattern = Pattern.compile("C\\$\\s\\d+(\\,\\d+)?"); // money
 * 
 */
