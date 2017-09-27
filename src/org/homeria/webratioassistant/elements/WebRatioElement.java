/**
 * WebRatio Assistant v3.0
 * 
 * University of Extremadura (Spain) www.unex.es
 * 
 * Developers:
 * 	- Carlos Aguado Fuentes (v2)
 * 	- Javier Sierra Blázquez (v3.0)
 * */
package org.homeria.webratioassistant.elements;

import java.util.Map;

import org.eclipse.draw2d.geometry.Point;

import com.webratio.commons.mf.IMFElement;

/**
 * Hierarchy used to serialize the elements parsed in the pattern. Abstract class.
 */
public abstract class WebRatioElement {
	protected String id;
	protected String name;
	protected Point position;

	/**
	 * Creates a new instance with the given data.
	 * 
	 * @param id
	 *            : used to uniquely identify the element.
	 * @param name
	 *            : the element name to display.
	 * @param x
	 *            : Relative X coordinate. Used to place the element in the model.
	 * @param y
	 *            : Relative Y coordinate. Used to place the element in the model.
	 */
	public WebRatioElement(String id, String name, String x, String y) {
		this.id = id;
		this.name = name;

		if (x == null || y == null)
			this.position = null;
		else
			try {
				this.position = new Point(Integer.valueOf(x), Integer.valueOf(y));
			} catch (NumberFormatException e) {
				this.position = new Point(0, 0);
			}
	}

	public String getId() {
		return this.id;
	}

	public String getName() {
		return this.name;
	}

	/**
	 * Returns a new instance of the actual object that contains the same attribute values. After executing this method, any change in the
	 * original object will not modify the copy and vice versa. They do not share memory references.
	 * 
	 * @return current object copy.
	 */
	public abstract WebRatioElement getCopy();

	/**
	 * Creates the unit in the WebRatio Model. All object data must be set before calling this method.
	 * 
	 * @param createdElements
	 *            : the elements previously generated. Used to solve the dependency of the parent and source/target
	 */
	public abstract IMFElement generate(Map<String, IMFElement> createdElements);
}
