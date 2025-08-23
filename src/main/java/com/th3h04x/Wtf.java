package com.th3h04x;

import burp.api.montoya.BurpExtension;
import burp.api.montoya.MontoyaApi;
import com.th3h04x.constant.CoreConstant;
import com.th3h04x.handler.WtfHttpHandler;
import com.th3h04x.ui.WtfInterface;

public class Wtf implements BurpExtension {

  private final WtfHttpHandler httpHandler;

  private MontoyaApi montoyaApi;

  public Wtf() {
    this.httpHandler = new WtfHttpHandler();
  }

  @Override
  public void initialize(MontoyaApi montoyaApi) {
    montoyaApi.extension().setName(CoreConstant.EXTENSION_NAME);

    this.httpHandler.setMontoyaApi(montoyaApi);
    // register the wtf http handler
    montoyaApi.http().registerHttpHandler(this.httpHandler);

    // register the ui panels
    WtfInterface wtfInterface = WtfInterface.getInstance();
    wtfInterface.setApi(montoyaApi);

    montoyaApi.userInterface().registerSuiteTab(CoreConstant.WTF, wtfInterface.paint().getPanel());

    montoyaApi.logging().logToOutput("WTF extension loaded successfully");
  }
}
