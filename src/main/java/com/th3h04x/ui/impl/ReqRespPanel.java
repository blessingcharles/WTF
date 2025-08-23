package com.th3h04x.ui.impl;

import com.th3h04x.constant.ColumnName;
import com.th3h04x.ui.WtfPanel;
import lombok.Getter;

import javax.swing.*;

public class ReqRespPanel implements WtfPanel {

  @Getter private final JTextArea requestArea;
  @Getter private final JTextArea responseArea;

  public ReqRespPanel() {
    this.requestArea = new JTextArea();
    this.responseArea = new JTextArea();
  }

  public JSplitPane build() {
    requestArea.setEditable(false);
    JScrollPane requestScroll = new JScrollPane(requestArea);
    requestScroll.setBorder(
        BorderFactory.createTitledBorder(String.valueOf(ColumnName.EXPANDED_REQUEST)));

    responseArea.setEditable(false);
    JScrollPane responseScroll = new JScrollPane(responseArea);
    responseScroll.setBorder(
        BorderFactory.createTitledBorder(String.valueOf(ColumnName.EXPANDED_RESPONSE)));

    JSplitPane bottomSplit =
        new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, requestScroll, responseScroll);

    // Split equally in the middle
    bottomSplit.setResizeWeight(0.5);

    return bottomSplit;
  }
}
