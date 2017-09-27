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

import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Data object to store the pattern information
 */
public class PatternRegisteredPOJO {
	private String id;
	private int timesUsed;

	private SortedMap<String, Integer> svReg;
	private SortedMap<String, Integer> elementsReg;

	/**
	 * Constructs a new instance.
	 */
	public PatternRegisteredPOJO() {
		this.id = "";
		this.timesUsed = 0;
		this.svReg = new TreeMap<String, Integer>();
		this.elementsReg = new TreeMap<String, Integer>();
	}

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getTimesUsed() {
		return this.timesUsed;
	}

	public void increaseTimesUsed() {
		this.timesUsed++;
	}

	public Map<String, Integer> getSvReg() {
		return this.svReg;
	}

	public Map<String, Integer> getElementsReg() {
		return this.elementsReg;
	}

	/**
	 * store the siteView id and initialize the counter. If its already stored, increments the counter.
	 * 
	 * @param svId
	 *            : the id of the SiteView
	 */
	public void addSv(String svId) {
		Integer count;

		if (this.svReg.containsKey(svId))
			count = 1 + this.svReg.get(svId);
		else
			count = new Integer(1);

		this.svReg.put(svId, count);
	}

	/**
	 * store the element id and initialize the counter. If its already stored, increments the counter.
	 * 
	 * @param elementType
	 *            : the type of element to store
	 */
	public void addElement(String elementType) {
		Integer count;

		if (this.elementsReg.containsKey(elementType))
			count = 1 + this.elementsReg.get(elementType);
		else
			count = new Integer(1);

		this.elementsReg.put(elementType, count);
	}

	@Override
	public String toString() {
		String output = "";

		output += " - Pattern: \n";
		output += "\t" + this.id + " (" + this.timesUsed + ")\n";
		output += "\n";
		output += " - SiteViews: \n";

		for (String sv : this.svReg.keySet())
			output += "\t" + sv + " (" + this.svReg.get(sv) + ")\n";

		output += "\n";
		output += " - Elements: \n";

		for (String element : this.elementsReg.keySet())
			output += "\t" + element + " (" + this.elementsReg.get(element) + ")\n";

		return output;
	}
}
