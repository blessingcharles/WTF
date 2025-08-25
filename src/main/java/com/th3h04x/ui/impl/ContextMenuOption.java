package com.th3h04x.ui.impl;

import burp.api.montoya.MontoyaApi;
import com.th3h04x.db.InMemory;
import com.th3h04x.model.WtfResult;
import com.th3h04x.store.WtfResultStore;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class ContextMenuOption {

  public static JPopupMenu prepareContextMenu(JTable table,
                                              DefaultTableModel tableModel,
                                              JTextArea requestArea,
                                              JTextArea responseArea,
                                              JTextArea modifiedRequestArea,
                                              MontoyaApi api) {
    // Inside your WtfTabPanel constructor or initializer
    JPopupMenu contextMenu = new JPopupMenu();

    JMenuItem sendToRepeater = new JMenuItem("Send to Repeater");
    sendToRepeater.addActionListener(
        e -> {
          int row = table.getSelectedRow();
          if (row >= 0) {
            WtfResult result = WtfResultStore.getInstance().getResult(row);
            if (result != null) {
              // Send request to Repeater using Montoya API
              api.repeater().sendToRepeater(result.getModifiedRequest());
            }
          }
        });

    JMenuItem sendToIntruder = new JMenuItem("Send to Intruder");
    sendToIntruder.addActionListener(
        e -> {
          int row = table.getSelectedRow();
          if (row >= 0) {
            WtfResult result = WtfResultStore.getInstance().getResult(row);
            if (result != null) {
              api.intruder().sendToIntruder(result.getModifiedRequest());
            }
          }
        });

    JMenuItem deleteItem = new JMenuItem("Delete Item");
    deleteItem.addActionListener(
        e -> {
          int row = table.getSelectedRow();
          if (row >= 0) {

            SwingUtilities.invokeLater(() -> {
              WtfResultStore.getInstance().removeItem(row);
              tableModel.removeRow(row);
              requestArea.setText(null);
              responseArea.setText(null);
              modifiedRequestArea.setText(null);
            });
          }
        });

    JMenuItem clearAll = new JMenuItem("Clear All");
    clearAll.addActionListener(
        e -> {
          tableModel.setRowCount(0);
          InMemory.SEEN_REQUESTS.clear();
          SwingUtilities.invokeLater(() -> WtfResultStore.getInstance().clear());
          requestArea.setText(null);
          responseArea.setText(null);
          modifiedRequestArea.setText(null);
        });

    contextMenu.add(sendToRepeater);
    contextMenu.add(sendToIntruder);
    contextMenu.add(deleteItem);
    contextMenu.add(clearAll);

    return contextMenu;
  }
}
