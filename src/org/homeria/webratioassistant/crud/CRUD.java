/**
 * PROYECTO FIN DE CARRERA:
 * 		- T�tulo: Generaci�n autom�tica de la arquitectura de una aplicaci�n web en WebML a partir de la
 *		  		  especificaci�n de requisitos
 * REALIZADO POR:
 * 		- CARLOS AGUADO FUENTES, DNI: 76036306P
 * 		- INGENIERIA INFORMATICA: 2012/2013, CONVOCATORIA DE JUNIO 
 */
package org.homeria.webratioassistant.crud;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.gef.commands.CommandStack;
import org.homeria.webratioassistant.plugin.Evento;
import org.homeria.webratioassistant.plugin.EventoNuevaAlternantiva;
import org.homeria.webratioassistant.plugin.EventoNuevaPagina;
import org.homeria.webratioassistant.plugin.EventoNuevaSelector;
import org.homeria.webratioassistant.plugin.EventoNuevaUnit;
import org.homeria.webratioassistant.plugin.EventoNuevoLink;
import org.homeria.webratioassistant.plugin.ProjectParameters;
import org.homeria.webratioassistant.plugin.Utilities;
import org.homeria.webratioassistant.webratioaux.AddSelectorConditionCommand;
import org.homeria.webratioassistant.webratioaux.CompositeMFCommand;

import com.webratio.commons.internal.mf.MFElement;
import com.webratio.commons.mf.IMFElement;
import com.webratio.commons.mf.operations.SetAttributeMFOperation;
import com.webratio.commons.mf.ui.commands.AbstractMFCommand;
import com.webratio.commons.mf.ui.commands.DeleteCommand;
import com.webratio.ide.core.UnitHelper;
import com.webratio.ide.model.IArea;
import com.webratio.ide.model.IAttribute;
import com.webratio.ide.model.IEntity;
import com.webratio.ide.model.ILinkParameter;
import com.webratio.ide.model.IOperationUnit;
import com.webratio.ide.model.IRelationship;
import com.webratio.ide.model.IRelationshipRole;
import com.webratio.ide.model.ISiteView;
import com.webratio.ide.model.ISubUnit;
import com.webratio.ide.model.IUnit;
import com.webratio.ide.units.core.ISubUnitType;

/**
 * @author Carlos Aguado Fuentes
 * @class CRUD
 */
/**
 * CRUD: Clase padre que engloba a todas las acciones del plugin agrupando todos
 * los atributos comunes a las cinco acciones que son crear, modificar, borrar,
 * leer y todo en uno.
 * 
 */
public class CRUD {
	private IEntity entidad;
	private HashMap<IEntity, IRelationshipRole> entidadesRelacionadas;
	private Evento evento;
	private IMFElement fieldOid;
	private List<IAttribute> listaAtributosDataOrForm;
	private List<IAttribute> listaAtributosIndex;

	private List<ISiteView> listaSiteViews;
	private List<IArea> listaAreas;
	private Map<IRelationshipRole, IAttribute> relaciones;
	protected int tamanioUnidad = 125;

	/**
	 * Constructor CRUD
	 * 
	 * @param listaSiteViews
	 * @param entidad
	 * @param listaAtributosIndex
	 * @param listaAtributosDataOrForm
	 * @param relaciones
	 */
	public CRUD(List<ISiteView> listaSiteViews,List<IArea> areas, IMFElement entidad,
			List<IAttribute> listaAtributosIndex,
			List<IAttribute> listaAtributosDataOrForm,
			Map<IRelationshipRole, IAttribute> relaciones) {
		this.listaSiteViews = listaSiteViews;
		this.listaAreas=areas;
		this.entidad = (IEntity) entidad;
		this.listaAtributosIndex = listaAtributosIndex;
		this.listaAtributosDataOrForm = listaAtributosDataOrForm;
		this.relaciones = relaciones;
	}

	/**
	 * Nombre: addArea Funcion: añade el elemento AREA al proyecto
	 * 
	 * @param padre
	 *            , lugar donde se añadira el area, normalmente un siteView
	 * @param x
	 *            , posicion x
	 * @param y
	 *            , posicion y
	 * @param nombre
	 *            , nombre del area
	 * @return elemento area que se ha añadido al siteView
	 */
	protected IMFElement addArea(IMFElement padre, int x, int y, String nombre) {
		evento = new EventoNuevaAlternantiva(padre, x, y, nombre 
				+ Utilities.getAttribute(this.entidad, "name"));
		return evento.ejecutar();
	}

	/**
	 * Nombre: addAtritubosAll Funcion: indica que el elemento mostrará todos
	 * sus atributos
	 * 
	 * @param elemento
	 */
	protected void addAtritubosAll(IMFElement elemento) {
		Utilities.setAttribute(elemento, "displayAttributes", this
				.getAtributos(this.getEntity().getAllAttributeList()));
	}

	/**
	 * Nombre: addAtritubosData Funcion: indica los atributos que se mostraran
	 * en el elemento, estos atributos estan contenidos dentro de la variable de
	 * la clase llamada listaAtributosDataOrForm
	 * 
	 * @param elemento
	 *            : IndexUnit o DataUnit
	 */
	protected void addAtritubosData(IMFElement elemento) {
		Utilities.setAttribute(elemento, "displayAttributes", this
				.getAtributos(this.listaAtributosDataOrForm));
	}

	/**
	 * Nombre: addAtritubosIndex Funcion: marca como visibles los atributos que
	 * se encuentran en la variable listaAtributosIndex
	 * 
	 * @param elemento
	 *            : IndexUnit
	 */
	protected void addAtritubosIndex(IMFElement elemento) {
		Utilities.setAttribute(elemento, "displayAttributes", this
				.getAtributos(this.listaAtributosIndex));
	}

	/**
	 * Nombre: addAttributesCondition Funcion: Añade una condición de atributo
	 * al elemento
	 * 
	 * @param element
	 * @return
	 */
	protected IMFElement addAttributesCondition(IMFElement element) {
		evento = new EventoNuevaSelector(element, "AttributesCondition");
		return evento.ejecutar();
	}

	/**
	 * Nombre: addKeyCondition Funcion: añade una condición de Key al elemento
	 * 
	 * @param element
	 * @return
	 */
	protected IMFElement addKeyCondition(IMFElement element) {
		evento = new EventoNuevaSelector(element, "KeyCondition");
		return evento.ejecutar();
	}

	/**
	 * Nombre: addKOLink Funcion: Crea un KO link entre dos unidades
	 * 
	 * @param source
	 *            : unidad origen
	 * @param target
	 *            : unidad destino
	 * @return: link creado
	 */
	protected IMFElement addKOLink(IMFElement source, IMFElement target) {
		evento = new EventoNuevoLink("KOLink", source, target, "KOLink");
		return evento.ejecutar();
	}

	/**
	 * Nombre: addNormalLink Funcion: crea un link normal entre dos unidades
	 * 
	 * @param source
	 *            : unidad origen
	 * @param target
	 *            : unidad destino
	 * @param nombre
	 *            : nombre del link
	 * @return: link creado
	 */
	protected IMFElement addNormalLink(IMFElement source, IMFElement target,
			String nombre) {
		evento = new EventoNuevoLink(nombre, source, target, "normal");
		return evento.ejecutar();
	}

