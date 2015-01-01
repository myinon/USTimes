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

import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Toolkit;
import java.util.Arrays;
import java.util.List;

import javax.swing.JFrame;

public class USTimesFrame extends JFrame {

	static final long serialVersionUID = -908292087742226149L;
	
	public USTimesFrame() {
		super("US Time Zones Clock");
		
		// https://www.iconfinder.com/icons/357916/clock_hour_schedule_time_wait_watch_icon#size=128
		// https://www.iconfinder.com/icons/283049/alarm_alarm_clock_clock_hour_hours_minute_morning_time_watch_icon#size=128
		List<Image> icons = Arrays.asList(
			Toolkit.getDefaultToolkit().createImage(
				getClass().getResource("/com/ustimes/resources/clock16.png")),
			Toolkit.getDefaultToolkit().createImage(
				getClass().getResource("/com/ustimes/resources/clock32.png")));
		MediaTracker mt = new MediaTracker(this);
		try {
			mt.addImage(icons.get(0), 1);
			mt.addImage(icons.get(1), 2);
			mt.waitForAll();
		} catch (InterruptedException shouldNotHappen) {
			shouldNotHappen.printStackTrace(System.err);
		}
		
		USTimesPanel p = new USTimesPanel();
		setAutoRequestFocus(true);
		setContentPane(p);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setIconImages(icons);
		setResizable(false);
		pack();
		setLocationRelativeTo(null);
	}
}