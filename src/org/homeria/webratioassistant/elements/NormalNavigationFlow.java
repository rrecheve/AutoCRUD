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

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.homeria.webratioassistant.webratio.NewLink;
import org.homeria.webratioassistant.webratio.ProjectParameters;
import org.homeria.webratioassistant.webratio.Utilities;
import org.homeria.webratioassistant.webratio.WebRatioCalls;

import com.webratio.commons.internal.mf.MFElement;
import com.webratio.commons.mf.IMFElement;
import com.webratio.commons.mf.operations.SetAttributeMFOperation;
import com.webratio.ide.model.IAttribute;
import com.webratio.ide.model.IEntity;
import com.webratio.ide.model.ILinkParameter;
import com.webratio.ide.model.IOperationUnit;
import com.webratio.ide.model.IRelationshipRole;
import com.webratio.ide.model.IUnit;

/**
 * This class contains the data previously parsed. It is needed to create the NormalNavigationFlow in the WebRatio Model using generate
 * method.
 */
public class NormalNavigationFlow extends Link {
	private IEntity entity;
	private Map<IRelationshipRole, IAttribute> relshipsSelected;
	private boolean validate;

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
	 * @param validate
	 *            : used to check the attribute "validate" in the Flow. Only "true" or "false" values are allowed. Default value is "true".
	 * @param entity
	 *            : the entity to associate to this unit
	 */
	public NormalNavigationFlow(String id, String name, String sourceId, String targetId, String type, String validate, IEntity entity) {
		super(id, name, sourceId, targetId, type);
		this.entity = entity;

		if (validate.equals("false"))
			this.validate = false;
		else
			this.validate = true;
	}

	/**
	 * Set the relationship roles selected in the UI. Each map entry is a pair <K,V>: Key is the relationship role. Value is the oid(key)
	 * attribute related with the role
	 * 
	 * @param relshipsSelected
	 *            : Map with the relationship roles selected
	 */
	public void setRelshipsSelected(Map<IRelationshipRole, IAttribute> relshipsSelected) {
		this.relshipsSelected = relshipsSelected;
	}

	/* (non-Javadoc)
	 * @see org.homeria.webratioassistant.elements.WebRatioElement#generate(java.util.Map)
	 */
	@Override
	public IMFElement generate(Map<String, IMFElement> createdElements) {
		IMFElement source = createdElements.get(this.sourceId);
		IMFElement target = createdElements.get(this.targetId);

		WebRatioCalls newLinkWRCall = new NewLink(this.name, source, target, "normal");
		IMFElement link = newLinkWRCall.execute();

		if (this.type.equals(ElementTypes.FLOW_ENTRY_TO_CREATE) || this.type.equals(ElementTypes.FLOW_ENTRY_TO_UPDATE)) {
			this.removeAutomaticCoupling(link);
			this.guessCouplingEntryToCreateModify(source, target, link, this.entity, this.relshipsSelected);

		} else if (this.type.equals(ElementTypes.NORMALFLOW_FIXED_VALUE)) {
			this.removeAutomaticCoupling(link);
			this.guessCouplingFixedValue(this.entity, target, link, String.valueOf(0));
			
		} else if (this.type.equals(ElementTypes.NORMALFLOW_IS_NOT_NULL)) {
			IMFElement oidField = ProjectParameters.entryKeyfieldMap.get(source);

			this.putCoupling(oidField, (IOperationUnit) target, "isnotnull", link);
		}

		if (this.validate == false)
			Utilities.setAttribute(link, "validate", "false");

		return link;
	}

