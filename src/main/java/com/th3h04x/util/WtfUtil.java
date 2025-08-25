package com.th3h04x.util;

import burp.api.montoya.http.message.requests.HttpRequest;
import com.th3h04x.db.InMemory;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Pattern;

public class WtfUtil {
  public static String trimTrailingSlash(String input) {
    return Optional.ofNullable(input)
        .filter(s -> s.endsWith("/"))
        .map(s -> s.substring(0, s.length() - 1))
        .orElse(input);
  }

  public static HttpRequest buildModifiedPath(
      HttpRequest request, String modifiedPathWithOutQuery) {
    String fullPath = modifiedPathWithOutQuery;

    if (!request.query().isBlank()) {
      fullPath = fullPath + "?" + request.query();
    }

    return request.withPath(fullPath);
  }

  public static boolean simpleWildCardMatches(String host, String pattern) {
    if (host == null || pattern == null) {
      return false;
    }

    // Normalize
    host = host.toLowerCase();
    pattern = pattern.toLowerCase();

    // If pattern starts with "*.", match any subdomain
    if (pattern.startsWith("*.")) {
      String domain = pattern.substring(2); // remove "*."
      return host.equals(domain) || host.endsWith("." + domain);
    }

    // Exact match
    return host.equals(pattern);
  }

  public static boolean isInScope(String host) {
    // TODO: modify to match complex regex patterns in domain.

    return InMemory.IN_SCOPE.stream()
        .anyMatch(pattern -> simpleWildCardMatches(host, pattern));
  }

  // Input:   /assets/test/foo/ , ..%2f
  // Output: /assets/test/foo/..%2f..%2f..%2f
  public static String buildTraversalPayload(String path, String payload) {
    if (path == null || path.isBlank()) {
      return path;
    }

    // Normalize: ensure trailing slash
    if (!path.endsWith("/")) {
      path = path + "/";
    }

    // Count non-empty segments
    String[] segments = path.split("/");
    long count = Arrays.stream(segments).filter(s -> !s.isBlank()).count();

    String traversal = payload.repeat((int) count);

    return path + traversal;
  }

  public static String insertBeforeLast(String uri, String insert) {
    if (uri == null || uri.isBlank() || insert == null) {
      return uri;
    }

    // Normalize slashes
    String normalized = uri.replace("\\", "/");

    // Find last slash
    int lastSlash = normalized.lastIndexOf('/');
    if (lastSlash == -1) {
      // No slash → just prepend
      return insert + "/" + normalized;
    }

    String before = normalized.substring(0, lastSlash);
    String after = normalized.substring(lastSlash); // includes '/filename' or just '/'

    if (Objects.equals(before, "/")) {
      before = "";
    }

    // If the path ends with "/", treat it as directory (no filename)
    if (after.equals("/")) {
      return before + insert + "/";
    }
    return before + insert + after;
  }
}
