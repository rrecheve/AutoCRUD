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

import org.homeria.webratioassistant.webratio.NewLink;
import org.homeria.webratioassistant.webratio.WebRatioCalls;

import com.webratio.commons.mf.IMFElement;

/**
 * This class contains the data previously parsed. It is needed to create the KOLink in the WebRatio Model using generate method.
 */
public class KOLink extends Link {
	private String message;

	/**
	 * Creates a new instance with the given data.
	 * 
	 * @param id
	 *            : used to uniquely identify the element.
	 * @param name
	 *            : the element name to display.
	 * @param sourceId
	 *            : the element's id that is the source of the flow
	 * @param targetId
	 *            : the element's id that is the target of the flow
	 * @param type
	 *            : specifies the type of coupling to do.
	 * @param message
	 *            : the message to show
	 */
	public KOLink(String id, String name, String sourceId, String targetId, String type, String message) {
		super(id, name, sourceId, targetId, type);
		this.message = message;
		if (null == message)
			this.message = "";
		else
			this.message = message;
	}

	/* (non-Javadoc)
	 * @see org.homeria.webratioassistant.elements.WebRatioElement#generate(java.util.Map)
	 */
	@Override
	public IMFElement generate(Map<String, IMFElement> createdElements) {
		IMFElement source = createdElements.get(this.sourceId);
		IMFElement target = createdElements.get(this.targetId);

		WebRatioCalls newLinkWRCall = new NewLink(this.name, source, target, "KOLink");
		IMFElement link = newLinkWRCall.execute();

		if (this.type.equals(ElementTypes.KO_LINK_NO_COUPLING)) {
			this.removeAutomaticCoupling(link);

		} else if (!this.message.isEmpty()) {
			this.removeAutomaticCoupling(link);
			this.putMessageOnMultiMessageUnit(link, target, this.message);
		}

		return link;
	}

	/* (non-Javadoc)
	 * @see org.homeria.webratioassistant.elements.WebRatioElement#getCopy()
	 */
	@Override
	public WebRatioElement getCopy() {
		return new KOLink(this.id, this.name, this.sourceId, this.targetId, this.type, this.message);
	}
}
