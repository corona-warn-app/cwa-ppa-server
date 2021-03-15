package app.coronawarn.datadonation.services.edus.otp;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class StringUtils {

  /**
   * Returns an stringified object.
   *
   * @param obj Object to be converted to string
   * @return Json String
   */
  public static String asJsonString(final Object obj) throws JsonProcessingException {
    return new ObjectMapper().writeValueAsString(obj);
  }
}