	/**
	 * Nombre: addOKLink Funcion: Crea un OK Link entre dos unidades
	 * 
	 * @param source
	 *            : unidad origen
	 * @param target
	 *            : unidad destino
	 * @return: link creado
	 */
	protected IMFElement addOKLink(IMFElement source, IMFElement target) {
		evento = new EventoNuevoLink("OKLink", source, target, "OKLink");
		return evento.ejecutar();
	}

	/**
	 * Nombre: addPagina Funcion: Añade una página al proyecto
	 * 
	 * @param padre
	 *            : cualquier elemento que pueda contener una página
	 * @param nombre
	 *            : nombre de la página
	 * @param x
	 *            : posicion x
	 * @param y
	 *            : posicion y
	 * @return: página creada
	 */
	protected IMFElement addPagina(IMFElement padre, String nombre, int x, int y) {
		evento = new EventoNuevaPagina(padre, x, y, nombre 
				+ Utilities.getAttribute(this.entidad, "name"));
		return evento.ejecutar();
	}

	/**
	 * Nombre: addRelationShipRoleCondition Funcion: Añade una condicion de
	 * relationShipRole en un elemento
	 * 
	 * @param element
	 *            elemento al que se añade la condición
	 * @return: elemento con la condición creada
	 */
	protected IMFElement addRelationShipRoleCondition(IMFElement element) {
		evento = new EventoNuevaSelector(element, "RelationshipRoleCondition");
		return evento.ejecutar();
	}

	/**
	 * Nombre: addRoleCondition Funcion: Añade una condicion de role en un
	 * elemento
	 * 
	 * @param roleCondition
	 *            : condición a añadir
	 * @param nombreRole
	 *            : nombre de la role
	 */
	protected void addRoleCondition(IMFElement roleCondition, String nombreRole) {
		Utilities.setAttribute(roleCondition, "role", nombreRole);

	}

	/**
	 * Nombre: addTransportLink Funcion: Añade un link de transporte entre dos
	 * unidades
	 * 
	 * @param source
	 *            : unidad origen
	 * @param target
	 *            : unidad destino
	 * @param nombre
	 *            : nombre del link de transporte
	 * @return: link creado
	 */
	protected IMFElement addTransportLink(IMFElement source, IMFElement target,
			String nombre) {
		evento = new EventoNuevoLink(nombre, source, target, "transport");
		return evento.ejecutar();
	}

	/**
	 * Nombre: addUnidad Funcion: Añade una unidad al proyecto, pueden ser
	 * unidades de contenido o de operacion
	 * 
	 * @param padre
	 *            : elemento en el que se añadirá la unidad
	 * @param tipo
	 *            : tipo de la unidad
	 * @param x
	 *            : posicion x
	 * @param y
	 *            : posicion y
	 * @param nombre
	 *            : nombre de la unidad
	 * @param entidadBool
	 *            : si es true la entidad será la que ya esta almacenada en la
	 *            clase, en caso contrario será la entidad que pasa como
	 *            parametro
	 * @param entidadRelacion
	 *            : entidad usada en lugar de la principal del crud
	 * @return: unidad creada
	 */
	protected IMFElement addUnidad(IMFElement padre, String tipo, int x, int y,
			String nombre, boolean entidadBool, IEntity entidadRelacion) {
	
		
		IEntity sEntidad;
		// Si la variable entidadBool es true se opta por la entidad que se usa
		// en  el crud, en caso contrario usaremos la entidad que psamos por
		// parametros.
		// Se usa principalmente en las SelectorUnit para elegir entidades
		// distintas a la que se esta usando para generar el crud.
		if (entidadBool)
			sEntidad = this.entidad;
		else
			sEntidad = null;
		if (entidadRelacion != null)
			sEntidad = entidadRelacion;
		evento = new EventoNuevaUnit(padre, tipo, x, y, nombre 
				+ Utilities.getAttribute(this.entidad, "name"), sEntidad);
		return evento.ejecutar();
	}

	/**
	 * Nombre: buscarRelation Funcion: Busca una relacion por su nombre
	 * 
	 * @param nombreCampo
	 *            : nombre por el que buscar
	 * @return la relacion si se encuentra, null en caso contrario
	 */
	private IRelationshipRole buscarRelation(String nombreCampo) {

		List<IRelationship> listaRelation = this.entidad
				.getIncomingRelationshipList();
		listaRelation.addAll(this.entidad.getOutgoingRelationshipList());
		IRelationship relation;
		IRelationshipRole role;
		for (Iterator<IRelationship> iter = listaRelation.iterator(); iter
				.hasNext();) {
			relation = iter.next();
			role = relation.getRelationshipRole1();
			if (Utilities.getAttribute(role, "name").equals(nombreCampo)) {
				return role;
			}
			role = relation.getRelationshipRole2();
			if (Utilities.getAttribute(role, "name").equals(nombreCampo)) {
				return role;
			}
		}
		return null;
	}

	/**
	 * Nombre: cleanIds Funcion: Funcion auxiliar, limpia una cadena eliminando
	 * el primer y ultimo caracter
	 * 
	 * @param cadena
	 *            : string que se quiere tratar
	 * @return: cadena resultante
	 */
	private String cleanIds(String cadena) {
		return cadena.substring(1, cadena.length() - 1);
	}

	/**
	 * Nombre: convertKeyConditionToRoleCondition Funcion: convierte una
	 * keycondition en una role condicion
	 * 
	 * @param element
	 *            : elemento sobre el que actuará
	 * @param nombreRole
	 *            : nombre que se le dará a la role
	 */
	protected void convertKeyConditionToRoleCondition(IMFElement element,
			String nombreRole) {
		List<IMFElement> l = new ArrayList<IMFElement>();
		// Selecciono el hijo TargetSelector del xml del elemento
		IMFElement targetSelector = element
				.selectSingleElement("TargetSelector");
		// Y lo borramos
		l.add(targetSelector);
		AbstractMFCommand cmd = new DeleteCommand(l);
		cmd.execute();
		ISubUnitType keyCondition = null;
		IMFElement condicion = null;
		// Creamos un nuevo TargetSelector
		ISubUnitType target = UnitHelper.getUnitType(element).getSubUnitType(
				"TargetSelector");
		// Y lo hacemos como un RelationShipRoleCondition
		keyCondition = target.getSubUnitType("RelationshipRoleCondition");
		// Lo añadimos al elemento
		AddSelectorConditionCommand cmd2 = new AddSelectorConditionCommand(
				element, keyCondition);
		cmd2.setEditor(ProjectParameters.getWebProjectEditor()
				.getActiveGraphEditor());
		((CommandStack) ProjectParameters.getWorkbenchPartWebRatio()
				.getAdapter(CommandStack.class)).execute(cmd2);
		condicion = element.selectSingleElement("TargetSelector")
				.selectSingleElement(keyCondition.getElementName());
		// Y le ponemos el nombre que queramos
		Utilities.setAttribute(condicion, "role", nombreRole);
	}

