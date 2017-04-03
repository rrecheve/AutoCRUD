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
import com.webratio.ide.model.IPage;
import com.webratio.ide.model.ISiteView;

public class Retrieve extends CRUD {

	/**
	 * 
	 * @param entity
	 * @param siteViews
	 * @param areas
	 * @param index
	 * @param data
	 */
	public Retrieve(IMFElement entity, List<ISiteView> siteViews,
			List<IArea> areas, List<IAttribute> index, List<IAttribute> data) {
		super(siteViews, areas, entity, index, data, null);

	}

	/**
	 * Nombre: ejecutar Funcion: Crea los elementos necesarios para la funcion
	 * Retrieve del CRUD
	 * 
	 * @param subProgressMonitor
	 */
	public void ejecutar(SubProgressMonitor subProgressMonitor) {

		int unidad = 1;
		int totalWork = this.getListaSiteViews().size() * (4) * unidad;

		subProgressMonitor.beginTask("Retrieve", totalWork);
		try {

			ISiteView siteView;

			for (Iterator<ISiteView> iteradorSiteView = this
					.getListaSiteViews().iterator(); iteradorSiteView.hasNext();) {

				siteView = iteradorSiteView.next();

				// Si para un siteView de la lista existe un area en la lista de
				// areas, el READ se hara sobre ese area
				// puedo hacerlo tb con el id que tiene ese area: EJ: area5
				// [sv1#area1#area3#area5], id del siteView: sv1
				// Si para un siteView de la lista no existe un area, el READ se
				// hara sobre ese siteView

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
				// crear en siteView
				if (null == listaAreaEnc) {
					Utilities.switchSiteView(siteView);
					retrieveCrearElementos(subProgressMonitor, unidad, siteView);
					subProgressMonitor.worked(unidad);
				} else {
					for (Iterator iterator = listaAreaEnc.iterator(); iterator
							.hasNext();) {
						IArea iArea = (IArea) iterator.next();
						Utilities.switchSiteView(siteView);
						retrieveCrearElementos(subProgressMonitor, unidad,
								iArea);
						subProgressMonitor.worked(unidad);

					}
				}
				subProgressMonitor.worked(unidad);

			}

		} catch (Exception e) {

		} finally {
			subProgressMonitor.done();
		}
	}

	/**
	 * 
	 * Nombre: retrieveCrearElementos Funcion:
	 * 
	 * @param subProgressMonitor
	 * @param unidad
	 * @param elementIMFE
	 */
	private void retrieveCrearElementos(SubProgressMonitor subProgressMonitor,
			int unidad, IMFElement elementIMFE) {
		IPage pagina;
		IContentUnit dataUnit;
		IContentUnit powerIndexUnit;
		Point posicion;
		int x;
		int y;
		posicion = Utilities.buscarHueco();

		x = posicion.x;
		y = posicion.y;

		pagina = (IPage) this.addPagina(elementIMFE, "Read", x, y);
		subProgressMonitor.worked(unidad);

		powerIndexUnit = (IContentUnit) this.addUnidad(pagina,
				"PowerIndexUnit", 5, 5, "Index", true, null);
		this.addAtritubosIndex(powerIndexUnit);
		subProgressMonitor.worked(unidad);

		dataUnit = (IContentUnit) this.addUnidad(pagina, "DataUnit", 200, 10,
				"Data", true, null);
		this.addAtritubosData(dataUnit);
		subProgressMonitor.worked(unidad);
		// Añadimos un link normal entre la powerIndex
		// y la dataUnit para mostrar el contenido
		this.addNormalLink((IMFElement) powerIndexUnit, (IMFElement) dataUnit,
				"View");
	}
}
