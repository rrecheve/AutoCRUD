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

import java.util.List;

import org.eclipse.gef.commands.CommandStack;
import org.homeria.webratioassistant.webratioaux.AddSelectorConditionCommand;

import com.webratio.commons.mf.IMFElement;
import com.webratio.ide.core.UnitHelper;
import com.webratio.ide.units.core.ISubUnitType;
import com.webratio.ide.units.internal.core.Selector;

/**
 * Manages the creation of a new SelectorUnit using WebRatio library calls
 */
@SuppressWarnings("restriction")
public final class NewSelector extends WebRatioCalls {

	private String type;
	private IMFElement element;

	/**
	 * Constructs a new instance.
	 * 
	 * @param element
	 * @param type
	 */
	public NewSelector(IMFElement element, String type) {
		super(null, 0, 0);
		this.element = element;
		this.type = type;
	}

	/* (non-Javadoc)
	 * @see org.homeria.webratioassistant.webratio.WebRatioCalls#execute()
	 */
	@Override
	public IMFElement execute() {
		List<ISubUnitType> list = UnitHelper.getUnitType(this.element).getSubUnitTypes();
		ISubUnitType keyCondition = null;
		Selector selector;
		IMFElement condition = null;
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i) instanceof Selector) {

				selector = (Selector) list.get(i);
				keyCondition = selector.getSubUnitType(this.type);

				AddSelectorConditionCommand cmd = new AddSelectorConditionCommand(this.element, keyCondition);
				cmd.setEditor(ProjectParameters.getWebProjectEditor().getActiveGraphEditor());
				((CommandStack) ProjectParameters.getWorkbenchPartWebRatio().getAdapter(CommandStack.class)).execute(cmd);
				condition = this.element.selectSingleElement(selector.getElementName()).selectSingleElement(keyCondition.getElementName());
			}
		}
		return condition;
	}
}
