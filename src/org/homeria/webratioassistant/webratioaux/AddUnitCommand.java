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

import org.apache.commons.lang.StringUtils;

import com.webratio.commons.mf.IMFElement;
import com.webratio.commons.mf.operations.IMFOperation;
import com.webratio.commons.mf.operations.MFUpdater;
import com.webratio.commons.mf.ui.commands.SelectionCommand;
import com.webratio.commons.startup.WRUIStartupPlugin;
import com.webratio.commons.startup.commons.Pair;
import com.webratio.ide.core.WebModelHelper;
import com.webratio.ide.core.grid.AutoPlacer;
import com.webratio.ide.core.grid.GridHelper;
import com.webratio.ide.model.IAbstractPage;
import com.webratio.ide.model.IArea;
import com.webratio.ide.model.IContentUnit;
import com.webratio.ide.model.IContentUnits;
import com.webratio.ide.model.IHybridModule;
import com.webratio.ide.model.IJob;
import com.webratio.ide.model.IOperationGroup;
import com.webratio.ide.model.IOperationModule;
import com.webratio.ide.model.IOperationUnit;
import com.webratio.ide.model.IOperationUnits;
import com.webratio.ide.model.IPort;
import com.webratio.ide.model.ISiteView;
import com.webratio.ide.model.ISubUnit;
import com.webratio.ide.model.IWebMLElement;
import com.webratio.ide.model.IWebProject;
import com.webratio.ide.ui.WRUIPlugin;
import com.webratio.ide.ui.viewers.WebMLElementLabelProvider;
import com.webratio.ide.units.core.ISelector;
import com.webratio.ide.units.core.ISubUnitType;
import com.webratio.ide.units.core.IUnitProperty;
import com.webratio.ide.units.core.IUnitType;

public class AddUnitCommand extends SelectionCommand {
	private Pair<List<IMFOperation>, List<IMFOperation>> postOperations;
	private IMFElement parent;
	private IMFElement newUnit;
	private IMFElement newLayoutUnit;
	private IMFElement location;
	private IMFElement selectorElement;
	protected final IUnitType unitType;
	protected IMFElement source;

	public AddUnitCommand(IUnitType unitType) {
		super("webml");
		this.unitType = unitType;
	}

	@Override
	public boolean canPreExecute() {
		return canExecute();
	}

	@Override
	public boolean canExecute() {

		this.source = getSingleSelectedModel();
		if ((getSelection() == null) || (getSelection().size() != 1)) {
			return false;
		}
		if ((this.source instanceof IAbstractPage))
			return this.unitType.isContent();
		if (((this.source instanceof ISiteView))
				|| ((this.source instanceof IArea))
				|| ((this.source instanceof IOperationGroup))
				|| ((this.source instanceof IPort))
				|| ((this.source instanceof IJob))
				|| ((this.source instanceof IOperationModule))
				|| ((this.source instanceof IHybridModule))) {
			return this.unitType.isOperation();
		}
		return false;
	}

