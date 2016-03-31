package uk.ac.exeter.QCRoutines.routines.ConstantValue;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.Seconds;

import uk.ac.exeter.QCRoutines.Routine;
import uk.ac.exeter.QCRoutines.RoutineException;
import uk.ac.exeter.QCRoutines.config.ColumnConfig;
import uk.ac.exeter.QCRoutines.config.ColumnConfigItem;
import uk.ac.exeter.QCRoutines.data.DataRecord;
import uk.ac.exeter.QCRoutines.data.DataRecordException;
import uk.ac.exeter.QCRoutines.data.NoSuchColumnException;
import uk.ac.exeter.QCRoutines.messages.Flag;

public class ConstantValueRoutine extends Routine {

	/**
	 * The name of the column to be checked
	 */
	private String columnName;
	
	/**
	 * The maximum time that a value can remain constant (in minutes)
	 */
	private int maxDuration;
	
	@Override
	public void initialise(List<String> parameters, ColumnConfig columnConfig) throws RoutineException {
		if (parameters.size() != 2) {
			throw new RoutineException("Incorrect number of parameters. Must be <columnName>,<maxDuration>");
		}
		
		columnName = parameters.get(0);
		if (!columnConfig.hasColumn(columnName)) {
			throw new RoutineException("Column '" + columnName + "' does not exist");
		}
		
		ColumnConfigItem column = columnConfig.getColumnConfig(columnName);
		if (!column.isNumeric()) {
			throw new RoutineException("Column '" + columnName + "' must be numeric");
		}
		
		try {
			maxDuration = Integer.parseInt(parameters.get(1));
		} catch (NumberFormatException e) {
			throw new RoutineException("Max duration parameter must be numeric");
		}
		
		if (maxDuration <= 0) {
			throw new RoutineException("Max duration must be greater than zero");
		}
	}

	@Override
	public void processRecords(List<DataRecord> records) throws RoutineException {

		List<DataRecord> recordCollection = new ArrayList<DataRecord>();
		
		
		for (DataRecord record : records) {
			// If there's no record stored, this is the first of a new constant value
			if (recordCollection.size() == 0) {
				recordCollection.add(record);
			} else {
				if (equalsConstant(record, recordCollection.get(0))) {
					// If it equals the value in the first record, then it's still a constant value
					recordCollection.add(record);
				} else {
					// The value is no longer constant.
					// See how long it was constant for
					doDurationCheck(recordCollection);

					// Clear the list of constant records and start again
					recordCollection.clear();
					recordCollection.add(record);
				}
				
			}
		}
	}
	
	/**
	 * Determines whether or not the value in the passed record is identical to that
	 * in the list of constant records
	 * @param record The record to be checked
	 * @return {@code true} if the value in the record equals that in the list of constant records; {@code false} otherwise.
	 * @throws SanityCheckException If the value cannot be compared.
	 */
	private boolean equalsConstant(DataRecord record, DataRecord firstRecord) throws RoutineException {

		boolean result = false;
		
		try {
			double currentValue = Double.parseDouble(firstRecord.getValue(columnName));
			double recordValue = Double.parseDouble(record.getValue(columnName));
			
			result = (currentValue == recordValue);
		} catch (NumberFormatException e) {
			throw new RoutineException("Cannot compare non-numeric values", e);
		} catch (NoSuchColumnException e) {
			throw new RoutineException("Could not find column '" + columnName + "' in record", e);
		}
		
		return result;
	}
	
	/**
	 * See how long the value has been constant in the set of stored records.
	 * If the value is constant for longer than the maximum time, flag each record accordingly.
	 * @throws SanityCheckException If the records cannot be flagged.
	 */
	private void doDurationCheck(List<DataRecord> constantRecords) throws RoutineException {

		// For measurements taken a long time apart, the value can easily be constant.
		// For example, measurements taken hourly can happily have the same value, but
		// if the constant check is set for 30 minutes it will always be triggered.
		//
		// Therefore we make sure there's more than two consecutive measurements with the
		// constant value.
		if (constantRecords.size() > 2) {
		
			double secondsDifference = Seconds.secondsBetween(constantRecords.get(0).getTime(), constantRecords.get(constantRecords.size() - 1).getTime()).getSeconds();
			double minutesDifference = secondsDifference / 60.0;
			
			
			if (minutesDifference > maxDuration) {
				try {
					for (DataRecord record : constantRecords) {
						addMessage(new ConstantValueMessage(record.getLineNumber(), record.getColumn(columnName), Flag.BAD, String.valueOf(minutesDifference), String.valueOf(maxDuration)), record);
					}
				} catch (DataRecordException e) {
					throw new RoutineException("Error while adding messages to records", e);
				}
			}
		}
	}
}