	/**
	 * Nombre: createParameter Funcion: Añade un parametro a un link, para
	 * conectar un atributo con un campo de formulario
	 * 
	 * @param atributo
	 *            : atributo que queremos conectar
	 * @param subUnit
	 *            : campo del formulario que queremos conectar
	 * @param link
	 *            : link que contendrá el parametro
	 * @return: el parametro creado
	 */
	private IMFElement createParameter(IAttribute atributo, ISubUnit subUnit,
			IMFElement link) {
		IMFElement linkParameter;
		IMFElement field = (IMFElement) subUnit;
		String nombre = Utilities.getAttribute(field, "name");
		// Creamos un linkParameter con los datos necesarios
		linkParameter = Utilities.createLinkParameter(link.getModelId(),
				ProjectParameters.getWebProject().getIdProvider(), link
						.getFinalId());
		// Id: se forma de las id del link más las id de los elementos
		new SetAttributeMFOperation(linkParameter, "id", this.cleanIds(link
				.getIdsByFinalId().toString())
				+ "#" + linkParameter.getFinalId(), link.getRootElement())
				.execute();
		// Nombre: se forma con el nombre del field
		new SetAttributeMFOperation(linkParameter, "name", nombre + "_"
				+ nombre, link.getRootElement()).execute();
		// Origen: con los datos del atributo
		new SetAttributeMFOperation(linkParameter, "source", this
				.cleanIds(atributo.getIdsByFinalId().toString())
				+ "Array", link.getRootElement()).execute();
		// Destino: se crea con los campos del field del formulario
		new SetAttributeMFOperation(linkParameter, "target", this
				.cleanIds(field.getIdsByFinalId().toString())
				+ "_slot", link.getRootElement()).execute();

		return linkParameter;
	}

	/**
	 * Nombre: createParameterField2Att Funcion: Su funcionamiento es igual que
	 * la funcion createParameter pero de manera inversa, se crea la relacion
	 * entre el campo del formulario y el atributo de una unidad (create,
	 * modify...)
	 * 
	 * @param atributo
	 *            : atributo que queremos relacionar
	 * @param unidadDestino
	 *            : unidad sobre la que trabajar
	 * @param subUnit
	 *            : field
	 * @param link
	 *            : link que contendrá el parametro
	 * @param key
	 *            : para indicar si es un oid
	 * @return: el parametro creado
	 */
	private IMFElement createParameterField2Att(IAttribute atributo,
			IMFElement unidadDestino, ISubUnit subUnit, IMFElement link,
			boolean key) {
		IMFElement linkParameter;
		IMFElement field = (IMFElement) subUnit;

		String nombre = Utilities.getAttribute(field, "name");

		String idAtributo = Utilities.getAttribute(atributo, "id");

		linkParameter = Utilities.createLinkParameter(link.getModelId(),
				ProjectParameters.getWebProject().getIdProvider(), link
						.getFinalId());
		new SetAttributeMFOperation(linkParameter, "id", this.cleanIds(link
				.getIdsByFinalId().toString())
				+ "#" + linkParameter.getFinalId(), link.getRootElement())
				.execute();
		// Si no es de tipo key (para relacionar el oid) se hace igual que la
		// funcion anterior.
		if (!key) {
			new SetAttributeMFOperation(linkParameter, "name", nombre + "_"
					+ nombre, link.getRootElement()).execute();

			new SetAttributeMFOperation(linkParameter, "source", this
					.cleanIds(field.getIdsByFinalId().toString()), link
					.getRootElement()).execute();

			new SetAttributeMFOperation(linkParameter, "target", this
					.cleanIds(unidadDestino.getIdsByFinalId().toString())
					+ "." + idAtributo, link.getRootElement()).execute();
		} else {
			// Si se relaciona con un oid se necesita la keyCondition del
			// elemento
			// para crear el parametro del link, además de cambiar el formato
			// del nombre
			IMFElement keyCondition = unidadDestino.selectSingleElement(
					"Selector").selectSingleElement("KeyCondition");
			String nameKey = Utilities.getAttribute(keyCondition, "name");
			
			new SetAttributeMFOperation(linkParameter, "name", nombre + "_"
					+ nameKey + " [oid]", link.getRootElement()).execute();

			new SetAttributeMFOperation(linkParameter, "source", this
					.cleanIds(field.getIdsByFinalId().toString()), link
					.getRootElement()).execute();

			new SetAttributeMFOperation(linkParameter, "target", this
					.cleanIds(keyCondition.getIdsByFinalId().toString())
					+ "." + idAtributo, link.getRootElement()).execute();

		}
		return linkParameter;
	}

	/**
	 * Nombre: createParameterFieldToRole Funcion: Crea un parametro desde un
	 * campo de un formulario con una roleCondition. El procedimiento es igual a
	 * las anteriores, solo variando el nombre y algunos datos mas
	 * 
	 * @param role
	 *            : role que queremos conectar
	 * @param unidadDestino
	 *            : unidad en la que estará la roleCondition (createUnit,
	 *            modifyUnit...)
	 * @param field
	 *            : campo del formulario
	 * @param link
	 *            : link que contendrá el parametro
	 * @return: el parametro creado
	 */
	private IMFElement createParameterFieldToRole(IRelationshipRole role,
			IMFElement unidadDestino, ISubUnit field, IMFElement link) {
		IMFElement linkParameter;
		String nameRole = Utilities.getAttribute(role, "name");
		String idRole = Utilities.getAttribute(role, "id");
		IAttribute atributo = this.relaciones.get(role);
		IEntity entidadParent = (IEntity) atributo.getParentElement();
		String nombreEntidad = Utilities.getAttribute(entidadParent, "name");

		List<IAttribute> listaAt = entidadParent.getAllAttributeList();

		// Recorremos los atributos buscando el keyCondition
		for (int i = 0; i < listaAt.size(); i++) {
			if (Utilities.getAttribute(listaAt.get(i), "key").equals("true")) {
				atributo = listaAt.get(i);
				// TODO comprobar, el break estaba fuera del if y no me cuadra
				break;
			}
		}
		// Al igual que en los metodos anteriores se crean los campos necesarios
		// (id,name,source,target)
		String idAtributo = Utilities.getAttribute(atributo, "id");
		String nombreAtributo = Utilities.getAttribute(atributo, "name");
		linkParameter = Utilities.createLinkParameter(link.getModelId(),
				ProjectParameters.getWebProject().getIdProvider(), link
						.getFinalId());

		new SetAttributeMFOperation(linkParameter, "id", this.cleanIds(link
				.getIdsByFinalId().toString())
				+ "#" + linkParameter.getFinalId(), link.getRootElement())
				.execute();

		new SetAttributeMFOperation(linkParameter, "name", nameRole + "_"
				+ nombreEntidad + "." + nombreAtributo + "(" + nameRole + ")"

		, link.getRootElement()).execute();

		new SetAttributeMFOperation(linkParameter, "source", this
				.cleanIds(field.getIdsByFinalId().toString()), link
				.getRootElement()).execute();
		new SetAttributeMFOperation(linkParameter, "target", this
				.cleanIds(unidadDestino.getIdsByFinalId().toString())
				+ "." + idRole + "." + idAtributo, link.getRootElement())
				.execute();

		return linkParameter;
	}

	/**
	 * Nombre: createParameterPreload Funcion: Crea un parametro en un link para
	 * poder hacer el preload de atributos
	 * 
	 * @param atributo
	 *            : atributo que queremos relacionar
	 * @param field
	 *            : campo que queremos precargar
	 * @param atributoSeleccion
	 *            : atributo necesario para obtener el nombre del source
	 * @param link
	 *            : link que contendrá el paramtro
	 * @param entidadOrigen
	 *            : entidad de la que proceden los atributos
	 */

