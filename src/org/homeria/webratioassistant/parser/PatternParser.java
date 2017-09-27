/**
 * WebRatio Assistant v3.0
 * 
 * University of Extremadura (Spain) www.unex.es
 * 
 * Developers:
 * 	- Carlos Aguado Fuentes (v2)
 * 	- Javier Sierra Blázquez (v3.0)
 * */
package org.homeria.webratioassistant.parser;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.homeria.webratioassistant.elements.ConnectUnit;
import org.homeria.webratioassistant.elements.CreateUnit;
import org.homeria.webratioassistant.elements.DataFlow;
import org.homeria.webratioassistant.elements.DataUnit;
import org.homeria.webratioassistant.elements.DeleteUnit;
import org.homeria.webratioassistant.elements.DisconnectUnit;
import org.homeria.webratioassistant.elements.ElementTypes;
import org.homeria.webratioassistant.elements.EntryUnit;
import org.homeria.webratioassistant.elements.IsNotNullUnit;
import org.homeria.webratioassistant.elements.KOLink;
import org.homeria.webratioassistant.elements.Link;
import org.homeria.webratioassistant.elements.MultiMessageUnit;
import org.homeria.webratioassistant.elements.NoOpContentUnit;
import org.homeria.webratioassistant.elements.NormalNavigationFlow;
import org.homeria.webratioassistant.elements.OKLink;
import org.homeria.webratioassistant.elements.Page;
import org.homeria.webratioassistant.elements.PowerIndexUnit;
import org.homeria.webratioassistant.elements.ReconnectUnit;
import org.homeria.webratioassistant.elements.SelectorUnit;
import org.homeria.webratioassistant.elements.Unit;
import org.homeria.webratioassistant.elements.UpdateUnit;
import org.homeria.webratioassistant.elements.WebRatioElement;
import org.homeria.webratioassistant.elements.XOR;
import org.homeria.webratioassistant.exceptions.ExceptionHandler;
import org.homeria.webratioassistant.exceptions.IdNotUniqueException;
import org.homeria.webratioassistant.registry.Registry;
import org.homeria.webratioassistant.webratio.Utilities;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.webratio.ide.model.IEntity;
import com.webratio.ide.model.IRelationship;
import com.webratio.ide.model.IRelationshipRole;

/** Class responsible for reading and analyzing pattern, serializing elements into more manageable objects. */
public class PatternParser {
	private static int UNIT_GAP = 120;

	private static final String PAGES = "PAGES";
	private static final String OUTSIDEUNITS = "OUTSIDEUNITS";
	private static final String LINKS = "LINKS";
	private static final String RELATIONS = "RELATIONS";
	private static final String NMRELATIONS = "NMRELATIONS";
	private static final String ALL = "ALL";
	private static final String FIRST = "FIRST";
	private static final String REMAINING = "REMAINING";
	private static final String LAST = "LAST";

	private static final String ID = "id";
	private static final String NAME = "name";
	private static final String TYPE = "type";
	private static final String PARENTID = "parentId";
	private static final String X = "x";
	private static final String Y = "y";
	private static final String SOURCEID = "sourceId";
	private static final String TARGETID = "targetId";
	private static final String VALIDATE = "validate";
	private static final String DEFAULT = "default";
	private static final String LANDMARK = "landmark";
	private static final String MESSAGE = "message";

	private static final String MARKER_RELATION = "#";
	private static final String MARKER_NMRELATION = "%";

	private static final String JAXP_SCHEMA_LANGUAGE = "http://java.sun.com/xml/jaxp/properties/schemaLanguage";
	private static final String W3C_XML_SCHEMA = "http://www.w3.org/2001/XMLSchema";

	private Document doc;
	private File fXmlFile;
	private DocumentBuilder dBuilder;

	private IEntity entity;

	private Queue<WebRatioElement> pages;
	private List<Unit> units;
	private List<Link> links;

