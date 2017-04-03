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
import java.util.Map;

import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.draw2d.geometry.Point;
import org.homeria.webratioassistant.plugin.Utilities;

import com.webratio.commons.mf.IMFElement;
import com.webratio.ide.model.IArea;
import com.webratio.ide.model.IAttribute;
import com.webratio.ide.model.IContentUnit;
import com.webratio.ide.model.IEntity;
import com.webratio.ide.model.IOperationUnit;
import com.webratio.ide.model.IPage;
import com.webratio.ide.model.IRelationship;
import com.webratio.ide.model.IRelationshipRole;
import com.webratio.ide.model.ISiteView;

public class Update extends CRUD {

	/**
	 * 
	 * @param entity
	 * @param siteViews
	 * @param areas
	 * @param relation
	 * @param atributos
	 * @param atributosIndice
	 */
	public Update(IMFElement entity, List<ISiteView> siteViews,
			List<IArea> areas, Map<IRelationshipRole, IAttribute> relation,
			List<IAttribute> atributos, List<IAttribute> atributosIndice) {// List<IAttribute>
		super(siteViews, areas, entity, atributosIndice, atributos, relation);
	}

	/**
	 * Nombre: ejecutar Funcion: Crea los elementos necesarios para la opcion
	 * Update del CRUD
	 * 
	 * @param subProgressMonitor
	 */
	public void ejecutar(SubProgressMonitor subProgressMonitor) {

		int unidad = 1;
		int totalWork = this.getListaSiteViews().size()
				* (11 + (8 * this.getRelaciones().keySet().size()));
		subProgressMonitor.beginTask("Update", totalWork);

		try {

			ISiteView siteView;

			for (Iterator<ISiteView> iteradorSiteView = this
					.getListaSiteViews().iterator(); iteradorSiteView.hasNext();) {

				siteView = iteradorSiteView.next();

				// Utilities.switchSiteView(siteView);

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

				if (null == listaAreaEnc) {
					Utilities.switchSiteView(siteView);
					updateCrearElementos(subProgressMonitor, unidad, siteView);
					subProgressMonitor.worked(unidad);
				} else {
					for (Iterator iterator = listaAreaEnc.iterator(); iterator
							.hasNext();) {
						IArea iArea = (IArea) iterator.next();
						Utilities.switchSiteView(siteView);
						updateCrearElementos(subProgressMonitor, unidad, iArea);
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
	 * Nombre: updateCrearElementos Funcion:
	 * 
	 * @param subProgressMonitor
	 * @param unidad
	 * @param elementIMFE
	 */
	private void updateCrearElementos(SubProgressMonitor subProgressMonitor,
			int unidad, IMFElement elementIMFE) {
		IPage pagina;
		IOperationUnit modifyUnit;
		IContentUnit entryUnit;
		IContentUnit selectorUnit;
		IContentUnit selectorEntidad;
		IContentUnit multiMessageUnit;
		IContentUnit powerIndexUnit;
		IEntity entidadPreload;
		IMFElement link;
		Point posicion;
		int x;
		int y;
		int posx;
		int posy;
		posicion = Utilities.buscarHueco();
		x = posicion.x;
		y = posicion.y;

		// Crear Pagina del CRUD AllInOne
		pagina = (IPage) this.addPagina(elementIMFE, "Update", x, y);
		subProgressMonitor.worked(unidad);

		// Read
		posx = posy = 5;
		posy = posy + Utilities.altoUnidad;
		// Añadimos la powerIndex e indicamos los atributos visibles
		powerIndexUnit = (IContentUnit) this.addUnidad(pagina,
				"PowerIndexUnit", posx, posy, "Index", true, null);
		this.addAtritubosIndex(powerIndexUnit);
		subProgressMonitor.worked(unidad);

		posx = posx + Utilities.anchoUnidad;
		entryUnit = (IContentUnit) this.addUnidad(pagina, "EntryUnit", posx,
				posy, "Form", true, null);
		this.setFields(entryUnit, true, true);
		subProgressMonitor.worked(unidad);

		posx = posx + Utilities.anchoUnidad;

		// Añadir multiMessage para los mensajes de las unidades
		multiMessageUnit = (IContentUnit) this.addUnidad(pagina,
				"MultiMessageUnit", posx, posy, "Message", false, null);
		subProgressMonitor.worked(unidad);

		// Añadir el selector de entidad y asignarle una keyCondition
		posx = posy = 5;
		selectorEntidad = (IContentUnit) this.addUnidad(pagina, "SelectorUnit",
				posx, posy, "Selector", true, null);
		this.addKeyCondition(selectorEntidad);
		subProgressMonitor.worked(unidad);

		// Añadir el enlace de Modificar entre la
		// powerIndexUnit y selectorEntidad
		this.addNormalLink((IMFElement) powerIndexUnit,
				(IMFElement) selectorEntidad, "Modify");
		subProgressMonitor.worked(unidad);
		// Añadir link de transporte entre la selectorUnit
		// y el formulario. Hacer un guessCoupling
		link = this.addTransportLink(selectorEntidad, entryUnit, "Load");
		this.setAutomaticCoupling(link);
		this.guessCouplingUnitToEntry(selectorEntidad, this.getEntity(),
				entryUnit, link);// , preload);
		subProgressMonitor.worked(unidad);
		// Añadir Selector para precarga formulario (Update)
		// Solo son validas las relaciones NaN, las demñs las
		// carga la selectorEntidad directamente.
		posx = 5 + Utilities.anchoUnidad;
		posy = 5 + 2 * (Utilities.altoUnidad);
		IMFElement roleCondition;
		IRelationship relation;
		IRelationshipRole role;
		for (Iterator<IRelationshipRole> iteradorRole = this.getRelaciones()
				.keySet().iterator(); iteradorRole.hasNext();) {
			role = iteradorRole.next();
			entidadPreload = this.getTargetEntity(role);
			relation = this.isNtoN(role);
			if (relation != null) {
				String idRole = Utilities.getAttribute(role, "id");
				selectorUnit = (IContentUnit) this.addUnidad(pagina,
						"SelectorUnit", posx, posy, "Entity", false,
						entidadPreload);
				roleCondition = this.addRelationShipRoleCondition(selectorUnit);// ,
				// idRole);
				this.addRoleCondition((IMFElement) roleCondition, idRole);
				// Añadir link y hacer guessCoupling
				link = this.addTransportLink(selectorEntidad, selectorUnit,
						"Load");
				link = this.addTransportLink(selectorUnit, entryUnit, "Load");
				this.setAutomaticCoupling(link);
				this.guessCouplingUnitToEntry(selectorUnit, entidadPreload,
						entryUnit, link, role);
				posx = posx + Utilities.anchoUnidad;

			}
			subProgressMonitor.worked(unidad);
		}

		// Añadir las selectorUnit que se encarga de rellenar
		// los campos multiSelectionField y selectionField
		posx = 5 + Utilities.anchoUnidad;
		posy = 5;
		for (Iterator<IRelationshipRole> iteradorRole = this.getRelaciones()
				.keySet().iterator(); iteradorRole.hasNext();) {
			role = iteradorRole.next();
			entidadPreload = this.getTargetEntity(role);

			selectorUnit = (IContentUnit) this
					.addUnidad(pagina, "SelectorUnit", posx, posy, "Entity",
							false, entidadPreload);
			subProgressMonitor.worked(unidad);
			posx = posx + Utilities.altoUnidad;
			link = this.addTransportLink(selectorUnit, entryUnit, "Load");

			this.setAutomaticCoupling(link);
			this.putPreload(entryUnit, role, link);// {
			subProgressMonitor.worked(unidad);
		}
		posicion = Utilities.buscarHueco();
		x = posicion.x;
		y = posicion.y;

		modifyUnit = (IOperationUnit) this.addUnidad(elementIMFE, "ModifyUnit",
				x, y, "Modify", true, null);
		link = this.addNormalLink(entryUnit, modifyUnit, "Load");

		this.setAutomaticCoupling(link);
		this.guessCouplingEntryToCreateModify(entryUnit, modifyUnit, link);
		subProgressMonitor.worked(unidad);

		IEntity entidad;
		IOperationUnit connectUnit;
		IOperationUnit disconnectUnit;
		String nombreRole, idRole;
		IMFElement anteriorModify = modifyUnit;
		IMFElement firstCreate = null;

		link = this.addKOLink(anteriorModify, multiMessageUnit);
		this.setAutomaticCoupling(link);

		this.putMessageOnMultiMessageUnit(link, multiMessageUnit,
				"Error modify data from " + this.getNombreEntity());
		subProgressMonitor.worked(unidad);

		IMFElement anteriorCreate = null;
		for (Iterator<IRelationshipRole> iteradorRole = this.getRelaciones()
				.keySet().iterator(); iteradorRole.hasNext();) {
			role = iteradorRole.next();
			entidad = this.getTargetEntity(role);
			relation = this.isNtoN(role);
			if (relation != null) {
				posicion = Utilities.buscarHueco();
				x = posicion.x;
				y = posicion.y;
				nombreRole = Utilities.getAttribute(role, "name");
				idRole = Utilities.getAttribute(role, "id");
				connectUnit = (IOperationUnit) this.addUnidad(elementIMFE,
						"ConnectUnit", x, y, nombreRole, false, null);
				Utilities.setAttribute(connectUnit, "relationship", idRole);
				if (anteriorCreate != null)
					this.addOKLink(anteriorCreate, connectUnit);
				subProgressMonitor.worked(unidad);
				link = this.addTransportLink(entryUnit, connectUnit, "Load");
				this.setAutomaticCoupling(link);
				this.guessCouplingEntryToConnect(entryUnit, connectUnit,
						entidad, role, link);
				subProgressMonitor.worked(unidad);
				anteriorCreate = connectUnit;
				link = this.addKOLink(anteriorCreate, multiMessageUnit);
				this.setAutomaticCoupling(link);

				this.putMessageOnMultiMessageUnit(
						link,
						multiMessageUnit,
						"Error while modifying data from "
								+ this.getNombreEntity());
				if (firstCreate == null)
					firstCreate = connectUnit;

				this.addTransportLink(modifyUnit, anteriorCreate, "Load");

				subProgressMonitor.worked(unidad);

				disconnectUnit = (IOperationUnit) this.addUnidad(elementIMFE,
						"DisconnectUnit", x, y + Utilities.altoUnidad,
						nombreRole, false, null);
				Utilities.setAttribute(disconnectUnit, "relationship", idRole);
				this.convertKeyConditionToRoleCondition(disconnectUnit, idRole);
				subProgressMonitor.worked(unidad);
				this.addOKLink(anteriorModify, disconnectUnit);
				anteriorModify = disconnectUnit;
				link = this.addKOLink(anteriorModify, multiMessageUnit);
				this.setAutomaticCoupling(link);

				this.putMessageOnMultiMessageUnit(
						link,
						multiMessageUnit,
						"Error while modifying data from entity "
								+ this.getNombreEntity());

				subProgressMonitor.worked(unidad);
			}
		}
		if (anteriorCreate != null) {
			link = this.addOKLink(anteriorCreate, multiMessageUnit);
			this.setAutomaticCoupling(link);

			this.putMessageOnMultiMessageUnit(link, multiMessageUnit,
					"Correctly modify " + this.getNombreEntity());
		}
		subProgressMonitor.worked(unidad);
		if (firstCreate == null) {
			link = this.addOKLink(anteriorModify, multiMessageUnit);
			this.setAutomaticCoupling(link);
			this.putMessageOnMultiMessageUnit(link, multiMessageUnit,
					"Correctly modify " + this.getNombreEntity());

		} else {
			link = this.addOKLink(anteriorModify, firstCreate);
			this.setAutomaticCoupling(link);
		}
	}
}
