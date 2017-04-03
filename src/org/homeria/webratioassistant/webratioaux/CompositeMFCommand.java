/**
 * PROYECTO FIN DE CARRERA:
 * 		- Título: Generación automática de la arquitectura de una aplicación web en WebML a partir de la
 *		  		  especificación de requisitos
 * REALIZADO POR:
 * 		- CARLOS AGUADO FUENTES, DNI: 76036306P
 * 		- INGENIERIA INFORMATICA: 2012/2013, CONVOCATORIA DE JUNIO 
 */
package org.homeria.webratioassistant.webratioaux;

import java.util.ArrayList;
import java.util.List;

import com.webratio.commons.mf.IMFElement;
import com.webratio.commons.mf.IMFRootElement;
import com.webratio.commons.mf.operations.AddChildMFOperation;
import com.webratio.commons.mf.operations.CreateGenericMFOperation;
import com.webratio.commons.mf.operations.DeleteMFOperation;
import com.webratio.commons.mf.operations.IMFOperation;
import com.webratio.commons.mf.operations.MFUpdater;
import com.webratio.commons.mf.operations.SetAttributeMFOperation;
import com.webratio.commons.mf.ui.commands.AbstractMFCommand;
import com.webratio.commons.startup.commons.Pair;
import com.webratio.ide.model.ISubUnit;
import com.webratio.ide.units.core.ISubUnitType;
import com.webratio.ide.units.core.IUnitProperty;

public class CompositeMFCommand extends AbstractMFCommand {
	private List<IMFOperation> undoOperations = new ArrayList<IMFOperation>();

	private List<IMFOperation> redoOperations = new ArrayList<IMFOperation>();
	private Pair<List<IMFOperation>, List<IMFOperation>> postOperations;
	private MFUpdater updater;

	public CompositeMFCommand(IMFRootElement rootElem) {
		super(rootElem);
		setLabel("Update Model");
		this.updater = rootElem.getModelUpdater();
	}

	@Override
	public void execute() {
		MFUpdater updater = this.rootElem.getRootElement().getModelUpdater();
		this.postOperations = updater.update();
		endOperationSession();
	}

	@Override
	public final void redo() {
		for (IMFOperation op : this.redoOperations) {
			op.execute();
		}
		if (this.postOperations != null) {
			executeOperations(this.postOperations.first);
		}
		endOperationSession();
	}

	@Override
	public final void undo() {
		if (this.postOperations != null) {
			executeOperations(this.postOperations.second);
		}
		for (IMFOperation op : this.undoOperations) {
			op.execute();
		}
		endOperationSession();
	}

	public void setAttributeValue(IMFElement element, String attr, String value) {
		String oldValue = element.valueOf("@" + attr);
		SetAttributeMFOperation redoSetAttr = new SetAttributeMFOperation(
				element, attr, value, this.rootElem);
		SetAttributeMFOperation undoSetAttr = new SetAttributeMFOperation(
				element, attr, oldValue, this.rootElem);
		this.redoOperations.add(redoSetAttr);
		this.undoOperations.add(0, undoSetAttr);
		redoSetAttr.execute();
	}

	@SuppressWarnings("unchecked")
	public IMFElement addSubUnit(ISubUnitType subUnitType, IMFElement parent) {
		IMFElement newSubUnit = (IMFElement) new CreateGenericMFOperation(
				ISubUnit.class, subUnitType.getElementName(), getModelId())
				.execute();
		Pair id = parent
				.getRootElement()
				.getIdProvider()
				.getFirstFreeId(parent.valueOf("@id"),
						subUnitType.getIdPrefix(), null, true);
		setAttribute(newSubUnit, "id", (String) id.first);
		setAttribute(newSubUnit, "name", subUnitType.getNamePrefix()
				+ id.second);
		for (IUnitProperty prop : subUnitType.getProperties()) {
			String defaultValue = prop.get("defaultValue");
			if (!defaultValue.equals("")) {
				String attrName = prop.get("attributeName");
				if (!attrName.equals("")) {
					setAttribute(newSubUnit, attrName, defaultValue);
				}
			}
		}
		AddChildMFOperation addChild = new AddChildMFOperation(newSubUnit,
				parent, null, this.rootElem);
		DeleteMFOperation deleteChild = new DeleteMFOperation(newSubUnit,
				this.rootElem);
		this.redoOperations.add(addChild);
		this.undoOperations.add(0, deleteChild);
		addChild.execute();
		this.updater.added(newSubUnit);
		return newSubUnit;
	}

	@SuppressWarnings("unchecked")
	public IMFElement addElement(Class<? extends IMFElement> elementClass,
			IMFElement parent) {
		Pair idPair = parent
				.getRootElement()
				.getIdProvider()
				.getFirstFreeId(parent.valueOf("@id"), elementClass, null, true);
		String id = (String) idPair.first;

		IMFElement element = createElement(elementClass, "webml");
		setAttribute(element, "id", id);
		IMFOperation addChild = new AddChildMFOperation(element, parent, null,
				this.rootElem);
		IMFOperation deleteChild = new DeleteMFOperation(element, this.rootElem);
		addChild.execute();
		this.updater.added(element);
		this.updater.prepareToPostUpdate(element);
		this.redoOperations.add(addChild);
		this.undoOperations.add(deleteChild);
		return element;
	}
}