	/**
	 * Constructs a new instance and prepares for parsing
	 * 
	 * @param path
	 *            : absolute path to the pattern file
	 * @param entity
	 *            : entity selected by the user
	 * @throws SAXException
	 * @throws IOException
	 * @throws ParserConfigurationException
	 */
	public PatternParser(String path, IEntity entity) throws SAXException, IOException, ParserConfigurationException {
		this.pages = new LinkedList<WebRatioElement>();
		this.units = new ArrayList<Unit>();
		this.links = new ArrayList<Link>();
		this.entity = entity;

		this.fXmlFile = new File(path);
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		dbFactory.setNamespaceAware(true);
		dbFactory.setValidating(true);
		dbFactory.setAttribute(JAXP_SCHEMA_LANGUAGE, W3C_XML_SCHEMA);
		this.dBuilder = dbFactory.newDocumentBuilder();
		this.dBuilder.setErrorHandler(new ExceptionHandler());
		this.generateDoc();

		String id = this.doc.getDocumentElement().getAttribute(ID);
		String name = this.doc.getDocumentElement().getAttribute(NAME);
		Registry.getInstance().setPatternData(id, name);

	}

	/**
	 * Checks if all patterns have different id (in the root node). If some id are not unique IdNotUniqueException is raised.
	 * 
	 * @param files
	 *            : array with the patterns for checking
	 * @throws IdNotUniqueException
	 * @throws SAXException
	 * @throws IOException
	 * @throws ParserConfigurationException
	 */
	public static void checkPatternsIdAreUnique(File[] files) throws IdNotUniqueException, SAXException, IOException,
			ParserConfigurationException {
		List<String> patternsIdsList = new ArrayList<String>();
		Document doc;
		DocumentBuilder dBuilder;
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		dBuilder = dbFactory.newDocumentBuilder();

		for (int i = 0; i < files.length; i++) {
			if (files[i].isFile() && files[i].getName().contains(".xml")) {

				doc = dBuilder.parse(files[i]);
				String patternId = doc.getDocumentElement().getAttribute(ID);

				if (patternsIdsList.contains(patternId))
					throw new IdNotUniqueException(patternId, "Root element of " + files[i].getAbsolutePath());
				else
					patternsIdsList.add(patternId);

			}
		}
	}

	public Queue<WebRatioElement> getPages() {
		return this.pages;
	}

	public void setPages(Queue<WebRatioElement> pages) {
		this.pages = pages;
	}

	public List<Unit> getUnits() {
		return this.units;
	}

	public void setUnits(List<Unit> units) {
		this.units = units;
	}

	public List<Link> getLinks() {
		return this.links;
	}

	public void setLinks(List<Link> links) {
		this.links = links;
	}

	private void generateDoc() throws SAXException, IOException {
		this.doc = this.dBuilder.parse(this.fXmlFile);
	}

	/**
	 * Parse the PAGES section. Call getPages() and getUnits() to get the element parsed.
	 */
	public void parsePagesSection() {
		NodeList pagesSection = this.doc.getElementsByTagName(PAGES);

		if (pagesSection.getLength() > 0) {
			NodeList pagesNodeList;
			pagesNodeList = pagesSection.item(0).getChildNodes();

			for (int iPage = 0; iPage < pagesNodeList.getLength(); iPage++) {
				Node nodePage = pagesNodeList.item(iPage);
				if (nodePage instanceof Element) {

					Element page = (Element) nodePage;
					// null because is the first page. Its parent is the siteView selected in the UI
					this.parsePage(page, null);
				}
			}
		}
	}

