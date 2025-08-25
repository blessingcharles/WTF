package com.th3h04x.payload;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CommonPayload {
  public static final List<String> NON_PRINTABLE_URL_ENCODED =
      Arrays.asList(
          "%00",
          "%01", "%02", "%03", "%04", "%05", "%06", "%07", "%08", "%09", "%0A", "%0B", "%0C", "%0D",
          "%0E", "%0F", "%10", "%11", "%12", "%13", "%14", "%15", "%16", "%17", "%18", "%19", "%1A",
          "%1B", "%1C", "%1D", "%1E", "%1F", "%7F");

  public static final Set<String> COMMON_STATIC_PATH =
      Set.of("/static/", "/assets/", "/public/", "/resources/",
          "/media/", "/content/", "/css/", "/js/", "/scripts/",
          "/images/", "/img/", "/fonts/", "/icons/", "/vendor/");
}
