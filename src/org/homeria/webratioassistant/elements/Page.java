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
import org.homeria.webratioassistant.webratio.NewPage;
import org.homeria.webratioassistant.webratio.Utilities;
import org.homeria.webratioassistant.webratio.WebRatioCalls;

import com.webratio.commons.mf.IMFElement;
import com.webratio.ide.model.IAlternative;
import com.webratio.ide.model.IPage;
import com.webratio.ide.model.ISiteView;

/**
 * This class contains the data previously parsed. It is needed to create the Page in the WebRatio Model using generate method.
 */
public class Page extends WebRatioElement {

	private String parentId;
	private boolean isLandmark;
	private boolean isDefaultPage;

	private IMFElement parent;

	/**
	 * Creates a new instance with the given data.
	 * 
	 * @param id
	 *            : used to uniquely identify the element.
	 * @param name
	 *            : the element name to display.
	 * @param parentId
	 *            : The id of the parent of this unit. If the parent is a SiteView or Area, this parameter is null.
	 * @param defaultPage
	 *            : set if is a default page or not. Only "true" or "false" values are allowed. Default value is "false".
	 * @param landmark
	 *            : set the property 'landmark' checked or not. Only "true" or "false" values are allowed. Default value is "false".
	 * @param x
	 *            : Relative X coordinate. Used to place the element in the model.
	 * @param y
	 *            : Relative Y coordinate. Used to place the element in the model.
	 */
	public Page(String id, String name, String parentId, String defaultPage, String landmark, String x, String y) {
		super(id, name, x, y);
		this.parentId = parentId;
		this.parent = null;

		if (defaultPage.equals("true"))
			this.isDefaultPage = true;
		else
			this.isDefaultPage = false;

		if (landmark.equals("true"))
			this.isLandmark = true;
		else
			this.isLandmark = false;

	}

	/**
	 * Set the parent of this page. Used to establish the siteview or area that is the immediate parent.
	 * 
	 * @param parent
	 *            : the SiteView or Area
	 */
	public void setParent(IMFElement parent) {
		this.parent = parent;
	}

	/**
	 * Get the parent of this Page
	 * 
	 * @return The SiteView or Area that is the parent of this Page
	 */
	public IMFElement getParent() {
		return this.parent;
	}

	/**
	 * Adds the current coordinates to the ones given by parameter. NOTE: this is done ONLY if the parent is a SiteView
	 * 
	 * @param coords
	 *            : the coordinates to add
	 */
	public void addToCurrentPosition(Point coords) {
		if (null == this.parentId && this.parent instanceof ISiteView) {
			this.position.x += coords.x;
			this.position.y += coords.y;
		}
	}

	/* (non-Javadoc)
	 * @see org.homeria.webratioassistant.elements.WebRatioElement#generate(java.util.Map)
	 */
	@Override
	public IMFElement generate(Map<String, IMFElement> createdElements) {
		IMFElement parent;
		if (null == this.parentId)
			parent = this.parent;
		else
			parent = createdElements.get(this.parentId);

		if (this.isDefaultPage && parent instanceof IAlternative) {
			// Get the default page from XOR page
			IPage defaultPage = ((IAlternative) parent).getPageList().get(0);
			Utilities.setAttribute(defaultPage, "name", this.name);

			return defaultPage;
		}

		WebRatioCalls newPageWRCall = new NewPage(parent, this.position.x, this.position.y, this.name, this.isLandmark);
		return newPageWRCall.execute();
	}

	/* (non-Javadoc)
	 * @see org.homeria.webratioassistant.elements.WebRatioElement#getCopy()
	 */
	@Override
	public WebRatioElement getCopy() {
		return new Page(this.id, this.name, this.parentId, String.valueOf(this.isDefaultPage), String.valueOf(this.isLandmark),
				String.valueOf(this.position.x), String.valueOf(this.position.y));
	}
}
