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

/**
 * Manages the creation of a new unit command using WebRatio library calls.This is used to create a new Unit.
 */
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
		return this.canExecute();
	}

	@Override
	public boolean canExecute() {

		this.source = this.getSingleSelectedModel();
		if ((this.getSelection() == null) || (this.getSelection().size() != 1)) {
			return false;
		}
		if ((this.source instanceof IAbstractPage))
			return this.unitType.isContent();
		if (((this.source instanceof ISiteView)) || ((this.source instanceof IArea)) || ((this.source instanceof IOperationGroup))
				|| ((this.source instanceof IPort)) || ((this.source instanceof IJob)) || ((this.source instanceof IOperationModule))
				|| ((this.source instanceof IHybridModule))) {
			return this.unitType.isOperation();
		}
		return false;
	}

	@Override
	public void execute() {
		this.source = this.getSingleSelectedModel();
		this.setRootElement(this.source);
		if ((this.source instanceof IAbstractPage)) {
			this.parent = this.source.selectSingleElement("ContentUnits");
			if (this.parent == null) {
				this.parent = this.createElement(IContentUnits.class, this.getModelId());
				this.addChild(this.parent, this.source, null);
			}
			this.newUnit = this.createGenericElement(IContentUnit.class, this.unitType.getName(), this.getModelId());
		} else {
			this.parent = this.source.selectSingleElement("OperationUnits");
			if (this.parent == null) {
				this.parent = this.createElement(IOperationUnits.class, this.getModelId());
				this.addChild(this.parent, this.source, null);
			}
			this.newUnit = this.createGenericElement(IOperationUnit.class, this.unitType.getName(), this.getModelId());
		}
		this.setLocation(this.newUnit, "gr:x", "gr:y");
		Pair id = this.source.getRootElement().getIdProvider()
				.getFirstFreeId(this.parent.getParentElement().valueOf("@id"), this.unitType.getIdPrefix(), null, true);
		this.setAttribute(this.newUnit, "id", (String) id.first);
		this.setAttribute(this.newUnit, "name", this.unitType.getNamePrefix() + id.second);
		for (IUnitProperty prop : this.unitType.getProperties()) {
			String attrName = prop.get("attributeName");
			if (StringUtils.isBlank(attrName)) {
				continue;
			}

			String defaultValue = prop.get("defaultValue");
			if (!defaultValue.equals("")) {
				this.setAttribute(this.newUnit, attrName, defaultValue);
			} else {
				if (!IUnitProperty.Type.SITE_VIEW.getElementName().equals(prop.getType()))
					continue;
				try {
					List availableSiteViews = SiteViewProperty.getAvailableSiteViews((IWebProject) this.parent.getRootElement(), prop);
					if ((availableSiteViews != null) && (availableSiteViews.size() == 1))
						this.setAttribute(this.newUnit, attrName, ((ISiteView) availableSiteViews.get(0)).valueOf("@id"));
				} catch (Throwable e) {
					WRUIStartupPlugin.logException(e, "com.webratio.ide.ui");
				}
			}
		}

		this.addChild(this.newUnit, this.parent, null);
		if (WRUIPlugin.getDefault().getPreferenceStore().getBoolean("MODELING_LAYOUT_UNIT_AUTOPLACEMENT")) {

			// caguadof--insertado por cambio deprecated de metodo
			// AutoPlacer.placeOnGrid(this.newUnit, this.source
			IMFElement location = null;
			IAbstractPage topLevelAncestorPage = WebModelHelper.getTopLevelAbstractPageWithoutContentPageTest(this.source);
			if (topLevelAncestorPage != null) {
				GridHelper helper = new GridHelper(topLevelAncestorPage);
				location = helper.getFirstFreeLocation((IWebMLElement) this.source);
			}

			Pair pair = AutoPlacer.placeOnGrid(this.newUnit, this.source, location);
			this.location = ((IMFElement) pair.first);
			this.newLayoutUnit = ((IMFElement) pair.second);
			if ((this.location != null) && (this.newLayoutUnit != null)) {
				this.addChild(this.newLayoutUnit, this.location, null);
			}
		}
		for (ISubUnitType subUnitType : this.unitType.getSubUnitTypes()) {
			if ((subUnitType instanceof ISelector)) {
				ISelector selector = (ISelector) subUnitType;
				if (selector.isAutomatic()) {
					this.selectorElement = this.createGenericElement(ISubUnit.class, selector.getElementName(), this.getModelId());
					Pair selectorId = this.newUnit.getRootElement().getIdProvider()
							.getFirstFreeId(this.newUnit.valueOf("@id"), subUnitType.getIdPrefix(), null, true);
					this.setAttribute(this.selectorElement, "id", (String) selectorId.first);
					this.setAttribute(this.selectorElement, "defaultPolicy", "fill");
					this.setAttribute(this.selectorElement, "booleanOperator", "and");
					this.addChild(this.selectorElement, this.newUnit, null);
					IMFElement keyCondition = this.createGenericElement(ISubUnit.class, "KeyCondition", this.getModelId());
					ISubUnitType type = subUnitType.getSubUnitType("KeyCondition");
					if (type != null) {
						Pair conditionId = this.newUnit.getRootElement().getIdProvider()
								.getFirstFreeId(this.selectorElement.valueOf("@id"), type.getIdPrefix(), null, true);
						this.setAttribute(keyCondition, "id", (String) conditionId.first);
						this.setAttribute(keyCondition, "name", type.getNamePrefix() + conditionId.second);
						this.setAttribute(keyCondition, "predicate", "in");
						this.setAttribute(keyCondition, "implied", "false");
					}
					this.addChild(keyCondition, this.selectorElement, null);
				}
			}
		}
		MFUpdater updater = this.parent.getRootElement().getModelUpdater();
		updater.added(this.newUnit);
		if (this.newLayoutUnit != null) {
			updater.added(this.newLayoutUnit);
		}
		this.postOperations = updater.update();
		this.endOperationSession();
		this.setLabel("Add " + WebMLElementLabelProvider.INSTANCE.getText(this.newUnit));
		this.directEdit(this.newUnit);
	}

	@Override
	public void undo() {
		if (this.postOperations != null) {
			this.executeOperations(this.postOperations.second);
		}
		this.delete(this.newUnit);
		if (this.newLayoutUnit != null) {
			this.delete(this.newLayoutUnit);
		}
		this.endOperationSession();
		this.select(this.source);
	}

	@Override
	public void redo() {
		this.addChild(this.newUnit, this.parent, null);
		if (this.newLayoutUnit != null) {
			this.addChild(this.newLayoutUnit, this.location, null);
		}
		if (this.postOperations != null) {
			this.executeOperations(this.postOperations.first);
		}
		this.endOperationSession();
		this.select(this.newUnit);
	}
}