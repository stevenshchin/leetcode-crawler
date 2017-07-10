package io.injectorstud.leetcodecrawler;

import com.google.common.collect.ImmutableList;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

class Crawler {
  private WebDriver driver;
  private ProblemSetParser problemSetParser;
  private WebDriverWait wait;

  Crawler(WebDriver driver, ProblemSetParser problemSetParser) {
    this.driver = driver;
    this.problemSetParser = problemSetParser;
    wait = new WebDriverWait(driver, 5);
  }

  void run() {
    if (!isLoggedIn()) {
      tryLogin();
    }

    dumpProblems();
    driver.quit();
  }

  private boolean isLoggedIn() {
    ensurePage("https://leetcode.com/");

    return checkLogin(0);
  }

  private boolean checkLogin(int timeOutInSeconds) {
    try {
      (new WebDriverWait(driver, timeOutInSeconds))
          .until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("#navbar-right .fa-user")));
      return true;
    } catch (TimeoutException e) {
      return false;
    }
  }

  private void tryLogin() {
    ensurePage("https://leetcode.com/accounts/login/");

    WebElement rememberMe = driver.findElement(By.id("id_remember"));

    if (!Objects.equals(rememberMe.getAttribute("checked"), "true")) {
      rememberMe.click();
    }

    if (!checkLogin(60)) {
      driver.quit();

      throw new RuntimeException("Can not login");
    }
  }

  private void dumpProblems() {
    ProblemSet problemSet = getProblemSet();
    Map<String, List<String>> problemKeysByTag = getGroupedByTagProblemKeys();

    problemSet.populateTags(problemKeysByTag);

    List<String> companies = new ArrayList<>();

    if (problemSet.isPaid()) {
      Map<String, List<String>> problemKeysByCompany = getGroupedByCompanyProblemKeys();

      problemSet.populateCompanies(problemKeysByCompany);
      companies.addAll(problemKeysByCompany.keySet());
    }

    generateReport(problemSet, new ArrayList<>(problemKeysByTag.keySet()), companies);
  }

  private void generateReport(ProblemSet problemSet, List<String> tags, List<String> companies) {
    tags.sort(String::compareTo);
    companies.sort(String::compareTo);

    // generate header
    {
      List<String> line = new ArrayList<>(ImmutableList.of("Id", "Title", "Level", "Frequency", "Status", "Link"));

      line.addAll(tags);
      line.addAll(companies);
      System.out.println(String.join(",", line));
    }

    // generate question rows
    for (Problem problem : problemSet.getProblems().values()) {
      List<String> line = new ArrayList<>();

      line.add(String.valueOf(problem.getId()));
      line.add("\"" + problem.getTitle() + "\"");
      line.add(problem.getDifficulty().name());
      line.add(String.valueOf(problem.getFrequency()));
      line.add(String.valueOf(problem.getStatus()));
      line.add("https://leetcode.com/problems/" + problem.getKey());

      for (String tag : tags) {
        line.add(problem.getTags().contains(tag) ? "Y" : "N");
      }

      for (String company : companies) {
        line.add(problem.getCompanies().contains(company) ? "Y" : "N");
      }

      System.out.println(String.join(",", line));
    }
  }

  private ProblemSet getProblemSet() {
    ensurePage("https://leetcode.com/api/problems/all");
    String content = driver.findElement(By.tagName("body")).getText();
    ProblemSet result = problemSetParser.getProblemSet(content);

    return result;
  }

  private Map<String, List<String>> getGroupedByCompanyProblemKeys() {
    ensurePage("https://leetcode.com/problemset/all/");
    driver.findElement(By.cssSelector("#expand-companies .btn")).click();

    List<WebElement> links = driver.findElements(By.cssSelector("#current-company-tags a"));
    List<SidebarItem> items = links.stream()
        .map(we -> new SidebarItem(we.findElement(By.tagName("span")).getText(), we.getAttribute("href")))
        .collect(Collectors.toList());

    return parseGroupedProblemKeys(items);
  }

  private Map<String, List<String>> getGroupedByTagProblemKeys() {
    ensurePage("https://leetcode.com/problemset/all/");
    wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".filterTags .filter-dropdown-button")));
    driver.findElement(By.cssSelector(".filterTags .filter-dropdown-button")).click();
    wait.until(
        ExpectedConditions.numberOfElementsToBeMoreThan(By.cssSelector(".filterTags .filter-dropdown-menu-item"), 1));

    List<WebElement> elems = driver.findElements(By.cssSelector(".filterTags .filter-dropdown-menu-item"));
    List<SidebarItem> items = elems.stream()
        .map(we -> new SidebarItem(we.getText(), "https://leetcode.com/tag/" + ((JavascriptExecutor) driver)
            .executeScript("" +
                    "return (function(elem) { " +
                    "  for (let p in elem) { " +
                    "    if (p.indexOf('__reactInternalInstance') == 0) { " +
                    "      return elem[p]._currentElement.key; " +
                    "    } " +
                    "  } " +
                    "})(arguments[0])",
                we)))
        .collect(Collectors.toList());

    return parseGroupedProblemKeys(items);
  }

  private Map<String, List<String>> parseGroupedProblemKeys(List<SidebarItem> items) {
    return items.stream().collect(Collectors.toMap(SidebarItem::getTitle,
        sidebarItem -> {
          ensurePage(sidebarItem.getLink());

          return driver.findElements(By.cssSelector("#question_list td > a")).stream()
              .map(we -> we.getAttribute("href"))
              .filter(h -> h != null && h.startsWith("https://leetcode.com/problems/"))
              .map(h -> h.split("/"))
              .map(p -> p[p.length - 1])
              .collect(Collectors.toList());
        }));
  }

  private void ensurePage(String url) {
    if (driver.getCurrentUrl().equals(url)) {
      return;
    }

    driver.get(url);
  }
}
