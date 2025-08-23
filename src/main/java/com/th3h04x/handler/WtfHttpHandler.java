package com.th3h04x.handler;

import burp.api.montoya.MontoyaApi;
import burp.api.montoya.http.handler.*;
import com.th3h04x.constant.CoreConstant;
import com.th3h04x.scanner.CoreScanner;
import org.apache.commons.codec.digest.DigestUtils;

import java.util.HashSet;
import java.util.Set;

public class WtfHttpHandler implements HttpHandler {

  private final CoreScanner coreScanner;
  private final Set<String> seenRequests = new HashSet<>();

  private MontoyaApi api;

  public WtfHttpHandler() {
    this.coreScanner = new CoreScanner();
  }

  public void setMontoyaApi(MontoyaApi api) {
    this.api = api;
  }

  @Override
  public RequestToBeSentAction handleHttpRequestToBeSent(HttpRequestToBeSent requestToBeSent) {

    if (isScannable(requestToBeSent)) {
      seenRequests.add(requestKeyGen(requestToBeSent));
      new Thread(() -> coreScanner.scan(requestToBeSent, api)).start();
    }

    // Always forward original request untouched
    return RequestToBeSentAction.continueWith(requestToBeSent);
  }

  @Override
  public ResponseReceivedAction handleHttpResponseReceived(HttpResponseReceived responseReceived) {
    return ResponseReceivedAction.continueWith(responseReceived);
  }

  private boolean isScannable(HttpRequestToBeSent requestToBeSent) {
    String key = requestKeyGen(requestToBeSent);

    return !seenRequests.contains(key)
        && !requestToBeSent.hasHeader(CoreConstant.WTF_MARKER_HEADER_KEY);
  }

  private String requestKeyGen(HttpRequestToBeSent requestToBeSent) {

    String key = requestToBeSent.method() + "::" + requestToBeSent.url();
    if (!requestToBeSent.bodyToString().isEmpty()) {
      key = key + "::" + DigestUtils.md5Hex(requestToBeSent.bodyToString());
    }
    return key;
  }
}