	/**
	 * Auxiliary method for parsePagesSection().
	 * 
	 * @param page
	 *            : XML Element that represents the page or xor page to parse.
	 * @param parentId
	 *            : the parent id of the page to parse
	 */
	private void parsePage(Element page, String parentId) {

		// Create new Page or Alternative (XOR)
		if (page.getTagName().equalsIgnoreCase(ElementTypes.PAGE))
			this.pages.add(new Page(page.getAttribute(ID), page.getAttribute(NAME), parentId, page.getAttribute(DEFAULT), page
					.getAttribute(LANDMARK), page.getAttribute(X), page.getAttribute(Y)));

		else if (page.getTagName().equalsIgnoreCase(ElementTypes.XOR_PAGE))
			this.pages.add(new XOR(page.getAttribute(ID), page.getAttribute(NAME), parentId, page.getAttribute(X), page.getAttribute(Y)));

		// Process the elements within each page:
		NodeList pageChild = page.getChildNodes();
		for (int iUnit = 0; iUnit < pageChild.getLength(); iUnit++) {
			Node nodeElement = pageChild.item(iUnit);
			if (nodeElement instanceof Element) {

				Element element = (Element) nodeElement;
				if (element.getTagName().equalsIgnoreCase(ElementTypes.PAGE)
						|| element.getTagName().equalsIgnoreCase(ElementTypes.XOR_PAGE))
					this.parsePage(element, page.getAttribute(ID));
				else {
					element.setAttribute(PARENTID, page.getAttribute(ID));
					// Creo la unit
					this.createContentUnit(element);
				}
			}
		}
	}

	/**
	 * Creates the ContentUnits
	 * 
	 * @param xmlUnit
	 *            : XML Element that represents the content unit to parse
	 */
	private void createContentUnit(Element xmlUnit) {
		String nodeName = xmlUnit.getNodeName();

		if (nodeName.equalsIgnoreCase(ElementTypes.POWER_INDEX_UNIT)) {
			this.units.add(new PowerIndexUnit(xmlUnit.getAttribute(ID), xmlUnit.getAttribute(NAME), xmlUnit.getAttribute(PARENTID), xmlUnit
					.getAttribute(X), xmlUnit.getAttribute(Y), this.entity));

		} else if (nodeName.equalsIgnoreCase(ElementTypes.DATA_UNIT)) {
			this.units.add(new DataUnit(xmlUnit.getAttribute(ID), xmlUnit.getAttribute(NAME), xmlUnit.getAttribute(PARENTID), xmlUnit
					.getAttribute(X), xmlUnit.getAttribute(Y), this.entity));

		} else if (nodeName.equalsIgnoreCase(ElementTypes.MULTI_MESSAGE_UNIT)) {
			this.units.add(new MultiMessageUnit(xmlUnit.getAttribute(ID), xmlUnit.getAttribute(NAME), xmlUnit.getAttribute(PARENTID),
					xmlUnit.getAttribute(X), xmlUnit.getAttribute(Y)));

		} else if (nodeName.equalsIgnoreCase(ElementTypes.ENTRY_UNIT)) {
			this.units.add(new EntryUnit(xmlUnit.getAttribute(ID), xmlUnit.getAttribute(NAME), xmlUnit.getAttribute(PARENTID), xmlUnit
					.getAttribute(TYPE), xmlUnit.getAttribute(X), xmlUnit.getAttribute(Y), this.entity));

		} else if (nodeName.equalsIgnoreCase(ElementTypes.SELECTOR_UNIT)) {
			this.units.add(new SelectorUnit(xmlUnit.getAttribute(ID), xmlUnit.getAttribute(NAME), xmlUnit.getAttribute(PARENTID), xmlUnit
					.getAttribute(TYPE), xmlUnit.getAttribute(X), xmlUnit.getAttribute(Y), this.entity));

		} else if (nodeName.equalsIgnoreCase(ElementTypes.NO_OP_CONTENT_UNIT)) {
			this.units.add(new NoOpContentUnit(xmlUnit.getAttribute(ID), xmlUnit.getAttribute(NAME), xmlUnit.getAttribute(PARENTID),
					xmlUnit.getAttribute(X), xmlUnit.getAttribute(Y), this.entity));

		}
	}

