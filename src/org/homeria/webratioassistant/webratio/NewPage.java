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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.gef.commands.CommandStack;

import com.webratio.commons.mf.IMFElement;
import com.webratio.commons.mf.ui.commands.SelectionCommand;
import com.webratio.ide.model.IAlternative;
import com.webratio.ide.model.IArea;
import com.webratio.ide.model.ISiteView;
import com.webratio.ide.ui.commands.AddPageCommand;

/**
 * Manages the creation of a new Page using WebRatio library calls
 */
@SuppressWarnings("restriction")
public final class NewPage extends WebRatioCalls {

	private String name;
	private IMFElement page;
	private Boolean isLandmark;

	/**
	 * Constructs a new instance.
	 * 
	 * @param parent
	 *            : the parent SiteView or Page
	 * @param x
	 *            : X coord to situate the XOR Page in WR model
	 * @param y
	 *            : Y coord to situate the XOR Page in WR model
	 * @param name
	 *            : display name
	 * @param isLandmark
	 *            : sets the property 'landmark' of this page.
	 */
	public NewPage(IMFElement parent, int x, int y, String name, Boolean isLandmark) {
		super(parent, x, y);
		this.name = name;
		this.isLandmark = isLandmark;
	}

	/* (non-Javadoc)
	 * @see org.homeria.webratioassistant.webratio.WebRatioCalls#execute()
	 */
	@Override
	public IMFElement execute() {
		try {
			// We verify that the site to create the page is a SiteView or an alternative page
			if ((this.getParent() instanceof ISiteView) || (this.getParent() instanceof IAlternative)
					|| (this.getParent() instanceof IArea)) {
				// We instantiated the command to create WebRatio Page
				SelectionCommand cmd = new AddPageCommand(this.getParent().getModelId());
				if (this.getParent() instanceof ISiteView)
					Utilities.switchSiteView((ISiteView) this.getParent());
				// It is necessary to include in the command a list with the elements where the page is to be inserted, in this case the
				// list will be only composed by the id of the parent (SiteView or AlternativePage)
				List<IMFElement> list = new ArrayList<IMFElement>();
				list.add(this.getParent());
				// We selected the parent as the place to put it
				cmd.setSelection(list);
				// And we indicate its spatial position X, Y
				cmd.setLocation(this.getPoint());

				// Execute
				((CommandStack) ProjectParameters.getWorkbenchPart().getAdapter(CommandStack.class)).execute(cmd);
				// Get the last page created
				this.page = this.getLastPage(this.getParent());
				// And modify the necessary attributes
				Utilities.setAttribute(this.page, "name", this.name);
				Utilities.setAttribute(this.page, "landmark", this.isLandmark.toString());
			}
		} catch (Exception e) {
			Debug.println(this.getClass().toString() + " " + new Exception().getStackTrace()[0].getMethodName(), "Failed to add a page");
			e.printStackTrace();
		}
		return this.page;
	}

	/**
	 * 
	 * 
	 * Returns the last page created *
	 * 
	 * @param element
	 *            : the parent that contains the last page created. If not an instance of ISiteView, IAlternative or IArea, returns null
	 * @return the last IPage created
	 */
	private IMFElement getLastPage(IMFElement element) {
		ISiteView siteView;
		IAlternative alternative;
		IArea area;
		if (element instanceof ISiteView) {
			siteView = (ISiteView) element;
			int numberPages = siteView.getPageList().size();
			return (siteView.getPageList().get(numberPages - 1));
		}
		if (element instanceof IAlternative) {
			alternative = (IAlternative) element;
			int numberPages = alternative.getPageList().size();
			return (alternative.getPageList().get(numberPages - 1));
		}
		if (element instanceof IArea) {
			area = (IArea) element;
			int numberPages = area.getPageList().size();
			return (area.getPageList().get(numberPages - 1));
		}
		return null;
	}

}
