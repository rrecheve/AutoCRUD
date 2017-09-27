/**
 * WebRatio Assistant v3.0
 * 
 * University of Extremadura (Spain) www.unex.es
 * 
 * Developers:
 * 	- Carlos Aguado Fuentes (v2)
 * 	- Javier Sierra Blázquez (v3.0)
 */

package org.homeria.webratioassistant.wizards;

import java.util.List;
import java.util.Map;
import java.util.Queue;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.homeria.webratioassistant.elements.Link;
import org.homeria.webratioassistant.elements.Unit;
import org.homeria.webratioassistant.elements.WebRatioElement;
import org.homeria.webratioassistant.exceptions.ExceptionHandler;
import org.homeria.webratioassistant.webratio.Debug;
import org.homeria.webratioassistant.webratio.ProjectParameters;

import com.webratio.commons.mf.IMFElement;
import com.webratio.ide.model.IAttribute;
import com.webratio.ide.model.IRelationshipRole;

/**
 * This Wizard Class manages the graphic assistant
 */
public class WizardCRUD extends Wizard implements INewWizard {
	private WizardPatternPage patternPage;

	private Queue<WebRatioElement> pages;
	private List<Unit> units;
	private List<Link> links;
	private List<IMFElement> siteViewsAreas;
	private Map<IRelationshipRole, IAttribute> relshipsSelected;

	/**
	 * 
	 */
	public WizardCRUD() {
		super();
		this.setNeedsProgressMonitor(true);
	}

	public Queue<WebRatioElement> getPagesGen() {
		return this.pages;
	}

	public List<Unit> getUnits() {
		return this.units;
	}

	public List<Link> getLinks() {
		return this.links;
	}

	public List<IMFElement> getSiteViewsAreas() {
		return this.siteViewsAreas;
	}

	public Map<IRelationshipRole, IAttribute> getRelshipsSelected() {
		return this.relshipsSelected;
	}

	@Override
	public void addPages() {
		this.patternPage = new WizardPatternPage();
		this.addPage(this.patternPage);
	}

	@Override
	public boolean canFinish() {
		return this.patternPage.canFinish();
	}

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		try {
			ProjectParameters.init();
			ProjectParameters.initSiteViews();
		} catch (ExecutionException e) {
			e.printStackTrace();
		} catch (Exception e) {
			Debug.println(this.getClass().toString(), e.toString());
			e.printStackTrace();
		}
	}

	@Override
	public boolean performFinish() {

		try {
			this.patternPage.finalizePage();

			this.pages = this.patternPage.getPages();
			this.units = this.patternPage.getUnits();
			this.links = this.patternPage.getLinks();
			this.siteViewsAreas = this.patternPage.getSvAreasSelected();
			this.relshipsSelected = this.patternPage.getRelationshipsSelected();

		} catch (Exception e) {
			ExceptionHandler.handle(e);
		}

		return true;
	}

	@Override
	public void finalize() {
		try {
			this.dispose();
		} catch (Throwable e) {
			e.printStackTrace();
		}

	}
}
