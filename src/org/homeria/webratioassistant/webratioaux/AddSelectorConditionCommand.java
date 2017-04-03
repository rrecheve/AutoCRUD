/**
 * PROYECTO FIN DE CARRERA:
 * 		- Título: Generación automática de la arquitectura de una aplicación web en WebML a partir de la
 *		  		  especificación de requisitos
 * REALIZADO POR:
 * 		- CARLOS AGUADO FUENTES, DNI: 76036306P
 * 		- INGENIERIA INFORMATICA: 2012/2013, CONVOCATORIA DE JUNIO 
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
		IMFElement sel = unit
				.selectSingleElement(this.selType.getElementName());
		if (sel != null) {
			this.parent = sel;
			this.addSelector = false;
		} else {
			this.parent = unit;
			this.addSelector = true;
		}
	}

	@SuppressWarnings("unchecked")
	public final void execute() {
		MFUpdater updater = this.parent.getRootElement().getModelUpdater();
		if (this.addSelector) {
			this.newElem = createGenericElement(ISubUnit.class,
					this.selType.getElementName(), "webml");
			Pair id = this.parent
					.getRootElement()
					.getIdProvider()
					.getFirstFreeId(this.parent.valueOf("@id"),
							this.selType.getIdPrefix(), null, true);
			setAttribute(this.newElem, "id", (String) id.first);
			addDefaultProperties(this.newElem, this.selType);
			addChild(this.newElem, this.parent, null);
			this.elemToSelect = addCondition(this.newElem);
		} else {
			this.newElem = (this.elemToSelect = addCondition(this.parent));
		}
		updater.added(this.newElem);
		this.postOperations = updater.update();
		endOperationSession();
		setLabel("Add "
				+ WebMLElementLabelProvider.INSTANCE.getText(this.newElem));
		directEdit(this.elemToSelect);
	}

	@SuppressWarnings("unchecked")
	private IMFElement addCondition(IMFElement selector) {
		IMFElement cond = createGenericElement(ISubUnit.class,
				this.condType.getElementName(), getModelId());
		Pair id = selector
				.getRootElement()
				.getIdProvider()
				.getFirstFreeId(selector.valueOf("@id"),
						this.condType.getIdPrefix(), null, true);
		setAttribute(cond, "id", (String) id.first);
		setAttribute(cond, "name", this.condType.getNamePrefix() + id.second);
		addDefaultProperties(cond, this.condType);
		addChild(cond, selector, null);
		return cond;
	}

	@SuppressWarnings("unchecked")
	private void addDefaultProperties(IMFElement elem, ISubUnitType subUnitType) {
		for (IUnitProperty prop : subUnitType.getProperties()) {
			String defaultValue = prop.get("defaultValue");
			if (!defaultValue.equals("")) {
				String attrName = prop.get("attributeName");
				if (!attrName.equals("")) {
					setAttribute(elem, attrName, defaultValue);
				}
			}
			if ((this.condType instanceof IRelationshipRoleCondition)) {
				String attrName = prop.get("attributeName");
				if ("role".equals(attrName)) {
					IMFElement refElem = this.unit.getElementById(this.unit
							.valueOf("@entity"));
					if ((refElem instanceof IEntity)) {
						List roles = DataModelHelper
								.getAllIncomingRelationshipRoles((IEntity) refElem);
						if (roles.size() == 1)
							setAttribute(elem, "role",
									((IRelationshipRole) roles.get(0))
											.valueOf("@id"));
					}
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	public final void undo() {
		if (this.postOperations != null) {
			executeOperations((List) this.postOperations.second);
		}
		delete(this.newElem);
		endOperationSession();
		select(this.parent);
	}

	@SuppressWarnings("unchecked")
	public final void redo() {
		addChild(this.newElem, this.parent, null);
		if (this.postOperations != null) {
			executeOperations((List) this.postOperations.first);
		}
		endOperationSession();
		select(this.elemToSelect);
	}
}