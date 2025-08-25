package com.th3h04x.scanner.impl;

import burp.api.montoya.MontoyaApi;
import burp.api.montoya.http.message.StatusCodeClass;
import burp.api.montoya.http.message.requests.HttpRequest;
import burp.api.montoya.http.message.responses.HttpResponse;
import com.th3h04x.db.InMemory;
import com.th3h04x.model.WtfResult;
import com.th3h04x.payload.CommonPayload;
import com.th3h04x.payload.WcdPayload;
import com.th3h04x.scanner.WtfScanner;
import com.th3h04x.ui.WtfInterface;
import com.th3h04x.util.CommonUtil;
import com.th3h04x.util.HttpUtil;
import com.th3h04x.util.ScannerUtil;
import com.th3h04x.util.WtfUtil;

import javax.swing.*;
import java.util.Objects;

// https://portswigger.net/web-security/web-cache-deception
public class WebCacheDeception implements WtfScanner {

  private final MontoyaApi api;

  private HttpResponse realHttpResponse;

  public WebCacheDeception(MontoyaApi api) {
    this.api = api;
  }

  @Override
  public void scan(HttpRequest request, HttpResponse realHttpResponse) {

    this.realHttpResponse = realHttpResponse;

    try {
      if (!realHttpResponse.isStatusCodeClass(StatusCodeClass.CLASS_2XX_SUCCESS)) {
        return;
      }

      // scan only for non cacheable request with 2xx responses
      if (!HttpUtil.isCacheableStaticResource(request.url())) {
        commonCacheableFiles(request);
        addRandomSuffix(request);
        delimiterDiscrepancy(request);
        addRandomExtension(request);
        nonPrintableCharacterDiscrepancies(request);
        pathNormalizationCacheServerNonCacheable(request);
        pathNormalizationOriginServerNonCacheablePath(request);
      } else {
        String dir = HttpUtil.stripFilename(request.pathWithoutQuery());
        // don't do it for /index.html where there is no nested static directories
        // only valid for /assets/js/main.js
        if (!InMemory.STATIC_DIR.contains(dir) && !dir.isBlank() && !dir.equals("/")) {
          InMemory.STATIC_DIR.add(dir);
          pathNormalizationOriginServerCacheablePath(request, dir);
          pathNormalizationCacheServerCacheable(request);
        }
      }

    } catch (Exception e) {
      api.logging().logToError("Exception happened in wcd scanner, exception: " + e);
    }
  }

  /**
   * Add a random ending path to the URI and check whether the server is returning the same
   * response.
   */
  private void addRandomSuffix(HttpRequest request) {

    for (String suffix : WcdPayload.RANDOM_PATH_SUFFIX) {
      String currPath = WtfUtil.trimTrailingSlash(request.pathWithoutQuery());
      String modifiedPath = currPath + suffix;
      HttpRequest modifiedHttpRequest = WtfUtil.buildModifiedPath(request, modifiedPath);

      sendAndAnalyzeResponse(request, modifiedHttpRequest, false, "random " + "suffix");
    }
  }

  /**
   * Add a random delimiter suffix /account;foo.js check whether the cache and origin servers
   * disagrees on the parsing behaviour.
   */
  private void delimiterDiscrepancy(HttpRequest request) {
    for (String suffix : WcdPayload.DELIMITER_DISCREPANCIES) {
      String modifiedPath =
          request.pathWithoutQuery()
              + suffix
              + "robots"
              + ".txt?"
              + CommonUtil.generateRandomStr(5);
      HttpRequest modifiedHttpRequest = WtfUtil.buildModifiedPath(request, modifiedPath);

      sendAndAnalyzeResponse(request, modifiedHttpRequest, false, "delimeter " + "discrepancy");
    }
  }

