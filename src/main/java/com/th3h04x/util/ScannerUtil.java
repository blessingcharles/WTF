package com.th3h04x.util;

import burp.api.montoya.MontoyaApi;
import burp.api.montoya.http.message.HttpHeader;
import burp.api.montoya.http.message.requests.HttpRequest;
import com.th3h04x.constant.CoreConstant;
import java.util.List;
import java.util.Objects;

public class ScannerUtil {

  public static HttpRequest buildModifiedUrl(
      MontoyaApi api, HttpRequest baseRequest, String newUrl) {
    return buildModifiedRequest(api, baseRequest, newUrl, null, null);
  }


  public static HttpRequest buildModifiedRequest(
      MontoyaApi api,
      HttpRequest baseRequest,
      String newUrl,
      String newBody,
      List<HttpHeader> httpHeaders) {
    // Start from base request
    HttpRequest modifiedReq = baseRequest;

    // ✅ Modify Body
    if (Objects.nonNull(newBody)) {
      modifiedReq = modifiedReq.withBody(newBody);
    }

    // Add or Replace Header
    if (Objects.nonNull(httpHeaders)) {
      for (HttpHeader httpHeader : httpHeaders) {
        modifiedReq = modifiedReq.withAddedHeader(httpHeader);
      }
    }

    // add the http header signature to avoid recursive scanning.
    modifiedReq =
        modifiedReq.withAddedHeader(
            CoreConstant.WTF_MARKER_HEADER_KEY, CoreConstant.WTF_MARKER_HEADER_VALUE);

    return HttpRequest.httpRequestFromUrl(newUrl)
        .withService(modifiedReq.httpService())
        .withMethod(modifiedReq.method())
        .withBody(modifiedReq.body())
        .withAddedHeaders(modifiedReq.headers());

  }
}
