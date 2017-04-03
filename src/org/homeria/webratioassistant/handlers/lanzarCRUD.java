/**
 * PROYECTO FIN DE CARRERA:
 * 		- Título: Generación automática de la arquitectura de una aplicación web en WebML a partir de la
 *		  		  especificación de requisitos
 * REALIZADO POR:
 * 		- CARLOS AGUADO FUENTES, DNI: 76036306P
 * 		- INGENIERIA INFORMATICA: 2012/2013, CONVOCATORIA DE JUNIO 
 */
package org.homeria.webratioassistant.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;
import org.homeria.webratioassistant.plugin.ProjectParameters;
import org.homeria.webratioassistant.wizards.WizardCRUD;

import com.webratio.commons.mf.IMFElement;
import com.webratio.commons.mf.ui.viewers.SelectionHelper;
import com.webratio.ide.model.IEntity;

public class lanzarCRUD extends AbstractHandler {
	public lanzarCRUD() {
	}

	public Object execute(ExecutionEvent event) throws ExecutionException {
		try {
			// Obtenemos, mediante la busqueda en el workbench, el elemento
			// seleccionado, si es una entidad se evitarÃ¡ usar la primera
			// pÃ¡gina
			// del asistente, en caso de estar seleccionado otro elemento el
			// asistente serÃ¡ completo
			IWorkbenchWindow window = HandlerUtil
					.getActiveWorkbenchWindowChecked(event);
			IWorkbenchPage page = window.getActivePage();
			IEditorPart editor = page.getActiveEditor();

			ISelection selection = null;
			IStructuredSelection structuredSelection = null;
			if (editor != null) {
				selection = editor.getSite().getSelectionProvider()
						.getSelection();
				// }
				if (((selection instanceof IStructuredSelection))
						& (!((IStructuredSelection) selection).isEmpty())) {
					structuredSelection = (IStructuredSelection) selection;
				}

				IMFElement element;
				element = SelectionHelper.getModelElement(structuredSelection,
						true);

				WizardCRUD wizard;

				ProjectParameters.init();
				// Si el asistente se inicia con una entidad ya seleccionada nos
				// ahorramos una pagina, en caso contrario mostramos el
				// asistente
				// completo dando la opción de elegir la entidad de la que
				// queremos obtener el CRUD
				if (element instanceof IEntity) {
					wizard = new WizardCRUD((IEntity) element);
				} else {
					wizard = new WizardCRUD();
				}
				wizard.init(window.getWorkbench(), structuredSelection);
				WizardDialog dialog = new WizardDialog(window.getShell(),
						wizard);
				dialog.setPageSize(640, 240);
				dialog.open();
			}
		} catch (Exception e) {
			e.printStackTrace();
			// Sacar mensaje de error por consola diciendo que no
			// se ha abierto y seleccionado un modelo
		}
		return null;
	}
}
