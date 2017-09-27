/**
 * WebRatio Assistant v3.0
 * 
 * University of Extremadura (Spain) www.unex.es
 * 
 * Developers:
 * 	- Carlos Aguado Fuentes (v2)
 * 	- Javier Sierra Blázquez (v3.0)
 */
package org.homeria.webratioassistant.webratio;

import java.util.Date;

/**
 * Permits printing error messages in console
 */
public abstract class Debug {
	public static void println(String path, String comment) {
		Date date = new Date();
		System.err.println("(DEBUG:" + date.getTime() + ") " + path + ": " + comment);
	}

}
