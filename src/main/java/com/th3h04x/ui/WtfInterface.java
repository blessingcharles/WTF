package com.th3h04x.ui;

import burp.api.montoya.MontoyaApi;
import com.th3h04x.constant.ColumnName;
import com.th3h04x.model.WtfResult;
import com.th3h04x.store.WtfResultStore;
import com.th3h04x.ui.impl.ReqRespPanel;
import com.th3h04x.ui.impl.TopOverViewPanel;
import lombok.Getter;
import lombok.Setter;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class WtfInterface {

  private final JPanel mainPanel;

  @Getter private static final WtfInterface instance = new WtfInterface();
  @Setter private MontoyaApi api;

  private TopOverViewPanel topOverViewPanel;
  private ReqRespPanel reqRespPanel;

  private WtfInterface() {
    mainPanel = new JPanel(new BorderLayout());
  }

  public WtfInterface paint() {

    // Bottom panel: two text areas split vertically (request / response)
    reqRespPanel = new ReqRespPanel();

    // Top panel: table with issues summary
    topOverViewPanel =
        new TopOverViewPanel(
            api,
            reqRespPanel.getRequestArea(),
            reqRespPanel.getModifiedRequestArea(),
            reqRespPanel.getResponseArea());

    // Main split: table on top, bottomSplit at the bottom
    JSplitPane mainSplit =
        new JSplitPane(JSplitPane.VERTICAL_SPLIT, topOverViewPanel.build(), reqRespPanel.build());
    mainSplit.setDividerLocation(300);

    mainPanel.add(mainSplit, BorderLayout.CENTER);
    return this;
  }

  public JPanel getPanel() {
    return mainPanel;
  }

  public void addScanResult(WtfResult wtfResult) {
    topOverViewPanel.addScanResult(wtfResult);
  }
}