	@Override
	@SuppressWarnings("unchecked")
	public void execute() {
		this.source = getSingleSelectedModel();
		setRootElement(this.source);
		if ((this.source instanceof IAbstractPage)) {
			this.parent = this.source.selectSingleElement("ContentUnits");
			if (this.parent == null) {
				this.parent = createElement(IContentUnits.class, getModelId());
				addChild(this.parent, this.source, null);
			}
			this.newUnit = createGenericElement(IContentUnit.class,
					this.unitType.getName(), getModelId());
		} else {
			this.parent = this.source.selectSingleElement("OperationUnits");
			if (this.parent == null) {
				this.parent = createElement(IOperationUnits.class, getModelId());
				addChild(this.parent, this.source, null);
			}
			this.newUnit = createGenericElement(IOperationUnit.class,
					this.unitType.getName(), getModelId());
		}
		setLocation(this.newUnit, "gr:x", "gr:y");
		Pair id = this.source
				.getRootElement()
				.getIdProvider()
				.getFirstFreeId(this.parent.getParentElement().valueOf("@id"),
						this.unitType.getIdPrefix(), null, true);
		setAttribute(this.newUnit, "id", (String) id.first);
		setAttribute(this.newUnit, "name", this.unitType.getNamePrefix()
				+ id.second);
		for (IUnitProperty prop : this.unitType.getProperties()) {
			String attrName = prop.get("attributeName");
			if (StringUtils.isBlank(attrName)) {
				continue;
			}

			String defaultValue = prop.get("defaultValue");
			if (!defaultValue.equals("")) {
				setAttribute(this.newUnit, attrName, defaultValue);
			} else {
				if (!IUnitProperty.Type.SITE_VIEW.getElementName().equals(
						prop.getType()))
					continue;
				try {
					List availableSiteViews = SiteViewProperty
							.getAvailableSiteViews(
									(IWebProject) this.parent.getRootElement(),
									prop);
					if ((availableSiteViews != null)
							&& (availableSiteViews.size() == 1))
						setAttribute(this.newUnit, attrName,
								((ISiteView) availableSiteViews.get(0))
										.valueOf("@id"));
				} catch (Throwable e) {
					WRUIStartupPlugin.logException(e, "com.webratio.ide.ui");
				}
			}
		}

		addChild(this.newUnit, this.parent, null);
		if (WRUIPlugin.getDefault().getPreferenceStore()
				.getBoolean("MODELING_LAYOUT_UNIT_AUTOPLACEMENT")) {

			// caguadof--insertado por cambio deprecated de metodo
			// AutoPlacer.placeOnGrid(this.newUnit, this.source
			IMFElement location = null;
			IAbstractPage topLevelAncestorPage = WebModelHelper
					.getTopLevelAbstractPageWithoutContentPageTest(source);
			if (topLevelAncestorPage != null) {
				GridHelper helper = new GridHelper(topLevelAncestorPage);
				location = helper.getFirstFreeLocation((IWebMLElement) source);
			}

			Pair pair = AutoPlacer.placeOnGrid(this.newUnit, this.source,
					location);
			this.location = ((IMFElement) pair.first);
			this.newLayoutUnit = ((IMFElement) pair.second);
			if ((this.location != null) && (this.newLayoutUnit != null)) {
				addChild(this.newLayoutUnit, this.location, null);
			}
		}
		for (ISubUnitType subUnitType : this.unitType.getSubUnitTypes()) {
			if ((subUnitType instanceof ISelector)) {
				ISelector selector = (ISelector) subUnitType;
				if (selector.isAutomatic()) {
					this.selectorElement = createGenericElement(ISubUnit.class,
							selector.getElementName(), getModelId());
					Pair selectorId = this.newUnit
							.getRootElement()
							.getIdProvider()
							.getFirstFreeId(this.newUnit.valueOf("@id"),
									subUnitType.getIdPrefix(), null, true);
					setAttribute(this.selectorElement, "id",
							(String) selectorId.first);
					setAttribute(this.selectorElement, "defaultPolicy", "fill");
					setAttribute(this.selectorElement, "booleanOperator", "and");
					addChild(this.selectorElement, this.newUnit, null);
					IMFElement keyCondition = createGenericElement(
							ISubUnit.class, "KeyCondition", getModelId());
					ISubUnitType type = subUnitType
							.getSubUnitType("KeyCondition");
					if (type != null) {
						Pair conditionId = this.newUnit
								.getRootElement()
								.getIdProvider()
								.getFirstFreeId(
										this.selectorElement.valueOf("@id"),
										type.getIdPrefix(), null, true);
						setAttribute(keyCondition, "id",
								(String) conditionId.first);
						setAttribute(keyCondition, "name", type.getNamePrefix()
								+ conditionId.second);
						setAttribute(keyCondition, "predicate", "in");
						setAttribute(keyCondition, "implied", "false");
					}
					addChild(keyCondition, this.selectorElement, null);
				}
			}
		}
		MFUpdater updater = this.parent.getRootElement().getModelUpdater();
		updater.added(this.newUnit);
		if (this.newLayoutUnit != null) {
			updater.added(this.newLayoutUnit);
		}
		this.postOperations = updater.update();
		endOperationSession();
		setLabel("Add "
				+ WebMLElementLabelProvider.INSTANCE.getText(this.newUnit));
		directEdit(this.newUnit);
	}

	@Override
	public void undo() {
		if (this.postOperations != null) {
			executeOperations(this.postOperations.second);
		}
		delete(this.newUnit);
		if (this.newLayoutUnit != null) {
			delete(this.newLayoutUnit);
		}
		endOperationSession();
		select(this.source);
	}

	@Override
	public void redo() {
		addChild(this.newUnit, this.parent, null);
		if (this.newLayoutUnit != null) {
			addChild(this.newLayoutUnit, this.location, null);
		}
		if (this.postOperations != null) {
			executeOperations(this.postOperations.first);
		}
		endOperationSession();
		select(this.newUnit);
	}
}