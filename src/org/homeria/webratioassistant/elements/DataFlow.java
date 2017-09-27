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
import com.webratio.ide.model.IRelationship;
import com.webratio.ide.model.IRelationshipRole;
import com.webratio.ide.model.ISubUnit;
import com.webratio.ide.model.IUnit;

/**
 * This class contains the data previously parsed. It is needed to create the DataFlow in the WebRatio Model using generate method.
 */
public class DataFlow extends Link {
	private IRelationshipRole role;
	private IEntity entity;
	private Map<IRelationshipRole, IAttribute> relshipsSelected;

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
	 * @param entity
	 *            : the entity to associate to this unit
	 * @param role
	 *            : the relationship role
	 */
	public DataFlow(String id, String name, String sourceId, String targetId, String type, IEntity entity, IRelationshipRole role) {
		super(id, name, sourceId, targetId, type);
		this.entity = entity;
		this.role = role;

	}

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
	 * @param entity
	 *            : the entity to associate to this unit
	 */
	public DataFlow(String id, String name, String sourceId, String targetId, String type, IEntity entity) {
		super(id, name, sourceId, targetId, type);
		this.type = type;
		this.entity = entity;
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

		WebRatioCalls newUnitWRCall = new NewLink(this.name, source, target, "transport");
		IMFElement link = newUnitWRCall.execute();

		if (this.type.equals(ElementTypes.DATAFLOW_PRELOAD)) {
			this.removeAutomaticCoupling(link);
			this.putPreload(target, this.role, link);

		} else if (this.type.equals(ElementTypes.DATAFLOW_ENTRY_TO_CONNECT) || this.type.equals(ElementTypes.DATAFLOW_ENTRY_TO_RECONNECT)) {
			this.removeAutomaticCoupling(link);
			this.guessCouplingEntryToConnect(source, target, this.getTargetEntity(this.role), this.role, link);

		} else if (this.type.equals(ElementTypes.DATAFLOW_UNIT_TO_ENTRY)) {
			this.removeAutomaticCoupling(link);
			this.guessCouplingUnitToEntry(source, target, this.entity, link);

		} else if (this.type.equals(ElementTypes.DATAFLOW_UNIT_TO_ENTRY_ROLE)) {
			this.removeAutomaticCoupling(link);
			this.guessCouplingUnitToEntry(source, target, this.entity, link, this.role);

		} else if (this.type.equals(ElementTypes.FLOW_ENTRY_TO_CREATE)) {
			this.removeAutomaticCoupling(link);
			this.guessCouplingEntryToCreateModify(source, target, link, this.entity, this.relshipsSelected);
		}

		return link;
	}

	/**
	 * This method creates the entryUnit fields that are related to the relationship role
	 * 
	 * @param target
	 *            : EntryUnit
	 * @param role
	 *            : Relationship role
	 * @param link
	 *            : Link making the coupling
	 */
	private void putPreload(IMFElement target, IRelationshipRole role, IMFElement link) {
		IEntity sourceEntity = this.getTargetEntity(role);
		IAttribute selectAtt = this.relshipsSelected.get(role);
		ISubUnit field;
		String fieldName;
		List<ISubUnit> fieldList = ((IUnit) target).getSubUnitList();
		String roleName = Utilities.getAttribute(role, "name");
		IEntity entityPreload = this.getTargetEntity(role);
		List<IAttribute> attList = entityPreload.getAllAttributeList();
		IAttribute attribute = null;
		for (int i = 0; i < attList.size(); i++) {
			if (Utilities.getAttribute(attList.get(i), "key").equals("true")) {
				attribute = attList.get(i);
				break;
			}
		}

		for (Iterator<ISubUnit> iter = fieldList.iterator(); iter.hasNext();) {
			field = iter.next();
			fieldName = Utilities.getAttribute(field, "name");
			if (fieldName.equals(roleName)) {
				this.createParameterPreload(attribute, field, selectAtt, link, sourceEntity);
				break;
			}
		}
	}

	/**
	 * Gets the target entity given a relationship role
	 * 
	 * @param role
	 *            : Role from which to obtain the target entity
	 * @return target entity required
	 */
	private IEntity getTargetEntity(IRelationshipRole role) {
		IRelationship relation = (IRelationship) role.getParentElement();
		if (relation.getTargetEntity() == this.entity) {
			return relation.getSourceEntity();
		} else
			return relation.getTargetEntity();

	}

