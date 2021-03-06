package uk.ac.exeter.QCRoutines.messages;

import java.lang.reflect.Constructor;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.TreeSet;

import uk.ac.exeter.QCRoutines.data.DataColumn;

/**
 * Holds message generated by QC routines.
 * 
 * <p>
 *   Note that all concrete implementations of this class MUST provide a constructor that
 *   matches {@link #Message(int, TreeSet, TreeSet, Flag, String, String)}.
 *   This allows message objects to be constructed programatically from the most
 *   basic components.
 *   This will be be checked whenever one of the provided constructors is invoked.
 * </p>
 * 
 * @author Steve Jones
 *
 */
public abstract class Message {
	
	/**
	 * Value indicating that a message does not relate to a specific column
	 */
	public static final int NO_COLUMN_INDEX = -999;
	
	/**
	 * Value indicating that a message does not relate to a specific line
	 */
	public static final int NO_LINE_NUMBER = -999;

	/**
	 * The index(es) of the column(s) to which this message applies
	 */
	protected TreeSet<Integer> columnIndices;
	
	/**
	 * The name(s) of the column(s) to which this message applies
	 */
	protected TreeSet<String> columnNames;
	
	/**
	 * The flag for the message
	 */
	private Flag flag;
	
	/**
	 * The line number to which this message applies
	 */
	protected int lineNumber;
	
	/**
	 * The value from the line that caused the message to be generated
	 */
	protected String fieldValue;
	
	/**
	 * An example of a valid value indicating what the line should contain.
	 * This can be a text description, e.g. "Between 12 and 24"
	 */
	protected String validValue;
	
	/**
	 * The generic constructor for a Message object.
	 * 
	 * <p>
	 * 	 All subclasses of this class <b>MUST</b> provide a constructor matching these parameters,
	 *   as it will be used to reconstruct message objects from the database.
	 * </p>
	 * 
	 * @param lineNumber The line to which this message applies
	 * @param columnIndices The index(es) of the column(s) to which this message applies
	 * @param columnNames The name(s) of the column(s) to which this message applies
	 * @param flag The flag for the message
	 * @param fieldValue The value from the line that caused the message to be generated
	 * @param validValue An example of a valid value indicating what the line should contain
	 */
	public Message(int lineNumber, TreeSet<Integer> columnIndices, TreeSet<String> columnNames, Flag flag, String fieldValue, String validValue) {
		this.lineNumber = lineNumber;
		this.columnIndices = columnIndices;
		this.columnNames = columnNames;
		this.flag = flag;
		this.fieldValue = fieldValue;
		this.validValue = validValue;
		
		// Note that we don't need to check the basic constructor here, because this is it!
	}
	
	/**
	 * Constructor for a message that refers to only one column.
	 * 
	 * @param lineNumber The line to which this message applies
	 * @param columnIndex The index of the column to which this message applies
	 * @param columnName The name of the column to which this message applies
	 * @param flag The flag for the message
	 * @param fieldValue The value from the line that caused the message to be generated
	 * @param validValue An example of a valid value indicating what the line should contain
	 * @throws MessageException If the current instance of the Message class does not provide the required generic constructor
	 * @see #Message(int, TreeSet, TreeSet, Flag, String, String)
	 * @see #validValue
	 */
	public Message(int lineNumber, int columnIndex, String columnName, Flag flag, String fieldValue, String validValue) throws MessageException {
		this.lineNumber = lineNumber;

		columnIndices = new TreeSet<Integer>();
		columnIndices.add(columnIndex);

		columnNames = new TreeSet<String>();
		columnNames.add(columnName);

		this.flag = flag;
		this.fieldValue = fieldValue;
		this.validValue = validValue;
		
		checkBasicConstructor(getClass());
	}
	
	/**
	 * Constructor for a message that refers to a single column specified by a {@link DataColumn}.
	 * 
	 * <p>
	 *   The data column will contain the field value that caused the message to be generated
	 * </p>
	 * 
	 * @param lineNumber The line to which this message applies
	 * @param dataColumn The column to which this message applies
	 * @param flag The flag for the message
	 * @param validValue An example of a valid value indicating what the line should contain
	 * @throws MessageException If the current instance of the Message class does not provide the required generic constructor
	 * @see #Message(int, TreeSet, TreeSet, Flag, String, String)
	 * @see #validValue
	 */
	public Message(int lineNumber, DataColumn dataColumn, Flag flag, String validValue) throws MessageException {
		this.lineNumber = lineNumber;
		
		columnIndices = new TreeSet<Integer>();
		columnIndices.add(dataColumn.getColumnIndex());

		columnNames = new TreeSet<String>();
		columnNames.add(dataColumn.getName());
		
		this.flag = flag;
		this.fieldValue = dataColumn.getValue();
		this.validValue = validValue;
		
		checkBasicConstructor(getClass());
	}
	
