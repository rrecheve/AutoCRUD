/**
 * WebRatio Assistant v3.0
 * 
 * University of Extremadura (Spain) www.unex.es
 * 
 * Developers:
 * 	- Carlos Aguado Fuentes (v2)
 * 	- Javier Sierra Blázquez (v3.0)
 */
package org.homeria.webratioassistant.elements;

import java.util.Map;

import org.eclipse.draw2d.geometry.Point;
import org.homeria.webratioassistant.webratio.NewSelector;
import org.homeria.webratioassistant.webratio.NewUnit;
import org.homeria.webratioassistant.webratio.Utilities;
import org.homeria.webratioassistant.webratio.WebRatioCalls;

import com.webratio.commons.mf.IMFElement;
import com.webratio.ide.model.IEntity;
import com.webratio.ide.model.IRelationshipRole;

/**
 * This class contains the data previously parsed. It is needed to create the SelectorUnit in the WebRatio Model using generate method.
 */
public class SelectorUnit extends UnitOutsidePage {

	private String parentId;
	private String type;
	IRelationshipRole role;

	/**
	 * Creates a new instance with the given data. It calls super constructor.
	 * 
	 * @param id
	 *            : used to uniquely identify the element.
	 * @param name
	 *            : the element name to display.
	 * @param parentId
	 *            : The id of the parent of this unit
	 * @param type
	 *            : type of SelectorUnit. {@link org.homeria.webratioassistant.elements.ElementTypes}
	 * @param x
	 *            : Relative X coordinate. Used to place the element in the model.
	 * @param y
	 *            : Relative Y coordinate. Used to place the element in the model.
	 * @param entity
	 *            : the entity to associate to this unit
	 */
	public SelectorUnit(String id, String name, String parentId, String type, String x, String y, IEntity entity) {
		super(id, name, x, y, entity);
		this.parentId = parentId;
		this.entity = entity;
		this.type = type;
	}

	/**
	 * Creates a new instance with the given data. It calls super constructor.
	 * 
	 * @param id
	 *            : used to uniquely identify the element.
	 * @param name
	 *            : the element name to display.
	 * @param parentId
	 *            : The id of the parent of this unit
	 * @param type
	 *            : type of SelectorUnit. {@link org.homeria.webratioassistant.elements.ElementTypes}
	 * @param x
	 *            : Relative X coordinate. Used to place the element in the model.
	 * @param y
	 *            : Relative Y coordinate. Used to place the element in the model.
	 * @param entity
	 *            : the entity to associate to this unit
	 * @param role
	 *            : the relationship role
	 */
	public SelectorUnit(String id, String name, String parentId, String type, String x, String y, IEntity entity, IRelationshipRole role) {
		super(id, name, x, y, entity);
		this.parentId = parentId;
		this.entity = entity;
		this.type = type;
		this.role = role;
	}

	/* (non-Javadoc)
	 * @see org.homeria.webratioassistant.elements.WebRatioElement#generate(java.util.Map)
	 */
	@Override
	public IMFElement generate(Map<String, IMFElement> createdElements) {
		IMFElement parent;
		if (null == this.parentId || "" == this.parentId)
			parent = this.parent;
		else
			parent = createdElements.get(this.parentId);

		WebRatioCalls newUnitWRCall = new NewUnit(parent, ElementTypes.SELECTOR_UNIT, this.position.x, this.position.y, this.name,
				this.entity);

		IMFElement selector = newUnitWRCall.execute();

		if (this.type.equals(ElementTypes.SELECTOR_KEYCONDITION)) {
			WebRatioCalls addKeyWRCall = new NewSelector(selector, "KeyCondition");
			addKeyWRCall.execute();

		} else if (this.type.equals(ElementTypes.SELECTOR_ROLECONDITION)) {
			WebRatioCalls addRoleWRCall = new NewSelector(selector, "RelationshipRoleCondition");

			IMFElement roleCondition = addRoleWRCall.execute();
			String idRole = Utilities.getAttribute(this.role, "id");
			Utilities.setAttribute(roleCondition, "role", idRole);
		}

		return selector;
	}

	/* (non-Javadoc)
	 * @see org.homeria.webratioassistant.elements.UnitOutsidePage#addToCurrentPosition(org.eclipse.draw2d.geometry.Point)
	 */
	@Override
	public void addToCurrentPosition(Point coords) {
		if (null == this.parentId || "" == this.parentId) {
			this.position.x += coords.x;
			this.position.y += coords.y;
		}
	}

	/* (non-Javadoc)
	 * @see org.homeria.webratioassistant.elements.WebRatioElement#getCopy()
	 */
	@Override
	public WebRatioElement getCopy() {
		return new SelectorUnit(this.id, this.name, this.parentId, this.type, String.valueOf(this.position.x),
				String.valueOf(this.position.y), this.entity, this.role);
	}
}
