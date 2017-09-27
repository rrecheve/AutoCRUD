/**
 * WebRatio Assistant v3.0
 * 
 * University of Extremadura (Spain) www.unex.es
 * 
 * Developers:
 * 	- Carlos Aguado Fuentes (v2)
 * 	- Javier Sierra Blázquez (v3.0)
 */
package org.homeria.webratioassistant.webratioaux;

import java.util.List;

import com.webratio.commons.mf.IMFElement;
import com.webratio.commons.mf.operations.IMFOperation;
import com.webratio.commons.mf.operations.MFUpdater;
import com.webratio.commons.mf.ui.commands.AbstractMFCommand;
import com.webratio.commons.startup.commons.Pair;
import com.webratio.ide.core.DataModelHelper;
import com.webratio.ide.model.IEntity;
import com.webratio.ide.model.IRelationshipRole;
import com.webratio.ide.model.ISubUnit;
import com.webratio.ide.ui.viewers.WebMLElementLabelProvider;
import com.webratio.ide.units.core.IRelationshipRoleCondition;
import com.webratio.ide.units.core.ISelector;
import com.webratio.ide.units.core.ISubUnitType;
import com.webratio.ide.units.core.IUnitProperty;

/**
 * Manages the creation of a new selector condition command using WebRatio library calls
 */
public final class AddSelectorConditionCommand extends AbstractMFCommand {
	private IMFElement parent;
	private IMFElement unit;
	private IMFElement newElem;
	private IMFElement elemToSelect;
	private ISelector selType;
	private ISubUnitType condType;
	private Pair<List<IMFOperation>, List<IMFOperation>> postOperations;
	private boolean addSelector;

	public AddSelectorConditionCommand(IMFElement unit, ISubUnitType condType) {
		super(unit);
		this.unit = unit;
		this.condType = condType;
		this.selType = ((ISelector) condType.getParent());
		IMFElement sel = unit.selectSingleElement(this.selType.getElementName());
		if (sel != null) {
			this.parent = sel;
			this.addSelector = false;
		} else {
			this.parent = unit;
			this.addSelector = true;
		}
	}

	public final void execute() {
		MFUpdater updater = this.parent.getRootElement().getModelUpdater();
		if (this.addSelector) {
			this.newElem = this.createGenericElement(ISubUnit.class, this.selType.getElementName(), "webml");
			Pair id = this.parent.getRootElement().getIdProvider()
					.getFirstFreeId(this.parent.valueOf("@id"), this.selType.getIdPrefix(), null, true);
			this.setAttribute(this.newElem, "id", (String) id.first);
			this.addDefaultProperties(this.newElem, this.selType);
			this.addChild(this.newElem, this.parent, null);
			this.elemToSelect = this.addCondition(this.newElem);
		} else {
			this.newElem = (this.elemToSelect = this.addCondition(this.parent));
		}
		updater.added(this.newElem);
		this.postOperations = updater.update();
		this.endOperationSession();
		this.setLabel("Add " + WebMLElementLabelProvider.INSTANCE.getText(this.newElem));
		this.directEdit(this.elemToSelect);
	}

	private IMFElement addCondition(IMFElement selector) {
		IMFElement cond = this.createGenericElement(ISubUnit.class, this.condType.getElementName(), this.getModelId());
		Pair id = selector.getRootElement().getIdProvider()
				.getFirstFreeId(selector.valueOf("@id"), this.condType.getIdPrefix(), null, true);
		this.setAttribute(cond, "id", (String) id.first);
		this.setAttribute(cond, "name", this.condType.getNamePrefix() + id.second);
		this.addDefaultProperties(cond, this.condType);
		this.addChild(cond, selector, null);
		return cond;
	}

	private void addDefaultProperties(IMFElement elem, ISubUnitType subUnitType) {
		for (IUnitProperty prop : subUnitType.getProperties()) {
			String defaultValue = prop.get("defaultValue");
			if (!defaultValue.equals("")) {
				String attrName = prop.get("attributeName");
				if (!attrName.equals("")) {
					this.setAttribute(elem, attrName, defaultValue);
				}
			}
			if ((this.condType instanceof IRelationshipRoleCondition)) {
				String attrName = prop.get("attributeName");
				if ("role".equals(attrName)) {
					IMFElement refElem = this.unit.getElementById(this.unit.valueOf("@entity"));
					if ((refElem instanceof IEntity)) {
						List roles = DataModelHelper.getAllIncomingRelationshipRoles((IEntity) refElem);
						if (roles.size() == 1)
							this.setAttribute(elem, "role", ((IRelationshipRole) roles.get(0)).valueOf("@id"));
					}
				}
			}
		}
	}

	public final void undo() {
		if (this.postOperations != null) {
			this.executeOperations(this.postOperations.second);
		}
		this.delete(this.newElem);
		this.endOperationSession();
		this.select(this.parent);
	}

	public final void redo() {
		this.addChild(this.newElem, this.parent, null);
		if (this.postOperations != null) {
			this.executeOperations(this.postOperations.first);
		}
		this.endOperationSession();
		this.select(this.elemToSelect);
	}
}