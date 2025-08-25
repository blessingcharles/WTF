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
      "..%252f");

  //https://portswigger.net/web-security/web-cache-deception/wcd-lab-delimiter-list
  public static final List<String> DELIMITER_DISCREPANCIES =
      List.of(
          "!f.js",
          "\"f.js",
          "#f.js",
          "$f.js",
          "%f.js",
          "&f.js",
          "'f.js",
          "(f.js",
          ")f.js",
          "*f.js",
          "+f.js",
          ",f.js",
          "-f.js",
          ".f.js",
          "/f.js",
          ":f.js",
          ";f.js",
          "<f.js",
          "=f.js",
          ">f.js",
          "?f.js",
          "@f.js",
          "[f.js",
          "\\f.js",
          "]f.js",
          "^f.js",
          "_f.js",
          "`f.js",
          "{f.js",
          "|f.js",
          "}f.js",
          "~f.js",
          "%0kf.js", // invalid hex
          "%21f.js",
          "%22f.js",
          "%23f.js",
          "%24f.js",
          "%25f.js",
          "%26f.js",
          "%27f.js",
          "%28f.js",
          "%29f.js",
          "%2Af.js",
          "%2Bf.js",
          "%2Cf.js",
          "%2Df.js",
          "%2Ef.js",
          "%2Ff.js",
          "%3Af.js",
          "%3Bf.js",
          "%3Cf.js",
          "%3Df.js",
          "%3Ef.js",
          "%3Ff.js",
          "%40f.js",
          "%5Bf.js",
          "%5Cf.js",
          "%5Df.js",
          "%5Ef.js",
          "%5Ff.js",
          "%60f.js",
          "%7Bf.js",
          "%7Cf.js",
          "%7Df.js",
          "%7Ef.js");
}
