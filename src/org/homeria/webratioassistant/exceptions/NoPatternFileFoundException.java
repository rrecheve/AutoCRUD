/**
 * WebRatio Assistant v3.0
 * 
 * University of Extremadura (Spain) www.unex.es
 * 
 * Developers:
 * 	- Carlos Aguado Fuentes (v2)
 * 	- Javier Sierra Blázquez (v3.0)
 * */
package org.homeria.webratioassistant.exceptions;

/**
 * Exception used to warn when no files are found in the patterns folder
 */
public class NoPatternFileFoundException extends Exception {

	private static final long serialVersionUID = 7826870590576594598L;

	/**
	 * Constructor used to create an instance of this Exception
	 * 
	 * @param path
	 *            : absolute path of the patterns folder
	 */
	public NoPatternFileFoundException(String path) {
		super("No pattern file found in " + path);
	}
}