	/**
	 * Adds a parameter to a link, to connect an attribute with a form field
	 * 
	 * @param attribute
	 *            : Attribute we want to connect
	 * @param subUnit
	 *            : Field of the form that we want to connect
	 * @param link
	 *            : Link that will contain the parameter
	 * @return: The parameter created
	 */
	private IMFElement createParameter(IAttribute attribute, ISubUnit subUnit, IMFElement link) {
		IMFElement linkParameter;
		IMFElement field = subUnit;
		String name = Utilities.getAttribute(field, "name");
		// We create a linkParameter with the necessary data
		linkParameter = this.createLinkParameter(link.getModelId(), ProjectParameters.getWebProject().getIdProvider(),
				link.getFinalId());
		// Id: form of the id of the link plus the id of the elements
		new SetAttributeMFOperation(linkParameter, "id", this.cleanIds(link.getIdsByFinalId().toString()) + "#"
				+ linkParameter.getFinalId(), link.getRootElement()).execute();
		// Name: is formed with the name of the field
		new SetAttributeMFOperation(linkParameter, "name", name + "_" + name, link.getRootElement()).execute();
		// Source: with attribute data
		new SetAttributeMFOperation(linkParameter, "source", this.cleanIds(attribute.getIdsByFinalId().toString()) + "Array",
				link.getRootElement()).execute();
		// Target: created with form field fields
		new SetAttributeMFOperation(linkParameter, "target", this.cleanIds(field.getIdsByFinalId().toString()) + "_slot",
				link.getRootElement()).execute();

		return linkParameter;
	}

	/**
	 * Create a parameter in a link to be able to preload attributes
	 * 
	 * @param attribute
	 *            : Attribute we want to relate
	 * @param field
	 *            : Field we want to preload
	 * @param selectAtt
	 *            : Attribute needed to get the name of the source
	 * @param link
	 *            : Link that will contain the parameter
	 * @param sourceEntity
	 *            : Entity from which the attributes
	 */
	private void createParameterPreload(IAttribute attribute, ISubUnit field, IAttribute selectAtt, IMFElement link, IEntity sourceEntity) {
		IMFElement linkParameter;
		IEntity parent = sourceEntity;

		String id = this.cleanIds(link.getIdsByFinalId().toString());
		String source_label = this.cleanIds(selectAtt.getIdsByFinalId().toString()) + "Array";
		String source_output = this.cleanIds(attribute.getIdsByFinalId().toString()) + "Array";

		String name_label = Utilities.getAttribute(selectAtt, "name") + "_" + Utilities.getAttribute(parent, "name") + " [label]";
		String name_output = "oid_" + Utilities.getAttribute(parent, "name") + " [output]";

		linkParameter = this.createLinkParameter(link.getModelId(), ProjectParameters.getWebProject().getIdProvider(),
				link.getFinalId());
		new SetAttributeMFOperation(linkParameter, "id", id + "#" + linkParameter.getFinalId(), link.getRootElement()).execute();
		new SetAttributeMFOperation(linkParameter, "name", name_label, link.getRootElement()).execute();
		new SetAttributeMFOperation(linkParameter, "source", source_label, link.getRootElement()).execute();
		new SetAttributeMFOperation(linkParameter, "target", this.cleanIds(field.getIdsByFinalId().toString()) + "_label",
				link.getRootElement()).execute();
		((MFElement) link).addChild(linkParameter, null);

		linkParameter = this.createLinkParameter(link.getModelId(), ProjectParameters.getWebProject().getIdProvider(),
				link.getFinalId());
		new SetAttributeMFOperation(linkParameter, "id", id + "#" + linkParameter.getFinalId(), link.getRootElement()).execute();
		new SetAttributeMFOperation(linkParameter, "name", name_output, link.getRootElement()).execute();
		new SetAttributeMFOperation(linkParameter, "source", source_output, link.getRootElement()).execute();
		new SetAttributeMFOperation(linkParameter, "target", this.cleanIds(field.getIdsByFinalId().toString()) + "_output",
				link.getRootElement()).execute();
		((MFElement) link).addChild(linkParameter, null);
	}

