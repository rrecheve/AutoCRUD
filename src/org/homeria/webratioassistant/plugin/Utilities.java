/**
 * PROYECTO FIN DE CARRERA:
 * 		- Título: Generación automática de la arquitectura de una aplicación web en WebML a partir de la
 *		  		  especificación de requisitos
 * REALIZADO POR:
 * 		- CARLOS AGUADO FUENTES, DNI: 76036306P
 * 		- INGENIERIA INFORMATICA: 2012/2013, CONVOCATORIA DE JUNIO 
 */
package org.homeria.webratioassistant.plugin;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.EditPartViewer;

import com.webratio.commons.internal.mf.MFElement;
import com.webratio.commons.mf.IMFElement;
import com.webratio.commons.mf.IMFIdProvider;
import com.webratio.commons.mf.operations.CreateMFOperation;
import com.webratio.commons.mf.operations.MFUpdater;
import com.webratio.commons.mf.ui.commands.SetAttributeCommand;
import com.webratio.commons.mf.ui.editors.MFMultiEditor;
import com.webratio.ide.core.UnitHelper;
import com.webratio.ide.model.IEntity;
import com.webratio.ide.model.ILinkParameter;
import com.webratio.ide.model.IRelationship;
import com.webratio.ide.model.IRelationshipRole;
import com.webratio.ide.model.ISiteView;
import com.webratio.ide.units.core.ISubUnitType;

public class Utilities {
	public final static int altoUnidad = 125;
	public final static int anchoUnidad = 150;

	private static SetAttributeCommand setCommand;
	private static MFUpdater updater;

	/**
	 * 
	 * Nombre: buscarHueco Funcion:
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static Point buscarHueco() {

		Point puntoInicio = new Point();
		try {
			// Se obtiene el editor seleccionado (un siteView)
			EditPartViewer editP = ProjectParameters.getEditPartViewer();
			// Se obtiene el mapa de coordenadas, cada una de ellas representa
			// un elemento del siteView
			Map mapa = editP.getVisualPartMap();
			Set<IFigure> figuras = mapa.keySet();

			Iterator<IFigure> it = figuras.iterator();
			IFigure figura;
			int maximoDerecha = 0;
			int maximoAncho = 0;
			// Recorremos el mapa de coordenadas con un iterador
			while (it.hasNext()) {
				figura = it.next();
				// Se comprueba que el borde a la derecha sea distinto al que ya
				// tenemos
				if (figura.getBounds().width != maximoAncho) {
					// Y ahora se comprueba que haya un hueco que de alto sea
					// como minimo de 1250 pixel
					if (figura.getBounds().getLocation().y < 1250) {
						// Si es así esa posición será valida, y solo nos
						// quedaremos con la más alejada a la derecha
						if (figura.getBounds().getLocation().x > maximoDerecha) {
							maximoDerecha = figura.getBounds().getLocation().x;
						}
					}
				}
			}
			// Si es la posicion inicial se le añaden 25 pixels para que no
			// quede mal visualmente
			if (maximoDerecha == 0)
				maximoDerecha = maximoDerecha + 25;
			else
				// Si ya hay elementos dibujados se le añaden 200 pixels para
				// separarlos
				maximoDerecha = maximoDerecha + 200;
			// La posicion Y siempre serán 25 pixels
			puntoInicio.setLocation(maximoDerecha, 25);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return puntoInicio;
	}

	@SuppressWarnings("unchecked")
	public static ILinkParameter createLinkParameter(String modelId,
			IMFIdProvider idProvider, String parentId) {
		Class publicType = ILinkParameter.class;
		ILinkParameter newLinkParameter = (ILinkParameter) new CreateMFOperation(
				publicType, modelId).execute();
		((MFElement) newLinkParameter)
				.setAttribute("id", idProvider.getFirstFreeId(parentId,
						publicType, null, true).first);
		return newLinkParameter;
	}

	/**
	 * 
	 * Nombre: getAttribute Funcion:
	 * 
	 * @param element
	 * @param attribute
	 * @return
	 */
	public static String getAttribute(IMFElement element, String attribute) {
		updater = element.getRootElement().getModelUpdater();
		return (updater.getAttribute(element, attribute));
	}

	/**
	 * 
	 */
	public static ISubUnitType getSubUnitType(IMFElement element, String name) {
		return UnitHelper.getUnitType(element).getSubUnitType(name);
	}

	/**
	 * 
	 * Nombre: setAttribute Funcion:
	 * 
	 * @param element
	 * @param attribute
	 * @param newValue
	 * @return
	 */
	public static boolean setAttribute(IMFElement element, String attribute,
			String newValue) {
		boolean canExecute;
		setCommand = new SetAttributeCommand(element, attribute, newValue,
				element.getModelId(), ProjectParameters.getEditPartViewer());
		canExecute = setCommand.canExecute();
		if (canExecute) {
			setCommand.execute();
		}
		return canExecute;
	}

	/**
	 * 
	 * Nombre: switchSiteView Funcion:
	 * 
	 * @param siteView
	 */
	public static void switchSiteView(ISiteView siteView) {
		try {
			MFMultiEditor multiEditor = (MFMultiEditor) ProjectParameters
					.getMFGraphEditor(siteView).getAdapter(MFMultiEditor.class);
			multiEditor.activateEditor(ProjectParameters
					.getMFGraphEditor(siteView));
			ProjectParameters.init();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * Nombre: getTargetEntity Funcion:
	 * 
	 * @param role
	 * @param entidad
	 * @return
	 */
	public static IEntity getTargetEntity(IRelationshipRole role,
			IEntity entidad) {
		IRelationship relation = (IRelationship) role.getParentElement();
		if (relation.getTargetEntity() == entidad) {
			return relation.getSourceEntity();
		} else
			return relation.getTargetEntity();

	}

}
