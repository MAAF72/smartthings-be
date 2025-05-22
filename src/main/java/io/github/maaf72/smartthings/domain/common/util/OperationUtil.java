package io.github.maaf72.smartthings.domain.common.util;

public class OperationUtil {
  public static Boolean or(Boolean... predicates ) {
    Boolean result = false;
    for (Boolean p : predicates) {
      result = result || p;

      if (result) break;
    }

    return result;
  }

  public static Boolean and(Boolean... predicates ) {
    Boolean result = true;
    for (Boolean p : predicates) {
      result = result && p;

      if (!result) break;
    } 

    return result;
  }
}
