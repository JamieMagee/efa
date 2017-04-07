//-----------------------------------------------------------------------------
// SunTimesApplet.java
//
// (c) 2004 Jonathan Stott
//
// Created on 12-Apr-2004
//
// 0.2 - 12 Apr 2004
//  - Initial Version
//-----------------------------------------------------------------------------

package uk.me.jstott.sun;

import java.awt.BorderLayout;

import javax.swing.JApplet;

/**
 * A simple applet to allow calculation of sunrise, sunset, etc. times using the
 * uk.me.jstott.sun.Sun class.
 * 
 * For more information on using this class, look at
 * http://www.jstott.me.uk/jsuntimes/
 * 
 * @author Jonathan Stott
 * @version 0.4
 */
public class SunTimesApplet extends JApplet {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7989425564676903148L;

	SunTimesPanel panel = new SunTimesPanel();

	/**
	 * Initialise the applet
	 * 
	 * @see java.applet.Applet#init()
	 */
	public void init() {
		getContentPane().add(panel, BorderLayout.CENTER);
	}
}
