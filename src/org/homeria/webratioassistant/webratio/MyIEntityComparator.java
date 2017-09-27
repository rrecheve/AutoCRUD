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

import java.util.Comparator;

import com.webratio.ide.model.IEntity;

/**
 * Compare two instances of IEntity by name.
 */
public class MyIEntityComparator implements Comparator<IEntity> {
	public int compare(IEntity entity1, IEntity entity2) {

		String name1 = Utilities.getAttribute(entity1, "name");
		String name2 = Utilities.getAttribute(entity2, "name");

		return (name1.compareTo(name2) < 0 ? -1
				: (name1.compareTo(name2) == 0 ? 0 : 1));
	}
}
