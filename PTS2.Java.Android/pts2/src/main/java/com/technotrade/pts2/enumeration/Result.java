package com.technotrade.pts2.enumeration;

import java.io.Serializable;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/// <summary>
/// List of errors where 0 is not an error
/// </summary>
public enum Result implements Serializable {
	SUCCESS(0),
	JSONPTS_ERROR_NOT_FOUND(1),
	INVALID_JSON_REQUEST(2),
	NO_DATA_ERROR(3),
	NO_DATA_FOR_RESPONSE(4),
	JSON_REQUEST_IS_TOO_LONG(5),
	JSONPTS_ERROR_NO_PERMISSIONS(6),
	JSONPTS_ERROR_NO_SD_FOUND(7),
	JSONPTS_ERROR_POWER_DOWN_DETECTED(8),
	JSONPTS_ERROR_SD_NOT_MOUNTED(9),
	JSONPTS_ERROR_FILE_UPLOAD_PROCESS_RUNNING(10),
	JSONPTS_ERROR_SD_ERROR(11),
	JSONPTS_ERROR_NO_CALIBRATION_CHART_FOUND_ERROR(12),
	JSONPTS_ERROR_COULD_NOT_CHECK_FILE(13),
	JSONPTS_ERROR_COULD_NOT_DELETE_FILE(14),
	JSONPTS_ERROR_INCORRECT_PUMPS_CONFIGURATION(15),
	JSONPTS_ERROR_INCORRECT_PROBES_CONFIGURATION(16),
	JSONPTS_ERROR_COULD_NOT_GET_DATETIME(17),
	JSONPTS_ERROR_COULD_NOT_GET_PUMP_NUMBER(18),
	JSONPTS_ERROR_COULD_NOT_GET_PUMP_TRANSACTION_NUMBER(19),
	JSONPTS_ERROR_PUMP_NUMBER_OUT_OF_RANGE(20),
	JSONPTS_ERROR_PUMP_TRANSACTION_NUMBER_OUT_OF_RANGE(21),
	JSONPTS_ERROR_PUMP_TRANSACTION_NOT_FOUND(22),
	JSONPTS_ERROR_TANK_NUMBER_OUT_OF_RANGE(23),
	JSONPTS_ERROR_COULD_NOT_GET_TANK_NUMBER(24),
	JSONPTS_ERROR_COULD_NOT_GET_TRANSACTION_NUMBER(25),
	JSONPTS_ERROR_TRANSACTION_NUMBER_OUT_OF_RANGE(26),
	JSONPTS_ERROR_TRANSACTION_NUMBER_NOT_MATCH(27),
	JSONPTS_ERROR_TRANSACTION_NUMBER_ALREADY_EXIST(28),
	JSONPTS_ERROR_COULD_NOT_GET_NOZZLE_NUMBER(29),
	JSONPTS_ERROR_COULD_NOT_GET_FUEL_GRADE_ID(30),
	JSONPTS_ERROR_COULD_NOT_GET_NOZZLE_NUMBER_FUEL_GRADE_ID(31),
	JSONPTS_ERROR_COULD_NOT_GET_NOZZLE_NUMBER_FROM_GRADE_ID(32),
	JSONPTS_ERROR_NOZZLE_NUMBER_OUT_OF_RANGE(33),
	JSONPTS_ERROR_COULD_NOT_GET_TYPE(34),
	JSONPTS_ERROR_COULD_NOT_GET_NAME(35),
	JSONPTS_ERROR_TYPE_OUT_OF_RANGE(36),
	JSONPTS_ERROR_COULD_NOT_GET_DOSE_VALUE(37),
	JSONPTS_ERROR_COULD_NOT_GET_PRICE_VALUE(38),
	JSONPTS_ERROR_DUPLICATED_AUTHORIZATION_REQUEST(39),
	JSONPTS_ERROR_PUMP_BUSY_OTHER_USER(40),
	JSONPTS_ERROR_PUMP_BUSY_OTHER_REQUEST_EXECUTED(41),
	JSONPTS_ERROR_PUMP_BUSY_FILLING(42),
	JSONPTS_ERROR_PUMP_NOT_IN_FILLING_PROCESS(43),
	JSONPTS_ERROR_COULD_NOT_GET_STATE_VALUE(44),
	JSONPTS_ERROR_STATE_VALUE_OUT_OF_RANGE(45),
	JSONPTS_ERROR_USER_NOT_MATCH(46),
	JSONPTS_ERROR_DATE_OUT_OF_RANGE(47),
	JSONPTS_ERROR_TIME_OUT_OF_RANGE(48),
	JSONPTS_ERROR_COULD_NOT_GET_HEIGHT(49),
	JSONPTS_ERROR_COULD_NOT_GET_PROBE_NUMBER(50),
	JSONPTS_ERROR_PROBE_NUMBER_OUT_OF_RANGE(51),
	JSONPTS_ERROR_PUMP_IS_NOT_CONFIGURED(52),
	JSONPTS_ERROR_RESTORE_CONFIGURATION_FAILED(53),
	JSONPTS_ERROR_CONFIGURATION_FILE_NOT_FOUND(54),
	JSONPTS_ERROR_CALIBRATION_CHART_NOT_CONFIGURED(55),
	JSONPTS_ERROR_TANK_NOT_CONFIGURED(56),
	JSONPTS_ERROR_PUMP_GRADE_NOT_CORRESPOND_TANK(57),
	JSONPTS_ERROR_INCORRECT_TANKS_CONFIGURATION(58),
	JSONPTS_ERROR_INCORRECT_PRICE_BOARDS_CONFIGURATION(59),
	JSONPTS_ERROR_INCORRECT_READERS_CONFIGURATION(60),
	JSONPTS_ERROR_COULD_NOT_GET_TAG(61),
	JSONPTS_ERROR_COULD_NOT_GET_READER_NUMBER(62),
	JSONPTS_ERROR_READER_NUMBER_OUT_OF_RANGE(63),
	JSONPTS_ERROR_READER_NOT_CONFIGURED(64),
	JSONPTS_ERROR_TANK_PRODUCT_HEIGHT_IS_CRITICAL_LOW(65),
	JSONPTS_ERROR_COULD_NOT_GET_PRICE_BOARD_NUMBER(66),
	JSONPTS_ERROR_PRICE_BOARD_NUMBER_OUT_OF_RANGE(67),
	JSONPTS_ERROR_PRICE_BOARD_NOT_CONFIGURED(68),
	JSONPTS_ERROR_PUMP_STATUS_NOT_END_OF_TRANSACTION(69),
	JSONPTS_ERROR_TAG_IS_NOT_VALID(70),
	JSONPTS_ERROR_DATETIME_INCORRECT_FORMAT(71),
	JSONPTS_ERROR_NO_FREE_USERS_TO_ADD(72),
	JSONPTS_ERROR_USER_LOGIN_USED(73),
	JSONPTS_ERROR_USER_ID_OUT_OF_RANGE(74),
	JSONPTS_ERROR_CAN_NOT_DELETE_LAST_USER(75),
	JSONPTS_ERROR_TAG_ALREADY_EXIST(76),
	JSONPTS_ERROR_TAG_NOT_EXIST(77),
	JSONPTS_ERROR_REPEATED_AUTHORIZE_PUMP_PRICE_DIFFERENT(78),
	JSONPTS_ERROR_CAN_NOT_USE_DATA_UPLOAD_WEBSOCKETS_SAME_TIME(79),
	JSONPTS_ERROR_NO_USER_WITH_CONFIG_PERMISSION_CONFIGURED(80),
	JSONPTS_ERROR_TOO_BIG_TAGS_TOTAL_NUMBER_REQUESTED(81),
	JSONPTS_ERROR_COULD_NOT_DELETE_EXISTING_TAG(82),
	INIT_ERROR(1000),
	NETWORK_ERROR(1001),
	TIMEOUT_ERROR(1002),
	RESPONSE_CODE_ERROR(1003),
	UNAUTHORIZED_ERROR(1004),
	PROTOCOL_ERROR(1005),
	JSON_PARSE_ERROR(1006),
	AT_LAST_ONE_REQUEST_IN_SEQUENCE_FAILED_ERROR(1007),
	UNKNOWN_ERROR(1008),
	NULL_POINTER_ERROR(1009),
	INPUT_DATA_IS_WRONG(1010);

