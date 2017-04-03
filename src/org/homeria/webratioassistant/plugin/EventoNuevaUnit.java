/**
 * PROYECTO FIN DE CARRERA:
 * 		- Título: Generación automática de la arquitectura de una aplicación web en WebML a partir de la
 *		  		  especificación de requisitos
 * REALIZADO POR:
 * 		- CARLOS AGUADO FUENTES, DNI: 76036306P
 * 		- INGENIERIA INFORMATICA: 2012/2013, CONVOCATORIA DE JUNIO 
 */
package org.homeria.webratioassistant.plugin;

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

public final class EventoNuevaUnit extends Evento {
	private IEntity entidad;
	private String nombre;
	private String tipo;
	private IUnit unit;
	private HashMap<String, IUnitType> UnitTypes;

	public EventoNuevaUnit(IMFElement padre, String tipo, int x, int y,
			String nombre, IEntity entity) {
		super(padre, x, y);
		this.nombre = nombre;
		this.loadUnitTypes();
		this.tipo = tipo;
		this.entidad = entity;
	}

	@Override
	public final IMFElement ejecutar() {
		IUnitType unitType;
		this.unit = null;
		// Obtenemos una unidad correspondiente con el tipo indicado
		unitType = this.getUnitType(this.tipo);
		try {
			// Creamos una instancia a la clase que crea las unidades,
			// indicandole el tipo
			AddUnitCommand cmd = new AddUnitCommand(unitType);
			// El procedimiento es similar a las anteriores
			List<IMFElement> l = new ArrayList<IMFElement>();
			l.add(this.getPadre());
			cmd.setSelection(l);
			Point point = this.getPunto();
			cmd.setLocation(point);
			// Se comprueba si se puede ejecutar esa unidad en el padre
			// seleccionado, comprueba por ejemplo que las unidades de operacion
			// no se encuentren dentro de páginas
			if (cmd.canExecute()) {
				((CommandStack) ProjectParameters.getWorkbenchPartWebRatio()
						.getAdapter(CommandStack.class)).execute(cmd);
				this.unit = this.getLastContentUnit(this.getPadre());
				// Si la unidad es de tipo IsNotNullUnit marcamos como true el
				// campo emptyStringAsNull, ya que en el asistente siemrpe que
				// usemos esta entidad será para compara cadenas vacias
				if (this.unit.getQName().getName().equals("IsNotNullUnit")) {
					Utilities.setAttribute(this.unit, "emptyStringAsNull",
							"true");
				}
				// Se añade el nombre
				Utilities.setAttribute(this.unit, "name", nombre);
				// Si al crear la instancia añadimos una entidad se la
				// indicaremos a la unidad creada, para los casos como DataUnit,
				// PoweIndexUnit y todo tipo de unidades que requieren una
				// entidad para funcionar
				if (this.entidad != null)
					Utilities.setAttribute(this.unit, "entity",
							this.entidad.getFinalId());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return unit;
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
			return siteView.getOperationUnitList().get(
					siteView.getOperationUnitList().size() - 1);
		}
		if (element instanceof IArea) {
			area = (IArea) element;
			return area.getOperationUnitList().get(
					area.getOperationUnitList().size() - 1);
		}

		return null;
	}

	private IUnitType getUnitType(String type) {
		return UnitTypes.get(type);
	}

	private void loadUnitTypes() {
		// Obtenemos el proyecto web
		IWebProject webProject = ProjectParameters.getWebProject();
		// Iniciamos la estructura hashMap para guardar las unidades y un string
		// para guardar su nombre
		UnitTypes = new HashMap<String, IUnitType>();
		// Hacemos las operaciones necesarias con las funciones de WebRatio para
		// obtener el listado de unidades
		IProject[] project = new IProject[1];
		project[0] = MFPlugin.getDefault().getFile(webProject).getProject();
		List<IUnitProject> IU = UnitsPlugin.getUnitModel().getUnitProjects(
				project);
		IUnitProject IUP;
		Iterator<IUnitProject> iter2 = IU.iterator();
		Iterator<IUnitType> iter;
		while (iter2.hasNext()) {
			IUP = iter2.next();
			// Obtenemos la lista de tipos de unidades
			List<IUnitType> IUL = IUP.getUnitTypes();
			iter = IUL.iterator();
			IUnitType iu;
			while (iter.hasNext()) {
				iu = iter.next();
				// Almacenamos su nombre y la unidad
				UnitTypes.put(iu.getName(), iu);
			}
		}
	}
}
