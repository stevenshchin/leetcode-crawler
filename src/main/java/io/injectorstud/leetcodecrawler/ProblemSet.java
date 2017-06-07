package io.injectorstud.leetcodecrawler;

import java.util.List;
import java.util.Map;

/**
 * It represents metadata for a problem-set
 */
class ProblemSet {
  private boolean isPaid;
  private Map<String, Problem> problems;

  boolean isPaid() {
    return isPaid;
  }

  void setPaid(boolean paid) {
    isPaid = paid;
  }

  Map<String, Problem> getProblems() {
    return problems;
  }

  void setProblems(Map<String, Problem> problems) {
    this.problems = problems;
  }

  void populateTags(Map<String, List<String>> problemKeysByTag) {
    problemKeysByTag.forEach((tag, value) -> {
      for (String problemKey : value) {
        problems.get(problemKey).addTag(tag);
      }
    });
  }

  void populateCompanies(Map<String, List<String>> problemKeysByCompany) {
    problemKeysByCompany.forEach((company, value) -> {
      for (String problemKey : value) {
        problems.get(problemKey).addCompany(company);
      }
    });
  }

  @Override
  public String toString() {
    return "ProblemSet{" +
        "isPaid=" + isPaid +
        ", problems=" + problems +
        '}';
  }
}
