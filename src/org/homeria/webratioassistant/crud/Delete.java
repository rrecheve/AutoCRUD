/**
 * PROYECTO FIN DE CARRERA:
 * 		- Título: Generación automática de la arquitectura de una aplicación web en WebML a partir de la
 *		  		  especificación de requisitos
 * REALIZADO POR:
 * 		- CARLOS AGUADO FUENTES, DNI: 76036306P
 * 		- INGENIERIA INFORMATICA: 2012/2013, CONVOCATORIA DE JUNIO 
 */
package org.homeria.webratioassistant.crud;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.draw2d.geometry.Point;
import org.homeria.webratioassistant.plugin.Utilities;

import com.webratio.commons.mf.IMFElement;
import com.webratio.ide.model.IArea;
import com.webratio.ide.model.IAttribute;
import com.webratio.ide.model.IContentUnit;
import com.webratio.ide.model.IOperationUnit;
import com.webratio.ide.model.IPage;
import com.webratio.ide.model.ISiteView;

public class Delete extends CRUD {

	/**
	 * 
	 * @param entidad
	 * @param siteViews
	 * @param areas
	 * @param index
	 */
	public Delete(IMFElement entidad, List<ISiteView> siteViews,
			List<IArea> areas, List<IAttribute> index) {
		super(siteViews, areas, entidad, index, null, null);
	}

	/**
	 * Nombre: ejecutar Funcion: ejecuta las operaciones que crean la parte
	 * Delete del CRUD
	 * 
	 * @param subProgressMonitor
	 */
	public void ejecutar(SubProgressMonitor subProgressMonitor) {
		int unidad = 1;
		int totalWork = this.getListaSiteViews().size() * (7) * unidad;

		subProgressMonitor.beginTask("Delete", totalWork);

		try {
			ISiteView siteView;

			for (Iterator<ISiteView> iteradorSiteView = this
					.getListaSiteViews().iterator(); iteradorSiteView.hasNext();) {
				siteView = iteradorSiteView.next();

				List<IArea> listaAreaEnc = null;
				// metodo que diga si en la lista de areas de entrada hay alguno
				// que corresponde al siteView del iterator
				if (null != siteView.getAreaList()
						&& siteView.getAreaList().size() > 0
						&& null != this.getListaAreas()
						&& this.getListaAreas().size() > 0) {

					for (Iterator iterator = this.getListaAreas().iterator(); iterator
							.hasNext();) {
						IArea iAreaSelected = (IArea) iterator.next();

						String[] partesNombreID = iAreaSelected.getRootXPath()
								.split("'"); // id('stv')
						String idPadre = partesNombreID[1];
						if (idPadre.compareTo(siteView.getFinalId()) == 0) {
							if (null == listaAreaEnc) {
								listaAreaEnc = new ArrayList<IArea>();
							}
							listaAreaEnc.add(iAreaSelected);
						}

					}
				}

				// Utilities.switchSiteView(siteView);

				if (null == listaAreaEnc) {
					Utilities.switchSiteView(siteView);
					deleteCrearElementos(subProgressMonitor, unidad, siteView);
					subProgressMonitor.worked(unidad);
				} else {
					for (Iterator iterator = listaAreaEnc.iterator(); iterator
							.hasNext();) {
						IArea iArea = (IArea) iterator.next();
						Utilities.switchSiteView(siteView);
						deleteCrearElementos(subProgressMonitor, unidad, iArea);
						subProgressMonitor.worked(unidad);

					}
				}

				subProgressMonitor.worked(unidad);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			subProgressMonitor.done();
		}
	}

	/**
	 * 
	 * Nombre: deleteCrearElementos Funcion:
	 * 
	 * @param subProgressMonitor
	 * @param unidad
	 * @param elementIMFE
	 */
	private void deleteCrearElementos(SubProgressMonitor subProgressMonitor,
			int unidad, IMFElement elementIMFE) {
		IPage pagina;
		IOperationUnit deleteUnit;
		IContentUnit powerIndexUnit;
		IContentUnit multiMessageUnit;
		Point posicion;
		int x;
		int y;
		posicion = Utilities.buscarHueco();
		x = posicion.x;
		y = posicion.y;

		pagina = (IPage) this.addPagina(elementIMFE, "Delete", x, y);
		subProgressMonitor.worked(unidad);
		powerIndexUnit = (IContentUnit) this.addUnidad(pagina,
				"PowerIndexUnit", 5, 5, "Index", true, null);
		this.addAtritubosIndex(powerIndexUnit);
		subProgressMonitor.worked(unidad);

		multiMessageUnit = (IContentUnit) this.addUnidad(pagina,
				"MultiMessageUnit", 5, 155, "Message", false, null);

		subProgressMonitor.worked(unidad);
		deleteUnit = (IOperationUnit) this.addUnidad(elementIMFE, "DeleteUnit",
				x + 250, y, "Delete", true, null);

		subProgressMonitor.worked(unidad);

		this.addNormalLink(powerIndexUnit, deleteUnit, "Delete");
		subProgressMonitor.worked(unidad);

		IMFElement link = this.addOKLink(deleteUnit, multiMessageUnit);
		this.setAutomaticCoupling(link);
		this.putMessageOnMultiMessageUnit(link, multiMessageUnit,
				"Deleted correctly");
		subProgressMonitor.worked(unidad);

		link = this.addKOLink(deleteUnit, multiMessageUnit);
		this.setAutomaticCoupling(link);

		this.putMessageOnMultiMessageUnit(link, multiMessageUnit,
				"Error deleting data");
	}
}
