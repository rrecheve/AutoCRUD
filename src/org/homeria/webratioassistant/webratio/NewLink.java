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

/**
 * Manages the creation of a new Link using WebRatio library calls
 */
@SuppressWarnings("restriction")
public final class NewLink extends WebRatioCalls {
	private IMFElement target;
	private IMFElement link;
	private String name;
	private IMFElement source;
	private String type;

	/**
	 * Constructs a new instance.
	 * 
	 * @param name
	 *            : link name
	 * @param source
	 *            : source element
	 * @param target
	 *            : target element
	 * @param type
	 *            : link type
	 */
	public NewLink(String name, IMFElement source, IMFElement target, String type) {
		super(null, 0, 0);
		this.name = name;
		this.source = source;
		this.target = target;
		this.link = null;
		this.type = type;
	}

	/* (non-Javadoc)
	 * @see org.homeria.webratioassistant.webratio.WebRatioCalls#execute()
	 */
	@Override
	public IMFElement execute() {
		try {
			// We get EditPart where we are drawing webratio modeling
			EditPart ep = ProjectParameters.getEditPartViewer().getRootEditPart();
			// With that editPart we create the corresponding ones for the source unit and the destination unit
			EditPart sourcePart = ProjectParameters.getEditPartViewer().getEditPartFactory().createEditPart(ep, this.source);
			EditPart targetPart = ProjectParameters.getEditPartViewer().getEditPartFactory().createEditPart(ep, this.target);
			ConnectionCommand cmd = null;

			// Depending on the type vary some functions
			if (this.type.equals("OKLink")) {
				cmd = new AddOKLinkCommand(this.source.getParentElement().getModelId());

				// We select the editor (SiteView normally)
				cmd.setEditPartViewer(ProjectParameters.getEditPartViewer());
				cmd.setEditor(ProjectParameters.getWebProjectEditor().getActiveGraphEditor());
				// Select source and target
				cmd.setSource(sourcePart);
				cmd.setTarget(targetPart);
				// If they are correct units (which they will be) is executed
				if (cmd.canStart()) {
					if (AddOKLinkCommand.canStart(this.source) && AddOKLinkCommand.canComplete(this.source, this.target)) {
						((CommandStack) ProjectParameters.getWorkbenchPartWebRatio().getAdapter(CommandStack.class)).execute(cmd);
					}
				}
				// We return the link to modify it in other functions
				this.link = this.getLastLink(this.source);
				// We give it the name that corresponds to it (View, Modify, Delete ...)
				Utilities.setAttribute(this.link, "name", this.name);
			}

			// Operation is same as above
			if (this.type.equals("KOLink")) {
				cmd = new AddKOLinkCommand(this.source.getParentElement().getModelId());
				cmd.setEditPartViewer(ProjectParameters.getEditPartViewer());
				cmd.setEditor(ProjectParameters.getWebProjectEditor().getActiveGraphEditor());
				cmd.setSource(sourcePart);
				cmd.setTarget(targetPart);
				if (cmd.canStart()) {
					if (AddKOLinkCommand.canStart(this.source) && AddKOLinkCommand.canComplete(this.source, this.target)) {
						((CommandStack) ProjectParameters.getWorkbenchPartWebRatio().getAdapter(CommandStack.class)).execute(cmd);
					}
				}
				this.link = this.getLastLink(this.source);
				Utilities.setAttribute(this.link, "name", this.name);
			}
			// The transport and normal links are the same, only varies a field (type) in the XML, and its method of execution is equal to
			// the previous two
			if (this.type.equals("normal") || this.type.equals("transport")) {

				cmd = new AddLinkCommand(this.source.getParentElement().getModelId());
				cmd.setEditPartViewer(ProjectParameters.getEditPartViewer());
				cmd.setEditor(ProjectParameters.getWebProjectEditor().getActiveGraphEditor());
				cmd.setSource(sourcePart);
				cmd.setTarget(targetPart);
				if (cmd.canStart()) {
					if (AddLinkCommand.canStart(this.source) && AddLinkCommand.canComplete(this.source, this.target)) {
						((CommandStack) ProjectParameters.getWorkbenchPartWebRatio().getAdapter(CommandStack.class)).execute(cmd);
					}
				}
				this.link = this.getLastLink(this.source);
				Utilities.setAttribute(this.link, "name", this.name);
				Utilities.setAttribute(this.link, "type", this.type);
			}
		} catch (Exception e) {
			Debug.println(Utilities.class.toString() + " (addLink)", e.getMessage());
			e.printStackTrace();
		}
		return this.link;
	}

	/**
	 * Returns the last link created
	 * 
	 * @param element
	 *            : the source element of the link.
	 * @return last link created
	 */
	private IMFElement getLastLink(IMFElement element) {
		IPage page;
		IContentUnit unit;
		IOperationUnit opUnit;

		if (element instanceof IContentUnit) {
			unit = (IContentUnit) element;
			return (unit.getOutgoingLinkList().get(unit.getOutgoingLinkList().size() - 1));
		}
		if (element instanceof IPage) {
			page = (IPage) element;
			return (page.getOutgoingLinkList().get(page.getOutgoingLinkList().size() - 1));
		}
		if (element instanceof IOperationUnit) {
			opUnit = (IOperationUnit) element;

			if (this.type.equals("OKLink")) {
				return (opUnit.getOutgoingOKLinkList().get(opUnit.getOutgoingOKLinkList().size() - 1));
			}
			if (this.type.equals("KOLink")) {
				return (opUnit.getOutgoingKOLinkList().get(opUnit.getOutgoingKOLinkList().size() - 1));
			}
			if (this.type.equals("normal") || this.type.equals("transport")) {
				return (opUnit.getOutgoingLinkList().get(opUnit.getOutgoingLinkList().size() - 1));
			}
		}
		return null;
	}
}
