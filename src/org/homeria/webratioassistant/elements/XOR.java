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

import org.homeria.webratioassistant.webratio.NewAlternative;
import org.homeria.webratioassistant.webratio.WebRatioCalls;

import com.webratio.commons.mf.IMFElement;

/**
 * This class contains the data previously parsed. It is needed to create the XOR Page in the WebRatio Model using generate method.
 */
public class XOR extends WebRatioElement {

	private String parentId;

	/**
	 * Creates a new instance with the given data.
	 * 
	 * @param id
	 *            : used to uniquely identify the element.
	 * @param name
	 *            : the element name to display.
	 * @param parentId
	 *            : The id of the parent of this unit. If the parent is a SiteView or Area, this parameter is null.
	 * @param x
	 *            : Relative X coordinate. Used to place the element in the model.
	 * @param y
	 *            : Relative Y coordinate. Used to place the element in the model.
	 */
	public XOR(String id, String name, String parentId, String x, String y) {
		super(id, name, x, y);
		this.parentId = parentId;
	}

	/* (non-Javadoc)
	 * @see org.homeria.webratioassistant.elements.WebRatioElement#generate(java.util.Map)
	 */
	@Override
	public IMFElement generate(Map<String, IMFElement> createdElements) {
		IMFElement parent = createdElements.get(this.parentId);

		WebRatioCalls newAlternWRCall = new NewAlternative(parent, this.position.x, this.position.y, this.name);
		return newAlternWRCall.execute();
	}

	/* (non-Javadoc)
	 * @see org.homeria.webratioassistant.elements.WebRatioElement#getCopy()
	 */
	@Override
	public WebRatioElement getCopy() {
		return new XOR(this.id, this.name, this.parentId, String.valueOf(this.position.x), String.valueOf(this.position.y));
	}
}
