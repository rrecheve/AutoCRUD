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
import com.webratio.ide.model.IPage;
import com.webratio.ide.ui.commands.AddAlternativeCommand;

/**
 * Manages the creation of a new Alternative Page (XOR) using WebRatio library calls
 */
@SuppressWarnings("restriction")
public final class NewAlternative extends WebRatioCalls {

	private String name;
	private IMFElement page;

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
	public NewAlternative(IMFElement parent, int x, int y, String name) {
		super(parent, x, y);
		this.name = name;
	}

	/* (non-Javadoc)
	 * @see org.homeria.webratioassistant.webratio.WebRatioCalls#execute()
	 */
	@Override
	public IMFElement execute() {
		try {
			// We verify that the parent is a page, since the alternative can only go within a page
			if (this.getParent() instanceof IPage) {
				SelectionCommand cmd = new AddAlternativeCommand(this.getParent().getModelId());
				List<IMFElement> list = new ArrayList<IMFElement>();
				list.add(this.getParent());
				cmd.setSelection(list);
				cmd.setLocation(this.getPoint());

				((CommandStack) ProjectParameters.getWorkbenchPartWebRatio().getAdapter(CommandStack.class)).execute(cmd);
				// We get the page that has been created within the alternative zone
				this.page = this.getLastAlternative(this.getParent());
				Utilities.setAttribute(this.page, "name", this.name);

			}
		} catch (Exception e) {
			Debug.println(this.getClass().toString() + " " + new Exception().getStackTrace()[0].getMethodName(), "Failed to add page");
			e.printStackTrace();
		}
		return this.page;

	}

	/**
	 * Returns the last alternative created
	 * 
	 * @param element
	 *            : the IPage that contains the XOR page. If not an IPage instance returns null.
	 * @return last XOR Page created
	 */
	private IMFElement getLastAlternative(IMFElement element) {
		IPage page;
		if (element instanceof IPage) {
			page = (IPage) element;
			int numberAlternatives = page.getAlternativeList().size();
			return (page.getAlternativeList().get(numberAlternatives - 1));
		}
		return null;
	}

}
