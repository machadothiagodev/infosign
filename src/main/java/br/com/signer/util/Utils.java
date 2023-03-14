package br.com.signer.util;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.Window;

public abstract class Utils {
	
	private Utils() {
		
	}
	
	public static void centralizeWindow(Window window) {
		Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
		window.setLocation(dimension.width / 2 - window.getSize().width / 2, dimension.height / 2 - window.getSize().height / 2);
	}

}
