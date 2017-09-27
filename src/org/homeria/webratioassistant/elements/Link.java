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

import org.homeria.webratioassistant.webratio.ProjectParameters;
import org.homeria.webratioassistant.webratio.Utilities;

import com.webratio.commons.internal.mf.MFElement;
import com.webratio.commons.mf.IMFElement;
import com.webratio.commons.mf.IMFIdProvider;
import com.webratio.commons.mf.operations.CreateMFOperation;
import com.webratio.commons.mf.operations.SetAttributeMFOperation;
import com.webratio.ide.model.IAttribute;
import com.webratio.ide.model.IEntity;
import com.webratio.ide.model.ILinkParameter;
import com.webratio.ide.model.IRelationship;
import com.webratio.ide.model.IRelationshipRole;
import com.webratio.ide.model.ISubUnit;
import com.webratio.ide.model.IUnit;

/**
 * Hierarchy part used to serialize the links parsed in the pattern. Abstract class.
 */
public abstract class Link extends WebRatioElement {

	protected String sourceId;
	protected String targetId;
	protected String type;

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
	 */
	public Link(String id, String name, String sourceId, String targetId, String type) {
		super(id, name, null, null);
		this.sourceId = sourceId;
		this.targetId = targetId;
		this.type = type;
	}

	public String getSourceId() {
		return this.sourceId;
	}

	public String getTargetId() {
		return this.targetId;
	}

	/**
	 * Removes the default binding. Used to do custom coupling.
	 * 
	 * @param link
	 *            : the link element
	 */
	protected void removeAutomaticCoupling(IMFElement link) {
		Utilities.setAttribute(link, "automaticCoupling", null);
	}

	/**
	 * Performs the guessCoupling between the entryUnit and the Create or Modify unit
	 * 
	 * @param source
	 *            : the entry unit
	 * @param target
	 *            : the create unit or modify unit
	 * @param link
	 *            : link on which the linkParameter is added
	 * @param relshipsSelected
	 *            : relationships selected in the UI
	 * @param entity
	 *            : entity selected in the UI
	 */
	protected void guessCouplingEntryToCreateModify(IMFElement source, IMFElement target, IMFElement link, IEntity entity,
			Map<IRelationshipRole, IAttribute> relshipsSelected) {
		// Variables
		String fieldType;
		IMFElement linkParameter;
		IRelationshipRole role;
		String keyAtt;
		IEntity guessEntity = entity;

		// Getting field list
		List<ISubUnit> fieldList = ((IUnit) source).getSubUnitList();
		// Get list of source entity attributes
		List<IAttribute> attList = (guessEntity).getAllAttributeList();

		String targetType = target.getQName().getName();

		// Generate maps for lists of fields and attributes
		Map<String, IAttribute> attMap = new HashMap<String, IAttribute>();
		Map<String, ISubUnit> fieldMap = new HashMap<String, ISubUnit>();

		// Init maps
		for (ISubUnit field : fieldList)
			fieldMap.put(Utilities.getAttribute(field, "name"), field);

		for (IAttribute attribute : attList)
			attMap.put(Utilities.getAttribute(attribute, "name"), attribute);

		IAttribute attribute;
		ISubUnit field;

		for (String fieldName : fieldMap.keySet()) {
			attribute = attMap.get(fieldName);
			// If it returns an attribute is a coupling by attribute
			if (attribute != null) {
				keyAtt = Utilities.getAttribute(attribute, "key");

				if (!keyAtt.equals("true")) {
					linkParameter = this.createParameterField2Att(attribute, target, fieldMap.get(fieldName), link, false);
					((MFElement) link).addChild(linkParameter, null);
				} else {
					if (targetType.equals("ModifyUnit")) {

						linkParameter = this.createParameterField2Att(attribute, target, fieldMap.get(fieldName), link, true);
						((MFElement) link).addChild(linkParameter, null);
					}
				}
			} else {
				// Otherwise it is a coupling with selection or multiselection
				role = this.findRelation(fieldName, entity);

				field = fieldMap.get(fieldName);
				fieldType = field.getQName().getName();
				if (fieldType.equals("SelectionField")) {
					linkParameter = this.createParameterFieldToRole(role, target, field, link, relshipsSelected);
					((MFElement) link).addChild(linkParameter, null);
				}
			}
		}
	}