	private void createParameterPreload(IAttribute atributo, ISubUnit field,
			IAttribute atributoSeleccion, IMFElement link, IEntity entidadOrigen) {
		IMFElement linkParameter;
		IEntity padre = entidadOrigen;

		String id = this.cleanIds(link.getIdsByFinalId().toString());
		String source_label = this.cleanIds(atributoSeleccion.getIdsByFinalId()
				.toString())
				+ "Array";
		String source_output = this.cleanIds(atributo.getIdsByFinalId()
				.toString())
				+ "Array";
		
		String name_label = Utilities.getAttribute(atributoSeleccion, "name")
				+ "_" + Utilities.getAttribute(padre, "name") + " [label]";
		String name_output = "oid_" + Utilities.getAttribute(padre, "name")
				+ " [output]";

		linkParameter = Utilities.createLinkParameter(link.getModelId(),
				ProjectParameters.getWebProject().getIdProvider(), link
						.getFinalId());
		new SetAttributeMFOperation(linkParameter, "id", id + "#"
				+ linkParameter.getFinalId(), link.getRootElement()).execute();
		new SetAttributeMFOperation(linkParameter, "name", name_label, link
				.getRootElement()).execute();
		new SetAttributeMFOperation(linkParameter, "source", source_label, link
				.getRootElement()).execute();
		new SetAttributeMFOperation(linkParameter, "target", this
				.cleanIds(field.getIdsByFinalId().toString())
				+ "_label", link.getRootElement()).execute();
		((MFElement) link).addChild(linkParameter, null);

		linkParameter = Utilities.createLinkParameter(link.getModelId(),
				ProjectParameters.getWebProject().getIdProvider(), link
						.getFinalId());
		new SetAttributeMFOperation(linkParameter, "id", id + "#"
				+ linkParameter.getFinalId(), link.getRootElement()).execute();
		new SetAttributeMFOperation(linkParameter, "name", name_output, link
				.getRootElement()).execute();
		new SetAttributeMFOperation(linkParameter, "source", source_output,
				link.getRootElement()).execute();
		new SetAttributeMFOperation(linkParameter, "target", this
				.cleanIds(field.getIdsByFinalId().toString())
				+ "_output", link.getRootElement()).execute();
		((MFElement) link).addChild(linkParameter, null);
	}

	/**
	 * Nombre: createParameterRoleToField Funcion: Crea los parametros del link
	 * que enlaza la role con un campo del formulario
	 * 
	 * @param role
	 *            : role de la que obtener el source del parametro
	 * @param field
	 *            : campo al que queremos enlazar
	 * @param link
	 *            : link que contendrá el parametro
	 * @param multi
	 *            : para distinguir entre selectionField y multiSelectionField
	 * @return: parametro creado.
	 */
	private IMFElement createParameterRoleToField(IRelationshipRole role,
			ISubUnit field, IMFElement link, boolean multi) {
		IMFElement linkParameter;
		String nameRole = Utilities.getAttribute(role, "name");
		String idRole = Utilities.getAttribute(role, "id");
		IAttribute atributo = this.relaciones.get(role);
		IEntity entidadParent = (IEntity) atributo.getParentElement();

		List<IAttribute> listaAt = entidadParent.getAllAttributeList();

		for (int i = 0; i < listaAt.size(); i++) {
			if (Utilities.getAttribute(listaAt.get(i), "key").equals("true"))
				atributo = listaAt.get(i);
			break;
		}

		linkParameter = Utilities.createLinkParameter(link.getModelId(),
				ProjectParameters.getWebProject().getIdProvider(), link
						.getFinalId());

		new SetAttributeMFOperation(linkParameter, "id", this.cleanIds(link
				.getIdsByFinalId().toString())
				+ "#" + linkParameter.getFinalId(), link.getRootElement())
				.execute();

		if (multi) {
			new SetAttributeMFOperation(linkParameter, "name", Utilities
					.getAttribute(atributo, "name")
					+ "_" + nameRole + " - Preselection", link.getRootElement())
					.execute();

			new SetAttributeMFOperation(linkParameter, "source", this
					.cleanIds(atributo.getIdsByFinalId().toString())
					+ "Array", link.getRootElement()).execute();
		} else {
			
			new SetAttributeMFOperation(linkParameter, "name", nameRole
					+ ".oid_" + nameRole + " - Preselection", link
					.getRootElement()).execute();

			new SetAttributeMFOperation(linkParameter, "source", idRole + "_"
					+ this.cleanIds(atributo.getIdsByFinalId().toString())
					+ "Array", link.getRootElement()).execute();
		}

		new SetAttributeMFOperation(linkParameter, "target", this
				.cleanIds(field.getIdsByFinalId().toString())
				+ "_presel", link.getRootElement()).execute();

		return linkParameter;
	}

	/**
	 * Nombre: getAtributos Funcion: Genera un texto con atributos de las
	 * entidades para poder añadirlas al XML generado por webratio. Es necesario
	 * distinguir entre las entidades propias de webratio como son User, Group y
	 * Modulo y las que creemos nosotros, ya que se deben escribir de manera
	 * distinta.
	 * 
	 * @param lista
	 *            : lista de atributos con los que generaremos la cadena de
	 *            texto
	 * @return
	 */
	private String getAtributos(List<IAttribute> lista) {
		boolean entidadWR = false;
		String tipoEntidad = Utilities.getAttribute(this.entidad, "id");
		if (!entidadWR)
			if (tipoEntidad.equals("User"))
				entidadWR = true;
		if (!entidadWR)
			if (tipoEntidad.equals("Group"))
				entidadWR = true;
		if (!entidadWR)
			if (tipoEntidad.equals("Module"))
				entidadWR = true;
	
		String atributos = "";
		Iterator<IAttribute> iteradorAtributos = lista.iterator();
		IAttribute atributo;
		while (iteradorAtributos.hasNext()) {
			atributo = iteradorAtributos.next();
			if (!entidadWR) {
				// Si no es propia de webratio se general con el formato
				// ent1#att1
				atributos = atributos + this.entidad.getFinalId() + "#"
						+ atributo.getFinalId() + " ";
			} else {
				atributos = atributos + atributo.getFinalId() + " ";
			}
		}
		if (atributos.length() != 0)
			// Formateamos correctamente la cadnea eliminando el espacio en
			// blanco fianl
			atributos = atributos.substring(0, atributos.length() - 1);
		return atributos;
	}

	/**
	 * Nombre: getEntity Funcion: retorna la entidad que se esta usando para
	 * generar el CRUD
	 * 
	 * @return: la entidad que se esta usando para generar el CRUD
	 */
	protected IEntity getEntity() {
		return this.entidad;
	}

	/**
	 * Nombre: getListaSiteViews Funcion: Obtiene una lista de siteViews
	 * 
	 * @return: la lista de siteViews en las que se va a crear el CRUD
	 */
	protected List<ISiteView> getListaSiteViews() {
		return this.listaSiteViews;
	}
	
	/**
	 * Nombre: getListaSiteViews Funcion: Obtiene una lista de siteViews
	 * 
	 * @return: la lista de siteViews en las que se va a crear el CRUD
	 */
	protected List<IArea> getListaAreas() {
		return this.listaAreas;
	}

	/**
	 * Nombre: getNombreEntity Funcion: Obtiene el nombre de la entidad
	 * 
	 * @return: String con el nombre de la entidad
	 */
	protected String getNombreEntity() {
		return Utilities.getAttribute(this.entidad, "name");
	}

