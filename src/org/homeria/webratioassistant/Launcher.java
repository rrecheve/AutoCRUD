/**
 * WebRatio Assistant v3.0
 * 
 * University of Extremadura (Spain) www.unex.es
 * 
 * Developers:
 * 	- Carlos Aguado Fuentes (v2)
 * 	- Javier Sierra Blázquez (v3.0)
 * */
package org.homeria.webratioassistant;

import java.util.List;
import java.util.Map;
import java.util.Queue;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;
import org.homeria.webratioassistant.elements.Link;
import org.homeria.webratioassistant.elements.Unit;
import org.homeria.webratioassistant.elements.WebRatioElement;
import org.homeria.webratioassistant.generation.Generate;
import org.homeria.webratioassistant.generation.StepGenerationAppWindow;
import org.homeria.webratioassistant.registry.Registry;
import org.homeria.webratioassistant.webratio.ProjectParameters;
import org.homeria.webratioassistant.webratio.Utilities;
import org.homeria.webratioassistant.wizards.WizardCRUD;
import org.homeria.webratioassistant.wizards.WizardDialogWithRegistryButton;

import com.webratio.commons.mf.IMFElement;
import com.webratio.ide.model.IAttribute;
import com.webratio.ide.model.IRelationshipRole;

/** Master Class where all plugin life-cycle occurs */
public class Launcher extends AbstractHandler {
	/** The size of the Main Dialog */
	private static final Point DIALOGSIZE = new Point(650, 400);

	public Launcher() {
	}

	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
		IWorkbenchPage page = window.getActivePage();
		IEditorPart editor = page.getActiveEditor();
		ProjectParameters.setShell(window.getShell());
		ISelection selection = null;
		IStructuredSelection structuredSelection = null;
		if (editor != null) {
			selection = editor.getSite().getSelectionProvider().getSelection();

			if (((selection instanceof IStructuredSelection)) & (!((IStructuredSelection) selection).isEmpty())) {
				structuredSelection = (IStructuredSelection) selection;
			}

			WizardCRUD wizard;

			wizard = new WizardCRUD();
			wizard.init(window.getWorkbench(), structuredSelection);
			Registry.reloadInstance();

			WizardDialogWithRegistryButton dialog = new WizardDialogWithRegistryButton(window.getShell(), wizard);
			dialog.setHelpAvailable(false);
			dialog.setPageSize(DIALOGSIZE.x, DIALOGSIZE.y);
			Utilities.setMasterDialog(dialog);
			Utilities.setIsClosed(false);

			if (dialog.open() == Window.OK && !Utilities.isPluginClosed()) {
				// Get all the Data from Wizard Page
				Queue<WebRatioElement> pages = wizard.getPagesGen();
				List<Unit> units = wizard.getUnits();
				List<Link> links = wizard.getLinks();
				List<IMFElement> siteViewsAreas = wizard.getSiteViewsAreas();
				Map<IRelationshipRole, IAttribute> relshipsSelected = wizard.getRelshipsSelected();

				Generate generate = new Generate(pages, units, links, siteViewsAreas, relshipsSelected);
				StepGenerationAppWindow appWindow = new StepGenerationAppWindow(window.getShell(), generate);

				appWindow.open();
			}
		}

		return null;
	}
}