	/**
	 * Create a parameter from a field in a form with a role condition.
	 * 
	 * @param role
	 *            : role wanted to connect
	 * @param targetUnit
	 *            : Unit in which will be the roleCondition (createUnit, modifyUnit ...)
	 * @param field
	 *            : field in the form
	 * @param link
	 *            : Link that will contain the parameter
	 * @param relshipsSelected
	 *            : relationships selected in the UI
	 * @return: The parameter created
	 */
	private IMFElement createParameterFieldToRole(IRelationshipRole role, IMFElement targetUnit, ISubUnit field, IMFElement link,
			Map<IRelationshipRole, IAttribute> relshipsSelected) {
		IMFElement linkParameter;
		String roleName = Utilities.getAttribute(role, "name");
		String idRole = Utilities.getAttribute(role, "id");
		IAttribute attribute = relshipsSelected.get(role);
		IEntity parentEntity = (IEntity) attribute.getParentElement();
		String entityName = Utilities.getAttribute(parentEntity, "name");

		List<IAttribute> attList = parentEntity.getAllAttributeList();

		// Browse the attributes by searching for the keyCondition
		for (int i = 0; i < attList.size(); i++) {
			if (Utilities.getAttribute(attList.get(i), "key").equals("true")) {
				attribute = attList.get(i);
				break;
			}
		}
		// As in the previous methods, you create the necessary fields (id, name, source, target)
		String attId = Utilities.getAttribute(attribute, "id");
		String attName = Utilities.getAttribute(attribute, "name");
		linkParameter = this.createLinkParameter(link.getModelId(), ProjectParameters.getWebProject().getIdProvider(), link.getFinalId());

		new SetAttributeMFOperation(linkParameter, "id", this.cleanIds(link.getIdsByFinalId().toString()) + "#"
				+ linkParameter.getFinalId(), link.getRootElement()).execute();

		new SetAttributeMFOperation(linkParameter, "name", roleName + "_" + entityName + "." + attName + "(" + roleName + ")",
				link.getRootElement()).execute();

		new SetAttributeMFOperation(linkParameter, "source", this.cleanIds(field.getIdsByFinalId().toString()), link.getRootElement())
				.execute();
		new SetAttributeMFOperation(linkParameter, "target", this.cleanIds(targetUnit.getIdsByFinalId().toString()) + "." + idRole + "."
				+ attId, link.getRootElement()).execute();

		return linkParameter;
	}

	/**
	 * The relationship between the form field and the attribute of a unit (create, modify ...) is created
	 * 
	 * @param attribute
	 *            : Attribute we want to connect
	 * @param sourceUnit
	 *            : Unit to work on
	 * @param subUnit
	 *            : field
	 * @param link
	 *            : Link that will contain the parameter
	 * @param key
	 *            : To indicate if it is an oid
	 * @return The parameter created
	 */
	private IMFElement createParameterField2Att(IAttribute attribute, IMFElement sourceUnit, ISubUnit subUnit, IMFElement link, boolean key) {
		IMFElement linkParameter;
		IMFElement field = subUnit;

		String name = Utilities.getAttribute(field, "name");

		String attId = Utilities.getAttribute(attribute, "id");

		linkParameter = this.createLinkParameter(link.getModelId(), ProjectParameters.getWebProject().getIdProvider(), link.getFinalId());
		new SetAttributeMFOperation(linkParameter, "id", this.cleanIds(link.getIdsByFinalId().toString()) + "#"
				+ linkParameter.getFinalId(), link.getRootElement()).execute();
		// If it is not of type key (to relate the oid) it is done just like the previous function.
		if (!key) {
			new SetAttributeMFOperation(linkParameter, "name", name + "_" + name, link.getRootElement()).execute();

			new SetAttributeMFOperation(linkParameter, "source", this.cleanIds(field.getIdsByFinalId().toString()), link.getRootElement())
					.execute();

			new SetAttributeMFOperation(linkParameter, "target", this.cleanIds(sourceUnit.getIdsByFinalId().toString()) + "." + attId,
					link.getRootElement()).execute();
		} else {
			// If it is related to an oid you need the keyCondition of the element to create the parameter of the link, besides changing the
			// format of the name
			IMFElement keyCondition = sourceUnit.selectSingleElement("Selector").selectSingleElement("KeyCondition");
			String keyName = Utilities.getAttribute(keyCondition, "name");

			new SetAttributeMFOperation(linkParameter, "name", name + "_" + keyName + " [oid]", link.getRootElement()).execute();

			new SetAttributeMFOperation(linkParameter, "source", this.cleanIds(field.getIdsByFinalId().toString()), link.getRootElement())
					.execute();

			new SetAttributeMFOperation(linkParameter, "target", this.cleanIds(keyCondition.getIdsByFinalId().toString()) + "." + attId,
					link.getRootElement()).execute();

		}
		return linkParameter;
	}

