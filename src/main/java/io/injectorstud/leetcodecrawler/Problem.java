package io.injectorstud.leetcodecrawler;

import java.util.HashSet;
import java.util.Set;

/**
 * It represents metadata for a problem
 */
class Problem {
  private int id;
  private String key;
  private String title;
  private double frequency;
  private ProblemDifficulty difficulty;
  private String status;
  private boolean isPaidOnly;
  private Set<String> companies = new HashSet<>();
  private Set<String> tags = new HashSet<>();

  int getId() {
    return id;
  }

  void setId(int id) {
    this.id = id;
  }

  String getKey() {
    return key;
  }

  void setKey(String key) {
    this.key = key;
  }

  String getTitle() {
    return title;
  }

  void setTitle(String title) {
    this.title = title;
  }

  double getFrequency() {
    return frequency;
  }

  void setFrequency(double frequency) {
    this.frequency = frequency;
  }

  ProblemDifficulty getDifficulty() {
    return difficulty;
  }

  void setDifficulty(ProblemDifficulty difficulty) {
    this.difficulty = difficulty;
  }

  String getStatus() {
    return status;
  }

  void setStatus(String status) {
    this.status = status;
  }

  boolean isPaidOnly() {
    return isPaidOnly;
  }

  void setPaidOnly(boolean paidOnly) {
    isPaidOnly = paidOnly;
  }

  Set<String> getCompanies() {
    return companies;
  }

  void addCompany(String company) {
    companies.add(company);
  }

  Set<String> getTags() {
    return tags;
  }

  void addTag(String tag) {
    tags.add(tag);
  }

  @Override
  public String toString() {
    return "Problem{" +
        "id=" + id +
        ", key='" + key + '\'' +
        ", title='" + title + '\'' +
        ", frequency=" + frequency +
        ", difficulty=" + difficulty +
        ", status='" + status + '\'' +
        ", isPaidOnly=" + isPaidOnly +
        ", companies=" + companies +
        ", tags=" + tags +
        '}';
  }
}