	/**
	 * Nombre: getNombreRole Funcion: Obtiene el nombre de la Role que tiene
	 * como target la entidad que se pasa por parametros
	 * 
	 * @param target
	 * @return
	 */
	protected IRelationshipRole getNombreRole(IMFElement target) {
		// Inicializamos las relaciones, para evitar duplicados o errores
		this.initRelationShips();
		// La variable entidadesRelacionadas es un map compuesto por Role,
		// targetEntity
		IRelationshipRole role = this.entidadesRelacionadas
				.get((IEntity) target);

		return role;

	}

	/**
	 * Nombre: getOidField Funcion: retorna el atributo que funciona como key en
	 * la entidad
	 * 
	 * @return: elemento (Atributo) que se corresponde con el oid
	 */
	protected IMFElement getOidField() {
		return this.fieldOid;
	}

	/**
	 * Nombre: getRelaciones Funcion: obtiene las relaciones y los atributos de
	 * la entidad que se usa para generar el CRUD.
	 * 
	 * @return: Mapa con las relaciones y los atributos
	 */
	protected Map<IRelationshipRole, IAttribute> getRelaciones() {
		return this.relaciones;
	}

	/**
	 * Nombre: getTargetEntity Funcion: Obtiene la entidad de destino dada una
	 * Role
	 * 
	 * @param role
	 *            : role de la que obtener la entidad destino
	 * @return: targetEntity
	 */
	protected IEntity getTargetEntity(IRelationshipRole role) {
		IRelationship relation = (IRelationship) role.getParentElement();
		if (relation.getTargetEntity() == this.entidad) {
			return relation.getSourceEntity();
		} else
			return relation.getTargetEntity();

	}

	/**
	 * Nombre: guessCouplingEntryToConnect Funcion: Simula el GuessCoupling de
	 * webRatio entre una EntryUnit y una ConnectUnit para conectar el oid de la
	 * entidad con la Role que esta en la condicion de la ConnectUnit
	 * 
	 * @param origen
	 *            : unidadOrigen (entryUnit)
	 * @param destino
	 *            : unidadDestino (connect o disconnect unit)
	 * @param entidadDestino
	 *            : Entidad de la conectUnit, no tiene por que ser la misma que
	 *            la del CRUD
	 * @param role
	 *            : role que esta en la roleCondition de la unidad destino
	 * @param link
	 *            : link que enlaza la unidad origen y destino, para añadirle el
	 *            linkParameter
	 */
	protected void guessCouplingEntryToConnect(IMFElement origen,
			IMFElement destino, IEntity entidadDestino, IRelationshipRole role,
			IMFElement link) {
		// Obtenemos la keyCondition de la unidadDestino
		IOperationUnit connectUnit = (IOperationUnit) destino;
		IMFElement keyCondition = connectUnit.selectSingleElement(
				"TargetSelector").selectSingleElement("KeyCondition");
		String name = Utilities.getAttribute(keyCondition, "name");
		String nombreBuscar = Utilities.getAttribute(entidadDestino, "name");

		IAttribute keyAtributo = null;
		IUnit entryUnit;
		entryUnit = (IUnit) origen;
		// Obtenemos la lista de campos del formularios y la lista de atributos
		// de la entidad destino
		List<ISubUnit> listaFields = entryUnit.getSubUnitList();
		List<IAttribute> listaAtributos = entidadDestino.getAllAttributeList();

		// Buscamos el atributo que funciona como key
		// TODO Otro break fuera del if!! (funcionaba porque la key suele ser la
		// primera!)
		for (int i = 0; i < listaAtributos.size(); i++) {
			if (Utilities.getAttribute(listaAtributos.get(i), "key").equals(
					"true")) {
				keyAtributo = listaAtributos.get(i);
				break;
			}
		}

		// Creamos un hashMap con el nombre del campo y el campo.
		Map<String, ISubUnit> mapaCampos = new HashMap<String, ISubUnit>();
		Iterator<ISubUnit> iteratorCampos = listaFields.iterator();
		ISubUnit field;
		while (iteratorCampos.hasNext()) {
			field = iteratorCampos.next();
			mapaCampos.put(Utilities.getAttribute(field, "name"), field);
		}

		// Obtenemos el campo que se relaciona con la role
		field = mapaCampos.get(Utilities.getAttribute(role, "name"));

		// Creamos el link parameter que añadimos al link.
		ILinkParameter linkParameter = Utilities.createLinkParameter(link
				.getModelId(), ProjectParameters.getWebProject()
				.getIdProvider(), link.getFinalId());
		new SetAttributeMFOperation(linkParameter, "id", this.cleanIds(link
				.getIdsByFinalId().toString())
				+ "#" + linkParameter.getFinalId(), link.getRootElement())
				.execute();

		new SetAttributeMFOperation(linkParameter, "source", this
				.cleanIds(field.getIdsByFinalId().toString()), link
				.getRootElement()).execute();
		
		new SetAttributeMFOperation(linkParameter, "name", nombreBuscar + "_"
				+ name + " [oid] [" + nombreBuscar + "] [Target]", link
				.getRootElement()).execute();
		new SetAttributeMFOperation(linkParameter, "target",
				this.cleanIds(keyCondition.getIdsByFinalId().toString())
						+ "."
						+ this.cleanIds(keyAtributo.getIdsByFinalId()
								.toString()), link.getRootElement()).execute();

		((MFElement) link).addChild(linkParameter, null);
	}

