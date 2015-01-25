/*
 * US Time Zone Clock - Convert between the four continental US time zones
 * Copyright (C) 2015  Yinon Michaeli
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Contact by e-mail if you discover any bugs or if you have a suggestion
 * to myinon2005@hotmail.com
 */

package com.ustimes;

import static javax.swing.GroupLayout.*;
import static javax.swing.GroupLayout.Alignment.*;
import static javax.swing.LayoutStyle.ComponentPlacement.*;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import javax.swing.Action;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerDateModel;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.DateFormatter;

public class USTimesPanel extends JPanel implements ActionListener, ChangeListener, MouseWheelListener {

	static final long serialVersionUID = -5216896799712258150L;
	
	private static final String ACTION_NAME_INCREMENT = "increment";
	private static final String ACTION_NAME_DECREMENT = "decrement";
	private static final String OTHER_KEY = "zones.other";
	
	private final Map<String, String> zoneMap = new HashMap<>();
	private final JSpinner[] times = new JSpinner[4];
	private SpinnerDateModel model;
	private JButton resetToCurrent; // reset to current time
	
	public USTimesPanel() {
		Calendar cal = Calendar.getInstance();
		model = new SpinnerDateModel(cal.getTime(), null, null, Calendar.SECOND);
		
		// Configuring each of the three spinners
		// 1st parameter is time zone, 2nd is index in array,
		// 3rd is an array of the other zones that would need updating
		configureSpinner("US/Eastern", 0, 1, 2, 3);
		configureSpinner("US/Central", 1, 0, 2, 3);
		configureSpinner("US/Mountain", 2, 0, 1, 3);
		configureSpinner("US/Pacific", 3, 0, 1, 2);
		
		// Limit zoneMap to just United States time zones
		/* Start of Eastern Time */
		zoneMap.put("America/Detroit", "US/Eastern");
		zoneMap.put("America/Fort_Wayne", "US/Eastern");
		zoneMap.put("America/Indiana/Indianapolis", "US/Eastern");
		zoneMap.put("America/Indiana/Marengo", "US/Eastern");
		zoneMap.put("America/Indiana/Petersburg", "US/Eastern");
		zoneMap.put("America/Indiana/Vevay", "US/Eastern");
		zoneMap.put("America/Indiana/Vincennes", "US/Eastern");
		zoneMap.put("America/Indiana/Winamac", "US/Eastern");
		zoneMap.put("America/Indianapolis", "US/Eastern");
		zoneMap.put("America/Kentucky/Louisville", "US/Eastern");
		zoneMap.put("America/Kentucky/Monticello", "US/Eastern");
		zoneMap.put("America/Louisville", "US/Eastern");
		zoneMap.put("America/New_York", "US/Eastern");
		zoneMap.put("EST", "US/Eastern");
		zoneMap.put("EST5EDT", "US/Eastern");
		zoneMap.put("Etc/GMT+5", "US/Eastern");
		zoneMap.put("IET", "US/Eastern");
		zoneMap.put("SystemV/EST5", "US/Eastern");
		zoneMap.put("SystemV/EST5EDT", "US/Eastern");
		zoneMap.put("US/East-Indiana", "US/Eastern");
		zoneMap.put("US/Eastern", "US/Eastern");
		zoneMap.put("US/Michigan", "US/Eastern");
		/* End of Eastern Time */
		
		/* Start of Central Time */
		zoneMap.put("America/Chicago", "US/Central");
		zoneMap.put("America/Indiana/Knox", "US/Central");
		zoneMap.put("America/Indiana/Tell_City", "US/Central");
		zoneMap.put("America/Knox_IN", "US/Central");
		zoneMap.put("America/Menominee", "US/Central");
		zoneMap.put("America/North_Dakota/Beulah", "US/Central");
		zoneMap.put("America/North_Dakota/Center", "US/Central");
		zoneMap.put("America/North_Dakota/New_Salem", "US/Central");
		zoneMap.put("CST", "US/Central");
		zoneMap.put("CST6CDT", "US/Central");
		zoneMap.put("Etc/GMT+6", "US/Central");
		zoneMap.put("SystemV/CST6", "US/Central");
		zoneMap.put("SystemV/CST6CDT", "US/Central");
		zoneMap.put("US/Central", "US/Central");
		zoneMap.put("US/Indiana-Starke", "US/Central");
		/* End of Central Time */
		
		/* Start of Mountain Time */
		zoneMap.put("America/Boise", "US/Mountain");
		zoneMap.put("America/Denver", "US/Mountain");
		zoneMap.put("America/Phoenix", "US/Mountain");
		zoneMap.put("America/Shiprock", "US/Mountain");
		zoneMap.put("Etc/GMT+7", "US/Mountain");
		zoneMap.put("MST", "US/Mountain");
		zoneMap.put("MST7MDT", "US/Mountain");
		zoneMap.put("Navajo", "US/Mountain");
		zoneMap.put("PNT", "US/Mountain");
		zoneMap.put("SystemV/MST7", "US/Mountain");
		zoneMap.put("SystemV/MST7MDT", "US/Mountain");
		zoneMap.put("US/Arizona", "US/Mountain");
		zoneMap.put("US/Mountain", "US/Mountain");
		/* End of Mountain Time */
		
		/* Start of Pacific Time */
		zoneMap.put("America/Los_Angeles", "US/Pacific");
		zoneMap.put("America/Metlakatla", "US/Pacific");
		zoneMap.put("Etc/GMT+8", "US/Pacific");
		zoneMap.put("PST", "US/Pacific");
		zoneMap.put("PST8PDT", "US/Pacific");
		zoneMap.put("SystemV/PST8", "US/Pacific");
		zoneMap.put("SystemV/PST8PDT", "US/Pacific");
		zoneMap.put("US/Pacific", "US/Pacific");
		zoneMap.put("US/Pacific-New", "US/Pacific");
		/* End of Pacific Time */
		
		resetToCurrent = new JButton("Reset to current time");
		resetToCurrent.setMnemonic(KeyEvent.VK_R);
		resetToCurrent.setRequestFocusEnabled(false);
		resetToCurrent.setActionCommand("reset");
		resetToCurrent.addActionListener((ActionListener) this);
		
		configureLayout();
	}
	
