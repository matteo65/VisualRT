package com.matteozapparoli.visualrt;

import java.io.File;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
 * Main class of VisualRT 
 * @author Matteo Zapparoli
 * 29.8.2024
 */
public class VisualRT extends JFrame {
	
	private static final int INITIAL_SIZE = 727;
	
	private final VisualRTPanel mapPanel = new VisualRTPanel();
	
	public VisualRT(String filename) {
		long year = (1970 + System.currentTimeMillis() / 31556952000L);
		setTitle("VisualDet32 v." + version() + " - Copyright(C) 2023 - " + year + " Matteo Zapparoli");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
        // Aggiungi il pannello al frame
        getContentPane().add(this.mapPanel);

        // Imposta la dimensione iniziale del frame
        setSize(INITIAL_SIZE + 250, INITIAL_SIZE + 40);
        
        if(filename != null) {
        	try {
				mapPanel.loadFile(new File(filename));
			} 
        	catch (IOException e1) {
				e1.printStackTrace();
			}
        }
        
        setVisible(true);
	}
	
	public VisualRT() {
		this(null);
	}

	public static final void main(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException {
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				if(args.length == 1) {
					new VisualRT(args[0]);
				}
				else {
					new VisualRT();
				}
			}
		});
	}
	
	public static String version() {
		// First release
		return "1.0";
	}


}