	/**
	 * Performs the GuessCoupling between an EntryUnit and a ConnectUnit. Used to connect the entity oid with the Role that is in the
	 * condition of the ConnectUnit
	 * 
	 * @param source
	 *            : Source unit (entryUnit)
	 * @param target
	 *            : Target unit (connect, disconnect or reconnect unit)
	 * @param targetEntity
	 *            : Entity of the target unit.
	 * @param role
	 *            : Role that is in the roleCondition of the target unit
	 * @param link
	 *            : Link to which linkParameter is added
	 */
	private void guessCouplingEntryToConnect(IMFElement source, IMFElement target, IEntity targetEntity, IRelationshipRole role,
			IMFElement link) {
		// We get the keyCondition of the target unit
		IOperationUnit connectUnit = (IOperationUnit) target;
		IMFElement keyCondition = connectUnit.selectSingleElement("TargetSelector").selectSingleElement("KeyCondition");
		String name = Utilities.getAttribute(keyCondition, "name");
		String nameToSearch = Utilities.getAttribute(targetEntity, "name");

		IAttribute keyAtt = null;
		IUnit entryUnit;
		entryUnit = (IUnit) source;
		// We get the list of form fields and the list of attributes of the target entity
		List<ISubUnit> fieldList = entryUnit.getSubUnitList();
		List<IAttribute> attList = targetEntity.getAllAttributeList();

		// We look for the attribute that works as key
		for (int i = 0; i < attList.size(); i++) {
			if (Utilities.getAttribute(attList.get(i), "key").equals("true")) {
				keyAtt = attList.get(i);
				break;
			}
		}

		// We create a hashMap with the name of the field and the field.
		Map<String, ISubUnit> fieldMap = new HashMap<String, ISubUnit>();
		Iterator<ISubUnit> fieldIterator = fieldList.iterator();
		ISubUnit field;
		while (fieldIterator.hasNext()) {
			field = fieldIterator.next();
			fieldMap.put(Utilities.getAttribute(field, "name"), field);
		}

		// We get the field that relates to the role
		field = fieldMap.get(Utilities.getAttribute(role, "name"));

		// We create the link parameter that we add to the link.
		ILinkParameter linkParameter = this.createLinkParameter(link.getModelId(), ProjectParameters.getWebProject().getIdProvider(),
				link.getFinalId());
		new SetAttributeMFOperation(linkParameter, "id", this.cleanIds(link.getIdsByFinalId().toString()) + "#"
				+ linkParameter.getFinalId(), link.getRootElement()).execute();

		new SetAttributeMFOperation(linkParameter, "source", this.cleanIds(field.getIdsByFinalId().toString()), link.getRootElement())
				.execute();

		new SetAttributeMFOperation(linkParameter, "name", nameToSearch + "_" + name + " [oid] [" + nameToSearch + "] [Target]",
				link.getRootElement()).execute();
		new SetAttributeMFOperation(linkParameter, "target", this.cleanIds(keyCondition.getIdsByFinalId().toString()) + "."
				+ this.cleanIds(keyAtt.getIdsByFinalId().toString()), link.getRootElement()).execute();

		((MFElement) link).addChild(linkParameter, null);
	}

	/**
	 * Performs the GuessCoupling between any Unit and a EntryUnit
	 * 
	 * @param source
	 *            : Unit (SelectorUnit, contentUnit...)
	 * @param target
	 *            : EntryUnit
	 * @param sourceEntity
	 *            : Entity that is selected in the source unit
	 * @param link
	 *            : Link in which the linkParameter is created
	 */
	private void guessCouplingUnitToEntry(IMFElement source, IMFElement target, IEntity sourceEntity, IMFElement link) {

		// Get field list
		List<ISubUnit> fieldList = ((IUnit) target).getSubUnitList();
		// Get list of source entity attributes
		List<IAttribute> attList = sourceEntity.getAllAttributeList();

		// Generate maps for lists of fields and attributes
		Map<String, IAttribute> attMap = new HashMap<String, IAttribute>();
		Map<String, ISubUnit> fieldMap = new HashMap<String, ISubUnit>();

		// Init hashmaps
		for (ISubUnit field : fieldList)
			fieldMap.put(Utilities.getAttribute(field, "name"), field);

		for (IAttribute att : attList)
			attMap.put(Utilities.getAttribute(att, "name"), att);

		ISubUnit field;
		IAttribute attribute;
		String fieldType;
		IMFElement linkParameter;
		IRelationshipRole relationRole;

		for (String fieldName : fieldMap.keySet()) {
			attribute = attMap.get(fieldName);
			// If it returns an attribute is a coupling by attribute
			if (attribute != null) {
				linkParameter = this.createParameter(attribute, fieldMap.get(fieldName), link);
				((MFElement) link).addChild(linkParameter, null);
			} else {
				// Otherwise it is a coupling with selection or multiselection
				relationRole = this.findRelation(fieldName, this.entity);

				field = fieldMap.get(fieldName);
				fieldType = field.getQName().getName();
				if (fieldType.equals("SelectionField")) {
					linkParameter = this.createParameterRoleToField(relationRole, field, link, false);
					((MFElement) link).addChild(linkParameter, null);
				}
			}
		}
	}