	/**
	 * This function configures each of the three US major time zone
	 * spinners for proper editing and displaying of time information.
	 * 
	 * @param zone - The time zone to display: Eastern, Central, or Pacific.
	 * @param index - The index in the spinner array that specifies the current spinner.
	 * @param other - An array with the indices of the spinners not including the one
	 *                referenced by {@code index} that should be updated.
	 */
	private void configureSpinner(String zone, int index, int... other) {
		JSpinner temp = new JSpinner(model);
		temp.addChangeListener((ChangeListener) this);
		temp.addMouseWheelListener((MouseWheelListener) this);
		temp.putClientProperty(OTHER_KEY, Arrays.copyOf(other, other.length));
		
		// Apply formatting pattern and time zone
		SimpleDateFormat format = ((JSpinner.DateEditor) temp.getEditor()).getFormat();
		format.applyPattern("hh:mm:ss a");
		format.setLenient(false);
		format.setTimeZone(TimeZone.getTimeZone(zone));
		
		// Match specific zone to a broader zone
		// Removed in order to limit to just United States time zones
		/*String[] ids = TimeZone.getAvailableIDs(format.getTimeZone().getRawOffset());
		for (String id : ids) {
			zoneMap.put(id, zone);
		}*/
		
		// Change behavior of the editor
		DateFormatter formatter = (DateFormatter) ((JSpinner.DateEditor) temp.getEditor()).getTextField().getFormatter();
		formatter.setCommitsOnValidEdit(true); // Manual updates shown immediately
		formatter.setAllowsInvalid(false);
		formatter.setOverwriteMode(true);
		
		((JSpinner.DateEditor) temp.getEditor()).getTextField().setValue((model.getValue()));
		times[index] = temp;
	}
	
	private void configureLayout() {
		String currentZone = TimeZone.getDefault().getID();
		
		JLabel elabel = new JLabel("US/Eastern:");
		elabel.setDisplayedMnemonic(KeyEvent.VK_E);
		elabel.setLabelFor(times[0]);
		
		if (zoneMap.get(currentZone).equals("US/Eastern")) {
			elabel.setForeground(Color.RED);
		}
		
		JLabel clabel = new JLabel("US/Central:");
		clabel.setDisplayedMnemonic(KeyEvent.VK_C);
		clabel.setLabelFor(times[1]);
		
		if (zoneMap.get(currentZone).equals("US/Central")) {
			clabel.setForeground(Color.RED);
		}
		
		JLabel mlabel = new JLabel("US/Mountain:");
		mlabel.setDisplayedMnemonic(KeyEvent.VK_M);
		mlabel.setLabelFor(times[2]);
		
		if (zoneMap.get(currentZone).equals("US/Mountain")) {
			mlabel.setForeground(Color.RED);
		}
		
		JLabel plabel = new JLabel("US/Pacific:");
		plabel.setDisplayedMnemonic(KeyEvent.VK_P);
		plabel.setLabelFor(times[3]);
		
		if (zoneMap.get(currentZone).equals("US/Pacific")) {
			plabel.setForeground(Color.RED);
		}
		
		GroupLayout layout = new GroupLayout(this);
		setLayout(layout);
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);
		layout.setHonorsVisibility(true);
		
