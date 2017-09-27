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
 * Exception used to warn when an id is not unique
 */
public class IdNotUniqueException extends Exception {
	private static final long serialVersionUID = -398373768189331969L;

	/**
	 * Constructor used to create an instance of this Exception
	 * 
	 * @param id
	 *            : the id that is not unique
	 * @param section
	 *            : the section where the id is located
	 */
	public IdNotUniqueException(String id, String section) {
		super("'Id' values must be unique. Check the id: '" + id + "' in " + section + " in the pattern definition.");
	}

}
