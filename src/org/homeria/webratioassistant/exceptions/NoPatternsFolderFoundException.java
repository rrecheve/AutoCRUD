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
 * Exception used to warn when the patterns folder is not found
 */
public class NoPatternsFolderFoundException extends Exception {

	private static final long serialVersionUID = 2819026587756669276L;

	/**
	 * Constructor used to create an instance of this Exception
	 * 
	 * @param path
	 *            : absolute path of the patterns folder
	 */
	public NoPatternsFolderFoundException(String path) {
		super("You need to create the directory 'patterns' in your Web Proyect folder.\n (" + path + ")");
	}
}
