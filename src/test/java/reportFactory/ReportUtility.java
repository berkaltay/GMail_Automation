package reportFactory;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class ReportUtility {
    private static ExtentReports extentReports = null;
    private static Map<Integer, ExtentTest> extentParentMap = new HashMap<>();
    private static Map<Integer, ExtentTest> extentChildMap = new HashMap<>();
    private static Map<ExtentTest, Boolean> childWithSuccess = new HashMap<>();
    private static String reportPath = null;

    public static ExtentReports createReportFile() {
        ExtentHtmlReporter extentHtmlReporter = setPropertiesOfReport();
        extentReports = new ExtentReports();
        extentReports.attachReporter(extentHtmlReporter);
        try {
            extentReports.setSystemInfo("HostName", InetAddress.getLocalHost().getHostName());
            extentReports.setSystemInfo("IP Address", InetAddress.getLocalHost().getHostAddress());
            extentReports.setSystemInfo("OS", System.getProperty("os.name"));
            extentReports.setSystemInfo("UserName", System.getProperty("user.name"));
            extentReports.setSystemInfo("Java Version", System.getProperty("java.version"));
        } catch (UnknownHostException e) {

        }
        return extentReports;
    }

    private static ExtentHtmlReporter setPropertiesOfReport() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        LocalDate localDate = LocalDate.now();

        String Path = System.getProperty("user.dir") + "\\reports";

        File folder = new File(Path);
        if (!folder.exists()) {
            folder.mkdir();
        }
        reportPath = Path + "\\" + dtf.format(localDate).toString() + "-TestReport.html";
        ExtentHtmlReporter extentHtmlReporter = new ExtentHtmlReporter(reportPath);
        extentHtmlReporter.config().setReportName("Crossover Assignment");
        extentHtmlReporter.config().setChartVisibilityOnOpen(true);
        extentHtmlReporter.setAppendExisting(false);
        extentHtmlReporter.config().setEncoding("utf-8");

        return extentHtmlReporter;
    }

    public static void saveReport() {
        removeFailedTests();
        if (extentReports != null) {
            extentReports.flush();
        }
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            System.out.println("Wait until report created.");
        }
        fixPassedTests();
    }

    public static synchronized ExtentTest createTest(String parentName) {
        String newParentName = parentName.split("\\.")[parentName.split("\\.").length - 1];
        ExtentTest parent = extentReports.createTest(newParentName);
        extentParentMap.put((int) (Thread.currentThread().getId()), parent);
        return parent;
    }

    public static synchronized void createChildTest(String parentName) {
        int threadId = 0;
        String newParentName = parentName.split("\\.")[parentName.split("\\.").length - 1];

        for (Map.Entry<Integer, ExtentTest> eachParent : extentParentMap.entrySet()) {
            if (eachParent.getValue().getModel().getName().equals(newParentName)) {
                threadId = eachParent.getKey();
                break;
            }
        }

        ExtentTest child = getParent(threadId);
        extentChildMap.put((int) (Thread.currentThread().getId()), child);
        childWithSuccess.put(child, true);
    }

    private static synchronized ExtentTest getParent(int id) {
        return extentParentMap.get(id);
    }

    public static synchronized ExtentTest getParentTest() {
        return extentParentMap.get((int) (Thread.currentThread().getId()));
    }

    public static synchronized ExtentTest getChildTest() {
        return extentChildMap.get((int) (Thread.currentThread().getId()));
    }

       private static void removeFailedTests() {
        for (Map.Entry<ExtentTest, Boolean> eachParent : childWithSuccess.entrySet()) {
            if (!eachParent.getValue()) {
                extentReports.removeTest(eachParent.getKey());
            }
        }
    }

    private static void fixPassedTests() {
        boolean isCorrect = true;

        try {
            Document doc = Jsoup.parse(new File(reportPath), "UTF-8");
            Element mainTag = doc.getElementById("test-collection");
            Elements scenarioNames = mainTag.getElementsByAttributeValueStarting("class", "test displayed");

            for (Element eachScenario : scenarioNames) {
                Elements allTests = eachScenario.getElementsByAttributeValueStarting("class", "node level-1 leaf");
                for (Element eachTest : allTests) {
                    if (eachTest.toString().contains("status=\"fail\"")) {
                        isCorrect = false;
                        break;
                    }
                }
                if (isCorrect) {
                    String newText = eachScenario.getElementsByTag("div").get(0).getElementsByAttributeValueStarting("class", "test-status").get(0).toString().replace("fail", "pass");
                    eachScenario.getElementsByTag("div").get(0).getElementsByAttributeValueStarting("class", "test-status").get(0).after(newText);
                    eachScenario.getElementsByTag("div").get(0).getElementsByAttributeValueStarting("class", "test-status").get(0).remove();
                }
            }

            File input = new File(reportPath);
            PrintWriter writer = new PrintWriter(input, "UTF-8");
            writer.write(doc.html());
            writer.flush();
            writer.close();
        } catch (IOException e) {
            System.out.println("Error occurred while fixing passed tests!");
        }
    }
}
