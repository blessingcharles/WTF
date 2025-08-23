package com.th3h04x.scanner.impl;

import burp.api.montoya.MontoyaApi;
import burp.api.montoya.http.message.requests.HttpRequest;
import burp.api.montoya.http.message.responses.HttpResponse;
import com.th3h04x.model.WtfResult;
import com.th3h04x.scanner.WtfScanner;
import com.th3h04x.ui.WtfInterface;
import com.th3h04x.util.HttpUtil;
import com.th3h04x.util.ScannerUtil;

import javax.swing.*;
import java.util.List;

public class WebCacheDeception implements WtfScanner {

  private final MontoyaApi api;
  private final List<String> randomSuffix = List.of("b", "b/");
  private HttpResponse realHttpResponse;

  public WebCacheDeception(MontoyaApi api) {
    this.api = api;
  }

  @Override
  public void scan(HttpRequest request) {

    realHttpResponse = api.http().sendRequest(request).response();
    if (realHttpResponse.statusCode() < 400) {
      addRandomSuffix(request);
    }
  }

  private void analyzeResponse(
      HttpRequest request, HttpRequest modifiedRequest, HttpResponse response) {
    // TODO: fix false positives like gql behaviour and redirections ??
    if (realHttpResponse.statusCode() == response.statusCode() && response.statusCode() < 400) {

      WtfResult wtfResult =
          WtfResult.builder()
              .modifiedRequest(modifiedRequest)
              .request(request)
              .response(response)
              .issueDescription("modified request has the same status as the " + "original")
              .scannerName("wcd")
              .build();
      SwingUtilities.invokeLater(() -> WtfInterface.getInstance().addScanResult(wtfResult));
    }
  }

  /**
   * Add a random ending path to the URI and check whether the server is returning the same
   * response.
   */
  private void addRandomSuffix(HttpRequest request) {

    for (String suffix : randomSuffix) {

      String currPath = request.pathWithoutQuery();
      String modifiedPath = currPath + (currPath.endsWith("/") ? "" : "/") + suffix;
      HttpRequest modifiedHttpRequest = request.withPath(modifiedPath);

      HttpResponse httpResponse = ScannerUtil.sendModifiedRequest(api, modifiedHttpRequest);

      analyzeResponse(request, modifiedHttpRequest, httpResponse);
    }
  }
}
