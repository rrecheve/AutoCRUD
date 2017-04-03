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

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.eclipse.gef.GraphicalEditPart;

import com.webratio.commons.mf.IMFElement;
import com.webratio.commons.mf.operations.IMFOperation;
import com.webratio.commons.mf.operations.MFUpdater;
import com.webratio.commons.startup.commons.Pair;
import com.webratio.ide.core.UnitHelper;
import com.webratio.ide.core.WebModelHelper;
import com.webratio.ide.core.operations.OperationHelper;
import com.webratio.ide.model.IAbstractPage;
import com.webratio.ide.model.IArea;
import com.webratio.ide.model.IContentUnit;
import com.webratio.ide.model.ILink;
import com.webratio.ide.model.IOperationUnit;
import com.webratio.ide.model.IPage;
import com.webratio.ide.model.IUnit;
import com.webratio.ide.ui.commands.AbstractAddWebMLConnectorCommand;
import com.webratio.ide.ui.viewers.WebMLElementLabelProvider;
import com.webratio.ide.units.core.IUnitType;

@SuppressWarnings("restriction")
public class AddLinkCommand extends AbstractAddWebMLConnectorCommand {
	private ILink newLink;
	private String oldValue;
	private IMFElement sourceModel;
	private Pair<List<IMFOperation>, List<IMFOperation>> postOperations;
	@SuppressWarnings("unused")
	private IMFElement source, target;

	public AddLinkCommand(String modelId) {
		super(modelId);
	}

	public void setSource(IMFElement source) {
		this.source = source;
	}

	public void setTarget(IMFElement target) {
		this.target = target;
	}

	@Override
	public boolean canStart() {
		this.sourceModel = getSourceModel();
		return canStart(this.sourceModel);
	}

	public static boolean canStart(IMFElement sourceModel) {
		if ((sourceModel instanceof IPage)) {
			if (((IPage) sourceModel).isContentPage()) {
				return false;
			}
			return ((IPage) sourceModel).isTopLevelPage();
		}
		if ((sourceModel instanceof IUnit)) {
			IUnitType unitType = UnitHelper.getUnitType(sourceModel);
			if (unitType != null) {
				return unitType.isLinkSource();
			}
			return false;
		}

		return false;
	}

	@Override
	public boolean canComplete() {
		return canComplete(this.sourceModel, getTargetModel());
	}

	public static final boolean canComplete(IMFElement sourceModel,
			IMFElement targetModel) {
		if (sourceModel == targetModel)
			return false;
		if ((targetModel instanceof IPage))
			return true;
		if ((targetModel instanceof IArea))
			return true;
		if ((targetModel instanceof IUnit)) {
			IUnitType unitType = UnitHelper.getUnitType(targetModel);
			if (unitType != null) {
				return unitType.isLinkTarget();
			}
			return false;
		}

		return false;
	}

	@Override
	@SuppressWarnings("unchecked")
	public void execute() {
		setRootElement(this.sourceModel.getRootElement());
		IMFElement targetModel = getTargetModel();
		this.newLink = ((ILink) createElement(ILink.class, getModelId()));
		Pair id = this.sourceModel
				.getRootElement()
				.getIdProvider()
				.getFirstFreeId(this.sourceModel.valueOf("@id"), ILink.class,
						null, true);
		setAttribute(this.newLink, "id", (String) id.first);
		setAttribute(this.newLink, "name", "Link" + id.second);
		setAttribute(this.newLink, "to", targetModel.valueOf("@id"));
		setAttribute(this.newLink, "automaticCoupling", "true");
		String type = "";
		if ((this.sourceModel instanceof IUnit)) {
			IUnitType unitType = UnitHelper.getUnitType(this.sourceModel);
			if (unitType != null) {
				type = getLinkType(this.sourceModel, targetModel, unitType);
			}
		}
		if (type.equals("")) {
			type = "normal";
		}
		setAttribute(this.newLink, "type", type);
		setAttribute(this.newLink, "validate", "true");
		try {
			int[] bendpointValues = computeBendpoint();
			if (bendpointValues != null)
				setAttribute(this.newLink, "gr:bendpoints", StringUtils.join(
						ArrayUtils.toObject(bendpointValues), ","));
		} catch (Throwable localThrowable) {
		}
		addChild(this.newLink, this.sourceModel, null);

		this.oldValue = this.newLink.getParentElement().valueOf("@linkOrder");
		String newValue = OperationHelper.getNewOrderAttribute(
				this.newLink.getParentElement(), "linkOrder",
				this.newLink.valueOf("@id"), true);
		setAttribute(this.newLink.getParentElement(), "linkOrder", newValue);

		if ((this.sourceModel instanceof IContentUnit)) {
			MFUpdater updater = this.sourceModel.getRootElement()
					.getModelUpdater();
			updater.added(this.newLink);
			this.postOperations = updater.update();
		}
		endOperationSession();
		setLabel("Add "
				+ WebMLElementLabelProvider.INSTANCE.getText(this.newLink));
		if (("normal".equals(type)) || ("automatic".equals(type)))
			directEdit(this.newLink);
		else
			select(this.newLink);
	}

	private String getLinkType(IMFElement sourceModel, IMFElement targetModel,
			IUnitType unitType) {
		if ((sourceModel instanceof IOperationUnit)) {
			return "transport";
		}
		if ((targetModel instanceof IOperationUnit))
			return unitType.getDefaultTowardsOperationLinkType();
		if ((targetModel instanceof IContentUnit)) {
			IAbstractPage page1 = WebModelHelper
					.getTopLevelAbstractPageWithoutContentPageTest(sourceModel);
			IAbstractPage page2 = WebModelHelper
					.getTopLevelAbstractPageWithoutContentPageTest(targetModel);
			if ((page1 != null) && (page1 == page2)) {
				return unitType.getDefaultIntraPageLinkType();
			}
			return "normal";
		}

		return "normal";
	}

	@Override
	public void undo() {
		if (this.postOperations != null) {
			executeOperations(this.postOperations.second);
		}
		setAttribute(this.newLink.getParentElement(), "linkOrder",
				this.oldValue);
		delete(this.newLink);
		endOperationSession();
		select(this.sourceModel);
	}

	@Override
	public void redo() {
		addChild(this.newLink, this.sourceModel, null);
		String newValue = OperationHelper.getNewOrderAttribute(
				this.newLink.getParentElement(), "linkOrder",
				this.newLink.valueOf("@id"), true);
		setAttribute(this.newLink.getParentElement(), "linkOrder", newValue);
		if (this.postOperations != null) {
			executeOperations(this.postOperations.first);
		}
		endOperationSession();
		select(this.newLink);
	}

	@Override
	protected GraphicalEditPart getSourceForBendpoints() {
		return (GraphicalEditPart) getSource();
	}

	@Override
	protected GraphicalEditPart getTargetForBendpoints() {
		return (GraphicalEditPart) getTarget();
	}
}