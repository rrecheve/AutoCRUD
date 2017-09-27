/**
 * WebRatio Assistant v3.0
 * 
 * University of Extremadura (Spain) www.unex.es
 * 
 * Developers:
 * 	- Carlos Aguado Fuentes (v2)
 * 	- Javier Sierra Blázquez (v3.0)
 */
package org.homeria.webratioassistant.webratio;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.commands.CommandStack;
import org.homeria.webratioassistant.webratioaux.AddUnitCommand;

import com.webratio.commons.mf.IMFElement;
import com.webratio.commons.mf.MFPlugin;
import com.webratio.ide.model.IArea;
import com.webratio.ide.model.IEntity;
import com.webratio.ide.model.IPage;
import com.webratio.ide.model.ISiteView;
import com.webratio.ide.model.IUnit;
import com.webratio.ide.model.IWebProject;
import com.webratio.ide.units.UnitsPlugin;
import com.webratio.ide.units.core.IUnitProject;
import com.webratio.ide.units.core.IUnitType;

/**
 * Manages the creation of a new Unit using WebRatio library calls
 */
public final class NewUnit extends WebRatioCalls {
	private IEntity entity;
	private String name;
	private String type;
	private IUnit unit;
	private HashMap<String, IUnitType> unitTypes;

	/**
	 * Constructs a new instance.
	 * 
	 * @param parent
	 *            : unit parent
	 * @param type
	 *            : unit type
	 * @param x
	 *            : X coord to situate the XOR Page in WR model
	 * @param y
	 *            : Y coord to situate the XOR Page in WR model
	 * @param name
	 *            : display name
	 * @param entity
	 *            : entity associated with the unit
	 */
	public NewUnit(IMFElement parent, String type, int x, int y, String name, IEntity entity) {
		super(parent, x, y);
		this.name = name;
		this.loadUnitTypes();
		this.type = type;
		this.entity = entity;
	}

	/* (non-Javadoc)
	 * @see org.homeria.webratioassistant.webratio.WebRatioCalls#execute()
	 */
	@Override
	public final IMFElement execute() {
		IUnitType unitType;
		this.unit = null;
		// We obtain a corresponding unit with the indicated type
		unitType = this.unitTypes.get(this.type);
		try {
			// We create an instance in the class that the units create, indicating the type
			AddUnitCommand cmd = new AddUnitCommand(unitType);
			// The procedure is similar to the previous ones
			List<IMFElement> list = new ArrayList<IMFElement>();
			list.add(this.getParent());
			cmd.setSelection(list);
			Point point = this.getPoint();
			cmd.setLocation(point);
			// It is checked if this unit can be run on the selected parent, for example check that the operating units are not within pages
			if (cmd.canExecute()) {
				((CommandStack) ProjectParameters.getWorkbenchPartWebRatio().getAdapter(CommandStack.class)).execute(cmd);
				this.unit = this.getLastContentUnit(this.getParent());
				// If the unit is of type IsNotNullUnit we mark the field emptyStringAsNull as true, since in the wizard whenever we use
				// this entity it will be to compare empty strings
				if (this.unit.getQName().getName().equals("IsNotNullUnit")) {
					Utilities.setAttribute(this.unit, "emptyStringAsNull", "true");
				}
				Utilities.setAttribute(this.unit, "name", this.name);
				// If we add an entity when we create the instance we will indicate it to the created unit, for cases like DataUnit,
				// PoweIndexUnit and all types of units that require an entity to work
				if (this.entity != null)
					Utilities.setAttribute(this.unit, "entity", this.entity.getFinalId());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return this.unit;
	}

	private IUnit getLastContentUnit(IMFElement element) {
		IPage page;
		ISiteView siteView;
		IArea area;
		if (element instanceof IPage) {
			page = (IPage) element;
			int numberContentUnits = page.getContentUnitList().size();
			return (page.getContentUnitList().get(numberContentUnits - 1));
		}
		if (element instanceof ISiteView) {
			siteView = (ISiteView) element;
			return siteView.getOperationUnitList().get(siteView.getOperationUnitList().size() - 1);
		}
		if (element instanceof IArea) {
			area = (IArea) element;
			return area.getOperationUnitList().get(area.getOperationUnitList().size() - 1);
		}

		return null;
	}

	private void loadUnitTypes() {
		IWebProject webProject = ProjectParameters.getWebProject();
		this.unitTypes = new HashMap<String, IUnitType>();
		// Get unit types list
		IProject[] project = new IProject[1];
		project[0] = MFPlugin.getDefault().getFile(webProject).getProject();
		List<IUnitProject> IU = UnitsPlugin.getUnitModel().getUnitProjects(project);
		IUnitProject IUP;
		Iterator<IUnitProject> iter2 = IU.iterator();
		Iterator<IUnitType> iter;
		while (iter2.hasNext()) {
			IUP = iter2.next();
			// Get unit types list
			List<IUnitType> IUL = IUP.getUnitTypes();
			iter = IUL.iterator();
			IUnitType iu;
			while (iter.hasNext()) {
				iu = iter.next();
				// Save the name and unitType
				this.unitTypes.put(iu.getName(), iu);
			}
		}
	}
}
