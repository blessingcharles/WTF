package com.th3h04x.scanner;

import burp.api.montoya.MontoyaApi;
import burp.api.montoya.http.message.requests.HttpRequest;
import burp.api.montoya.http.message.responses.HttpResponse;
import com.th3h04x.db.InMemory;
import com.th3h04x.scanner.impl.WebCacheDeception;
import com.th3h04x.util.HttpUtil;

import java.util.Arrays;
import java.util.List;

public class CoreScanner {

  /** fuzz and analyze all the requests/response for various scanning impl. */
  public void scan(HttpRequest request, MontoyaApi api) {
    try {
      // TODO: find a way to dynamically load the inbuilt scanners.

      List<WtfScanner> wtfScanners = List.of(new WebCacheDeception(api));
      HttpResponse realHttpResponse = api.http().sendRequest(request).response();

      String pathWithoutQuery = request.pathWithoutQuery();
      // store all the paths in the website
      if(HttpUtil.containCacheableHeader(realHttpResponse)){

        InMemory.CACHEABLE_PATH.add(pathWithoutQuery);
      }
      else {
        InMemory.NON_CACHEABLE_PATH.add(pathWithoutQuery);
      }

      // add all query parameters in the scope
      InMemory.QUERY_PARAMETERS.addAll(Arrays.asList(request.query().split("&")));

      for (WtfScanner scanner : wtfScanners) {
        scanner.scan(request, realHttpResponse);
      }

    } catch (Exception e) {
      api.logging().logToError("Error during scan: " + e);
    }
  }
}