	/**
	 * Nombre: guessCouplingEntryToCreateModify Funcion: Simula el guessCoupling
	 * entre la entryUnit y la Create o Modify unit
	 * 
	 * @param origen
	 *            : entry unit
	 * @param destino
	 *            : unidad create o modify
	 * @param link
	 *            : link sobre el que añadir el linkParameter
	 */
	protected void guessCouplingEntryToCreateModify(IMFElement origen,
			IMFElement destino, IMFElement link) {
		// Variables
		ISubUnit field;
		IAttribute atributo;
		String nombreCampo;
		String tipoCampo;
		IMFElement linkParameter;
		IRelationshipRole role;
		String keyAtributo;
		IEntity entidadGuess = this.getEntity();
		// Obtener lista FIELDS
		List<ISubUnit> listaFields = ((IUnit) origen).getSubUnitList();
		// Obtener lista Atributos de la entidad origen
		List<IAttribute> listaAtributos = ((IEntity) entidadGuess)
				.getAllAttributeList();

		String tipoDestino = destino.getQName().getName();

		// Generar mapas para las listas de campos y atributos
		Map<String, IAttribute> mapaAtributos = new HashMap<String, IAttribute>();
		Map<String, ISubUnit> mapaCampos = new HashMap<String, ISubUnit>();

		// Iniciar los hashMap
		for (Iterator<ISubUnit> iter = listaFields.iterator(); iter.hasNext();) {
			field = iter.next();
			mapaCampos.put(Utilities.getAttribute(field, "name"), field);
		}
		for (Iterator<IAttribute> iter = listaAtributos.iterator(); iter
				.hasNext();) {
			atributo = iter.next();
			mapaAtributos.put(Utilities.getAttribute(atributo, "name"),
					atributo);
		}

		// Recorremos todos los campos
		for (Iterator<String> iter = mapaCampos.keySet().iterator(); iter
				.hasNext();) {
			nombreCampo = iter.next();
			atributo = mapaAtributos.get(nombreCampo);
			// Si nos retorna un atributo es un coupling por atributo
			if (atributo != null) {
				keyAtributo = Utilities.getAttribute(atributo, "key");

				if (!keyAtributo.equals("true")) {
					linkParameter = this.createParameterField2Att(atributo,
							destino, mapaCampos.get(nombreCampo), link, false);
					((MFElement) link).addChild(linkParameter, null);
				} else {
					if (tipoDestino.equals("ModifyUnit")) {

						linkParameter = this.createParameterField2Att(atributo,
								destino, mapaCampos.get(nombreCampo), link,
								true);
						((MFElement) link).addChild(linkParameter, null);
					}
				}
			} else {
				// En caso contrario es un coupling con selection o
				// multiselection
				role = this.buscarRelation(nombreCampo);

				field = mapaCampos.get(nombreCampo);
				tipoCampo = field.getQName().getName();
				if (tipoCampo.equals("SelectionField")) {
					linkParameter = this.createParameterFieldToRole(role,
							destino, field, link);
					((MFElement) link).addChild(linkParameter, null);
				}
			}
		}
	}

	
	/** Parameter de Link desde origen a destino para una keycondition en la cual en el mapeo el origen va a ser un valor fijo.
	 * 
	 */
	protected void guessCouplingFieldAttConValor(IEntity entidadOrigen,IMFElement destino,IMFElement link, String valor){
		
		//Uso para keyCondition
		
		//<Link id="ln3" name="New Town" to="seu1" type="normal" validate="true">
		//  <LinkParameter id="par21" name="0_KeyCondition3 [oid]" sourceValue="0" target="kcond3.att2"/>
		//</Link>

		// Variables
		//ISubUnit field;
		IAttribute atributo;
		String nombreCampo;
		//String tipoCampo;
		IMFElement linkParameter;
		String keyAtributo;
		
		try{
			// Obtener lista Atributos de la entidad origen
			List<IAttribute> listaAtributos = entidadOrigen.getAllAttributeList();
		
			// Generar mapas para las listas de campos y atributos
			Map<String, IAttribute> mapaAtributos = new HashMap<String, IAttribute>();
	
			// Iniciar los hashMa
			for (Iterator<IAttribute> iter = listaAtributos.iterator(); iter
					.hasNext();) {
				atributo = iter.next();
				
				keyAtributo = Utilities.getAttribute(atributo, "key");
				if (keyAtributo.equals("true")) {
					mapaAtributos.put(Utilities.getAttribute(atributo, "name"),atributo);
				}
			}
	
			if(null!=mapaAtributos && mapaAtributos.size()>0){
				// Recorremos todos los campos
				for (Iterator<String> iter = mapaAtributos.keySet().iterator(); iter
						.hasNext();) {
					nombreCampo = iter.next();
					atributo = mapaAtributos.get(nombreCampo);
					// Si nos retorna un atributo es un coupling por atributo
					if (atributo != null) {
						keyAtributo = Utilities.getAttribute(atributo, "key");
						if (keyAtributo.equals("true")) {
								linkParameter = this.createParameterField2AttValor(atributo,destino, valor , link,true);
								((MFElement) link).addChild(linkParameter, null);
						} 
					}
				}
			}
		}catch (Exception e){
			//captura excepcion ya que esto solo es para a�adir un linkParameter a un parameter
		}
	}
	
	
	/**
	 * 
	 * Nombre: createParameterField2AttCarlos
	 * Funcion: 
	 * @param atributo
	 * @param unidadDestino
	 * @param valor
	 * @param link
	 * @param key
	 * @return
	 */
	private IMFElement createParameterField2AttValor(IAttribute atributo,
			IMFElement unidadDestino, String valor, IMFElement link,
			boolean key) {
		
		IMFElement linkParameter;
		String idAtributo = Utilities.getAttribute(atributo, "id");

		linkParameter = Utilities.createLinkParameter(link.getModelId(),
				ProjectParameters.getWebProject().getIdProvider(), link
						.getFinalId());
		new SetAttributeMFOperation(linkParameter, "id", this.cleanIds(link
				.getIdsByFinalId().toString())
				+ "#" + linkParameter.getFinalId(), link.getRootElement())
				.execute();
		// Si no es de tipo key (para relacionar el oid) se hace igual que la
		// funcion anterior.
		if (key) {
			// Si se relaciona con un oid se necesita la keyCondition del
			// elemento
			// para crear el parametro del link, además de cambiar el formato
			// del nombre
			IMFElement keyCondition = unidadDestino.selectSingleElement(
					"Selector").selectSingleElement("KeyCondition");
			String nameKey = Utilities.getAttribute(keyCondition, "name");
			
			new SetAttributeMFOperation(linkParameter, "name", valor + "_"
					+ nameKey + " [oid]", link.getRootElement()).execute();

			new SetAttributeMFOperation(linkParameter, "sourceValue", valor, link
			.getRootElement()).execute();
			
			new SetAttributeMFOperation(linkParameter, "target", this
					.cleanIds(keyCondition.getIdsByFinalId().toString())
					+ "." + idAtributo, link.getRootElement()).execute();

		}
		return linkParameter;
	}

	
	/**
	 * Nombre: guessCouplingUnitToEntry Funcion: Simula un guessCoupling entre
	 * cualquier unidad y una entryUnit llamando a los metodos creados
	 * anteriormente
	 * 
	 * @param origen
	 *            : unidad (SelectorUnit, contentUnit...)
	 * @param entidadOrigen
	 *            : entidad que esta seleccionada en la unidad de origen
	 * @param destino
	 *            : entryUnit
	 * @param link
	 *            : link sobre el que se creara el linkParameter
	 */
	protected void guessCouplingUnitToEntry(IMFElement origen,
			IEntity entidadOrigen, IMFElement destino, IMFElement link) {
		// Variables
		ISubUnit field;
		IAttribute atributo;
		String nombreCampo;
		String tipoCampo;
		// String nombreAtributo;
		IMFElement linkParameter;
		IRelationshipRole role;

		// Obtener lista FIELDS
		List<ISubUnit> listaFields = ((IUnit) destino).getSubUnitList();
		// Obtener lista Atributos de la entidad origen
		List<IAttribute> listaAtributos = entidadOrigen.getAllAttributeList();

		// Generar mapas para las listas de campos y atributos
		Map<String, IAttribute> mapaAtributos = new HashMap<String, IAttribute>();
		Map<String, ISubUnit> mapaCampos = new HashMap<String, ISubUnit>();

		// Iniciar los hashMap
		for (Iterator<ISubUnit> iter = listaFields.iterator(); iter.hasNext();) {
			field = iter.next();
			mapaCampos.put(Utilities.getAttribute(field, "name"), field);
		}
		for (Iterator<IAttribute> iter = listaAtributos.iterator(); iter
				.hasNext();) {
			atributo = iter.next();
			mapaAtributos.put(Utilities.getAttribute(atributo, "name"),
					atributo);
		}

		// Recorremos todos los campos
		for (Iterator<String> iter = mapaCampos.keySet().iterator(); iter
				.hasNext();) {
			nombreCampo = iter.next();
			atributo = mapaAtributos.get(nombreCampo);
			// Si nos retorna un atributo es un coupling por atributo
			if (atributo != null) {
				linkParameter = this.createParameter(atributo, mapaCampos
						.get(nombreCampo), link);
				((MFElement) link).addChild(linkParameter, null);
			} else {
				// En caso contrario es un coupling con selection o
				// multiselection
				role = this.buscarRelation(nombreCampo);

				field = mapaCampos.get(nombreCampo);
				tipoCampo = field.getQName().getName();
				if (tipoCampo.equals("SelectionField")) {
					// System.out.println("coupling de selection, N a 1 para"+nombreCampo);
					linkParameter = this.createParameterRoleToField(role,
							field, link, false);
					((MFElement) link).addChild(linkParameter, null);
				}
			}
		}
	}

