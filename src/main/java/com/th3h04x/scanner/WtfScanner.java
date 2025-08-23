package com.th3h04x.scanner;

import burp.api.montoya.MontoyaApi;
import burp.api.montoya.http.message.requests.HttpRequest;

public interface WtfScanner {
    void scan(HttpRequest request, MontoyaApi api) ;
}
