package io.injectorstud.leetcodecrawler;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.HashMap;
import java.util.Map;

/**
 * Parser / transformer for problem-set metadata in JSON format
 */
class ProblemSetParser {
  private static final Map<Integer, ProblemDifficulty> DIFFICULTY_MAP =
      ImmutableMap.<Integer, ProblemDifficulty>builder().put(1, ProblemDifficulty.EASY).put(2, ProblemDifficulty.MEDIUM)
          .put(3, ProblemDifficulty.HARD).build();

  ProblemSet getProblemSet(String data) {
    JsonParser parser = new JsonParser();

    JsonElement rootElement = parser.parse(data);
    JsonObject root = rootElement.getAsJsonObject();

    return parseProblemSet(root);
  }

  private ProblemSet parseProblemSet(JsonObject jsonProblemSet) {
    Map<String, Problem> problems = new HashMap<>();

    for (JsonElement jasonProblem : jsonProblemSet.getAsJsonArray("stat_status_pairs")) {
      Problem problem = parseProblem(jasonProblem.getAsJsonObject());

      problems.put(problem.getKey(), problem);
    }

    ProblemSet problemSet = new ProblemSet();

    problemSet.setPaid(jsonProblemSet.get("is_paid").getAsBoolean());
    problemSet.setProblems(problems);

    return problemSet;
  }

  private Problem parseProblem(JsonObject jsonProblem) {
    Problem problem = new Problem();
    JsonObject stat = jsonProblem.getAsJsonObject("stat");

    problem.setId(stat.get("question_id").getAsInt());
    problem.setKey(stat.get("question__title_slug").getAsString());
    problem.setTitle(stat.get("question__title").getAsString());
    problem.setFrequency(jsonProblem.get("frequency").getAsDouble());
    problem.setDifficulty(parseDifficulty(jsonProblem.getAsJsonObject("difficulty")));
    problem.setStatus(jsonProblem.get("status").isJsonNull() ? "" : jsonProblem.get("status").getAsString());
    problem.setPaidOnly(jsonProblem.get("paid_only").getAsBoolean());

    return problem;
  }

  private ProblemDifficulty parseDifficulty(JsonObject difficulty) {
    return DIFFICULTY_MAP.getOrDefault(difficulty.get("level").getAsInt(), ProblemDifficulty.UNKNOWN);
  }
}
