package com.th3h04x.ui;

import burp.api.montoya.MontoyaApi;
import com.th3h04x.db.InMemory;
import com.th3h04x.model.WtfResult;
import com.th3h04x.store.WtfResultStore;
import com.th3h04x.ui.impl.ReqRespPanel;
import com.th3h04x.ui.impl.TopOverViewPanel;
import lombok.Getter;
import lombok.Setter;

import javax.swing.*;
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

    // === New Filter Panel at the top ===
    JPanel filterPanel = new JPanel(new BorderLayout());
    JLabel filterLabel = new JLabel("Filter (comma-separated regex domains): ");
    JTextField filterInput = new JTextField();
    JButton applyFilterButton = new JButton("Apply Filter");
    JButton clearAllFilterButton = new JButton("Reset");

    // Layout: label | input | button
    filterPanel.add(filterLabel, BorderLayout.WEST);
    filterPanel.add(filterInput, BorderLayout.CENTER);
    filterPanel.add(applyFilterButton, BorderLayout.EAST);
    filterPanel.add(clearAllFilterButton, BorderLayout.NORTH);

    filterPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

    // When button is clicked, apply filter
    applyFilterButton.addActionListener(e -> {
      String filterText = filterInput.getText().trim();
      topOverViewPanel.applyFilter(filterText); // call your existing method
    });

    clearAllFilterButton.addActionListener(e -> {
      SwingUtilities.invokeLater(() -> {
        topOverViewPanel.getTableModel().setRowCount(0);
        InMemory.SEEN_REQUESTS.clear();
        InMemory.CACHEABLE_PATH.clear();
        InMemory.STATIC_DIR.clear();
        InMemory.NON_CACHEABLE_PATH.clear();
        InMemory.QUERY_PARAMETERS.clear();
        WtfResultStore.getInstance().clear();

        reqRespPanel.getRequestArea().setText(null);
        reqRespPanel.getResponseArea().setText(null);
        reqRespPanel.getModifiedRequestArea().setText(null);
      });
    });

    // Main split: table on top, bottomSplit at the bottom
    JPanel topPanelWithFilter = new JPanel(new BorderLayout());
    topPanelWithFilter.add(filterPanel, BorderLayout.NORTH);
    topPanelWithFilter.add(topOverViewPanel.build(), BorderLayout.CENTER);

    JSplitPane mainSplit =
        new JSplitPane(JSplitPane.VERTICAL_SPLIT, topPanelWithFilter, reqRespPanel.build());
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
