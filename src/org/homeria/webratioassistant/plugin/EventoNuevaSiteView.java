/**
 * PROYECTO FIN DE CARRERA:
 * 		- Título: Generación automática de la arquitectura de una aplicación web en WebML a partir de la
 *		  		  especificación de requisitos
 * REALIZADO POR:
 * 		- CARLOS AGUADO FUENTES, DNI: 76036306P
 * 		- INGENIERIA INFORMATICA: 2012/2013, CONVOCATORIA DE JUNIO 
 */
package org.homeria.webratioassistant.plugin;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.gef.commands.CommandStack;

import com.webratio.ide.ui.commands.AddSiteViewCommand;


public class EventoNuevaSiteView {


	public EventoNuevaSiteView(String nombre) {
	}

	
	/**
	 * Nombre: ejecutar Funcion: Crea SiteView
	 * Retrieve del CRUD
	 * 
	 * @param subProgressMonitor
	 */
	public void ejecutar(SubProgressMonitor subProgressMonitor,
			String nombreSiteViewNuevo) {

		try {

			// props
			Map<String, Object> props = new HashMap<String, Object>();
			props.put("name", nombreSiteViewNuevo);
			props.put("protected", Boolean.FALSE); 
			props.put("secure", Boolean.FALSE); 
			props.put("localized", Boolean.TRUE);

			// Creamos una instancia a la clase que crea las unidades
			AddSiteViewCommand cmdSTV = new AddSiteViewCommand(
					ProjectParameters.getWebModel(), props,
					ProjectParameters.getMultiEditor());

			if (cmdSTV.canExecute()) {
				// Ejecutamos
				((CommandStack) ProjectParameters.getWorkbenchPartWebRatio()
						.getAdapter(CommandStack.class)).execute(cmdSTV);

			}
		} catch (Exception e) {
			Debug.println(
					this.getClass().toString()
							+ " "
							+ new Exception().getStackTrace()[0]
									.getMethodName(),
					"No se ha podido aniadir la siteView");
			e.printStackTrace();
		} finally {
			subProgressMonitor.done();
		}
	}
}