	/**
	 * LinkParameter for a keycondition in which in the mapping the origin is going to be a fixed value.
	 * 
	 * @param sourceEntity
	 * @param target
	 * @param link
	 * @param value
	 */
	private void guessCouplingFixedValue(IEntity sourceEntity, IMFElement target, IMFElement link, String value) {

		// Use to keyCondition:

		// <Link id="ln3" name="New Town" to="seu1" type="normal" validate="true">
		// <LinkParameter id="par21" name="0_KeyCondition3 [oid]" sourceValue="0" target="kcond3.att2"/>
		// </Link>

		IAttribute attribute;
		String fieldName;
		IMFElement linkParameter;
		String keyAttribute;

		try {
			// Get list of source entity attributes
			List<IAttribute> attList = sourceEntity.getAllAttributeList();

			// Generate map for the lists of fields and attributes
			Map<String, IAttribute> attMap = new HashMap<String, IAttribute>();

			for (Iterator<IAttribute> iter = attList.iterator(); iter.hasNext();) {
				attribute = iter.next();

				keyAttribute = Utilities.getAttribute(attribute, "key");
				if (keyAttribute.equals("true")) {
					attMap.put(Utilities.getAttribute(attribute, "name"), attribute);
				}
			}

			if (null != attMap && attMap.size() > 0) {
				for (Iterator<String> iter = attMap.keySet().iterator(); iter.hasNext();) {
					fieldName = iter.next();
					attribute = attMap.get(fieldName);
					// If it returns an attribute is a coupling by attribute
					if (attribute != null) {
						keyAttribute = Utilities.getAttribute(attribute, "key");
						if (keyAttribute.equals("true")) {
							linkParameter = this.createParameterField2AttValue(attribute, target, value, link, true);
							((MFElement) link).addChild(linkParameter, null);
						}
					}
				}
			}
		} catch (Exception e) {
			// nothing to do, its only a linkParameter
		}
	}

	/**
	 * Creates the linkParameter
	 * 
	 * @param attribute
	 * @param targetUnit
	 * @param value
	 * @param link
	 * @param key
	 * @return the linkParameter created
	 */
	private IMFElement createParameterField2AttValue(IAttribute attribute, IMFElement targetUnit, String value, IMFElement link, boolean key) {

		IMFElement linkParameter;
		String idAtt = Utilities.getAttribute(attribute, "id");

		linkParameter = this.createLinkParameter(link.getModelId(), ProjectParameters.getWebProject().getIdProvider(),
				link.getFinalId());
		new SetAttributeMFOperation(linkParameter, "id", this.cleanIds(link.getIdsByFinalId().toString()) + "#"
				+ linkParameter.getFinalId(), link.getRootElement()).execute();
		// If it is not of type key (to relate the oid) it is done just like the previous function.
		if (key) {
			// If it is related to an oid you need the keyCondition of the element to create the parameter of the link, besides changing the
			// format of the name
			IMFElement keyCondition = targetUnit.selectSingleElement("Selector").selectSingleElement("KeyCondition");
			String nameKey = Utilities.getAttribute(keyCondition, "name");

			new SetAttributeMFOperation(linkParameter, "name", value + "_" + nameKey + " [oid]", link.getRootElement()).execute();

			new SetAttributeMFOperation(linkParameter, "sourceValue", value, link.getRootElement()).execute();

			new SetAttributeMFOperation(linkParameter, "target", this.cleanIds(keyCondition.getIdsByFinalId().toString()) + "." + idAtt,
					link.getRootElement()).execute();

		}
		return linkParameter;
	}

	/**
	 * This method puts a coupling between two units, especially used for isNotNullUnit
	 * 
	 * @param oidField
	 *            : Field of the form / entity used for coupling
	 * @param target
	 *            : Target unit (isNotNullUnit)
	 * @param type
	 *            : Type of coupling (eg isnotnull)
	 * @param link
	 *            : Link in which the coupling is created
	 */
	private void putCoupling(IMFElement oidField, IUnit target, String type, IMFElement link) {
		// The automaticCoupling of the link is removed, to do it manually
		Utilities.setAttribute(link, "automaticCoupling", null);
		String name = Utilities.getAttribute(oidField, "name").toLowerCase();

		ILinkParameter linkParameter = this.createLinkParameter(link.getModelId(), ProjectParameters.getWebProject().getIdProvider(),
				link.getFinalId());
		new SetAttributeMFOperation(linkParameter, "id", this.cleanIds(link.getIdsByFinalId().toString()) + "#"
				+ linkParameter.getFinalId(), link.getRootElement()).execute();
		new SetAttributeMFOperation(linkParameter, "name", name + "_" + name, link.getRootElement()).execute();

		new SetAttributeMFOperation(linkParameter, "source", this.cleanIds(oidField.getIdsByFinalId().toString()), link.getRootElement())
				.execute();
		new SetAttributeMFOperation(linkParameter, "target", this.cleanIds(target.getIdsByFinalId().toString()) + "." + type,
				link.getRootElement()).execute();

		((MFElement) link).addChild(linkParameter, null);
	}

	/* (non-Javadoc)
	 * @see org.homeria.webratioassistant.elements.WebRatioElement#getCopy()
	 */
	@Override
	public WebRatioElement getCopy() {
		return new NormalNavigationFlow(this.id, this.name, this.sourceId, this.targetId, this.type, String.valueOf(this.validate),
				this.entity);
	}
}
