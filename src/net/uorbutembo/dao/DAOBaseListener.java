/**
 * 
 */
package net.uorbutembo.dao;

import java.util.EventObject;

/**
 * @author Esaie MUHASA
 *
 */
public interface DAOBaseListener {
	
	void onEvent (DAOEvent event);
	
	/**
	 * @author Esaie MUHASA
	 * conteneur des informations d'un evenement
	 */
	public static final class DAOEvent extends EventObject{
		private static final long serialVersionUID = 2286229181287605933L;
		
		private Object data;
		private EventType type;
		private int requestId;

		/**
		 * Constructeur d'initialisation
		 * @param source
		 * @param data
		 * @param type
		 * @param requestId
		 */
		public DAOEvent(Object source, Object data, EventType type, int requestId) {
			super(source);
			this.data = data;
			this.type = type;
			this.requestId = requestId;
		}

		/**
		 * @return the data
		 */
		public Object getData() {
			return data;
		}

		/**
		 * @return the type
		 */
		public EventType getType() {
			return type;
		}

		/**
		 * @return the requestId
		 */
		public int getRequestId() {
			return requestId;
		}
		
	}
	
	/**
	 * @author Esaie MUHASA
	 * Type d'evenement du DAO
	 */
	public static enum EventType {
		CREATE,
		UPDATE,
		DELETE,
		SIGNLE_SELECTION,
		MULTI_SELECTION,
		ERROR,
		RELOAD
	}
}
