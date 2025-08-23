package com.th3h04x.scanner;

import burp.api.montoya.MontoyaApi;
import burp.api.montoya.http.message.requests.HttpRequest;
import com.th3h04x.scanner.impl.WebCacheDeception;

import java.util.List;

public class CoreScanner {

  /** fuzz and analyze all the requests/response for various scanning impl. */
  public void scan(HttpRequest request, MontoyaApi api) {
    try {
      // TODO: find a way to dynamically load the inbuilt scanners.

      List<WtfScanner> wtfScanners = List.of(new WebCacheDeception());

      for (WtfScanner scanner : wtfScanners) {
        scanner.scan(request, api);
      }

    } catch (Exception e) {
      api.logging().logToError("Error during scan: " + e);
    }
  }
}
