/**
 * PROYECTO FIN DE CARRERA:
 * 		- Título: Generación automática de la arquitectura de una aplicación web en WebML a partir de la
 *		  		  especificación de requisitos
 * REALIZADO POR:
 * 		- CARLOS AGUADO FUENTES, DNI: 76036306P
 * 		- INGENIERIA INFORMATICA: 2012/2013, CONVOCATORIA DE JUNIO 
 */
package org.homeria.webratioassistant.plugin;

import java.util.Comparator;

import com.webratio.ide.model.IEntity;

/**
 * 
 * @author carlos
 *
 */
public class MyIEntityComparator implements Comparator<IEntity> {
	public int compare(IEntity entity1, IEntity entity2) {

		String name1 = Utilities.getAttribute(entity1, "name");
		String name2 = Utilities.getAttribute(entity2, "name");

		return (name1.compareTo(name2) < 0 ? -1
				: (name1.compareTo(name2) == 0 ? 0 : 1));
	}
}
