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
import java.util.List;

import org.eclipse.gef.commands.CommandStack;

import com.webratio.commons.mf.IMFElement;
import com.webratio.commons.mf.ui.commands.SelectionCommand;
import com.webratio.ide.model.IAlternative;
import com.webratio.ide.model.IArea;
import com.webratio.ide.model.ISiteView;
import com.webratio.ide.ui.commands.AddAreaCommand;

@SuppressWarnings("restriction")
public final class EventoNuevaArea extends Evento {

	private String nombre;
	private IMFElement elemento;

	public EventoNuevaArea(IMFElement padre, int x, int y, String nombre) {
		super(padre, x, y);
		this.nombre = nombre;
	}

	public IMFElement ejecutar() {
		try {
			// Comprobamos que el padre sea una siteView o Area, el área
			// solo puede ir dentro de una siteView u otro Area
			if ((this.getPadre() instanceof ISiteView)
					|| (this.getPadre() instanceof IArea)) {

				SelectionCommand cmd = new AddAreaCommand(this.getPadre()
						.getModelId());
				if (this.getPadre() instanceof ISiteView)
					Utilities.switchSiteView((ISiteView) this.getPadre());

				List<IMFElement> lista = new ArrayList<IMFElement>();
				lista.add(this.getPadre());
				cmd.setSelection(lista);
				cmd.setLocation(this.getPunto());
				
				//Ejecutar
				((CommandStack) ProjectParameters.getWorkbenchPartWebRatio()
						.getAdapter(CommandStack.class)).execute(cmd);
				// Obtenemos la pagina que se ha creado dentro de la zona
				// alternativa
				this.elemento = this.getLastArea(this.getPadre());
				Utilities.setAttribute(this.elemento, "name", this.nombre);

			}
		} catch (Exception e) {
			Debug.println(
					this.getClass().toString()+ " "
					+ new Exception().getStackTrace()[0].getMethodName(),
					"No se ha podido aï¿½adir el area");
			e.printStackTrace();
		}
		return elemento;

	}

	private IMFElement getLastArea(IMFElement element) {
		ISiteView siteView;
		IAlternative alternativa;
		IArea area;
		if (element instanceof ISiteView) {
			siteView = (ISiteView) element;
			int number = siteView.getAreaList().size();
			return (siteView.getAreaList().get(number - 1));
		}
		if (element instanceof IArea) {
			area = (IArea) element;
			int number = area.getAreaList().size();
			return (area.getAreaList().get(number - 1));
		}
		return null;
	}

}
