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
import com.webratio.ide.model.IPage;
import com.webratio.ide.ui.commands.AddAlternativeCommand;

@SuppressWarnings("restriction")
public final class EventoNuevaAlternantiva extends Evento {

	private String nombre;
	private IMFElement pagina;

	public EventoNuevaAlternantiva(IMFElement padre, int x, int y, String nombre) {
		super(padre, x, y);
		this.nombre = nombre;
	}

	/**
	 * 
	 */
	public IMFElement ejecutar() {
		try {
			// Comprobamos que el padre sea una pagina, ya que la alternativa
			// solo puede ir dentro de una pagina
			if (this.getPadre() instanceof IPage) {
				SelectionCommand cmd = new AddAlternativeCommand(this
						.getPadre().getModelId());
				List<IMFElement> lista = new ArrayList<IMFElement>();
				lista.add(this.getPadre());
				cmd.setSelection(lista);
				cmd.setLocation(this.getPunto());

				((CommandStack) ProjectParameters.getWorkbenchPartWebRatio()
						.getAdapter(CommandStack.class)).execute(cmd);
				// Obtenemos la pÃ¡gina que se ha creado dentro de la zona
				// alternativa
				this.pagina = this.getLastArea(this.getPadre());
				Utilities.setAttribute(this.pagina, "name", this.nombre);

			}
		} catch (Exception e) {
			Debug.println(
					this.getClass().toString()
							+ " "
							+ new Exception().getStackTrace()[0]
									.getMethodName(),
					"No se ha podido aï¿½adir la pï¿½gina");
			e.printStackTrace();
		}
		return pagina;

	}

	/**
	 * 
	 * Nombre: getLastArea Funcion:
	 * 
	 * @param element
	 * @return
	 */
	private IMFElement getLastArea(IMFElement element) {
		IPage pagina;
		if (element instanceof IPage) {
			pagina = (IPage) element;
			int numberAlternatives = pagina.getAlternativeList().size();
			return (pagina.getAlternativeList().get(numberAlternatives - 1));
		}
		return null;
	}

}