	/**
	 * Nombre: guessCouplingUnitToEntry Funcion: Simula un guess Coupling entre
	 * cualquier unidad y la entryUnit
	 * 
	 * @param origen
	 *            : unidad origen
	 * @param entidadOrigen
	 *            : entidad que esta seleccionada en la unidad origen
	 * @param destino
	 *            : unidad destino, en este caso entryUnit
	 * @param link
	 *            : link sobre el que se creara el linkPArameter
	 * @param role
	 *            : role que de la unidad origen, en caso de tenerla
	 */
	protected void guessCouplingUnitToEntry(IMFElement origen,
			IEntity entidadOrigen, IMFElement destino, IMFElement link,
			IRelationshipRole role) {// , boolean preload){
		ISubUnit field;
		ISubUnit preselect = null;
		String nombreCampo;
		IMFElement linkParameter;
		String nombreRole = Utilities.getAttribute(role, "name");
		// Obtener lista FIELDS
		List<ISubUnit> listaFields = ((IUnit) destino).getSubUnitList();
	
		// Iniciar los hashMap
		for (Iterator<ISubUnit> iter = listaFields.iterator(); iter.hasNext();) {
			field = iter.next();
			nombreCampo = Utilities.getAttribute(field, "name");
			if (nombreCampo.contains(nombreRole)) {
				preselect = field;
				break;
			}
		}
		linkParameter = this.createParameterRoleToField(role, preselect, link,
				true);
		((MFElement) link).addChild(linkParameter, null);
	}

	/**
	 * Nombre: initRelationShips Funcion: Inicia las relaciones, obtiene todas
	 * las relaciones en las que participa la entidad seleccionada para generar
	 * el crud, guardando la role involucrada y la otra entidad de la role.
	 */
	private void initRelationShips() {
		// Guardamos todas las relaciones en una lista
		List<IRelationship> lista = this.entidad.getOutgoingRelationshipList();
		lista.addAll(this.entidad.getIncomingRelationshipList());
		Iterator<IRelationship> iteratorRelacion = lista.iterator();
		IRelationship relacion;
		IRelationshipRole role;
		this.entidadesRelacionadas = new HashMap<IEntity, IRelationshipRole>();
		// Recorremos las relaciones
		while (iteratorRelacion.hasNext()) {
			relacion = iteratorRelacion.next();
			// Si la entidad del crud es la source guardamos la target y la role
			if (relacion.getSourceEntity() == this.entidad) {
				role = relacion.getRelationshipRole1();
				this.entidadesRelacionadas
						.put(relacion.getTargetEntity(), role);
			} else {
				// En caso contrario guardamos la source
				role = relacion.getRelationshipRole2();
				this.entidadesRelacionadas
						.put(relacion.getSourceEntity(), role);
			}
		}
	}

	/**
	 * Nombre: isNtoN Funcion: Comprueba si la relacion es NaN
	 * 
	 * @param role
	 *            : relacion en la que comprobar la cardinalidad
	 * @return: la relacion en caso se existir o null en caso contrario
	 */
	protected IRelationship isNtoN(IRelationshipRole role) {
		IEntity entidad1 = this.entidad;
		List<IRelationship> lista = entidad1.getOutgoingRelationshipList();
		lista.addAll(entidad1.getIncomingRelationshipList());
		IRelationship relacion = (IRelationship) role.getParentElement();
		IRelationshipRole role1;
		IRelationshipRole role2;
		String maxCard1;
		String maxCard2;
		role1 = relacion.getRelationshipRole1();
		role2 = relacion.getRelationshipRole2();
		maxCard1 = Utilities.getAttribute(role1, "maxCard");
		maxCard2 = Utilities.getAttribute(role2, "maxCard");
		// Si ambos cardinales son N se retorna la relación
		if ((maxCard1.equals("N")) && (maxCard2.equals("N")))
			return relacion;
		return null;

	}

	/**
	 * Nombre: putCoupling Funcion: Pone un coupling entre dos unidades,
	 * especialmente se usa para las unidades isNotNullUnit
	 * 
	 * @param oidField
	 *            : campo del formulario/entidad que se usa para el copling
	 * @param destino
	 *            : unidad de destino (isNotNullUnit)
	 * @param tipo
	 *            : tipo de coupling (por ejemplo isnotnull)
	 * @param link
	 *            : link en el que se crea el coupling
	 */
	protected void putCoupling(IMFElement oidField, IUnit destino, String tipo,
			IMFElement link) {
		// Se elimina el automaticCoupling del link, para hacerlo manual
		Utilities.setAttribute(link, "automaticCoupling", null);
		String nombre = Utilities.getAttribute(oidField, "name").toLowerCase();

		ILinkParameter linkParameter = Utilities.createLinkParameter(link
				.getModelId(), ProjectParameters.getWebProject()
				.getIdProvider(), link.getFinalId());
		new SetAttributeMFOperation(linkParameter, "id", this.cleanIds(link
				.getIdsByFinalId().toString())
				+ "#" + linkParameter.getFinalId(), link.getRootElement())
				.execute();
		new SetAttributeMFOperation(linkParameter, "name", nombre + "_"
				+ nombre, link.getRootElement()).execute();
		
		new SetAttributeMFOperation(linkParameter, "source", this
				.cleanIds(oidField.getIdsByFinalId().toString()), link
				.getRootElement()).execute();
		new SetAttributeMFOperation(linkParameter, "target", this
				.cleanIds(destino.getIdsByFinalId().toString())
				+ "." + tipo, link.getRootElement()).execute();

		((MFElement) link).addChild(linkParameter, null);
	}

	/**
	 * Nombre: putPreload Funcion: Clase que se encarga de poner los datos
	 * preload dada una RelationShipRole
	 * 
	 * @param destino
	 *            : unidad destino, entryUnit
	 * @param role
	 *            : role que
	 * @param link
	 */
	protected void putPreload(IMFElement destino, IRelationshipRole role,
			IMFElement link) {
		IEntity entidadOrigen = this.getTargetEntity(role);
		IAttribute atributoSeleccion = this.relaciones.get(role);
		ISubUnit field;
		String nombreCampo;
		List<ISubUnit> listaFields = ((IUnit) destino).getSubUnitList();
		String nombreRole = Utilities.getAttribute(role, "name");
		IEntity entidadPreload = this.getTargetEntity(role);
		List<IAttribute> listaAt = entidadPreload.getAllAttributeList();
		IAttribute atributo = null;
		for (int i = 0; i < listaAt.size(); i++) {
			if (Utilities.getAttribute(listaAt.get(i), "key").equals("true")) {
				atributo = listaAt.get(i);
				break;
			}
		}

		for (Iterator<ISubUnit> iter = listaFields.iterator(); iter.hasNext();) {
			field = iter.next();
			nombreCampo = Utilities.getAttribute(field, "name");
			if (nombreCampo.equals(nombreRole)) {
				this.createParameterPreload(atributo, field, atributoSeleccion,
						link, entidadOrigen);
				break;
			}
		}
	}