	/**
	 * Parse the OUTSIDEUNITS section. Call getUnits() to get the element parsed.
	 */
	public void parseOutsideUnitsSection() {
		NodeList outUnitSection = this.doc.getElementsByTagName(OUTSIDEUNITS);

		if (outUnitSection.getLength() > 0) {
			NodeList outUnitChilds = outUnitSection.item(0).getChildNodes();

			for (int i = 0; i < outUnitChilds.getLength(); i++) {
				Node node = outUnitChilds.item(i);
				if (node instanceof Element) {
					Element outUnit = (Element) node;

					this.createOutsideUnit(outUnit);
				}
			}
		}
	}

	/**
	 * Creates the OutsideUnit (units that its parents are the SiteView or Area)
	 * 
	 * @param xmlUnit
	 *            : XML Element that represents the outsideunit to parse
	 */
	private void createOutsideUnit(Element xmlUnit) {
		String nodeName = xmlUnit.getNodeName();

		if (nodeName.equalsIgnoreCase(ElementTypes.SELECTOR_UNIT)) {
			this.units.add(new SelectorUnit(xmlUnit.getAttribute(ID), xmlUnit.getAttribute(NAME), xmlUnit.getAttribute(PARENTID), xmlUnit
					.getAttribute(TYPE), xmlUnit.getAttribute(X), xmlUnit.getAttribute(Y), this.entity));

		} else if (nodeName.equalsIgnoreCase(ElementTypes.IS_NOT_NULL_UNIT)) {
			this.units.add(new IsNotNullUnit(xmlUnit.getAttribute(ID), xmlUnit.getAttribute(NAME), xmlUnit.getAttribute(X), xmlUnit
					.getAttribute(Y), null));

		} else if (nodeName.equalsIgnoreCase(ElementTypes.CREATE_UNIT)) {
			this.units.add(new CreateUnit(xmlUnit.getAttribute(ID), xmlUnit.getAttribute(NAME), xmlUnit.getAttribute(X), xmlUnit
					.getAttribute(Y), this.entity));

		} else if (nodeName.equalsIgnoreCase(ElementTypes.DELETE_UNIT)) {
			this.units.add(new DeleteUnit(xmlUnit.getAttribute(ID), xmlUnit.getAttribute(NAME), xmlUnit.getAttribute(X), xmlUnit
					.getAttribute(Y), this.entity));

		} else if (nodeName.equalsIgnoreCase(ElementTypes.UPDATE_UNIT)) {
			this.units.add(new UpdateUnit(xmlUnit.getAttribute(ID), xmlUnit.getAttribute(NAME), xmlUnit.getAttribute(X), xmlUnit
					.getAttribute(Y), this.entity));

		}
	}

	/**
	 * Parse the LINKS section. Call getLinks() to get the element parsed.
	 */
	public void parseLinksSection() {
		NodeList linksSection = this.doc.getElementsByTagName(LINKS);

		if (linksSection.getLength() > 0) {
			NodeList linkChilds = linksSection.item(0).getChildNodes();

			for (int i = 0; i < linkChilds.getLength(); i++) {
				Node node = linkChilds.item(i);
				if (node instanceof Element) {
					Element link = (Element) node;

					this.createLink(link);
				}
			}
		}
	}

