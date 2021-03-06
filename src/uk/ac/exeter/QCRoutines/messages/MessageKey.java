package uk.ac.exeter.QCRoutines.messages;

import java.util.Set;

/**
 * Key class for organising messages. Consists of the column index
 * and message type
 */
public class MessageKey {

	/**
	 * The index of the column that messages under this key
	 * refer to
	 */
	private Set<Integer> columnIndices;
	
	/**
	 * The type of the messages referred to by this key
	 */
	private Class<?> messageClass;
	
	/**
	 * Construct a MessageKey object
	 * @param columnIndex The column index
	 * @param messageType The message type
	 */
	public MessageKey(Set<Integer> columnIndices, Class<?> messageClass) {
		this.columnIndices = columnIndices;
		this.messageClass = messageClass;
	}
	
	/**
	 * Return the column index
	 * @return The column index
	 */
	public Set<Integer> getColumnIndices() {
		return columnIndices;
	}
	
	/**
	 * Return the message type
	 * @return The message type
	 */
	public Class<?> getMessageClass() {
		return messageClass;
	}
	
	@Override
	public boolean equals(Object compare) {
		
		boolean equals = false;
		
		if (compare instanceof MessageKey) {
			MessageKey compareKey = (MessageKey) compare;
			equals = (this.messageClass.equals(compareKey.messageClass) && this.columnIndices.equals(compareKey.columnIndices));
		}
		
		return equals;
	}
}