		layout.setHorizontalGroup(layout.createSequentialGroup()
			.addGroup(layout.createParallelGroup(LEADING)
				.addComponent(elabel)
				.addComponent(clabel)
				.addComponent(mlabel)
				.addComponent(plabel))
			.addGap(32)
			.addGroup(layout.createParallelGroup(LEADING)
				.addComponent(times[0], DEFAULT_SIZE, 125, Short.MAX_VALUE)
				.addComponent(times[1], DEFAULT_SIZE, 125, Short.MAX_VALUE)
				.addComponent(times[2], DEFAULT_SIZE, 125, Short.MAX_VALUE)
				.addComponent(times[3], DEFAULT_SIZE, 125, Short.MAX_VALUE)
				.addComponent(resetToCurrent))
		);
		
		layout.linkSize(SwingConstants.HORIZONTAL, times[0], times[1], times[2], times[3], resetToCurrent);
		layout.linkSize(SwingConstants.HORIZONTAL, elabel, clabel, mlabel, plabel);
		
		layout.setVerticalGroup(layout.createSequentialGroup()
			.addGroup(layout.createBaselineGroup(false, false)
				.addComponent(elabel)
				.addComponent(times[0], PREFERRED_SIZE, DEFAULT_SIZE, Short.MAX_VALUE))
			.addPreferredGap(RELATED)
			.addGroup(layout.createBaselineGroup(false, false)
				.addComponent(clabel)
				.addComponent(times[1], PREFERRED_SIZE, DEFAULT_SIZE, Short.MAX_VALUE))
			.addPreferredGap(RELATED)
			.addGroup(layout.createBaselineGroup(false, false)
				.addComponent(mlabel)
				.addComponent(times[2], PREFERRED_SIZE, DEFAULT_SIZE, Short.MAX_VALUE))
			.addPreferredGap(RELATED)
			.addGroup(layout.createBaselineGroup(false, false)
				.addComponent(plabel)
				.addComponent(times[3], PREFERRED_SIZE, DEFAULT_SIZE, Short.MAX_VALUE))
			.addPreferredGap(UNRELATED)
			.addGap(20)
			.addGroup(layout.createBaselineGroup(false, false)
				.addComponent(resetToCurrent))
		);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		String cmd = e.getActionCommand();
		
		switch (cmd) {
			case "reset": {
				Date time = Calendar.getInstance().getTime();
				for (JSpinner spinner : times) {
					spinner.setValue(time);
				}
			}
			break;
			default: break;
		}
	}
	
	@Override
	public void stateChanged(ChangeEvent e) {
		JSpinner spinner = (JSpinner) e.getSource();
		Date date = (Date) spinner.getValue();
		int[] others = (int[]) spinner.getClientProperty(OTHER_KEY);
		
		for (int i = 0; i < others.length; i++) {
			times[others[i]].setValue(date);
		}
	}
	
	// Borrowed from https://github.com/jidesoft/jide-oss/blob/master/src/com/jidesoft/spinner/SpinnerWheelSupport.java
	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		JSpinner spinner = (JSpinner) e.getComponent();
		int rotation = e.getWheelRotation();
		if (rotation < 0) {
			Action action = spinner.getActionMap().get(ACTION_NAME_INCREMENT);
			if (action != null) {
				action.actionPerformed(new ActionEvent(
					e.getSource(), ActionEvent.ACTION_PERFORMED, ACTION_NAME_INCREMENT));
			}
		}
		else if (rotation > 0) {
			Action action = spinner.getActionMap().get(ACTION_NAME_DECREMENT);
			if (action != null) {
				action.actionPerformed(new ActionEvent(
					e.getSource(), ActionEvent.ACTION_PERFORMED, ACTION_NAME_DECREMENT));
			}
		}
	}
}