	/**
	 * Creates the links
	 * 
	 * @param xmlUnit
	 *            : XML Element that represents the outsideunit to parse
	 */
	private void createLink(Element xmlUnit) {
		String nodeName = xmlUnit.getNodeName();

		if (nodeName.equalsIgnoreCase(ElementTypes.NORMAL_NAVIGATION_FLOW)) {
			this.links.add(new NormalNavigationFlow(xmlUnit.getAttribute(ID), xmlUnit.getAttribute(NAME), xmlUnit.getAttribute(SOURCEID),
					xmlUnit.getAttribute(TARGETID), xmlUnit.getAttribute(TYPE), xmlUnit.getAttribute(VALIDATE), this.entity));

		} else if (nodeName.equalsIgnoreCase(ElementTypes.DATA_FLOW)) {
			this.links.add(new DataFlow(xmlUnit.getAttribute(ID), xmlUnit.getAttribute(NAME), xmlUnit.getAttribute(SOURCEID), xmlUnit
					.getAttribute(TARGETID), xmlUnit.getAttribute(TYPE), this.entity));

		} else if (nodeName.equalsIgnoreCase(ElementTypes.OK_LINK)) {
			this.links.add(new OKLink(xmlUnit.getAttribute(ID), xmlUnit.getAttribute(NAME), xmlUnit.getAttribute(SOURCEID), xmlUnit
					.getAttribute(TARGETID), xmlUnit.getAttribute(TYPE), xmlUnit.getAttribute(MESSAGE)));

		} else if (nodeName.equalsIgnoreCase(ElementTypes.KO_LINK)) {
			this.links.add(new KOLink(xmlUnit.getAttribute(ID), xmlUnit.getAttribute(NAME), xmlUnit.getAttribute(SOURCEID), xmlUnit
					.getAttribute(TARGETID), xmlUnit.getAttribute(TYPE), xmlUnit.getAttribute(MESSAGE)));

		}

	}

	/**
	 * Parse the RELATIONS section. Call getUnits() and getLinks() to get the element parsed.
	 * 
	 * @param relationshipRolesSelected
	 *            : the relationship roles selected by the user
	 * @throws SAXException
	 * @throws IOException
	 */
	public void parseRelations(Set<IRelationshipRole> relationshipRolesSelected) throws SAXException, IOException {
		int countRel = 0;
		int countNMRel = 0;
		boolean someNMrelation = false;

		Element nmRelSection = (Element) this.doc.getElementsByTagName(NMRELATIONS).item(0);
		Element relSection = (Element) this.doc.getElementsByTagName(RELATIONS).item(0);

		for (IRelationshipRole role : relationshipRolesSelected) {
			if (this.isNtoN(role, this.entity) && null != nmRelSection) {
				this.nmRelationsSection(nmRelSection, role, countRel, countNMRel++);
				someNMrelation = true;
			} else if (null != relSection)
				this.relationsSection(relSection, role, countRel);

			countRel++;
			// generateDoc within the loop, so in each pass are taken the original elements and not those modified by the previous iteration
			this.generateDoc();
			nmRelSection = (Element) this.doc.getElementsByTagName(NMRELATIONS).item(0);
			relSection = (Element) this.doc.getElementsByTagName(RELATIONS).item(0);
		}

		// LAST:
		Element section;
		if (someNMrelation) {
			section = nmRelSection;
			countNMRel -= 1;
			countRel = 0;

		} else {
			section = relSection;
			countNMRel = 0;
			countRel -= 1;
		}
		if (null != section) {
			Element last = (Element) section.getElementsByTagName(LAST).item(0);
			if (null != last) {

				NodeList elementNodeList = last.getChildNodes();
				for (int i = 0; i < elementNodeList.getLength(); i++) {
					Node node = elementNodeList.item(i);
					if (node instanceof Element) {
						Element link = (Element) node;

						this.replaceMarkersWithNum(link, countRel, countNMRel);
						this.createLink(link);
					}
				}
			}
		}
	}

	/**
	 * Auxiliary function for parseRelations() to parse RELATIONS section
	 * 
	 * @param relSection
	 *            : XML Element that represents the RELATIONS section
	 * @param role
	 *            : the relationship role
	 * @param countRel
	 *            : number of relations processed
	 */
	private void relationsSection(Element relSection, IRelationshipRole role, int countRel) {
		Element section;

		section = (Element) relSection.getElementsByTagName(ALL).item(0);
		if (null != section)
			this.createElementsRole(section, role, countRel, 0);
	}