	/**
	 * It adds a message to an OK / KO link addressed to a multiMessageUnit
	 * 
	 * @param link
	 *            : Link to which to add the message
	 * @param target
	 *            : multiMessageUnit that shows the message
	 * @param message
	 *            : message to show
	 */
	protected void putMessageOnMultiMessageUnit(IMFElement link, IMFElement target, String message) {
		ILinkParameter linkParameter = this.createLinkParameter(link.getModelId(), ProjectParameters.getWebProject().getIdProvider(),
				link.getFinalId());

		new SetAttributeMFOperation(linkParameter, "id", this.cleanIds(link.getIdsByFinalId().toString()) + "#"
				+ linkParameter.getFinalId(), link.getRootElement()).execute();

		new SetAttributeMFOperation(linkParameter, "name", message + "_" + "Shown Messages", link.getRootElement()).execute();

		new SetAttributeMFOperation(linkParameter, "sourceValue", message, link.getRootElement()).execute();

		new SetAttributeMFOperation(linkParameter, "target", this.cleanIds(target.getIdsByFinalId().toString()) + ".shownMessages",
				link.getRootElement()).execute();
		((MFElement) link).addChild(linkParameter, null);
	}

	/**
	 * Auxiliary function. Clears a string by removing the first and last character
	 * 
	 * @param string
	 *            : String to be treated
	 * @return the same string without the first and last character
	 */
	protected String cleanIds(String string) {
		return string.substring(1, string.length() - 1);
	}

	/**
	 * Find a relationship by its name
	 * 
	 * @param fieldName
	 *            : Name to look for
	 * @param entity
	 *            : Entity from which relationships are obtained
	 * @return the relationship if found, null otherwise
	 */
	protected IRelationshipRole findRelation(String fieldName, IEntity entity) {

		List<IRelationship> relationList = entity.getIncomingRelationshipList();
		relationList.addAll(entity.getOutgoingRelationshipList());

		IRelationship relation;
		IRelationshipRole role;
		for (Iterator<IRelationship> iter = relationList.iterator(); iter.hasNext();) {
			relation = iter.next();
			role = relation.getRelationshipRole1();
			if (Utilities.getAttribute(role, "name").equals(fieldName)) {
				return role;
			}
			role = relation.getRelationshipRole2();
			if (Utilities.getAttribute(role, "name").equals(fieldName)) {
				return role;
			}
		}
		return null;
	}

	/**
	 * Creates a link parameter
	 * 
	 * @param modelId
	 * @param idProvider
	 * @param parentId
	 * @return the LinkParameter created
	 */
	protected ILinkParameter createLinkParameter(String modelId, IMFIdProvider idProvider, String parentId) {
		Class<ILinkParameter> publicType = ILinkParameter.class;
		ILinkParameter newLinkParameter = (ILinkParameter) new CreateMFOperation(publicType, modelId).execute();
		((MFElement) newLinkParameter).setAttribute("id", idProvider.getFirstFreeId(parentId, publicType, null, true).first);
		return newLinkParameter;
	}
}
