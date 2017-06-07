package io.injectorstud.test;

import com.google.common.io.CharStreams;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

public class DataUtils {
  public static String getResourceAsString(String path) {
    InputStream dataStream = DataUtils.class.getResourceAsStream(path);
    String data = null;

    try (final Reader reader = new InputStreamReader(dataStream)) {
      data = CharStreams.toString(reader);
    } catch (IOException e) {
      e.printStackTrace();
    }

    return data;
  }
}
