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
import org.homeria.webratioassistant.webratio.Utilities;
import org.homeria.webratioassistant.webratio.WebRatioCalls;

import com.webratio.commons.mf.IMFElement;
import com.webratio.ide.model.IEntity;
import com.webratio.ide.model.IRelationshipRole;

/**
 * This class contains the data previously parsed. It is needed to create the ReconnectUnit in the WebRatio Model using generate method.
 */
public class ReconnectUnit extends UnitOutsidePage {
	private IRelationshipRole role;

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
	 * @param role
	 *            : the relationship role
	 */
	public ReconnectUnit(String id, String name, String x, String y, IEntity entity, IRelationshipRole role) {
		super(id, name, x, y, entity);
		this.role = role;
	}

	/* (non-Javadoc)
	 * @see org.homeria.webratioassistant.elements.WebRatioElement#generate(java.util.Map)
	 */
	@Override
	public IMFElement generate(Map<String, IMFElement> createdElements) {
		String idRole = Utilities.getAttribute(this.role, "id");
		WebRatioCalls newUnitWRCall = new NewUnit(this.parent, ElementTypes.RECONNECT_UNIT, this.position.x, this.position.y, this.name,
				this.entity);
		IMFElement reconnectUnit = newUnitWRCall.execute();
		Utilities.setAttribute(reconnectUnit, "relationship", idRole);

		return reconnectUnit;
	}

	/* (non-Javadoc)
	 * @see org.homeria.webratioassistant.elements.WebRatioElement#getCopy()
	 */
	@Override
	public WebRatioElement getCopy() {
		return new ReconnectUnit(this.id, this.name, String.valueOf(this.position.x), String.valueOf(this.position.y), this.entity,
				this.role);
	}
}
