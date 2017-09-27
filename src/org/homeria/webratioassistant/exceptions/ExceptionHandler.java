/**
 * WebRatio Assistant v3.0
 * 
 * University of Extremadura (Spain) www.unex.es
 * 
 * Developers:
 * 	- Carlos Aguado Fuentes (v2)
 * 	- Javier Sierra Blázquez (v3.0)
 * */
package org.homeria.webratioassistant.exceptions;

import org.homeria.webratioassistant.webratio.Utilities;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/** This class is used to manage all exceptions occurred in this project. */
public class ExceptionHandler implements ErrorHandler {

	/**
	 * Show a MessageBox with the message of the exception. When the box is closed, the plugin stops.
	 * 
	 * @param exception
	 *            : the exception raised
	 */
	public static void handle(Exception exception) {
		Utilities.showErrorUIMessage(exception.getMessage());
		Utilities.closePlugin();
	}

	/**
	 * Conforms the string with all the data given by the SAXParseException parameter
	 * 
	 * @param spe
	 *            : the SAXParseException
	 * @return the string conformed
	 */
	private String getParseExceptionInfo(SAXParseException spe) {
		String systemId = spe.getSystemId();
		if (systemId == null) {
			systemId = "null";
		}
		String info = " - URI=" + systemId + "\n - Line=" + spe.getLineNumber() + ": " + spe.getMessage();
		return info;
	}

	// The following methods are standard SAX ErrorHandler methods.

	public void warning(SAXParseException spe) throws SAXException {
		System.out.println("Warning:\n" + this.getParseExceptionInfo(spe));
	}

	public void error(SAXParseException spe) throws SAXException {
		String message = "Error:\n" + this.getParseExceptionInfo(spe);
		throw new SAXException(message);
	}

	public void fatalError(SAXParseException spe) throws SAXException {
		String message = "Fatal Error:\n" + this.getParseExceptionInfo(spe);
		throw new SAXException(message);
	}
}