	/**
	 * Auxiliary function for parseRelations() to parse NMRELATIONS section
	 * 
	 * @param nmSection
	 *            : XML Element that represents the NMRELATIONS section
	 * @param role
	 *            : the relationship role
	 * @param countRel
	 *            : number of relations processed
	 * @param countNMRel
	 *            : number of N:M relations processed
	 */
	private void nmRelationsSection(Element nmSection, IRelationshipRole role, int countRel, int countNMRel) {
		Element section;

		section = (Element) nmSection.getElementsByTagName(ALL).item(0);
		if (null != section)
			this.createElementsRole(section, role, countRel, countNMRel);

		if (countNMRel == 0) {
			section = (Element) nmSection.getElementsByTagName(FIRST).item(0);
			if (null != section)
				this.createElementsRole(section, role, countRel, countNMRel);
		} else {
			section = (Element) nmSection.getElementsByTagName(REMAINING).item(0);
			if (null != section)
				this.createElementsRole(section, role, countRel, countNMRel);
		}

	}

	/**
	 * Creates the elements with a relationship role
	 * 
	 * @param section
	 *            : the section that contains the Element nodes with the units or links
	 * @param role
	 *            : the relationship role
	 * @param countRel
	 *            : number of relations processed
	 * @param countNMRel
	 *            : number of N:M relations processed
	 */
	private void createElementsRole(Element section, IRelationshipRole role, int countRel, int countNMRel) {
		NodeList elements = section.getChildNodes();

		for (int i = 0; i < elements.getLength(); i++) {
			Node node = elements.item(i);
			if (node instanceof Element) {
				Element element = (Element) node;

				this.replaceMarkersWithNum(element, countRel, countNMRel);

				String nodeName = element.getNodeName();

				if (nodeName.equalsIgnoreCase(ElementTypes.DATA_FLOW)) {
					this.links.add(new DataFlow(element.getAttribute(ID), element.getAttribute(NAME), element.getAttribute(SOURCEID),
							element.getAttribute(TARGETID), element.getAttribute(TYPE), this.entity, role));

				} else if (nodeName.equalsIgnoreCase(ElementTypes.CONNECT_UNIT)) {
					this.units.add(new ConnectUnit(element.getAttribute(ID), element.getAttribute(NAME), element.getAttribute(X), element
							.getAttribute(Y), this.entity, role));

				} else if (nodeName.equalsIgnoreCase(ElementTypes.DISCONNECT_UNIT)) {
					this.units.add(new DisconnectUnit(element.getAttribute(ID), element.getAttribute(NAME), element.getAttribute(X),
							element.getAttribute(Y), this.entity, role));

				} else if (nodeName.equalsIgnoreCase(ElementTypes.RECONNECT_UNIT)) {
					this.units.add(new ReconnectUnit(element.getAttribute(ID), element.getAttribute(NAME), element.getAttribute(X), element
							.getAttribute(Y), this.entity, role));

				} else if (nodeName.equalsIgnoreCase(ElementTypes.SELECTOR_UNIT)) {
					this.units.add(new SelectorUnit(element.getAttribute(ID), element.getAttribute(NAME), element.getAttribute(PARENTID),
							element.getAttribute(TYPE), element.getAttribute(X), element.getAttribute(Y), Utilities.getTargetEntity(role,
									this.entity), role));
				} else {
					this.createLink(element);
				}
			}
		}
	}