  private void addRandomExtension(HttpRequest request) {
    for (String suffix : WcdPayload.CACHEABLE_EXTENSIONS) {
      if (Objects.equals(request.pathWithoutQuery(), "")
          || Objects.equals(request.pathWithoutQuery(), "/")) {
        continue;
      }

      String modifiedPath = request.pathWithoutQuery() + suffix;
      HttpRequest modifiedHttpRequest = WtfUtil.buildModifiedPath(request, modifiedPath);

      sendAndAnalyzeResponse(request, modifiedHttpRequest, false, "random " + "extension");
    }
  }

  private void nonPrintableCharacterDiscrepancies(HttpRequest request) {
    for (String suffix : CommonPayload.NON_PRINTABLE_URL_ENCODED) {
      String modifiedPath = request.pathWithoutQuery() + suffix + "f";
      HttpRequest modifiedHttpRequest = WtfUtil.buildModifiedPath(request, modifiedPath);

      sendAndAnalyzeResponse(request, modifiedHttpRequest, false, "non " + "printable character");
    }
  }

  /*
   * [Path Normalization]
   * Check whether the origin server is normalizing the path, but not the
   * cache server eg: /assets/..%2fprofile
   *
   * Condition to satisfy
   * [*] Cache interpretation: /assets/..%2fprofile
   * (Cache may be configured to cache anything nested inside these static
   * folders, so we can cache victim's sensitive info inside the cache)
   * [*] Origin interpretation: /profile
   */
  private void pathNormalizationOriginServerNonCacheablePath(HttpRequest request) {
    // when a non-static path comes, combine it with previously found static dir

    for (String path : InMemory.STATIC_DIR) {
      for (String encodedDotDotSlash : WcdPayload.PATH_NORMALIZATION) {

        String payload = WtfUtil.buildTraversalPayload(path, encodedDotDotSlash);
        String modifiedPath = WtfUtil.insertBeforeLast(request.pathWithoutQuery(), payload);
        HttpRequest modifiedRequest = WtfUtil.buildModifiedPath(request, modifiedPath);
        sendAndAnalyzeResponse(
            request, modifiedRequest, true, "path " + "normalization origin server");
      }
    }
  }

  private void pathNormalizationOriginServerCacheablePath(HttpRequest request, String staticDir) {
    // when a new static path comes fuzz it with all non cacheable path
    String currentPath = request.pathWithoutQuery();

    for (String path : InMemory.NON_CACHEABLE_PATH) {
      for (String encodedDotDotSlash : WcdPayload.PATH_NORMALIZATION) {

        String payload = WtfUtil.buildTraversalPayload(staticDir, encodedDotDotSlash);

        String modifiedPath = WtfUtil.insertBeforeLast(path, payload);
        HttpRequest modifiedRequest = WtfUtil.buildModifiedPath(request, modifiedPath);
        sendAndAnalyzeResponse(request, modifiedRequest, true, "path " + "normalization");
      }
    }
  }

  /*
   * Check whether the cache server is normalizing the path before caching,
   *  but not the origin server
   * /path/profile;..%2f..%2f/static/js/test.js
   *
   * Cache: /static/js/test.js
   * Origin Server: /path/profile (we also need a delimiter which is only
   *        seen by the origin server)
   */
  private void pathNormalizationCacheServerNonCacheable(HttpRequest request) {
    // when non cacheable path comes
    String currPathWithoutQuery = request.pathWithoutQuery();
    if (currPathWithoutQuery.isBlank() || Objects.equals(currPathWithoutQuery, "/")) {
      return;
    }

    for (String cacheablePath : InMemory.CACHEABLE_PATH) {
      for (String delimiters : WcdPayload.DELIMITER_DISCREPANCIES) {
        for (String dotDotSlash : WcdPayload.PATH_NORMALIZATION) {
          String withDelimeters = currPathWithoutQuery + delimiters;
          String traversalPayload = WtfUtil.buildTraversalPayload(withDelimeters, dotDotSlash);

          String cacheBustingQuery = CommonUtil.generateRandomStr(4) + "=1";

          if (!request.query().isBlank()) {
            cacheBustingQuery = "&" + cacheBustingQuery;
          }
          String modifiedPath =
              traversalPayload
                  + cacheablePath.substring(1)
                  + "?"
                  + request.query()
                  + cacheBustingQuery;

          HttpRequest modifiedHttpRequest = WtfUtil.buildModifiedPath(request, modifiedPath);

          sendAndAnalyzeResponse(
              request, modifiedHttpRequest, true, "path normalization cache server");
        }
      }
    }
  }

