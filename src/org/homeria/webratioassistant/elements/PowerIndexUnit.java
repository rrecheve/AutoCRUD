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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.widgets.Table;
import org.homeria.webratioassistant.webratio.NewUnit;
import org.homeria.webratioassistant.webratio.Utilities;
import org.homeria.webratioassistant.webratio.WebRatioCalls;

import com.webratio.commons.mf.IMFElement;
import com.webratio.ide.model.IAttribute;
import com.webratio.ide.model.IEntity;

/**
 * This class contains the data previously parsed. It is needed to create the PowerIndexUnit in the WebRatio Model using generate method.
 */
public class PowerIndexUnit extends Unit {

	private String parentId;
	private String selectedAttributes;
	private Table table;

	/**
	 * Creates a new instance with the given data. It calls super constructor.
	 * 
	 * @param id
	 *            : used to uniquely identify the element.
	 * @param name
	 *            : the element name to display.
	 * @param parentId
	 *            : The id of the parent of this unit
	 * @param x
	 *            : Relative X coordinate. Used to place the element in the model.
	 * @param y
	 *            : Relative Y coordinate. Used to place the element in the model.
	 * @param entity
	 *            : the entity to associate to this unit
	 */
	public PowerIndexUnit(String id, String name, String parentId, String x, String y, IEntity entity) {
		super(id, name, x, y, entity);
		this.parentId = parentId;
		this.selectedAttributes = "";
	}

	/**
	 * Associates this object with the widget element (UI) that contains the attributes selected by the user
	 * 
	 * @param table
	 *            widget with the attributes
	 */
	public void setTable(Table table) {
		this.table = table;
	}

	public void setSelectedAttributes(String selectedAttributes) {
		this.selectedAttributes = selectedAttributes;
	}

	/* (non-Javadoc)
	 * @see org.homeria.webratioassistant.elements.WebRatioElement#generate(java.util.Map)
	 */
	@Override
	public IMFElement generate(Map<String, IMFElement> createdElements) {
		IMFElement parent = createdElements.get(this.parentId);

		WebRatioCalls newUnitWRCall = new NewUnit(parent, ElementTypes.POWER_INDEX_UNIT, this.position.x, this.position.y, this.name,
				this.entity);

		IMFElement pIndexUnit = newUnitWRCall.execute();

		Utilities.setAttribute(pIndexUnit, "displayAttributes", this.selectedAttributes);
		
		return pIndexUnit;
	}

	/**
	 * After running this method, the data member 'selectedAttributes' of this object is filled with the names of the selected attributes
	 * contained in the data member 'table'. selectedAttributes member is used by generate method to create the unit with the selected
	 * attributes. This method must be executed before the widget table disposes.
	 */
	public void extractSelectedTableAttributes() {
		List<IAttribute> entityList = this.entity.getAllAttributeList();
		List<IAttribute> itemsSelectedList = new ArrayList<IAttribute>();

		for (int i = 0; i < entityList.size(); i++) {
			if (this.table.getItem(i).getChecked())
				itemsSelectedList.add(entityList.get(i));
		}

		// Transform attributes to string:
		boolean webRatioEntity = false;
		String entityType = Utilities.getAttribute(this.entity, "id");
		if (entityType.equals("User") || entityType.equals("Group") || entityType.equals("Module"))
			webRatioEntity = true;

		this.selectedAttributes = "";
		for (IAttribute att : itemsSelectedList) {
			if (!webRatioEntity) {
				// If it is not own webratio it is generated with the format ent1 # att1
				this.selectedAttributes = this.selectedAttributes + this.entity.getFinalId() + "#" + att.getFinalId() + " ";
			} else {
				this.selectedAttributes = this.selectedAttributes + att.getFinalId() + " ";
			}
		}
		if (this.selectedAttributes.length() != 0)
			// We format the string correctly by removing the final white space
			this.selectedAttributes = this.selectedAttributes.substring(0, this.selectedAttributes.length() - 1);
	}

	/* (non-Javadoc)
	 * @see org.homeria.webratioassistant.elements.WebRatioElement#getCopy()
	 */
	@Override
	public WebRatioElement getCopy() {
		WebRatioElement piu = new PowerIndexUnit(this.id, this.name, this.parentId, String.valueOf(this.position.x),
				String.valueOf(this.position.y), this.entity);
		((PowerIndexUnit) piu).setSelectedAttributes(this.selectedAttributes);
		return piu;
	}
}