	/**
	 * Replace the pad with the counter parameter and operate if necessary (# + n or # -n) on the attributes id, name, sourceId, targetId
	 * and at the x and y coordinates. <br>
	 * Examples: <br>
	 * Input: Attribute "id" with value "idCreate#" and countRel=5 Output: Attribute "id" with value "idCreate5". <br>
	 * Input: Attribute "id" with value "idConnect%-1" and countNMRel=3 Output: Attribute "id" with value "idConnect2"
	 * 
	 * @param element
	 *            : XML Element that contains the attributes to modify
	 * @param countRel
	 *            : number of relations processed
	 * @param countNMRel
	 *            : number of N:M relations processed
	 */
	private void replaceMarkersWithNum(Element element, int countRel, int countNMRel) {
		this.replaceAtt(element, ID, countRel, countNMRel);
		this.replaceAtt(element, NAME, countRel, countNMRel);
		this.replaceAtt(element, SOURCEID, countRel, countNMRel);
		this.replaceAtt(element, TARGETID, countRel, countNMRel);

		this.replaceCoord(element, X, countRel, countNMRel);
		this.replaceCoord(element, Y, countRel, countNMRel);
	}

	/** @see org.homeria.webratioassistant.parser.PatternParser#replaceMarkersWithNum(Element, int, int) */
	private void replaceAtt(Element element, String attribute, int countRel, int countNMRel) {
		String aux = element.getAttribute(attribute);
		if (aux.contains(MARKER_RELATION))
			aux = this.replaceMarkerWithNum2(aux, MARKER_RELATION, countRel);
		if (aux.contains(MARKER_NMRELATION))
			aux = this.replaceMarkerWithNum2(aux, MARKER_NMRELATION, countNMRel);
		element.setAttribute(attribute, aux);
	}

	/** @see org.homeria.webratioassistant.parser.PatternParser#replaceMarkersWithNum(Element, int, int) */
	private void replaceCoord(Element element, String coord, int countRel, int countNMRel) {
		String aux = element.getAttribute(coord);
		if (aux.contains(MARKER_RELATION))
			aux = this.replaceCoords(aux, MARKER_RELATION, countRel);
		if (aux.contains(MARKER_NMRELATION))
			aux = this.replaceCoords(aux, MARKER_NMRELATION, countNMRel);
		element.setAttribute(coord, aux);
	}

	/** @see org.homeria.webratioassistant.parser.PatternParser#replaceMarkersWithNum(Element, int, int) */
	private String replaceMarkerWithNum2(String str, String marker, int count) {
		String s2[] = str.split(marker);
		if (s2.length > 1) {
			// tanto si es + como - cojo el número a la derecha
			if (s2[1].contains("-")) {
				count -= Integer.valueOf(s2[1].split("-")[1]);
			} else if (s2[1].contains("+")) {
				count += Integer.valueOf(s2[1].split("\\+")[1]);
			}
		}
		s2[0] += String.valueOf(count);
		return s2[0];
	}

	/** @see org.homeria.webratioassistant.parser.PatternParser#replaceMarkersWithNum(Element, int, int) */
	private String replaceCoords(String str, String marker, int count) {
		String s2[] = str.split(marker);
		int value = Integer.valueOf(s2[0]);
		value += UNIT_GAP * count;
		s2[0] = String.valueOf(value);

		return s2[0];
	}

	/**
	 * Check if the relationship is N: M
	 * 
	 * @param role
	 *            : Relationship in which to check the cardinality
	 * @return: true if the relationship is N:M, false otherwise
	 */
	private boolean isNtoN(IRelationshipRole role, IEntity entity) {
		IEntity entity1 = entity;
		List<IRelationship> list = entity1.getOutgoingRelationshipList();
		list.addAll(entity1.getIncomingRelationshipList());
		IRelationship relation = (IRelationship) role.getParentElement();
		IRelationshipRole role1;
		IRelationshipRole role2;
		String maxCard1;
		String maxCard2;
		role1 = relation.getRelationshipRole1();
		role2 = relation.getRelationshipRole2();
		maxCard1 = Utilities.getAttribute(role1, "maxCard");
		maxCard2 = Utilities.getAttribute(role2, "maxCard");
		// Si ambos cardinales son N se retorna la relación
		if ((maxCard1.equals("N")) && (maxCard2.equals("N")))
			return true;
		return false;

	}

}