  private void pathNormalizationCacheServerCacheable(HttpRequest request) {
    // when cacheable path comes
    String currPathWithoutQuery = request.pathWithoutQuery();
    if (currPathWithoutQuery.isBlank() || Objects.equals(currPathWithoutQuery, "/")) {
      return;
    }

    for (String nonCacheablePath : InMemory.NON_CACHEABLE_PATH) {
      for (String delimiters : WcdPayload.DELIMITER_DISCREPANCIES) {
        for (String dotDotSlash : WcdPayload.PATH_NORMALIZATION) {
          String withDelimeters = nonCacheablePath + delimiters;
          String traversalPayload = WtfUtil.buildTraversalPayload(withDelimeters, dotDotSlash);

          String cacheBustingQuery = CommonUtil.generateRandomStr(4) + "=1";

          if (!request.query().isBlank()) {
            cacheBustingQuery = "&" + cacheBustingQuery;
          }
          String modifiedPath =
              traversalPayload
                  + currPathWithoutQuery.substring(1)
                  + "?"
                  + request.query()
                  + cacheBustingQuery;

          HttpRequest modifiedHttpRequest = WtfUtil.buildModifiedPath(request, modifiedPath);

          sendAndAnalyzeResponse(
              request, modifiedHttpRequest, true, "path normalization cache server");
        }
      }
    }
  }

  private void commonCacheableFiles(HttpRequest request) {
    String currPathWithoutQuery = request.pathWithoutQuery();
    if (currPathWithoutQuery.isBlank() || currPathWithoutQuery.equals("/")) {
      return;
    }

    for (String delimiters : WcdPayload.DELIMITER_DISCREPANCIES) {
      for (String dotDotSlash : WcdPayload.PATH_NORMALIZATION) {
        for (String filenames : CommonPayload.COMMON_CACHEABLE_FILES) {
          for (String slashes : CommonPayload.VARIOUS_SLASHES) {

            String modifiedDotDotSlash =
                dotDotSlash.repeat(CommonUtil.countSlashes(currPathWithoutQuery));
            String modifiedPath =
                currPathWithoutQuery + delimiters + slashes + modifiedDotDotSlash + filenames + "?" + CommonUtil.generateRandomStr(6);

            HttpRequest modifiedHttpRequest = WtfUtil.buildModifiedPath(request, modifiedPath);

            sendAndAnalyzeResponse(
                request, modifiedHttpRequest, true, "common cacheable files parsing discrepancies");
          }
        }
      }
    }
  }

  private void sendAndAnalyzeResponse(
      HttpRequest request,
      HttpRequest modifiedRequest,
      boolean checkForCacheableHeader,
      String issueDescription) {
    HttpResponse response = ScannerUtil.sendModifiedRequest(api, modifiedRequest);

    // TODO: fix false positives like gql behaviour and redirections ??
    if (realHttpResponse.statusCode() == response.statusCode()
        && response.statusCode() < 300
        && (!checkForCacheableHeader || HttpUtil.containCacheableHeader(response))) {

      WtfResult wtfResult =
          WtfResult.builder()
              .modifiedRequest(modifiedRequest)
              .request(request)
              .response(response)
              .issueDescription(issueDescription)
              .scannerName("web cache deception")
              .build();
      SwingUtilities.invokeLater(() -> WtfInterface.getInstance().addScanResult(wtfResult));
    }
  }
}
