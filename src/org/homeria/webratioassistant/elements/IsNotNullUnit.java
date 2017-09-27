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

import org.homeria.webratioassistant.webratio.NewUnit;
import org.homeria.webratioassistant.webratio.WebRatioCalls;

import com.webratio.commons.mf.IMFElement;
import com.webratio.ide.model.IEntity;

/**
 * This class contains the data previously parsed. It is needed to create the IsNotNullUnit in the WebRatio Model using generate method.
 */
public class IsNotNullUnit extends UnitOutsidePage {

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
	public IsNotNullUnit(String id, String name, String x, String y, IEntity entity) {
		super(id, name, x, y, entity);
	}

	/* (non-Javadoc)
	 * @see org.homeria.webratioassistant.elements.WebRatioElement#generate(java.util.Map)
	 */
	@Override
	public IMFElement generate(Map<String, IMFElement> createdElements) {

		WebRatioCalls newUnitWRCall = new NewUnit(this.parent, ElementTypes.IS_NOT_NULL_UNIT, this.position.x, this.position.y, this.name,
				this.entity);
		return newUnitWRCall.execute();
	}

	/* (non-Javadoc)
	 * @see org.homeria.webratioassistant.elements.WebRatioElement#getCopy()
	 */
	@Override
	public WebRatioElement getCopy() {
		return new IsNotNullUnit(this.id, this.name, String.valueOf(this.position.x), String.valueOf(this.position.y), this.entity);
	}

}
