package app.coronawarn.datadonation.services.els.utils;

import app.coronawarn.datadonation.services.els.JsonParsingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class StringUtils {

  /**
   * Returns an stringified object.
   *
   * @param obj Object to be converted to string
   * @return Json String
   */
  public static String asJsonString(final Object obj) {
    try {
      return new ObjectMapper().writeValueAsString(obj);
    } catch (Exception e) {
      throw new JsonParsingException(e.getMessage());
    }
  }
}
