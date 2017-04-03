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
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import com.webratio.commons.mf.ui.MFUIPlugin;
import com.webratio.commons.mf.ui.editors.MFGraphEditor;
import com.webratio.commons.mf.ui.editors.MFMultiEditor;
import com.webratio.ide.model.IDataModel;
import com.webratio.ide.model.ISiteView;
import com.webratio.ide.model.IWebModel;
import com.webratio.ide.model.IWebProject;
import com.webratio.ide.ui.editors.WebProjectEditor;

public class ProjectParameters {

	private static IEditorPart activeEditor;
	private static IWorkbenchPage activePage;
	private static IWorkbenchWindow activeWindow;
	private static IDataModel dataModel;
	private static EditPartViewer editPartViewer;
	private static Map<ISiteView, MFGraphEditor> siteViews;
	private static IWebModel webModel;
	private static IWebProject webProject;
	static WebProjectEditor webProjectEditor;
	private static IWorkbenchPart workbenchPart;
	private static IWorkbenchPart workbenchPartWebRatio = null;

	// datos intermedios auxiliares para cambios en estructura de arbol de
	// pagina intermedia: SiteView-Area-Area
	private static Tree arbolPaginaAlta = null;
	private static List<ObjStViewArea> listaSiteViewArea = null;

	private static MFMultiEditor multiEditor;

	static public MFMultiEditor getMultiEditor() {
		return ProjectParameters.multiEditor;
	}

	static public List<ObjStViewArea> getlistaSiteViewArea() {
		return ProjectParameters.listaSiteViewArea;
	}

	static public void setlistaSiteViewArea(List lista) {
		ProjectParameters.listaSiteViewArea = lista;
	}

	static public Tree getArbolPaginaAlta() {
		return ProjectParameters.arbolPaginaAlta;
	}

	static public void setArbolPaginaAlta(Tree arbol) {
		ProjectParameters.arbolPaginaAlta = arbol;
	}

	static public IEditorPart getActiveEditor() {
		return ProjectParameters.activeEditor;
	}

	static public IWorkbenchPage getActivePage() {
		return ProjectParameters.activePage;
	}

	static public IWorkbenchWindow getActiveWindow() {
		return ProjectParameters.activeWindow;
	}

	static public IDataModel getDataModel() {
		return ProjectParameters.dataModel;
	}

	static public EditPartViewer getEditPartViewer() {
		return ProjectParameters.editPartViewer;
	}

	static public MFGraphEditor getMFGraphEditor(ISiteView siteView) {
		return ProjectParameters.siteViews.get(siteView);
	}

	static public IWebModel getWebModel() {
		return ProjectParameters.webModel;
	}

	static public IWebProject getWebProject() {
		return ProjectParameters.webProject;
	}

	static public WebProjectEditor getWebProjectEditor() {
		return ProjectParameters.webProjectEditor;
	}

	static public IWorkbenchPart getWorkbenchPart() {
		return ProjectParameters.workbenchPart;
	}

	static public IWorkbenchPart getWorkbenchPartWebRatio() {
		return ProjectParameters.workbenchPartWebRatio;
	}

	static public void init() throws ExecutionException {
		ProjectParameters.init(PlatformUI.getWorkbench());
	}

