/**
 * WebRatio Assistant v3.0
 * 
 * University of Extremadura (Spain) www.unex.es
 * 
 * Developers:
 * 	- Carlos Aguado Fuentes (v2)
 * 	- Javier Sierra Blázquez (v3.0)
 */
package org.homeria.webratioassistant.elements;

/**
 * Abstract class used to identify the element types
 */
public abstract class ElementTypes {
	public static final String PAGE = "Page";
	public static final String XOR_PAGE = "XOR";

	public static final String POWER_INDEX_UNIT = "PowerIndexUnit";
	public static final String DATA_UNIT = "DataUnit";
	public static final String MULTI_MESSAGE_UNIT = "MultiMessageUnit";
	public static final String ENTRY_UNIT = "EntryUnit";
	public static final String CREATE_UNIT = "CreateUnit";
	public static final String UPDATE_UNIT = "UpdateUnit";
	public static final String MODIFY_UNIT = "ModifyUnit";
	public static final String DELETE_UNIT = "DeleteUnit";
	public static final String CONNECT_UNIT = "ConnectUnit";
	public static final String DISCONNECT_UNIT = "DisconnectUnit";
	public static final String RECONNECT_UNIT = "ReconnectUnit";
	public static final String SELECTOR_UNIT = "SelectorUnit";
	public static final String NO_OP_CONTENT_UNIT = "NoOpContentUnit";
	public static final String IS_NOT_NULL_UNIT = "IsNotNullUnit";

	// LINKS
	public static final String NORMAL_NAVIGATION_FLOW = "NormalNavigationFlow";
	public static final String DATA_FLOW = "DataFlow";
	public static final String OK_LINK = "OKLink";
	public static final String KO_LINK = "KOLink";

	// - Specfic types:

	// ENTRY UNIT TYPES
	/** Used to check 'preloaded' property in EntryUnit's fields */
	public static String ENTRYUNIT_PRELOADED = "preloaded";

	// SELECTOR UNIT TYPES
	/** Used create a keyCondition in a SelectorUnit */
	public static String SELECTOR_KEYCONDITION = "keyCondition";
	/** Used create a roleCondition in a SelectorUnit */
	public static String SELECTOR_ROLECONDITION = "roleCondition";

	// DATA FLOW TYPES:

	/** It is used to create fields in the EntryUnit (destination) that must be preloaded using the relationship role */
	public static final String DATAFLOW_PRELOAD = "preload";
	/** Used to set a coupling between EntryUnit and ConnectUnit */
	public static final String DATAFLOW_ENTRY_TO_CONNECT = "entryToConnect";
	/** Used to set a coupling between EntryUnit and ReconnectUnit */
	public static final String DATAFLOW_ENTRY_TO_RECONNECT = "entryToReconnect";
	/** Used to set a coupling between any unit (without role) and EntryUnit */
	public static final String DATAFLOW_UNIT_TO_ENTRY = "unitToEntry";
	/** Used to set a coupling between any unit (with role) and EntryUnit */
	public static final String DATAFLOW_UNIT_TO_ENTRY_ROLE = "unitToEntryRole";

	// NORMAL NAVIGATION FLOW TYPES:
	/** Used to set a binding with a fixed value */
	public static final String NORMALFLOW_FIXED_VALUE = "fixedValue";
	/** Used to set a coupling with a IsNotNullUnit */
	public static final String NORMALFLOW_IS_NOT_NULL = "isNotNull";

	// TYPES FOR DATA FLOW AND NORMAL NAVIGATION FLOW (BOTH)
	/** Used to set a coupling between EntryUnit and CreateUnit */
	public static final String FLOW_ENTRY_TO_CREATE = "entryToCreate";
	/** Used to set a coupling between EntryUnit and UpdateUnit */
	public static final String FLOW_ENTRY_TO_UPDATE = "entryToUpdate";

	// OK/KO TYPES
	/** Used to set empty binding in OKLink */
	public static final String OK_LINK_NO_COUPLING = "noCoupling";
	/** Used to set empty binding in OKLink */
	public static final String KO_LINK_NO_COUPLING = "noCoupling";

}
