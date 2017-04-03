/**
 * PROYECTO FIN DE CARRERA:
 * 		- Título: Generación automática de la arquitectura de una aplicación web en WebML a partir de la
 *		  		  especificación de requisitos
 * REALIZADO POR:
 * 		- CARLOS AGUADO FUENTES, DNI: 76036306P
 * 		- INGENIERIA INFORMATICA: 2012/2013, CONVOCATORIA DE JUNIO 
 */
package org.homeria.webratioassistant.plugin;

import java.util.Date;

public class Debug {
	static boolean debug = true;

	public static void println(String Ruta, String Comentario) {
		Date d = new Date();
		if (debug)
			System.err.println("(DEBUG:" + d.getTime() + ") " + Ruta + ": "
					+ Comentario);
	}

	public static void setOff() {
		debug = false;
	}

	public static void setOn() {
		debug = true;
	}
}
