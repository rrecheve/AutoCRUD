/**
 * PROYECTO FIN DE CARRERA:
 * 		- Título: Generación automática de la arquitectura de una aplicación web en WebML a partir de la
 *		  		  especificación de requisitos
 * REALIZADO POR:
 * 		- CARLOS AGUADO FUENTES, DNI: 76036306P
 * 		- INGENIERIA INFORMATICA: 2012/2013, CONVOCATORIA DE JUNIO 
 */
package org.homeria.webratioassistant.plugin;

import java.util.List;

import org.eclipse.gef.commands.CommandStack;
import org.homeria.webratioassistant.webratioaux.AddSelectorConditionCommand;

import com.webratio.commons.mf.IMFElement;
import com.webratio.ide.core.UnitHelper;
import com.webratio.ide.units.core.ISubUnitType;
import com.webratio.ide.units.internal.core.Selector;

@SuppressWarnings("restriction")
public final class EventoNuevaSelector extends Evento {

	private String tipo;
	private IMFElement element;

	public EventoNuevaSelector(IMFElement element, String tipo) {
		super(null, 0, 0);
		this.element = element;
		this.tipo = tipo;
	}

	@Override
	public IMFElement ejecutar() {
		List<ISubUnitType> lista = UnitHelper.getUnitType(element)
				.getSubUnitTypes();
		ISubUnitType keyCondition = null;
		Selector selector;
		IMFElement condicion = null;
		for (int i = 0; i < lista.size(); i++) {
			if (lista.get(i) instanceof Selector) {

				selector = (Selector) lista.get(i);
				keyCondition = selector.getSubUnitType(this.tipo);

				AddSelectorConditionCommand cmd = new AddSelectorConditionCommand(
						this.element, keyCondition);
				cmd.setEditor(ProjectParameters.getWebProjectEditor()
						.getActiveGraphEditor());
				((CommandStack) ProjectParameters.getWorkbenchPartWebRatio()
						.getAdapter(CommandStack.class)).execute(cmd);
				condicion = element.selectSingleElement(
						selector.getElementName()).selectSingleElement(
						keyCondition.getElementName());
			}
		}
		return condicion;
	}
}
