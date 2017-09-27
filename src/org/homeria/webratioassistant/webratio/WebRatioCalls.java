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

import org.eclipse.draw2d.geometry.Point;

import com.webratio.commons.mf.IMFElement;

/**
 * Manages the creation of a new WebRatio IMFElement using WebRatio library calls. Abstract class.
 */
public abstract class WebRatioCalls {

	private IMFElement parent;
	private Point point;

	/**
	 * Constructs a new instance.
	 * 
	 * @param parent
	 *            : the parent of the element
	 * @param x
	 *            : X coord to situate the element in WR model
	 * @param y
	 *            : Y coord to situate the element in WR model
	 * 
	 */
	public WebRatioCalls(IMFElement parent, int x, int y) {

		this.parent = parent;
		this.point = new Point(x, y);
	}

	/**
	 * Creates the element in WebRatio model with the info previously provided
	 * 
	 * @return the WebRatio IMFElement created
	 */
	public abstract IMFElement execute();

	/**
	 * the parent of this element
	 * 
	 * @return the parent of this element
	 */
	protected IMFElement getParent() {
		return this.parent;
	}

	/**
	 * Get actual location coordinates
	 * 
	 * @return the point that represents the coordinates
	 */
	protected Point getPoint() {
		return this.point;
	}

}
