/*
 * Copyright 2011 00Green27 <00Green27@gmail.com>
 *
 * This file is part of mDBExplorer.
 *
 * This code is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This code is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this work.  If not, see http://www.gnu.org/licenses/.
 */

package ru.green.ui;

import net.miginfocom.swing.MigLayout;
import ru.green.DBAnalyzer;
import ru.green.utils.IOUtils;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * User: ehd
 * Date: 19.01.11 12:07
 */
public class HistoryDialog extends JDialog {

    public static final String HISTORY_FILE = "etc/history";

    private MainFrame frame;
    private List title= new ArrayList();
    private JButton insertButton;
    private JButton cancelButton;
    private JList list;

    public HistoryDialog(MainFrame frame) {
		super(frame, true);
        this.frame = frame;
        title.add(DBAnalyzer.getProperty("sqlhistory-dialog.table.col1"));
        setTitle(DBAnalyzer.getProperty("sqlhistory-dialog.title"));
        getContentPane().setLayout(new MigLayout("insets dialog", "[grow][]", "[][grow][][]"));
        JLabel matchLabel = new JLabel(DBAnalyzer.getProperty("sqlhistory-dialog.match-label"));
        JTextField matchField = new JTextField(20);
        list = new JList();

		ActionHandler handler = new ActionHandler();
        insertButton = new JButton(DBAnalyzer.getProperty("sqlhistory-dialog.insert-button"));
        insertButton.addActionListener(handler);
        cancelButton = new JButton(DBAnalyzer.getProperty("sqlhistory-dialog.close-button"));
        cancelButton.addActionListener(handler);
//        getContentPane().add(matchLabel, "cell 0 0, tag right");
//        getContentPane().add(matchField, "cell 1 0, tag right");

        list = new JList();
        list.setSelectionMode(DefaultListSelectionModel.SINGLE_SELECTION);
        getContentPane().add(new JScrollPane(list), "cell 0 1 2 1, grow, height  20:100:");
        getContentPane().add(insertButton, "tag right,cell 1 3");
        getContentPane().add(cancelButton, "tag right,cell 1 3");
        setSize(600,200);
        setLocationRelativeTo(frame);
        File file = new File(HISTORY_FILE);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    public void setVisible(boolean b) {
        String s = IOUtils.readFile(HISTORY_FILE);
        String[] history = null;
        if (s != null && !s.isEmpty()) {
            history = prepareHistrory(s);
            list.setListData(history);
        }
        super.setVisible(b);
    }


    private String[] prepareHistrory(String string) {
        String[] data = string.split(System.getProperty("line.separator"));
        List<String> l = Arrays.asList(data);
        Set<String> set = new HashSet<String>(l);
        String[] result = new String[set.size()];
        set.toArray(result);
        if (result.length >= 20) {
            String[] strings = Arrays.copyOf(result, 20);
            IOUtils.writeFile(arrayToString(strings), HISTORY_FILE);
        }
        Arrays.sort(result, Collections.reverseOrder());
        IOUtils.writeFile(arrayToString(result), HISTORY_FILE);
        return result;
    }

    private String arrayToString(String[] array) {
        StringBuilder b = new StringBuilder();
        for (String s : array) {
            b.append(s).append(System.getProperty("line.separator"));
        }
        return b.toString();
    }

    private void insert() {
        int index = list.getSelectedIndex();
        if (index >= 0){
            Object sql = list.getModel().getElementAt(index);
            if(sql != null)
                frame.setText(sql.toString());
        }
        dispose();
    }


    class ActionHandler implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == insertButton) {
				insert();
			} else if (e.getSource() == cancelButton) {
				dispose();
			}

		}

	}

}
