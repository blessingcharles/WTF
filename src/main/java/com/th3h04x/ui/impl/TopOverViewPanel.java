package com.th3h04x.ui.impl;

import burp.api.montoya.MontoyaApi;
import com.th3h04x.constant.ColumnName;
import com.th3h04x.db.InMemory;
import com.th3h04x.model.WtfResult;
import com.th3h04x.store.WtfResultStore;
import com.th3h04x.ui.WtfPanel;
import com.th3h04x.util.WtfUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class TopOverViewPanel implements WtfPanel {

  private final MontoyaApi api;
  private final JTextArea requestArea;
  private final JTextArea modifiedRequestArea;
  private final JTextArea responseArea;
  private DefaultTableModel tableModel;
  private JTable table;

  public TopOverViewPanel(
      MontoyaApi api,
      JTextArea requestArea,
      JTextArea modifiedRequestArea,
      JTextArea responseArea) {
    this.api = api;
    this.requestArea = requestArea;
    this.modifiedRequestArea = modifiedRequestArea;
    this.responseArea = responseArea;
  }

  @Override
  public JScrollPane build() {
    // Table model with columns
    tableModel =
        new DefaultTableModel(
            new Object[] {
              ColumnName.SERIAL_NO, ColumnName.REQUEST, ColumnName.SCANNER_NAME, ColumnName.ISSUE
            },
            0);

    table = new JTable(tableModel);
    table.setRowHeight(50);

    TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
    table.setRowSorter(sorter);

    // Adjust column widths
    table.getColumnModel().getColumn(0).setPreferredWidth(50); // S.NO
    table.getColumnModel().getColumn(1).setPreferredWidth(375); // Request Line
    table.getColumnModel().getColumn(2).setPreferredWidth(75); // Scanner Name
    table.getColumnModel().getColumn(3).setPreferredWidth(300); // Issue

    paintTheUi();

    return new JScrollPane(table);
  }

  public void addScanResult(WtfResult wtfResult) {
    int row = tableModel.getRowCount() + 1;
    tableModel.addRow(
        new Object[] {
          row,
          wtfResult.getModifiedRequest().url(),
          wtfResult.getScannerName(),
          wtfResult.getIssueDescription()
        });

    WtfResultStore.getInstance().addResult(wtfResult);
  }

  private void paintTheUi() {

    // set up the extension options
    table.setComponentPopupMenu(ContextMenuOption.prepareContextMenu(table, api));
    addClickListeners();
  }

  private void addClickListeners() {
    // Row selection listener
    table
        .getSelectionModel()
        .addListSelectionListener(
            e -> {
              int row = table.getSelectedRow();
              if (row >= 0) {
                WtfResult wtfResult = WtfResultStore.getInstance().getResult(row);
                if (wtfResult != null) {
                  requestArea.setText(wtfResult.getRequest().toString());
                  modifiedRequestArea.setText(wtfResult.getModifiedRequest().toString());
                  responseArea.setText(wtfResult.getResponse().toString());
                }
              }
            });
  }

  // applying filter for the given scope
  public void applyFilter(String domains) {
    WtfResultStore wtfResultStore = WtfResultStore.getInstance();
    InMemory.IN_SCOPE.clear();

    List<String> filters = Arrays.stream(domains.split(","))
        .map(String::trim)
        .filter(s -> !s.isEmpty())
        .toList();

    InMemory.IN_SCOPE.addAll(filters);

    TableRowSorter<?> sorter = (TableRowSorter<?>) table.getRowSorter();
    if (filters.isEmpty()) {
      sorter.setRowFilter(null); // show all
    } else {
      sorter.setRowFilter(new RowFilter<TableModel, Integer>() {

        @Override
        public boolean include(Entry entry) {
          int modelRow = (int) entry.getIdentifier();
          WtfResult wtfResult = wtfResultStore.getResult(modelRow);
          if (wtfResult == null) return false;

          String host = wtfResult.getRequest().httpService().host();
          return WtfUtil.isInScope(host); // keep only in-scope rows
        }
      });
    }
  }
}