	/**
	 * Nombre: setAutomaticCoupling Funcion: Elimina el automaticCoupling de un
	 * link
	 * 
	 * @param link
	 *            : link al que se le elimina el atributo
	 */
	protected void setAutomaticCoupling(IMFElement link) {
		Utilities.setAttribute(link, "automaticCoupling", null);
	}

	/**
	 * Nombre: setFields Funcion: Añade campos a un formluario, los campos son
	 * los que estan en la variable de listaAtributosDataOrForm
	 * 
	 * @param element
	 *            : entryUnit en la que se añadiran los campos
	 * @param precarga
	 *            : si es true los campos estaran marcados como preload
	 */
	protected void setFields(IMFElement element, boolean precarga,boolean noDerivado) {
		CompositeMFCommand cmd = new CompositeMFCommand(element
				.getRootElement());
		List<IAttribute> listaAtributos = this.listaAtributosDataOrForm;
		IAttribute atributo;
		IAttribute atributoKey;
		boolean key = false;
		// Recorremos todos los atributos para ver si en la lista existe
		// algun atributo de tipo key, ya que se creara de forma oculta
		// para el usuario
		for (Iterator<IAttribute> iter = listaAtributos.iterator(); iter
				.hasNext();) {
			atributo = iter.next();
			if (Utilities.getAttribute(atributo, "key").equals("true")
					&& (Utilities.getAttribute(atributo, "name")
							.contains("oid")||Utilities.getAttribute(atributo, "name")
							.contains("OID")))
				key = true; // Entre los seleccionados existe un key
		}
		// Si no existe key recorremos toda la lista de atributos para
		// encontrarlo
		// ya que en el formulario deberá aparecer como oculto
		if (!key) {
			atributoKey = null;
			List<IAttribute> listaAtributosKey = this.entidad
					.getAllAttributeList();
			for (Iterator<IAttribute> iter = listaAtributosKey.iterator(); iter
					.hasNext();) {
				atributo = iter.next();
				if (Utilities.getAttribute(atributo, "key").equals("true")
						&& (Utilities.getAttribute(atributo, "name")
								.contains("oid")||Utilities.getAttribute(atributo, "name")
								.contains("OID"))) {
					atributoKey = atributo; // Es el atributo oid
					//TODO DUDA CAGUADOF OID problamemente no haya que pararse en el primero y continuar guardando en listaAtributos tantos como haya encontrado
					break;
				}
			}
			if (atributoKey != null) {
				// Lo añadimos a la lista de atributos
				listaAtributos.add(atributoKey);
			}
		}
	
		Iterator<IAttribute> iteratorAtributos = listaAtributos.iterator();
	
		String nombre, tipo;
		IMFElement field;
		// Recorremos la lista final de atributos para crear los campos en el
		// formulario
		while (iteratorAtributos.hasNext()) {
			atributo = iteratorAtributos.next();
			
			//Si no se quieren atributos derivados y el atributo es derivado
			if(noDerivado && null!=Utilities.getAttribute(atributo, "derivationQuery") && !Utilities.getAttribute(atributo, "derivationQuery").isEmpty()){
				//10.11 no atributos derivados
				//Es un atributo derivado y por el parametro del metodo no quiere derivado
			}else{
				nombre = Utilities.getAttribute(atributo, "name");
				tipo = Utilities.getAttribute(atributo, "type");
				field = cmd.addSubUnit(Utilities.getSubUnitType(element, "Field"),
						element);
				// Si el tipo es password lo pasamos a tipo string por comodidad a
				// la hora de modificar el formulario
				if (tipo.equals("password"))
					tipo = "string";
				Utilities.setAttribute(field, "type", tipo);
				if (precarga)
					Utilities.setAttribute(field, "preloaded", "true");
				new SetAttributeMFOperation(field, "name", nombre, element
						.getRootElement()).execute();
		
				if ((Utilities.getAttribute(atributo, "name").contains("oid")
						||Utilities.getAttribute(atributo, "name").contains("OID"))
						&& Utilities.getAttribute(atributo, "key").equals("true")) {
					Utilities.setAttribute(field, "hidden", "true");
					Utilities.setAttribute(field, "modifiable", "false");
					 
					this.fieldOid = field;
		
				}
				
				// Mantener el contenType de los campos en el formulario.
				if(null!=Utilities.getAttribute(atributo,"contentType")){
					Utilities.setAttribute(field,"contentType",Utilities.getAttribute(atributo,"contentType"));
				}
			}
			
			
		}
	
		// Ahora hacemos lo mismo creando selectionField en caso de relaciones
		// 1aN y multiSelectionField en caso de relaciones NaN
		if (this.relaciones != null) {
			Set<IRelationshipRole> entidades = this.relaciones.keySet();
			Iterator<IRelationshipRole> iteratorEntidades = entidades
					.iterator();
			IRelationshipRole role;
			IRelationship relation;
			String maxCard1;
			String maxCard2;
			while (iteratorEntidades.hasNext()) {
				role = iteratorEntidades.next();
				relation = (IRelationship) role.getParentElement();
				atributo = this.relaciones.get(role);
				nombre = Utilities.getAttribute(role, "name");
				tipo = Utilities.getAttribute(atributo, "type");
	
				maxCard1 = Utilities.getAttribute(relation
						.getRelationshipRole1(), "maxCard");
				maxCard2 = Utilities.getAttribute(relation
						.getRelationshipRole2(), "maxCard");
				
				if (maxCard1.equals("N") && maxCard2.equals("N")) {
					field = cmd.addSubUnit(Utilities.getSubUnitType(element,
							"MultiSelectionField"), element);
				} else {
					field = cmd.addSubUnit(Utilities.getSubUnitType(element,
							"SelectionField"), element);
				}
	
				Utilities.setAttribute(field, "type", tipo);
	
				new SetAttributeMFOperation(field, "name", nombre, element
						.getRootElement()).execute();
			}
		}
	
	}

	/**
	 * Nombre: putMessageOnMultiMessageUnit 
	 * Funcion: añade un mensaje a un link OK o linkKO que van dirigidos
	 * a una multiMessageUnit 
	 * @param link: link al que añadir el mensaje
	 * @param destino: multiMessageUnit que mostrará el mensaje
	 * @param mensaje: mensaje a mostrar
	 */
	protected void putMessageOnMultiMessageUnit(IMFElement link,
			IMFElement destino, String mensaje) {
		ILinkParameter linkParameter = Utilities.createLinkParameter(link
				.getModelId(), ProjectParameters.getWebProject()
				.getIdProvider(), link.getFinalId());

		new SetAttributeMFOperation(linkParameter, "id", this.cleanIds(link
				.getIdsByFinalId().toString())
				+ "#" + linkParameter.getFinalId(), link.getRootElement())
				.execute();

		new SetAttributeMFOperation(linkParameter, "name", mensaje + "_"
				+ "Shown Messages", link.getRootElement()).execute();

		new SetAttributeMFOperation(linkParameter, "sourceValue", mensaje, link
				.getRootElement()).execute();

		new SetAttributeMFOperation(linkParameter, "target", this
				.cleanIds(destino.getIdsByFinalId().toString())
				+ ".shownMessages", link.getRootElement()).execute();
		((MFElement) link).addChild(linkParameter, null);
	}
}