	/**
	 * Constructor for a message that refers to a single column specified by a {@link DataColumn}.
     *
     * <p>
     *   Although the {@code DataColumn} will contain a field value, it may not be that value
     *   that caused the message to be generated. This constructor allows a field value to
     *   be specified explicitly.
     * </p>
     * 
 	 * @param lineNumber The line to which this message applies
	 * @param dataColumn The column to which this message applies
	 * @param flag The flag for the message
	 * @param fieldValue The value caused the message to be generated
	 * @param validValue An example of a valid value indicating what the line should contain
	 * @throws MessageException If the current instance of the Message class does not provide the required generic constructor
	 * @see #Message(int, TreeSet, TreeSet, Flag, String, String)
	 * @see #validValue
	 */
	public Message(int lineNumber, DataColumn dataColumn, Flag flag, String fieldValue, String validValue) throws MessageException {
		this.lineNumber = lineNumber;
		
		columnIndices = new TreeSet<Integer>();
		columnIndices.add(dataColumn.getColumnIndex());

		columnNames = new TreeSet<String>();
		columnNames.add(dataColumn.getName());

		this.flag = flag;
		this.fieldValue = fieldValue;
		this.validValue = validValue;
		
		checkBasicConstructor(getClass());
	}
	
	/**
	 * Returns the line number for which this message was raised.
	 * @return The line number for which this message was raised.
	 */
	public int getLineNumber() {
		return lineNumber;
	}
	
	/**
	 * Returns the column indices for which this message was raised.
	 * @return The column indices for which this message was raised.
	 */
	public TreeSet<Integer> getColumnIndices() {
		return columnIndices;
	}

	/**
	 * Returns the column names for which this message was raised
	 * @return the name of the column(s) for which this message was raised.
	 */
	public TreeSet<String> getColumnNames() {
		return columnNames;
	}
	
	/**
	 * Returns the list of column names as a String, with names separated by '|'
	 * @return The list of column names
	 */
	public String getColumnNamesAsString() {
		StringBuffer result = new StringBuffer();
		
		int nameCount = 0;
		for (String columnName : columnNames) {
			nameCount++;
			result.append(columnName);
			if (nameCount < columnNames.size()) {
				result.append('|');
			}
		}
		
		return result.toString();
	}

	/**
	 * Returns the flag of the message
	 * @return The flag of the message
	 */
	public Flag getFlag() {
		return flag;
	}
	
	/**
	 * Create the {@link MessageKey} object for this message,
	 * to be used in storing the message.
	 * 
	 * @return The {@link MessageKey} object for this message 
	 */
	public MessageKey generateMessageKey() {
		return new MessageKey(columnIndices, getClass());
	}
	
	/**
	 * Returns the long form of the message text
	 * @return The long message
	 */
	public abstract String getFullMessage();
	
	/**
	 * Returns the short form of the message text
	 * @return The short message
	 */
	public abstract String getShortMessage();
	
	/**
	 * Generate a {@link RebuildCode} that can be used to
	 * reconstruct this {@link Message} object in the future.
	 * 
	 * @return A rebuild code for this message
	 * @throws MessageException If the current instance of the Message class does not provide the required generic constructor
	 * @see #Message(int, TreeSet, TreeSet, Flag, String, String)
	 */
	public RebuildCode getRebuildCode() throws MessageException {
		return new RebuildCode(this);
	}
	
	/**
	 * Returns the field value that caused this message to be generated
	 * @return The field value
	 */
	public String getFieldValue() {
		return fieldValue;
	}
	
	/**
	 * Returns an example of a valid value indicating what the line should contain.
	 * @return A valid field value
	 * @see #validValue
	 */
	public String getValidValue() {
		return validValue;
	}
	
	@Override
	public String toString() {
		return getFullMessage();
	}
	
	@Override
	public boolean equals(Object o) {
		boolean equals = true;
		
		if (!(o instanceof Message)) {
			equals = false;
		} else {
			Message compare = (Message) o;
			if (!compare.columnIndices.equals(columnIndices) ||
					!compare.columnNames.equals(columnNames) ||
					!compare.flag.equals(flag) ||
					compare.lineNumber != lineNumber ||
					!compare.fieldValue.equals(fieldValue) ||
					!compare.validValue.equals(validValue)) {
				equals = false;
			}
		}
		
		return equals;
	}
	
	/**
	 * Ensure that a given {@link Message} class implements the required generic constructor.
	 * 
	 * @param messageClass The {@link Message} class
	 * @throws MessageException If the message class does not implement the constructor
	 * @see #Message(int, TreeSet, TreeSet, Flag, String, String)
	 */
	protected static void checkBasicConstructor(Class<? extends Message> messageClass) throws MessageException {
		
		boolean hasConstructor = true;
		
		try {
			Constructor<?> constructor = messageClass.getConstructor(int.class, TreeSet.class, TreeSet.class, Flag.class, String.class, String.class);
			
			// Check that the Set is for Integers
			Type[] constructorGenericTypes = constructor.getGenericParameterTypes();
			if (constructorGenericTypes.length != 6) {
				hasConstructor = false;
			} else {
				if (!(constructorGenericTypes[1] instanceof ParameterizedType)) {
					hasConstructor = false;
				} else {
					Type[] actualTypeArguments = ((ParameterizedType) constructorGenericTypes[1]).getActualTypeArguments();
					if (actualTypeArguments.length != 1) {
						hasConstructor = false;
					} else {
						Class<?> typeArgumentClass = (Class<?>) actualTypeArguments[0];
						if (!typeArgumentClass.equals(Integer.class)) {
							hasConstructor = false;
						}
					}
				}
			}
		} catch (NoSuchMethodException e) {
			hasConstructor = false;
		}
		
		if (!hasConstructor) {
			throw new MessageException("Message class " + messageClass.getName() + " is missing the basic constructor");
		}
	}
}
