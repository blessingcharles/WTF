package com.th3h04x.model;

import burp.api.montoya.http.message.requests.HttpRequest;
import burp.api.montoya.http.message.responses.HttpResponse;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WtfResult {

    private HttpRequest request;
    private HttpRequest modifiedRequest;
    private HttpResponse response;
    private String url;
    private String scannerName;
    private String issueDescription;
    private String fullRequestResponse;
}
