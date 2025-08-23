package com.th3h04x.util;

import burp.api.montoya.MontoyaApi;
import burp.api.montoya.http.message.requests.HttpRequest;
import burp.api.montoya.http.message.responses.HttpResponse;
import com.th3h04x.constant.CoreConstant;

public class ScannerUtil {


  public static HttpResponse sendModifiedRequest(MontoyaApi api,
      HttpRequest modifiedReq) {

    //adding a signature header to avoid recursion of scans.
    modifiedReq =
        modifiedReq.withAddedHeader(
            CoreConstant.WTF_MARKER_HEADER_KEY, CoreConstant.WTF_MARKER_HEADER_VALUE);

    return api.http().sendRequest(modifiedReq).response();
  }
}
