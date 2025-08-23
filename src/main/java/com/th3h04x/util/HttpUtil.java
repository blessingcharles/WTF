package com.th3h04x.util;

import burp.api.montoya.http.HttpService;
import burp.api.montoya.http.message.requests.HttpRequest;

public class HttpUtil {

  public static String buildModifiedPath(HttpRequest request,
                                      String modifiedPath) {
    HttpService service = request.httpService();
    String scheme = service.secure() ? "https" : "http";
    String hostPort = scheme + "://" + service.host() + ":" + service.port();

    return  hostPort + modifiedPath + request.query() ;
  }
}
