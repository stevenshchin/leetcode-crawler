LeetCode Progress Tracker
=========================

A tool to collect your progress on [LeetCode](https://leetcode.com/). It will generate a report in CSV format (to stdout) which then could be imported into tools such as [Google Sheets](https://docs.google.com/spreadsheets/u/0/) for better tracking.

![Better filtering in Google Sheets](https://user-images.githubusercontent.com/3030082/26870741-d08c8942-4b25-11e7-9b81-7fde8bba5938.png)

It uses [Selenium WebDriver](http://www.seleniumhq.org/projects/webdriver/) to get data / content on [LeetCode](https://leetcode.com/). The main reason for choosing [Selenium WebDriver](http://www.seleniumhq.org/projects/webdriver/) is that it's easier to build and debug for such a tiny project, and performance probably won't never be a concern.

Install
-------
1. Ensure Chromium/Google Chrome is installed in a recognized location. You usually won't have to do anything if Google Chrome is installed by the official installer.
2. Download [ChromeDriver - WebDriver for Chrome](https://sites.google.com/a/chromium.org/chromedriver/) and make sure the binary is named `chromedriver` and put that under `bin/` directory in this repo.
3. `mvn install`

Run
---
```
mvn exec:java -Dexec.mainClass=io.injectorstud.leetcodecrawler.Cli
```

It will launch a Chrome instance and use that to fetch all the data it looks for. It might ask you to log with your [LeetCode](https://leetcode.com/) account when you run it the very first time or when the stored session is expired. The season file (Chrome profile) will locate in `run/chrome/profile/` once it's created.

The tracker supports both free and paid user. If you use a free user to login, the report it generates won't contain the company information for each problem. Although login is not necessary if you only want to get publicly available content, the program currently requires login since it's a **progress tracker** for a user. You could modify [io.injectorstud.leetcodecrawler.Crawler::run()](src/main/java/io/injectorstud/leetcodecrawler/Crawler.java) to skip login check if you want.