	/**
	 * Performs the GuessCoupling between any Unit and a EntryUnit
	 * 
	 * @param source
	 *            : unit (SelectorUnit, contentUnit...)
	 * @param target
	 *            : entryUnit
	 * @param sourceEntity
	 *            : Entity that is selected in the source unit
	 * @param link
	 *            : Link in which the linkParameter is created
	 * @param role
	 *            : relationship role from source unit
	 */
	private void guessCouplingUnitToEntry(IMFElement source, IMFElement target, IEntity sourceEntity, IMFElement link,
			IRelationshipRole role) {
		ISubUnit field;
		ISubUnit preselect = null;
		String fieldName;
		IMFElement linkParameter;
		String roleName = Utilities.getAttribute(role, "name");
		// Get field list
		List<ISubUnit> fieldList = ((IUnit) target).getSubUnitList();

		// init hashMap
		for (Iterator<ISubUnit> iter = fieldList.iterator(); iter.hasNext();) {
			field = iter.next();
			fieldName = Utilities.getAttribute(field, "name");
			if (fieldName.contains(roleName)) {
				preselect = field;
				break;
			}
		}
		linkParameter = this.createParameterRoleToField(role, preselect, link, true);
		((MFElement) link).addChild(linkParameter, null);
	}

	/**
	 * Create the parameters of the link that links the role with a form field
	 * 
	 * @param role
	 *            : Role from which to obtain the source of the parameter
	 * @param field
	 *            : Field to which we want to link
	 * @param link
	 *            : Link that will contain the parameter
	 * @param multi
	 *            : Used to distinguish between selectionField and multiSelectionField
	 * @return: The parameter created.
	 */
	private IMFElement createParameterRoleToField(IRelationshipRole role, ISubUnit field, IMFElement link, boolean multi) {
		IMFElement linkParameter;
		String nameRole = Utilities.getAttribute(role, "name");
		String idRole = Utilities.getAttribute(role, "id");
		IAttribute attribute = this.relshipsSelected.get(role);
		IEntity parentEntity = (IEntity) attribute.getParentElement();

		List<IAttribute> attList = parentEntity.getAllAttributeList();

		for (int i = 0; i < attList.size(); i++) {
			if (Utilities.getAttribute(attList.get(i), "key").equals("true")) {
				attribute = attList.get(i);
				break;
			}
		}

		linkParameter = this.createLinkParameter(link.getModelId(), ProjectParameters.getWebProject().getIdProvider(),
				link.getFinalId());

		new SetAttributeMFOperation(linkParameter, "id", this.cleanIds(link.getIdsByFinalId().toString()) + "#"
				+ linkParameter.getFinalId(), link.getRootElement()).execute();

		if (multi) {
			new SetAttributeMFOperation(linkParameter, "name", Utilities.getAttribute(attribute, "name") + "_" + nameRole
					+ " - Preselection", link.getRootElement()).execute();
			new SetAttributeMFOperation(linkParameter, "source", this.cleanIds(attribute.getIdsByFinalId().toString()) + "Array",
					link.getRootElement()).execute();

		} else {
			new SetAttributeMFOperation(linkParameter, "name", nameRole + ".oid_" + nameRole + " - Preselection", link.getRootElement())
					.execute();
			new SetAttributeMFOperation(linkParameter, "source", idRole + "_" + this.cleanIds(attribute.getIdsByFinalId().toString())
					+ "Array", link.getRootElement()).execute();
		}

		new SetAttributeMFOperation(linkParameter, "target", this.cleanIds(field.getIdsByFinalId().toString()) + "_presel",
				link.getRootElement()).execute();

		return linkParameter;
	}

	/* (non-Javadoc)
	 * @see org.homeria.webratioassistant.elements.WebRatioElement#getCopy()
	 */
	@Override
	public WebRatioElement getCopy() {
		return new DataFlow(this.id, this.name, this.sourceId, this.targetId, this.type, this.entity, this.role);
	}
}
