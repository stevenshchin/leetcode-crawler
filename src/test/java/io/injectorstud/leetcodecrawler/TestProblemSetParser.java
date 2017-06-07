package io.injectorstud.leetcodecrawler;

import io.injectorstud.test.DataUtils;
import org.testng.Assert;
import org.testng.annotations.Test;

public class TestProblemSetParser {
  @Test
  public void testParse() throws Exception {
    String data = DataUtils.getResourceAsString("/data/problem-set.json");
    ProblemSetParser parser = new ProblemSetParser();
    ProblemSet problemSet = parser.getProblemSet(data);

    Assert.assertEquals(problemSet.isPaid(), true);
    Assert.assertEquals(problemSet.getProblems().size(), 2);
    verifyProblem(problemSet.getProblems().get("longest-harmonious-subsequence"), "Longest Harmonious Subsequence",
        258.12799346113303d,
        ProblemDifficulty.EASY, "longest-harmonious-subsequence", false);
    verifyProblem(problemSet.getProblems().get("valid-square"), "Valid Square", 173.97358120633726d,
        ProblemDifficulty.MEDIUM, "valid-square", true);
  }

  private void verifyProblem(Problem problem, String expectedTitle, double expectedFrequency,
                             ProblemDifficulty expectedDifficulty, String expectedLink, boolean expectedIsPaidOnly) {
    Assert.assertEquals(problem.getTitle(), expectedTitle);
    Assert.assertTrue(Math.abs(problem.getFrequency() - expectedFrequency) < 0.001d);
    Assert.assertEquals(problem.getDifficulty(), expectedDifficulty);
    Assert.assertEquals(problem.getKey(), expectedLink);
    Assert.assertEquals(problem.isPaidOnly(), expectedIsPaidOnly);
  }
}
