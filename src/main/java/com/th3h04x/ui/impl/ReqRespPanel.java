package com.th3h04x.ui.impl;

import com.th3h04x.constant.ColumnName;
import com.th3h04x.ui.WtfPanel;
import lombok.Getter;

import javax.swing.*;

public class ReqRespPanel implements WtfPanel {

  @Getter private final JTextArea requestArea;
  @Getter private final JTextArea modifiedRequestArea;
  @Getter private final JTextArea responseArea;

  public ReqRespPanel() {
    this.requestArea = new JTextArea();
    this.modifiedRequestArea = new JTextArea();
    this.responseArea = new JTextArea();
  }

  public JSplitPane build() {
    requestArea.setEditable(false);
    JScrollPane requestScroll = new JScrollPane(requestArea);
    requestScroll.setBorder(
        BorderFactory.createTitledBorder(String.valueOf(ColumnName.EXPANDED_REQUEST)));

    modifiedRequestArea.setEditable(false);
    JScrollPane modifiedRequestScroll = new JScrollPane(modifiedRequestArea);
    modifiedRequestScroll.setBorder(
        BorderFactory.createTitledBorder(String.valueOf(ColumnName.EXPANDED_MODIFIED_REQUEST)));

    responseArea.setEditable(false);
    JScrollPane responseScroll = new JScrollPane(responseArea);
    responseScroll.setBorder(
        BorderFactory.createTitledBorder(String.valueOf(ColumnName.EXPANDED_RESPONSE)));

    // First split: request | modified request
    JSplitPane leftSplit =
        new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, requestScroll, modifiedRequestScroll);
    leftSplit.setResizeWeight(0.5); // split equally

    // Now split leftSplit with response => (request | modified) | response
    JSplitPane bottomSplit =
        new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftSplit, responseScroll);
    bottomSplit.setResizeWeight(0.66); // so it divides ~1/3,1/3,1/3

    return bottomSplit;
  }
}
