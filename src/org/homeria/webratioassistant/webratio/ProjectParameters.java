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

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import com.webratio.commons.mf.IMFElement;
import com.webratio.commons.mf.ui.MFUIPlugin;
import com.webratio.commons.mf.ui.editors.MFGraphEditor;
import com.webratio.commons.mf.ui.editors.MFMultiEditor;
import com.webratio.ide.model.IDataModel;
import com.webratio.ide.model.ISiteView;
import com.webratio.ide.model.IWebModel;
import com.webratio.ide.model.IWebProject;
import com.webratio.ide.ui.editors.WebProjectEditor;

/**
 * This class contains the variables needed to interactuate with WebRatio Platform. Abstract class.
 */
public abstract class ProjectParameters {
	private static Shell shell;
	private static IEditorPart activeEditor;
	private static IWorkbenchPage activePage;
	private static IWorkbenchWindow activeWindow;
	private static IDataModel dataModel;
	private static EditPartViewer editPartViewer;
	private static Map<ISiteView, MFGraphEditor> siteViews;
	private static IWebModel webModel;
	private static IWebProject webProject;
	private static WebProjectEditor webProjectEditor;
	private static IWorkbenchPart workbenchPart;
	private static IWorkbenchPart workbenchPartWebRatio = null;

	private static MFMultiEditor multiEditor;
	/**
	 * To save the key(oid) field with the unitEntry. Needed to coupling. Syntax: Map (entryUnit,fieldOid)
	 */
	public static Map<IMFElement, IMFElement> entryKeyfieldMap;

	static public MFMultiEditor getMultiEditor() {
		return ProjectParameters.multiEditor;
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

	/**
	 * Initialices the ProjectParameters and its variables.
	 * 
	 * @param workbench
	 * @throws ExecutionException
	 */
	static public void init(IWorkbench workbench) throws ExecutionException {
		ProjectParameters.toNullValues();
		entryKeyfieldMap = new HashMap<IMFElement, IMFElement>();

		try {
			ProjectParameters.activeWindow = MFUIPlugin.getActiveWorkbenchWindow();

			if (ProjectParameters.activeWindow == null)
				throw new ExecutionException("No active workbench window");

			ProjectParameters.activePage = ProjectParameters.activeWindow.getActivePage();
			if (ProjectParameters.activePage == null)
				throw new ExecutionException("no active page");

			ProjectParameters.activeWindow.getActivePage().activate(
					(IWorkbenchPart) ProjectParameters.activePage.getActiveEditor().getAdapter(WebProjectEditor.class));

			ProjectParameters.workbenchPart = MFUIPlugin.getActiveWorkbenchWindow().getPartService().getActivePart();

			ProjectParameters.workbenchPartWebRatio = ProjectParameters.workbenchPart;
			if (ProjectParameters.workbenchPart == null)
				throw new ExecutionException("no workbenchPart");

			ProjectParameters.activeEditor = ProjectParameters.activePage.getActiveEditor();

			if (ProjectParameters.activeEditor == null)
				throw new ExecutionException("No active editor");
			ProjectParameters.editPartViewer = (EditPartViewer) ProjectParameters.activeEditor.getAdapter(EditPartViewer.class);
			if ((ProjectParameters.activeEditor instanceof WebProjectEditor))
				ProjectParameters.webProjectEditor = (WebProjectEditor) ProjectParameters.activeEditor;
			else
				throw new ExecutionException("This active editor is not instance of WebProjectEditor");

			if (ProjectParameters.getWebProjectEditor() != null) {
				ProjectParameters.webProject = (IWebProject) ProjectParameters.getWebProjectEditor().getInputModel();
				ProjectParameters.dataModel = (IDataModel) ProjectParameters.webProject.selectSingleElement("DataModel");
				ProjectParameters.webModel = (IWebModel) ProjectParameters.webProject.selectSingleElement("WebModel");

			}

			// Create siteView
			ProjectParameters.multiEditor = MFUIPlugin.getActiveMultiEditor();

		} catch (ExecutionException e) {
			Debug.println(ProjectParameters.class.toString(), e.getMessage());
		} catch (Exception e) {
			Debug.println(ProjectParameters.class.toString(), e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * Fill "Map<ISiteView, MFGraphEditor> siteViews" variable with the SiteViews
	 */
	public static void initSiteViews() {
		ProjectParameters.siteViews = new HashMap<ISiteView, MFGraphEditor>();
		// We get the list of editors, in which siteviews are created among many more elements
		List<MFGraphEditor> multiEditors = MFUIPlugin.getActiveMultiEditor().getGraphEditorList();
		Iterator<MFGraphEditor> iterator = multiEditors.iterator();
		Iterator<ISiteView> iteratorSiteView;
		MFGraphEditor editor;
		String XML, idSiteView;
		ISiteView siteView;
		List<ISiteView> siteViewsList = ProjectParameters.getWebModel().getSiteViewList();
		iterator.next();
		iterator.next();
		// We go through the graphic editors that WebRatio has at that moment
		while (iterator.hasNext()) {
			editor = iterator.next();
			// Obtain the XML structure of an element of the graphic editor, it can be a site or any other element
			XML = editor.getEditorInput().getName();
			iteratorSiteView = siteViewsList.iterator();
			while (iteratorSiteView.hasNext()) {
				// Now it is necessary to create a search pattern with all the names of siteviews, to see what is included in the XML
				siteView = iteratorSiteView.next();
				idSiteView = Utilities.getAttribute(siteView, "id");
				// If the XML contains the created pattern it is about that specific siteview
				if (XML.contains(" id=\"" + idSiteView + "\"")) {
					// And in that case we store it in the HashMap
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

	public static void setShell(Shell shell) {
		ProjectParameters.shell = shell;
	}

	public static Shell getShell() {
		return ProjectParameters.shell;
	}
}