    private final int mCode;
    private static final Map<Integer, Result> lookup
            = new HashMap<Integer,Result>();

    Result(int code) {
        this.mCode = code;
    }

    static {
        for (Result w : EnumSet.allOf(Result.class))
            lookup.put(w.getCode(), w);
    }

    public int getCode() { return mCode; }

    public static Result get(int code) {
        return lookup.get(code);
    }

	/// <summary>
	/// Description getter
	/// </summary>
	public String getDescription() {
		return Result.getDescription(this);
	}
	public static String getDescription(Result result) {
		switch (result) {
			case SUCCESS: return "No error";
			case JSONPTS_ERROR_NOT_FOUND: return "Request not found";
			case INVALID_JSON_REQUEST: return "Invalid JSON request";
			case NO_DATA_ERROR: return "No data";
			case NO_DATA_FOR_RESPONSE: return "No data for response";
			case JSON_REQUEST_IS_TOO_LONG: return "JSON request is too long";
			case JSONPTS_ERROR_NO_PERMISSIONS: return "Access forbidden";
			case JSONPTS_ERROR_NO_SD_FOUND: return "No SD found";
			case JSONPTS_ERROR_POWER_DOWN_DETECTED: return "Power down detected";
			case JSONPTS_ERROR_SD_NOT_MOUNTED: return "SD is not mounted";
			case JSONPTS_ERROR_FILE_UPLOAD_PROCESS_RUNNING: return "File upload process running";
			case JSONPTS_ERROR_SD_ERROR: return "SD error";
			case JSONPTS_ERROR_NO_CALIBRATION_CHART_FOUND_ERROR: return "No calibration chart found";
			case JSONPTS_ERROR_COULD_NOT_CHECK_FILE: return "Could not check file";
			case JSONPTS_ERROR_COULD_NOT_DELETE_FILE: return "Could not delete file";
			case JSONPTS_ERROR_INCORRECT_PUMPS_CONFIGURATION: return "Incorrect pumps configuration";
			case JSONPTS_ERROR_INCORRECT_PROBES_CONFIGURATION: return "Incorrect probes configuration";
			case JSONPTS_ERROR_COULD_NOT_GET_DATETIME: return "Could not get datetime";
			case JSONPTS_ERROR_COULD_NOT_GET_PUMP_NUMBER: return "Could not get pump number";
			case JSONPTS_ERROR_COULD_NOT_GET_PUMP_TRANSACTION_NUMBER: return "Could not get pump transaction number";
			case JSONPTS_ERROR_PUMP_NUMBER_OUT_OF_RANGE: return "Pump number is out of range";
			case JSONPTS_ERROR_PUMP_TRANSACTION_NUMBER_OUT_OF_RANGE: return "Pump transaction number is out of range";
			case JSONPTS_ERROR_PUMP_TRANSACTION_NOT_FOUND: return "Pump transaction not found";
			case JSONPTS_ERROR_TANK_NUMBER_OUT_OF_RANGE: return "Tank number is out of range";
			case JSONPTS_ERROR_COULD_NOT_GET_TANK_NUMBER: return "Could not get tank number";
			case JSONPTS_ERROR_COULD_NOT_GET_TRANSACTION_NUMBER: return "Could not get transaction number";
			case JSONPTS_ERROR_TRANSACTION_NUMBER_OUT_OF_RANGE: return "Transaction number is out of range";
			case JSONPTS_ERROR_TRANSACTION_NUMBER_NOT_MATCH: return "Transaction number does not match";
			case JSONPTS_ERROR_TRANSACTION_NUMBER_ALREADY_EXIST: return "Transaction number already exist";
			case JSONPTS_ERROR_COULD_NOT_GET_NOZZLE_NUMBER: return "Could not get nozzle number";
			case JSONPTS_ERROR_COULD_NOT_GET_FUEL_GRADE_ID: return "Could not get fuel grade Id";
			case JSONPTS_ERROR_COULD_NOT_GET_NOZZLE_NUMBER_FUEL_GRADE_ID: return "Could not get nozzle number and fuel grade Id";
			case JSONPTS_ERROR_COULD_NOT_GET_NOZZLE_NUMBER_FROM_GRADE_ID: return "Could not get nozzle number from fuel grade Id";
			case JSONPTS_ERROR_NOZZLE_NUMBER_OUT_OF_RANGE: return "Nozzle number is out of range";
			case JSONPTS_ERROR_COULD_NOT_GET_TYPE: return "Could not get type";
			case JSONPTS_ERROR_COULD_NOT_GET_NAME: return "Could not get name";
			case JSONPTS_ERROR_TYPE_OUT_OF_RANGE: return "Type is out of range";
			case JSONPTS_ERROR_COULD_NOT_GET_DOSE_VALUE: return "Could not get dose value";
			case JSONPTS_ERROR_COULD_NOT_GET_PRICE_VALUE: return "Could not get price value";
			case JSONPTS_ERROR_DUPLICATED_AUTHORIZATION_REQUEST: return "Duplicated authorization request";
			case JSONPTS_ERROR_PUMP_BUSY_OTHER_USER: return "Pump is busy by other user";
			case JSONPTS_ERROR_PUMP_BUSY_OTHER_REQUEST_EXECUTED: return "Pump is busy with other request being executed";
			case JSONPTS_ERROR_PUMP_BUSY_FILLING: return "Pump is busy with filling";
			case JSONPTS_ERROR_PUMP_NOT_IN_FILLING_PROCESS: return "Pump is not in filling process";
			case JSONPTS_ERROR_COULD_NOT_GET_STATE_VALUE: return "Could not get state value";
			case JSONPTS_ERROR_STATE_VALUE_OUT_OF_RANGE: return "State value is out of range";
			case JSONPTS_ERROR_USER_NOT_MATCH: return "User does not match";
			case JSONPTS_ERROR_DATE_OUT_OF_RANGE: return "Date is out of range";
			case JSONPTS_ERROR_TIME_OUT_OF_RANGE: return "Time is out of range";
			case JSONPTS_ERROR_COULD_NOT_GET_HEIGHT: return "Could not get height value";
			case JSONPTS_ERROR_COULD_NOT_GET_PROBE_NUMBER: return "Could not get probe number";
			case JSONPTS_ERROR_PROBE_NUMBER_OUT_OF_RANGE: return "Probe number is out of range";
			case JSONPTS_ERROR_PUMP_IS_NOT_CONFIGURED: return "Pump is not configured";
			case JSONPTS_ERROR_RESTORE_CONFIGURATION_FAILED: return "Restore of configuration failed";
			case JSONPTS_ERROR_CONFIGURATION_FILE_NOT_FOUND: return "Configuration file not found";
			case JSONPTS_ERROR_CALIBRATION_CHART_NOT_CONFIGURED: return "Calibration chart for tank is not configured";
			case JSONPTS_ERROR_TANK_NOT_CONFIGURED: return "Tank is not configured";
			case JSONPTS_ERROR_PUMP_GRADE_NOT_CORRESPOND_TANK: return "Pump fuel grade does not correspond to the tank fuel";
			case JSONPTS_ERROR_INCORRECT_TANKS_CONFIGURATION: return "Incorrect tanks configuration";
			case JSONPTS_ERROR_INCORRECT_PRICE_BOARDS_CONFIGURATION: return "Incorrect price boards configuration";
			case JSONPTS_ERROR_INCORRECT_READERS_CONFIGURATION: return "Incorrect readers configuration";
			case JSONPTS_ERROR_COULD_NOT_GET_TAG: return "Could not get tag";
			case JSONPTS_ERROR_COULD_NOT_GET_READER_NUMBER: return "Could not get reader number";
			case JSONPTS_ERROR_READER_NUMBER_OUT_OF_RANGE: return "Reader number is out of range";
			case JSONPTS_ERROR_READER_NOT_CONFIGURED: return "Reader is not configured";
			case JSONPTS_ERROR_TANK_PRODUCT_HEIGHT_IS_CRITICAL_LOW: return "Product height in tank is critical low";
			case JSONPTS_ERROR_COULD_NOT_GET_PRICE_BOARD_NUMBER: return "Could not get price board number";
			case JSONPTS_ERROR_PRICE_BOARD_NUMBER_OUT_OF_RANGE: return "Price board number is out of range";
			case JSONPTS_ERROR_PRICE_BOARD_NOT_CONFIGURED: return "Price board is not configured";
			case JSONPTS_ERROR_PUMP_STATUS_NOT_END_OF_TRANSACTION: return "Pump is not in the end of transaction status";
			case JSONPTS_ERROR_TAG_IS_NOT_VALID: return "Tag is not valid";
			case JSONPTS_ERROR_DATETIME_INCORRECT_FORMAT: return "DateTime has incorrect format";
			case JSONPTS_ERROR_NO_FREE_USERS_TO_ADD: return "No free non - configured users to add";
			case JSONPTS_ERROR_USER_LOGIN_USED: return "User login is already used";
			case JSONPTS_ERROR_USER_ID_OUT_OF_RANGE: return "User ID is out of range";
			case JSONPTS_ERROR_CAN_NOT_DELETE_LAST_USER: return "Can not delete last user from user’s configuration";
			case JSONPTS_ERROR_TAG_ALREADY_EXIST: return "Specified tag already exists";
			case JSONPTS_ERROR_TAG_NOT_EXIST: return "Specified tag does not exist";
			case JSONPTS_ERROR_REPEATED_AUTHORIZE_PUMP_PRICE_DIFFERENT: return "Different price sent in repeated authorization request";
			case JSONPTS_ERROR_CAN_NOT_USE_DATA_UPLOAD_WEBSOCKETS_SAME_TIME: return "Can not use upload data and WebSocket same time";
			case JSONPTS_ERROR_NO_USER_WITH_CONFIG_PERMISSION_CONFIGURED: return "No user with configuration permission is configured";
			case JSONPTS_ERROR_TOO_BIG_TAGS_TOTAL_NUMBER_REQUESTED: return "Too big tags total number requested";
			case JSONPTS_ERROR_COULD_NOT_DELETE_EXISTING_TAG: return "Could not delete existing tag";
			case INIT_ERROR: return "Not inited";
			case NETWORK_ERROR: return "Network error";
			case TIMEOUT_ERROR: return "Timed out";
			case RESPONSE_CODE_ERROR: return "HTTP response code is wrong";
			case UNAUTHORIZED_ERROR: return "Authorization error";
			case PROTOCOL_ERROR: return "Protocol error";
			case JSON_PARSE_ERROR: return "JSON parse error";
			case AT_LAST_ONE_REQUEST_IN_SEQUENCE_FAILED_ERROR: return "At last one request in sequence was failed";
			case NULL_POINTER_ERROR: return "Null pointer error";
			case INPUT_DATA_IS_WRONG: return "Input data is wrong";
			case UNKNOWN_ERROR:
			default: return "Unknown error";
		}
	}
};