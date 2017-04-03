/**
 * PROYECTO FIN DE CARRERA:
 * 		- Título: Generación automática de la arquitectura de una aplicación web en WebML a partir de la
 *		  		  especificación de requisitos
 * REALIZADO POR:
 * 		- CARLOS AGUADO FUENTES, DNI: 76036306P
 * 		- INGENIERIA INFORMATICA: 2012/2013, CONVOCATORIA DE JUNIO 
 */
package org.homeria.webratioassistant.plugin;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.commands.CommandStack;

import com.webratio.commons.mf.IMFElement;
import com.webratio.commons.mf.ui.commands.ConnectionCommand;
import com.webratio.ide.model.IContentUnit;
import com.webratio.ide.model.IOperationUnit;
import com.webratio.ide.model.IPage;
import com.webratio.ide.ui.commands.AddKOLinkCommand;
import com.webratio.ide.ui.commands.AddLinkCommand;
import com.webratio.ide.ui.commands.AddOKLinkCommand;

@SuppressWarnings("restriction")
public final class EventoNuevoLink extends Evento {
	private IMFElement destino;
	private IMFElement link;
	private String nombre;
	private IMFElement origen;
	private String tipo;

	public EventoNuevoLink(String nombre, IMFElement source, IMFElement target,
			String tipo) {
		super(null, 0, 0);
		this.nombre = nombre;
		this.origen = source;
		this.destino = target;
		this.link = null;
		this.tipo = tipo;
	}

	@Override
	public IMFElement ejecutar() {
		try {
			// Obtenemos el EditPart donde se esta dibujando el modelado de
			// webratio
			EditPart ep = ProjectParameters.getEditPartViewer()
					.getRootEditPart();
			// Con ese editPart creamos los correspondientes para la unidad de
			// origen y la de destino
			EditPart sourcePart = ProjectParameters.getEditPartViewer()
					.getEditPartFactory().createEditPart(ep, this.origen);
			EditPart targetPart = ProjectParameters.getEditPartViewer()
					.getEditPartFactory().createEditPart(ep, this.destino);
			ConnectionCommand cmd = null;
			// Dependiendo del tipo varian algunas funciones
			if (this.tipo.equals("OKLink")) {
				// Creamos el comando
				cmd = new AddOKLinkCommand(this.origen.getParentElement()
						.getModelId());

				// Seleccionamos el editor (SiteView normalmente)
				cmd.setEditPartViewer(ProjectParameters.getEditPartViewer());
				cmd.setEditor(ProjectParameters.getWebProjectEditor()
						.getActiveGraphEditor());
				// Seleccionamos el source y el target
				cmd.setSource(sourcePart);
				cmd.setTarget(targetPart);
				// Si son unidades correctas (que lo serán) se ejecuta
				if (cmd.canStart()) {
					if (AddOKLinkCommand.canStart(this.origen)
							&& AddOKLinkCommand.canComplete(this.origen,
									this.destino)) {
						((CommandStack) ProjectParameters
								.getWorkbenchPartWebRatio().getAdapter(
										CommandStack.class)).execute(cmd);
					}
				}
				// Retornamos el link para modificarlo en otras funciones
				this.link = this.getLastLink(this.origen);
				// Le damos el nombre que le corresponda (View, Modfy,
				// Delete...)
				Utilities.setAttribute(this.link, "name", this.nombre);
			}
			// El funcionamiento es igual al anterior
			if (this.tipo.equals("KOLink")) {
				cmd = new AddKOLinkCommand(this.origen.getParentElement()
						.getModelId());
				cmd.setEditPartViewer(ProjectParameters.getEditPartViewer());
				cmd.setEditor(ProjectParameters.getWebProjectEditor()
						.getActiveGraphEditor());
				cmd.setSource(sourcePart);
				cmd.setTarget(targetPart);
				if (cmd.canStart()) {
					if (AddKOLinkCommand.canStart(this.origen)
							&& AddKOLinkCommand.canComplete(this.origen,
									this.destino)) {
						((CommandStack) ProjectParameters
								.getWorkbenchPartWebRatio().getAdapter(
										CommandStack.class)).execute(cmd);
					}
				}
				this.link = this.getLastLink(this.origen);
				Utilities.setAttribute(this.link, "name", this.nombre);
			}
			// Los links de transporte y normal son iguales, solo varia un campo
			// (type) en el XML, y su metodo de ejecución es igual a los dos
			// anteriores
			if (this.tipo.equals("normal") || this.tipo.equals("transport")) {

				cmd = new AddLinkCommand(this.origen.getParentElement()
						.getModelId());
				cmd.setEditPartViewer(ProjectParameters.getEditPartViewer());
				cmd.setEditor(ProjectParameters.getWebProjectEditor()
						.getActiveGraphEditor());
				cmd.setSource(sourcePart);
				cmd.setTarget(targetPart);
				if (cmd.canStart()) {
					if (AddLinkCommand.canStart(this.origen)
							&& AddLinkCommand.canComplete(this.origen,
									this.destino)) {
						((CommandStack) ProjectParameters
								.getWorkbenchPartWebRatio().getAdapter(
										CommandStack.class)).execute(cmd);
					}
				}
				this.link = this.getLastLink(this.origen);
				Utilities.setAttribute(this.link, "name", this.nombre);
				// Asignamos el tipo
				Utilities.setAttribute(this.link, "type", this.tipo);
			}
		} catch (Exception e) {
			Debug.println(Utilities.class.toString() + " (addLink)",
					e.getMessage());
			e.printStackTrace();
		}
		return this.link;
	}

	private IMFElement getLastLink(IMFElement element) {
		IPage pagina;
		IContentUnit unidad;
		IOperationUnit unidadOperacion;

		if (element instanceof IContentUnit) {
			unidad = (IContentUnit) element;
			return (unidad.getOutgoingLinkList().get(unidad
					.getOutgoingLinkList().size() - 1));
		}
		if (element instanceof IPage) {
			pagina = (IPage) element;
			return (pagina.getOutgoingLinkList().get(pagina
					.getOutgoingLinkList().size() - 1));
		}
		if (element instanceof IOperationUnit) {
			unidadOperacion = (IOperationUnit) element;

			if (this.tipo.equals("OKLink")) {
				return (unidadOperacion.getOutgoingOKLinkList()
						.get(unidadOperacion.getOutgoingOKLinkList().size() - 1));
			}
			if (this.tipo.equals("KOLink")) {
				return (unidadOperacion.getOutgoingKOLinkList()
						.get(unidadOperacion.getOutgoingKOLinkList().size() - 1));
			}
			if (this.tipo.equals("normal") || this.tipo.equals("transport")) {
				return (unidadOperacion.getOutgoingLinkList()
						.get(unidadOperacion.getOutgoingLinkList().size() - 1));
			}
		}
		return null;
	}
}