	static public void init(IWorkbench workbench) throws ExecutionException {
		ProjectParameters.toNullValues();

		try {
			ProjectParameters.activeWindow = MFUIPlugin
					.getActiveWorkbenchWindow();

			if (ProjectParameters.activeWindow == null)
				throw new ExecutionException("No active workbench window");

			ProjectParameters.activePage = ProjectParameters.activeWindow
					.getActivePage();
			if (ProjectParameters.activePage == null)
				throw new ExecutionException("no active page");

			ProjectParameters.activeWindow.getActivePage().activate(
					(IWorkbenchPart) ProjectParameters.activePage
							.getActiveEditor().getAdapter(
									WebProjectEditor.class));

			ProjectParameters.workbenchPart = MFUIPlugin
					.getActiveWorkbenchWindow().getPartService()
					.getActivePart();

			// if (ProjectParameters.workbenchPartWebRatio == null)
			ProjectParameters.workbenchPartWebRatio = ProjectParameters.workbenchPart;
			if (ProjectParameters.workbenchPart == null)
				throw new ExecutionException("no workbenchPart");

			ProjectParameters.activeEditor = ProjectParameters.activePage
					.getActiveEditor();

			if (ProjectParameters.activeEditor == null)
				throw new ExecutionException("No active editor");
			ProjectParameters.editPartViewer = (EditPartViewer) ProjectParameters.activeEditor
					.getAdapter(EditPartViewer.class);
			if ((ProjectParameters.activeEditor instanceof WebProjectEditor))
				ProjectParameters.webProjectEditor = (WebProjectEditor) ProjectParameters.activeEditor;
			else
				throw new ExecutionException(
						"This active editor is not instance of WebProjectEditor");

			if (ProjectParameters.getWebProjectEditor() != null) {
				ProjectParameters.webProject = (IWebProject) ProjectParameters
						.getWebProjectEditor().getInputModel();
				ProjectParameters.dataModel = (IDataModel) ProjectParameters.webProject
						.selectSingleElement("DataModel");
				ProjectParameters.webModel = (IWebModel) ProjectParameters.webProject
						.selectSingleElement("WebModel");

			}

			// Crear siteView
			ProjectParameters.multiEditor = MFUIPlugin.getActiveMultiEditor();


		} catch (ExecutionException e) {
			Debug.println(ProjectParameters.class.toString(), e.getMessage());
		} catch (Exception e) {
			Debug.println(ProjectParameters.class.toString(), e.getMessage());
			e.printStackTrace();
		}
	}

	public static void initSiteViews() {
		// Iniciamos la estructura que albergará los siteviews
		ProjectParameters.siteViews = new HashMap<ISiteView, MFGraphEditor>();
		// Obtenemos la lista de editores, en los que estan creados los
		// siteviews entre muchos mas elementos
		List<MFGraphEditor> multiEditors = MFUIPlugin.getActiveMultiEditor()
				.getGraphEditorList();
		Iterator<MFGraphEditor> iterador = multiEditors.iterator();
		Iterator<ISiteView> iteradorSiteView;
		MFGraphEditor editor;
		String XML, idSiteView;
		ISiteView siteView;
		// Obtenemos la lista con los nombres de los siteviews
		List<ISiteView> listaSiteViews = ProjectParameters.getWebModel()
				.getSiteViewList();
		iterador.next();
		iterador.next();
		// Recorremos los editores gráficos que tiene en ese momento WebRatio
		while (iterador.hasNext()) {
			editor = iterador.next();
			// Obtenemos la estructura XML de un elemento del editor grafico,
			// podrá ser un siteview o cualquier otro elemento
			XML = editor.getEditorInput().getName();
			iteradorSiteView = listaSiteViews.iterator();
			while (iteradorSiteView.hasNext()) {
				// Ahora es necesario crear un patron de busqueda con todos los
				// nombre de siteviews, para ver cual esta incluido en el XML
				siteView = iteradorSiteView.next();
				idSiteView = Utilities.getAttribute(siteView, "id");
				// Si el XML contiene el patron creado se tratará de ese
				// siteview en concreto
				if (XML.contains(" id=\"" + idSiteView + "\"")) {
					// y en ese caso lo almacenamos en el HashMap
					ProjectParameters.siteViews.put(siteView, editor);
				}
			}
		}
	}

	private static void toNullValues() {
		ProjectParameters.activeEditor = null;
		ProjectParameters.activeWindow = null;
		ProjectParameters.activePage = null;
		ProjectParameters.webProjectEditor = null;
		ProjectParameters.webProject = null;
		ProjectParameters.dataModel = null;
		ProjectParameters.editPartViewer = null;
	}
}
