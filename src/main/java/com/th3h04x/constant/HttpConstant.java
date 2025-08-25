package com.th3h04x.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class HttpConstant {
  public static final Set<String> CACHE_RELATED_HEADERS =
      Set.of("ETag", "Cache-Control", "Expires", "Last-Modified", "Age",
          "Vary");

  // Common static resource extensions
  public static final Set<String> CACHEABLE_EXTENSIONS = Set.of(
      "js", "css", "png", "jpg", "jpeg", "gif", "svg", "ico",
      "woff", "woff2", "ttf", "eot", "otf", "map", "webp", "mp4", "mp3"
  );
}
