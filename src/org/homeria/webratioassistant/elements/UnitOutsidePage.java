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

import org.eclipse.draw2d.geometry.Point;

import com.webratio.commons.mf.IMFElement;
import com.webratio.ide.model.IEntity;

/**
 * Hierarchy part used to serialize the entity-based elements parsed in the pattern, whose parent is the SiteView. Abstract class.
 */
public abstract class UnitOutsidePage extends Unit {
	protected IMFElement parent;

	/**
	 * Creates a new instance with the given data. It calls super constructor.
	 * 
	 * @param id
	 *            : used to uniquely identify the element.
	 * @param name
	 *            : the element name to display.
	 * @param x
	 *            : Relative X coordinate. Used to place the element in the model.
	 * @param y
	 *            : Relative Y coordinate. Used to place the element in the model.
	 * @param entity
	 *            : the entity to associate to this unit
	 */
	public UnitOutsidePage(String id, String name, String x, String y, IEntity entity) {
		super(id, name, x, y, entity);
	}

	/**
	 * Set the SiteView or Area which is the parent of the unit
	 * 
	 * @param parent
	 *            : the SiteView or Area
	 */

	public void setParent(IMFElement parent) {
		this.parent = parent;
	}

	/**
	 * Adds the current coordinates to the ones given by parameter
	 * 
	 * @param coords
	 *            : the coordinates to add
	 */
	public void addToCurrentPosition(Point coords) {
		this.position.x += coords.x;
		this.position.y += coords.y;
	}
}
