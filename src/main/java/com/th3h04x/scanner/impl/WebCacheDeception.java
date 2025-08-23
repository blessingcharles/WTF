package com.th3h04x.scanner.impl;

import burp.api.montoya.MontoyaApi;
import burp.api.montoya.http.message.requests.HttpRequest;
import burp.api.montoya.http.message.responses.HttpResponse;
import com.th3h04x.model.WtfResult;
import com.th3h04x.scanner.WtfScanner;
import com.th3h04x.ui.WtfInterface;
import com.th3h04x.util.ScannerUtil;

import javax.swing.*;

public class WebCacheDeception implements WtfScanner {

  @Override
  public void scan(HttpRequest request, MontoyaApi api) {
    String payload = "' OR '1'='1";
    String modifiedUrl =
        request.url() + (request.url().contains("?") ? "&" : "?") + "test=" + payload;

    HttpRequest modifiedHttpRequest = ScannerUtil.buildModifiedUrl(api, request, modifiedUrl);
    HttpResponse httpResponse = api.http().sendRequest(modifiedHttpRequest).response();

    analyzeResponse(api, request, modifiedHttpRequest, httpResponse);
  }

  private void analyzeResponse(
      MontoyaApi api, HttpRequest request, HttpRequest modifiedRequest, HttpResponse response) {

    WtfResult wtfResult =
        WtfResult.builder()
            .modifiedRequest(modifiedRequest)
            .request(request)
            .response(response)
            .issueDescription("web cache deception")
            .scannerName("wcd")
            .build();
    SwingUtilities.invokeLater(() -> WtfInterface.getInstance().addScanResult(wtfResult));

    api.logging().logToOutput(response);
  }
}
