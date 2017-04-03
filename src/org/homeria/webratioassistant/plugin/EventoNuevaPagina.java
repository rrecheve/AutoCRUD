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
import com.webratio.ide.ui.commands.AddPageCommand;

@SuppressWarnings("restriction")
public final class EventoNuevaPagina extends Evento {

	private String nombre;
	private IMFElement pagina;

	public EventoNuevaPagina(IMFElement padre, int x, int y, String nombre) {
		super(padre, x, y);
		this.nombre = nombre;
	}

	/**
 * 
 */
	public IMFElement ejecutar() {
		try {
			// Se comprueba que el sitio para rear lal página sea un SiteView o
			// una página Alternativa
			if ((this.getPadre() instanceof ISiteView)
					|| (this.getPadre() instanceof IAlternative)
					|| (this.getPadre() instanceof IArea)) {
				// Instanciamos al comando para crear Página de WebRatio
				SelectionCommand cmd = new AddPageCommand(this.getPadre()
						.getModelId());
				if (this.getPadre() instanceof ISiteView)
					Utilities.switchSiteView((ISiteView) this.getPadre());
				// Es necesario incluir en el comando una lista con los
				// elementos donde se va a insertar la página, en este caso la
				// lista estará unicamente compuesta por el id del padre
				// (SiteViwe o AlternativePage)
				List<IMFElement> lista = new ArrayList<IMFElement>();
				lista.add(this.getPadre());
				// Seleccionamos el padre como lugar donde colocarlo
				cmd.setSelection(lista);
				// Y le indicamos su posición espacial X,Y
				cmd.setLocation(this.getPunto());

				// Ejecutamos
				((CommandStack) ProjectParameters.getWorkbenchPart()
						.getAdapter(CommandStack.class)).execute(cmd);
				// Retornamos la última página creada
				this.pagina = this.getLastPage(this.getPadre());
				// Y le modificamos los atributos necesarios
				Utilities.setAttribute(this.pagina, "name", this.nombre);
				Utilities.setAttribute(this.pagina, "landmark", "true");

				if (this.getPadre() instanceof IArea
						&& this.nombre.contains("CRUD")) {
					// se crea en el xml, pero no se muesta por pantalla
					Utilities.setAttribute(this.pagina, "default", "true");
					// default="true" landmark="true" protected="true"
					// secure="true"
				}

			}
		} catch (Exception e) {
			Debug.println(
					this.getClass().toString()
							+ " "
							+ new Exception().getStackTrace()[0]
									.getMethodName(),
					"No se ha podido añadir la página");
			e.printStackTrace();
		}
		return pagina;
	}

	/**
	 * 
	 * Nombre: getLastPage Funcion:
	 * 
	 * @param element
	 * @return
	 */
	private IMFElement getLastPage(IMFElement element) {
		ISiteView siteView;
		IAlternative alternativa;
		IArea area;
		if (element instanceof ISiteView) {
			siteView = (ISiteView) element;
			int numberPages = siteView.getPageList().size();
			return (siteView.getPageList().get(numberPages - 1));
		}
		if (element instanceof IAlternative) {
			alternativa = (IAlternative) element;
			int numberPages = alternativa.getPageList().size();
			return (alternativa.getPageList().get(numberPages - 1));
		}
		if (element instanceof IArea) {
			area = (IArea) element;
			int numberPages = area.getPageList().size();
			return (area.getPageList().get(numberPages - 1));
		}
		return null;
	}

}
