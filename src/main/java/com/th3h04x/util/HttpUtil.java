package com.th3h04x.util;

import burp.api.montoya.http.HttpService;
import burp.api.montoya.http.message.requests.HttpRequest;
import burp.api.montoya.http.message.responses.HttpResponse;
import com.th3h04x.constant.HttpConstant;

import java.net.URL;

public class HttpUtil {

  public static String buildModifiedPath(HttpRequest request, String modifiedPath) {
    HttpService service = request.httpService();
    String scheme = service.secure() ? "https" : "http";
    String hostPort = scheme + "://" + service.host() + ":" + service.port();

    return hostPort + modifiedPath + request.query();
  }

  /**
   * Checks if the given URL points to a cacheable static resource.
   *
   * @param urlString the URL as a string
   * @return true if the URL ends with a known static extension, false otherwise
   */
  public static boolean isCacheableStaticResource(String urlString) {
    try {
      URL url = new URL(urlString);
      String path = url.getPath().toLowerCase();

      // Extract the extension
      int lastDot = path.lastIndexOf('.');
      if (lastDot != -1 && lastDot < path.length() - 1) {
        String ext = path.substring(lastDot + 1);
        return HttpConstant.CACHEABLE_EXTENSIONS.contains(ext);
      }
    } catch (Exception e) {
      // Invalid URL format
      return false;
    }
    return false;
  }

  public static boolean containCacheableHeader(HttpResponse httpResponse) {
    return httpResponse.headers().stream()
        .anyMatch(httpHeader -> HttpConstant.CACHE_RELATED_HEADERS.contains(httpHeader.name()));
  }

  public static String stripFilename(String uri) {
    if (uri == null || uri.isBlank()) {
      return uri;
    }

    // Normalize to always use forward slashes
    String normalized = uri.replace("\\", "/");

    // If it's already a directory (ends with /), return as-is
    if (normalized.endsWith("/")) {
      return normalized;
    }

    int lastSlash = normalized.lastIndexOf('/');
    if (lastSlash == -1) {
      // No slash at all, means it's just a filename
      return "/";
    }

    return normalized.substring(0, lastSlash + 1);
  }
}
