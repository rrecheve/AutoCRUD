/**
 * WebRatio Assistant v3.0
 * 
 * University of Extremadura (Spain) www.unex.es
 * 
 * Developers:
 * 	- Carlos Aguado Fuentes (v2)
 * 	- Javier Sierra Blázquez (v3.0)
 * */
package org.homeria.webratioassistant.registry;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.homeria.webratioassistant.webratio.Utilities;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * This class implements the Singleton pattern. The Registry is the information of all generated elements by the application.
 */
public class Registry {
	private static Registry instance = null;

	private static final String FILENAME = "Registry";
	private static final String ROOTNODE = "Registry";
	private static final String PATTERN = "Pattern";
	private static final String SITEVIEW = "SiteView";
	private static final String ID = "id";
	private static final String NAME = "name";

	// absolute pattern folder path
	private String path;
	private Document document;
	private Element root;
	private Element currentSv;
	private Element currentPattern;

	private Registry(String path) {
		this.path = path;
	}

	/**
	 * 
	 * @return singleton Registry instance
	 */
	public static Registry getInstance() {
		if (instance == null) {
			instance = new Registry(Utilities.getPatternsPath() + FILENAME);
		}
		return instance;
	}

	/**
	 * Puts the instance to null, forcing to create a new object when getInstance() is called
	 */
	public static void reloadInstance() {
		instance = null;
	}

	/**
	 * Check if the Registry file exists
	 * 
	 * @return true if the file exists, false otherwise
	 */
	public boolean fileExists() {
		return new File(this.path).exists();
	}

	/**
	 * Constructs the Document element parsing the registry file. If the registry file does not exist, initializes the Document with a new
	 * root node.
	 * 
	 * @throws SAXException
	 * @throws IOException
	 * @throws ParserConfigurationException
	 */
	private void fillDOM() throws SAXException, IOException, ParserConfigurationException {
		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();

		if (this.fileExists()) {
			this.document = documentBuilder.parse(this.path);
			this.root = this.document.getDocumentElement();

		} else {
			this.document = documentBuilder.newDocument();
			this.root = this.document.createElement(ROOTNODE);
			this.document.appendChild(this.root);

		}
	}

	/**
	 * Stores the pattern data in a node which is the parent of the SiteViews.
	 * 
	 * @param id
	 *            : the pattern id
	 * @param name
	 *            : the pattern name
	 * @throws SAXException
	 * @throws IOException
	 * @throws ParserConfigurationException
	 */
	public void setPatternData(String id, String name) throws SAXException, IOException, ParserConfigurationException {
		this.fillDOM();

		this.currentPattern = this.document.createElement(PATTERN);
		this.currentPattern.setAttribute(ID, id);
		this.currentPattern.setAttribute(NAME, name);

		this.root.appendChild(this.currentPattern);
	}

	/**
	 * Creates a new node with the SiteView's information and set the current SiteView to it. The parent node is the current Pattern.
	 * 
	 * @param finalId
	 *            : final id of the SiteView
	 * @param name
	 *            : name of the SiteView
	 */
	public void addSiteView(String finalId, String name) {
		Element sv = this.document.createElement(SITEVIEW);
		sv.setAttribute(ID, finalId);
		sv.setAttribute(NAME, name);
		this.currentPattern.appendChild(sv);
		this.currentSv = sv;
	}

	/**
	 * Creates a new node with the element's information. The parent node is the current SiteView
	 * 
	 * @param type
	 *            : type of the element
	 * @param finalId
	 *            : final id of the element
	 */
	public void addElement(String type, String finalId) {
		Element element = this.document.createElement(type);
		element.setAttribute("id", finalId);

		this.currentSv.appendChild(element);

	}

	/**
	 * Stores the Document object with all the data in the registry file
	 * 
	 * @throws TransformerException
	 */
	public void saveToFile() throws TransformerException {
		DOMSource source = new DOMSource(this.document);

		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		StreamResult result = new StreamResult(this.path);
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
		transformer.transform(source, result);
	}

	/**
	 * Parse the registry file and returns all the data.
	 * 
	 * @return the map with all the data of the Registry. <br>
	 *         SortedMap <K,V> <br>
	 *         K = the id that identifies the patternData. <br>
	 *         V = the patternData identified by the key
	 * @throws SAXException
	 * @throws IOException
	 * @throws ParserConfigurationException
	 */
	public SortedMap<String, PatternRegisteredPOJO> getAllData() throws SAXException, IOException, ParserConfigurationException {
		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder documentBuilder;
		documentBuilder = documentBuilderFactory.newDocumentBuilder();

		Document document = documentBuilder.parse(this.path);
		Element root = document.getDocumentElement();

		SortedMap<String, PatternRegisteredPOJO> map = new TreeMap<String, PatternRegisteredPOJO>();

		NodeList pattNodeList = root.getElementsByTagName(PATTERN);

		for (int i = 0; i < pattNodeList.getLength(); i++) {
			Node node = pattNodeList.item(i);
			if (node instanceof Element) {
				Element pattern = (Element) node;

				this.parsePattern(pattern, map);
			}
		}

		return map;
	}

	private void parsePattern(Element pattern, Map<String, PatternRegisteredPOJO> map) {
		PatternRegisteredPOJO data;
		String id = pattern.getAttribute("id");
		String name = pattern.getAttribute("name");
		id = id + " - " + name;

		if (map.containsKey(id)) {
			data = map.get(id);
		} else {
			data = new PatternRegisteredPOJO();
			data.setId(id);
		}

		data.increaseTimesUsed();

		NodeList svNodeList = pattern.getElementsByTagName(SITEVIEW);

		for (int i = 0; i < svNodeList.getLength(); i++) {
			Node node = svNodeList.item(i);
			if (node instanceof Element) {
				Element sv = (Element) node;

				this.parseSv(sv, data);
			}
		}
		map.put(id, data);

	}

	private void parseSv(Element sv, PatternRegisteredPOJO data) {
		String id = sv.getAttribute("id");
		String name = sv.getAttribute("name");

		data.addSv(id + " - " + name);

		NodeList unitNodeList = sv.getChildNodes();

		for (int i = 0; i < unitNodeList.getLength(); i++) {
			Node node = unitNodeList.item(i);
			if (node instanceof Element) {
				Element unit = (Element) node;

				data.addElement(unit.getTagName());
			}
		}
	}
}
