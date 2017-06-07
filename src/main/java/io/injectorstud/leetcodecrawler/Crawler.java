package io.injectorstud.leetcodecrawler;

import com.google.common.collect.ImmutableList;
import org.openqa.selenium.By;
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

  Crawler(WebDriver driver, ProblemSetParser problemSetParser) {
    this.driver = driver;
    this.problemSetParser = problemSetParser;
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
    Map<String, List<String>> problemKeysByTag = getGroupedProblemKeys("glyphicon glyphicon-tags");

    problemSet.populateTags(problemKeysByTag);

    List<String> companies = new ArrayList<>();

    if (problemSet.isPaid()) {
      Map<String, List<String>> problemKeysByCompany = getGroupedProblemKeys("fa fa-building");

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
    List<String> categoryLinks = getProblemCategoryLinks();
    ProblemSet result = null;

    for (String categoryLink : categoryLinks) {
      ensurePage("https://leetcode.com/api/problems/" + categoryLink);
      String content = driver.findElement(By.tagName("body")).getText();
      ProblemSet problemSet = problemSetParser.getProblemSet(content);

      result = mergeProblemSet(result, problemSet);
    }

    return result;
  }

  private ProblemSet mergeProblemSet(ProblemSet set1, ProblemSet set2) {
    if (set1 == null) {
      return set2;
    }

    if (set2 == null) {
      return set1;
    }

    set1.getProblems().putAll(set2.getProblems());

    return set1;
  }

  private List<String> getProblemCategoryLinks() {
    ensurePage("https://leetcode.com/");
    List<WebElement> links = driver.findElements(By.cssSelector(".navbar .problems-menu .dropdown-menu a"));

    return links.stream()
        .map(l -> l.getAttribute("href"))
        .filter(h -> h.startsWith("https://leetcode.com/problemset/"))
        .map(h -> h.replaceFirst("https://leetcode.com/problemset/", ""))
        .collect(Collectors.toList());
  }

  private Map<String, List<String>> getGroupedProblemKeys(String sectionIdentify) {
    ensurePage("https://leetcode.com/problemset/algorithms/");

    WebElement sidebar = driver.findElement(By.cssSelector(".blog-sidebar"));

    List<WebElement> links =
        sidebar.findElements(By.xpath("//ul[@class='list-group' and .//@class='" + sectionIdentify + "']/a"));
    List<SidebarItem> items = links.stream()
        .map(we -> new SidebarItem(we.findElement(By.tagName("small")).getText(), we.getAttribute("href")))
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
