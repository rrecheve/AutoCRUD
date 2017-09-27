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
import com.webratio.ide.model.IArea;
import com.webratio.ide.model.ISiteView;
import com.webratio.ide.ui.commands.AddAreaCommand;

/**
 * Manages the creation of a new Area using WebRatio library calls
 */
@SuppressWarnings("restriction")
public final class NewArea extends WebRatioCalls {

	private String name;
	private IMFElement element;

	/**
	 * Constructs a new instance.
	 * 
	 * @param parent
	 *            : the parent Page
	 * @param x
	 *            : X coord to situate the XOR Page in WR model
	 * @param y
	 *            : Y coord to situate the XOR Page in WR model
	 * @param name
	 *            : display name
	 */
	public NewArea(IMFElement parent, int x, int y, String name) {
		super(parent, x, y);
		this.name = name;
	}

	/* (non-Javadoc)
	 * @see org.homeria.webratioassistant.webratio.WebRatioCalls#execute()
	 */
	@Override
	public IMFElement execute() {
		try {
			// We verify that the parent is a SiteView or Area, the Area can only go within a SiteView or another Area
			if ((this.getParent() instanceof ISiteView) || (this.getParent() instanceof IArea)) {

				SelectionCommand cmd = new AddAreaCommand(this.getParent().getModelId());
				if (this.getParent() instanceof ISiteView)
					Utilities.switchSiteView((ISiteView) this.getParent());

				List<IMFElement> list = new ArrayList<IMFElement>();
				list.add(this.getParent());
				cmd.setSelection(list);
				cmd.setLocation(this.getPoint());

				// Execute
				((CommandStack) ProjectParameters.getWorkbenchPartWebRatio().getAdapter(CommandStack.class)).execute(cmd);
				// We get the page that has been created within the alternative zone
				this.element = this.getLastArea(this.getParent());
				Utilities.setAttribute(this.element, "name", this.name);

			}
		} catch (Exception e) {
			Debug.println(this.getClass().toString() + " " + new Exception().getStackTrace()[0].getMethodName(), "Failed to add area");
			e.printStackTrace();
		}
		return this.element;

	}

	/**
	 * Returns the last area created
	 * 
	 * @param element
	 *            : the ISiteView or IArea parent of the last area created. If not an ISiteView or IArea instance returns null.
	 * @return last IArea created
	 */
	private IMFElement getLastArea(IMFElement element) {
		ISiteView siteView;
		IArea area;
		if (element instanceof ISiteView) {
			siteView = (ISiteView) element;
			int number = siteView.getAreaList().size();
			return (siteView.getAreaList().get(number - 1));
		}
		if (element instanceof IArea) {
			area = (IArea) element;
			int number = area.getAreaList().size();
			return (area.getAreaList().get(number - 1));
		}
		return null;
	}

}
