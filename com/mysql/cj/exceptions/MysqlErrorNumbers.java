package com.mysql.cj.exceptions;

import com.mysql.cj.Messages;
import java.util.HashMap;
import java.util.Map;

public final class MysqlErrorNumbers {
  public static final int ER_ERROR_MESSAGES = 298;
  
  public static final int ER_HASHCHK = 1000;
  
  public static final int ER_NISAMCHK = 1001;
  
  public static final int ER_NO = 1002;
  
  public static final int ER_YES = 1003;
  
  public static final int ER_CANT_CREATE_FILE = 1004;
  
  public static final int ER_CANT_CREATE_TABLE = 1005;
  
  public static final int ER_CANT_CREATE_DB = 1006;
  
  public static final int ER_DB_CREATE_EXISTS = 1007;
  
  public static final int ER_DB_DROP_EXISTS = 1008;
  
  public static final int ER_DB_DROP_DELETE = 1009;
  
  public static final int ER_DB_DROP_RMDIR = 1010;
  
  public static final int ER_CANT_DELETE_FILE = 1011;
  
  public static final int ER_CANT_FIND_SYSTEM_REC = 1012;
  
  public static final int ER_CANT_GET_STAT = 1013;
  
  public static final int ER_CANT_GET_WD = 1014;
  
  public static final int ER_CANT_LOCK = 1015;
  
  public static final int ER_CANT_OPEN_FILE = 1016;
  
  public static final int ER_FILE_NOT_FOUND = 1017;
  
  public static final int ER_CANT_READ_DIR = 1018;
  
  public static final int ER_CANT_SET_WD = 1019;
  
  public static final int ER_CHECKREAD = 1020;
  
  public static final int ER_DISK_FULL = 1021;
  
  public static final int ER_DUP_KEY = 1022;
  
  public static final int ER_ERROR_ON_CLOSE = 1023;
  
  public static final int ER_ERROR_ON_READ = 1024;
  
  public static final int ER_ERROR_ON_RENAME = 1025;
  
  public static final int ER_ERROR_ON_WRITE = 1026;
  
  public static final int ER_FILE_USED = 1027;
  
  public static final int ER_FILSORT_ABORT = 1028;
  
  public static final int ER_FORM_NOT_FOUND = 1029;
  
  public static final int ER_GET_ERRNO = 1030;
  
  public static final int ER_ILLEGAL_HA = 1031;
  
  public static final int ER_KEY_NOT_FOUND = 1032;
  
  public static final int ER_NOT_FORM_FILE = 1033;
  
  public static final int ER_NOT_KEYFILE = 1034;
  
  public static final int ER_OLD_KEYFILE = 1035;
  
  public static final int ER_OPEN_AS_READONLY = 1036;
  
  public static final int ER_OUTOFMEMORY = 1037;
  
  public static final int ER_OUT_OF_SORTMEMORY = 1038;
  
  public static final int ER_UNEXPECTED_EOF = 1039;
  
  public static final int ER_CON_COUNT_ERROR = 1040;
  
  public static final int ER_OUT_OF_RESOURCES = 1041;
  
  public static final int ER_BAD_HOST_ERROR = 1042;
  
  public static final int ER_HANDSHAKE_ERROR = 1043;
  
  public static final int ER_DBACCESS_DENIED_ERROR = 1044;
  
  public static final int ER_ACCESS_DENIED_ERROR = 1045;
  
  public static final int ER_NO_DB_ERROR = 1046;
  
  public static final int ER_UNKNOWN_COM_ERROR = 1047;
  
  public static final int ER_BAD_NULL_ERROR = 1048;
  
  public static final int ER_BAD_DB_ERROR = 1049;
  
  public static final int ER_TABLE_EXISTS_ERROR = 1050;
  
  public static final int ER_BAD_TABLE_ERROR = 1051;
  
  public static final int ER_NON_UNIQ_ERROR = 1052;
  
  public static final int ER_SERVER_SHUTDOWN = 1053;
  
  public static final int ER_BAD_FIELD_ERROR = 1054;
  
  public static final int ER_WRONG_FIELD_WITH_GROUP = 1055;
  
  public static final int ER_WRONG_GROUP_FIELD = 1056;
  
  public static final int ER_WRONG_SUM_SELECT = 1057;
  
  public static final int ER_WRONG_VALUE_COUNT = 1058;
  
  public static final int ER_TOO_LONG_IDENT = 1059;
  
  public static final int ER_DUP_FIELDNAME = 1060;
  
  public static final int ER_DUP_KEYNAME = 1061;
  
  public static final int ER_DUP_ENTRY = 1062;
  
  public static final int ER_WRONG_FIELD_SPEC = 1063;
  
  public static final int ER_PARSE_ERROR = 1064;
  
  public static final int ER_EMPTY_QUERY = 1065;
  
  public static final int ER_NONUNIQ_TABLE = 1066;
  
  public static final int ER_INVALID_DEFAULT = 1067;
  
  public static final int ER_MULTIPLE_PRI_KEY = 1068;
  
  public static final int ER_TOO_MANY_KEYS = 1069;
  
  public static final int ER_TOO_MANY_KEY_PARTS = 1070;
  
  public static final int ER_TOO_LONG_KEY = 1071;
  
  public static final int ER_KEY_COLUMN_DOES_NOT_EXITS = 1072;
  
  public static final int ER_BLOB_USED_AS_KEY = 1073;
  
  public static final int ER_TOO_BIG_FIELDLENGTH = 1074;
  
  public static final int ER_WRONG_AUTO_KEY = 1075;
  
  public static final int ER_READY = 1076;
  
  public static final int ER_NORMAL_SHUTDOWN = 1077;
  
  public static final int ER_GOT_SIGNAL = 1078;
  
  public static final int ER_SHUTDOWN_COMPLETE = 1079;
  
  public static final int ER_FORCING_CLOSE = 1080;
  
  public static final int ER_IPSOCK_ERROR = 1081;
  
  public static final int ER_NO_SUCH_INDEX = 1082;
  
  public static final int ER_WRONG_FIELD_TERMINATORS = 1083;
  
  public static final int ER_BLOBS_AND_NO_TERMINATED = 1084;
  
  public static final int ER_TEXTFILE_NOT_READABLE = 1085;
  
  public static final int ER_FILE_EXISTS_ERROR = 1086;
  
  public static final int ER_LOAD_INFO = 1087;
  
  public static final int ER_ALTER_INFO = 1088;
  
  public static final int ER_WRONG_SUB_KEY = 1089;
  
  public static final int ER_CANT_REMOVE_ALL_FIELDS = 1090;
  
  public static final int ER_CANT_DROP_FIELD_OR_KEY = 1091;
  
  public static final int ER_INSERT_INFO = 1092;
  
  public static final int ER_UPDATE_TABLE_USED = 1093;
  
  public static final int ER_NO_SUCH_THREAD = 1094;
  
  public static final int ER_KILL_DENIED_ERROR = 1095;
  
  public static final int ER_NO_TABLES_USED = 1096;
  
  public static final int ER_TOO_BIG_SET = 1097;
  
  public static final int ER_NO_UNIQUE_LOGFILE = 1098;
  
  public static final int ER_TABLE_NOT_LOCKED_FOR_WRITE = 1099;
  
  public static final int ER_TABLE_NOT_LOCKED = 1100;
  
  public static final int ER_BLOB_CANT_HAVE_DEFAULT = 1101;
  
  public static final int ER_WRONG_DB_NAME = 1102;
  
  public static final int ER_WRONG_TABLE_NAME = 1103;
  
  public static final int ER_TOO_BIG_SELECT = 1104;
  
  public static final int ER_UNKNOWN_ERROR = 1105;
  
  public static final int ER_UNKNOWN_PROCEDURE = 1106;
  
  public static final int ER_WRONG_PARAMCOUNT_TO_PROCEDURE = 1107;
  
  public static final int ER_WRONG_PARAMETERS_TO_PROCEDURE = 1108;
  
  public static final int ER_UNKNOWN_TABLE = 1109;
  
  public static final int ER_FIELD_SPECIFIED_TWICE = 1110;
  
  public static final int ER_INVALID_GROUP_FUNC_USE = 1111;
  
  public static final int ER_UNSUPPORTED_EXTENSION = 1112;
  
  public static final int ER_TABLE_MUST_HAVE_COLUMNS = 1113;
  
  public static final int ER_RECORD_FILE_FULL = 1114;
  
  public static final int ER_UNKNOWN_CHARACTER_SET = 1115;
  
  public static final int ER_TOO_MANY_TABLES = 1116;
  
  public static final int ER_TOO_MANY_FIELDS = 1117;
  
  public static final int ER_TOO_BIG_ROWSIZE = 1118;
  
  public static final int ER_STACK_OVERRUN = 1119;
  
  public static final int ER_WRONG_OUTER_JOIN = 1120;
  
  public static final int ER_NULL_COLUMN_IN_INDEX = 1121;
  
  public static final int ER_CANT_FIND_UDF = 1122;
  
  public static final int ER_CANT_INITIALIZE_UDF = 1123;
  
  public static final int ER_UDF_NO_PATHS = 1124;
  
  public static final int ER_UDF_EXISTS = 1125;
  
  public static final int ER_CANT_OPEN_LIBRARY = 1126;
  
  public static final int ER_CANT_FIND_DL_ENTRY = 1127;
  
  public static final int ER_FUNCTION_NOT_DEFINED = 1128;
  
  public static final int ER_HOST_IS_BLOCKED = 1129;
  
  public static final int ER_HOST_NOT_PRIVILEGED = 1130;
  
  public static final int ER_PASSWORD_ANONYMOUS_USER = 1131;
  
  public static final int ER_PASSWORD_NOT_ALLOWED = 1132;
  
  public static final int ER_PASSWORD_NO_MATCH = 1133;
  
  public static final int ER_UPDATE_INFO = 1134;
  
  public static final int ER_CANT_CREATE_THREAD = 1135;
  
  public static final int ER_WRONG_VALUE_COUNT_ON_ROW = 1136;
  
  public static final int ER_CANT_REOPEN_TABLE = 1137;
  
  public static final int ER_INVALID_USE_OF_NULL = 1138;
  
  public static final int ER_REGEXP_ERROR = 1139;
  
  public static final int ER_MIX_OF_GROUP_FUNC_AND_FIELDS = 1140;
  
  public static final int ER_NONEXISTING_GRANT = 1141;
  
  public static final int ER_TABLEACCESS_DENIED_ERROR = 1142;
  
  public static final int ER_COLUMNACCESS_DENIED_ERROR = 1143;
  
  public static final int ER_ILLEGAL_GRANT_FOR_TABLE = 1144;
  
  public static final int ER_GRANT_WRONG_HOST_OR_USER = 1145;
  
  public static final int ER_NO_SUCH_TABLE = 1146;
  
  public static final int ER_NONEXISTING_TABLE_GRANT = 1147;
  
  public static final int ER_NOT_ALLOWED_COMMAND = 1148;
  
  public static final int ER_SYNTAX_ERROR = 1149;
  
  public static final int ER_DELAYED_CANT_CHANGE_LOCK = 1150;
  
  public static final int ER_TOO_MANY_DELAYED_THREADS = 1151;
  
  public static final int ER_ABORTING_CONNECTION = 1152;
  
  public static final int ER_NET_PACKET_TOO_LARGE = 1153;
  
  public static final int ER_NET_READ_ERROR_FROM_PIPE = 1154;
  
  public static final int ER_NET_FCNTL_ERROR = 1155;
  
  public static final int ER_NET_PACKETS_OUT_OF_ORDER = 1156;
  
  public static final int ER_NET_UNCOMPRESS_ERROR = 1157;
  
  public static final int ER_NET_READ_ERROR = 1158;
  
  public static final int ER_NET_READ_INTERRUPTED = 1159;
  
  public static final int ER_NET_ERROR_ON_WRITE = 1160;
  
  public static final int ER_NET_WRITE_INTERRUPTED = 1161;
  
  public static final int ER_TOO_LONG_STRING = 1162;
  
  public static final int ER_TABLE_CANT_HANDLE_BLOB = 1163;
  
  public static final int ER_TABLE_CANT_HANDLE_AUTO_INCREMENT = 1164;
  
  public static final int ER_DELAYED_INSERT_TABLE_LOCKED = 1165;
  
  public static final int ER_WRONG_COLUMN_NAME = 1166;
  
  public static final int ER_WRONG_KEY_COLUMN = 1167;
  
  public static final int ER_WRONG_MRG_TABLE = 1168;
  
  public static final int ER_DUP_UNIQUE = 1169;
  
  public static final int ER_BLOB_KEY_WITHOUT_LENGTH = 1170;
  
  public static final int ER_PRIMARY_CANT_HAVE_NULL = 1171;
  
  public static final int ER_TOO_MANY_ROWS = 1172;
  
  public static final int ER_REQUIRES_PRIMARY_KEY = 1173;
  
  public static final int ER_NO_RAID_COMPILED = 1174;
  
  public static final int ER_UPDATE_WITHOUT_KEY_IN_SAFE_MODE = 1175;
  
  public static final int ER_KEY_DOES_NOT_EXITS = 1176;
  
  public static final int ER_CHECK_NO_SUCH_TABLE = 1177;
  
  public static final int ER_CHECK_NOT_IMPLEMENTED = 1178;
  
  public static final int ER_CANT_DO_THIS_DURING_AN_TRANSACTION = 1179;
  
  public static final int ER_ERROR_DURING_COMMIT = 1180;
  
  public static final int ER_ERROR_DURING_ROLLBACK = 1181;
  
  public static final int ER_ERROR_DURING_FLUSH_LOGS = 1182;
  
  public static final int ER_ERROR_DURING_CHECKPOINT = 1183;
  
  public static final int ER_NEW_ABORTING_CONNECTION = 1184;
  
  public static final int ER_DUMP_NOT_IMPLEMENTED = 1185;
  
  @Deprecated
  public static final int ER_FLUSH_MASTER_BINLOG_CLOSED = 1186;
  
  public static final int ER_FLUSH_SOURCE_BINLOG_CLOSED = 1186;
  
  public static final int ER_INDEX_REBUILD = 1187;
  
  @Deprecated
  public static final int ER_MASTER = 1188;
  
  public static final int ER_SOURCE = 1188;
  
  @Deprecated
  public static final int ER_MASTER_NET_READ = 1189;
  
  public static final int ER_SOURCE_NET_READ = 1189;
  
  @Deprecated
  public static final int ER_MASTER_NET_WRITE = 1190;
  
  public static final int ER_SOURCE_NET_WRITE = 1190;
  
  public static final int ER_FT_MATCHING_KEY_NOT_FOUND = 1191;
  
  public static final int ER_LOCK_OR_ACTIVE_TRANSACTION = 1192;
  
  public static final int ER_UNKNOWN_SYSTEM_VARIABLE = 1193;
  
  public static final int ER_CRASHED_ON_USAGE = 1194;
  
  public static final int ER_CRASHED_ON_REPAIR = 1195;
  
  public static final int ER_WARNING_NOT_COMPLETE_ROLLBACK = 1196;
  
  public static final int ER_TRANS_CACHE_FULL = 1197;
  
  @Deprecated
  public static final int ER_SLAVE_MUST_STOP = 1198;
  
  public static final int ER_REPLICA_MUST_STOP = 1198;
  
  @Deprecated
  public static final int ER_SLAVE_NOT_RUNNING = 1199;
  
  public static final int ER_REPLICA_NOT_RUNNING = 1199;
  
  @Deprecated
  public static final int ER_BAD_SLAVE = 1200;
  
  public static final int ER_BAD_REPLICA = 1200;
  
  @Deprecated
  public static final int ER_MASTER_INFO = 1201;
  
  public static final int ER_SOURCE_INFO = 1201;
  
  @Deprecated
  public static final int ER_SLAVE_THREAD = 1202;
  
  public static final int ER_REPLICA_THREAD = 1202;
  
  public static final int ER_TOO_MANY_USER_CONNECTIONS = 1203;
  
  public static final int ER_SET_CONSTANTS_ONLY = 1204;
  
  public static final int ER_LOCK_WAIT_TIMEOUT = 1205;
  
  public static final int ER_LOCK_TABLE_FULL = 1206;
  
  public static final int ER_READ_ONLY_TRANSACTION = 1207;
  
  public static final int ER_DROP_DB_WITH_READ_LOCK = 1208;
  
  public static final int ER_CREATE_DB_WITH_READ_LOCK = 1209;
  
  public static final int ER_WRONG_ARGUMENTS = 1210;
  
  public static final int ER_NO_PERMISSION_TO_CREATE_USER = 1211;
  
  public static final int ER_UNION_TABLES_IN_DIFFERENT_DIR = 1212;
  
  public static final int ER_LOCK_DEADLOCK = 1213;
  
  public static final int ER_TABLE_CANT_HANDLE_FT = 1214;
  
  public static final int ER_CANNOT_ADD_FOREIGN = 1215;
  
  public static final int ER_NO_REFERENCED_ROW = 1216;
  
  public static final int ER_ROW_IS_REFERENCED = 1217;
  
  @Deprecated
  public static final int ER_CONNECT_TO_MASTER = 1218;
  
  public static final int ER_CONNECT_TO_SOURCE = 1218;
  
  @Deprecated
  public static final int ER_QUERY_ON_MASTER = 1219;
  
  public static final int ER_QUERY_ON_SOURCE = 1219;
  
  public static final int ER_ERROR_WHEN_EXECUTING_COMMAND = 1220;
  
  public static final int ER_WRONG_USAGE = 1221;
  
  public static final int ER_WRONG_NUMBER_OF_COLUMNS_IN_SELECT = 1222;
  
  public static final int ER_CANT_UPDATE_WITH_READLOCK = 1223;
  
  public static final int ER_MIXING_NOT_ALLOWED = 1224;
  
  public static final int ER_DUP_ARGUMENT = 1225;
  
  public static final int ER_USER_LIMIT_REACHED = 1226;
  
  public static final int ER_SPECIFIC_ACCESS_DENIED_ERROR = 1227;
  
  public static final int ER_LOCAL_VARIABLE = 1228;
  
  public static final int ER_GLOBAL_VARIABLE = 1229;
  
  public static final int ER_NO_DEFAULT = 1230;
  
  public static final int ER_WRONG_VALUE_FOR_VAR = 1231;
  
  public static final int ER_WRONG_TYPE_FOR_VAR = 1232;
  
  public static final int ER_VAR_CANT_BE_READ = 1233;
  
  public static final int ER_CANT_USE_OPTION_HERE = 1234;
  
  public static final int ER_NOT_SUPPORTED_YET = 1235;
  
  @Deprecated
  public static final int ER_MASTER_FATAL_ERROR_READING_BINLOG = 1236;
  
  public static final int ER_SOURCE_FATAL_ERROR_READING_BINLOG = 1236;
  
  @Deprecated
  public static final int ER_SLAVE_IGNORED_TABLE = 1237;
  
  public static final int ER_REPLICA_IGNORED_TABLE = 1237;
  
  public static final int ER_INCORRECT_GLOBAL_LOCAL_VAR = 1238;
  
  public static final int ER_WRONG_FK_DEF = 1239;
  
  public static final int ER_KEY_REF_DO_NOT_MATCH_TABLE_REF = 1240;
  
  public static final int ER_OPERAND_COLUMNS = 1241;
  
  public static final int ER_SUBQUERY_NO_1_ROW = 1242;
  
  public static final int ER_UNKNOWN_STMT_HANDLER = 1243;
  
  public static final int ER_CORRUPT_HELP_DB = 1244;
  
  public static final int ER_CYCLIC_REFERENCE = 1245;
  
  public static final int ER_AUTO_CONVERT = 1246;
  
  public static final int ER_ILLEGAL_REFERENCE = 1247;
  
  public static final int ER_DERIVED_MUST_HAVE_ALIAS = 1248;
  
  public static final int ER_SELECT_REDUCED = 1249;
  
  public static final int ER_TABLENAME_NOT_ALLOWED_HERE = 1250;
  
  public static final int ER_NOT_SUPPORTED_AUTH_MODE = 1251;
  
  public static final int ER_SPATIAL_CANT_HAVE_NULL = 1252;
  
  public static final int ER_COLLATION_CHARSET_MISMATCH = 1253;
  
  @Deprecated
  public static final int ER_SLAVE_WAS_RUNNING = 1254;
  
  public static final int ER_REPLICA_WAS_RUNNING = 1254;
  
  @Deprecated
  public static final int ER_SLAVE_WAS_NOT_RUNNING = 1255;
  
  public static final int ER_REPLICA_WAS_NOT_RUNNING = 1255;
  
  public static final int ER_TOO_BIG_FOR_UNCOMPRESS = 1256;
  
  public static final int ER_ZLIB_Z_MEM_ERROR = 1257;
  
  public static final int ER_ZLIB_Z_BUF_ERROR = 1258;
  
  public static final int ER_ZLIB_Z_DATA_ERROR = 1259;
  
  public static final int ER_CUT_VALUE_GROUP_CONCAT = 1260;
  
  public static final int ER_WARN_TOO_FEW_RECORDS = 1261;
  
  public static final int ER_WARN_TOO_MANY_RECORDS = 1262;
  
  public static final int ER_WARN_NULL_TO_NOTNULL = 1263;
  
  public static final int ER_WARN_DATA_OUT_OF_RANGE = 1264;
  
  public static final int ER_WARN_DATA_TRUNCATED = 1265;
  
  public static final int ER_WARN_USING_OTHER_HANDLER = 1266;
  
  public static final int ER_CANT_AGGREGATE_2COLLATIONS = 1267;
  
  public static final int ER_DROP_USER = 1268;
  
  public static final int ER_REVOKE_GRANTS = 1269;
  
  public static final int ER_CANT_AGGREGATE_3COLLATIONS = 1270;
  
  public static final int ER_CANT_AGGREGATE_NCOLLATIONS = 1271;
  
  public static final int ER_VARIABLE_IS_NOT_STRUCT = 1272;
  
  public static final int ER_UNKNOWN_COLLATION = 1273;
  
  @Deprecated
  public static final int ER_SLAVE_IGNORED_SSL_PARAMS = 1274;
  
  public static final int ER_REPLICA_IGNORED_SSL_PARAMS = 1274;
  
  public static final int ER_SERVER_IS_IN_SECURE_AUTH_MODE = 1275;
  
  public static final int ER_WARN_FIELD_RESOLVED = 1276;
  
  @Deprecated
  public static final int ER_BAD_SLAVE_UNTIL_COND = 1277;
  
  public static final int ER_BAD_REPLICA_UNTIL_COND = 1277;
  
  @Deprecated
  public static final int ER_MISSING_SKIP_SLAVE = 1278;
  
  public static final int ER_MISSING_SKIP_REPLICA = 1278;
  
  public static final int ER_UNTIL_COND_IGNORED = 1279;
  
  public static final int ER_WRONG_NAME_FOR_INDEX = 1280;
  
  public static final int ER_WRONG_NAME_FOR_CATALOG = 1281;
  
  public static final int ER_WARN_QC_RESIZE = 1282;
  
  public static final int ER_BAD_FT_COLUMN = 1283;
  
  public static final int ER_UNKNOWN_KEY_CACHE = 1284;
  
  public static final int ER_WARN_HOSTNAME_WONT_WORK = 1285;
  
  public static final int ER_UNKNOWN_STORAGE_ENGINE = 1286;
  
  public static final int ER_WARN_DEPRECATED_SYNTAX = 1287;
  
  public static final int ER_NON_UPDATABLE_TABLE = 1288;
  
  public static final int ER_FEATURE_DISABLED = 1289;
  
  public static final int ER_OPTION_PREVENTS_STATEMENT = 1290;
  
  public static final int ER_DUPLICATED_VALUE_IN_TYPE = 1291;
  
  public static final int ER_TRUNCATED_WRONG_VALUE = 1292;
  
  public static final int ER_TOO_MUCH_AUTO_TIMESTAMP_COLS = 1293;
  
  public static final int ER_INVALID_ON_UPDATE = 1294;
  
  public static final int ER_UNSUPPORTED_PS = 1295;
  
  public static final int ER_GET_ERRMSG = 1296;
  
  public static final int ER_GET_TEMPORARY_ERRMSG = 1297;
  
  public static final int ER_UNKNOWN_TIME_ZONE = 1298;
  
  public static final int ER_WARN_INVALID_TIMESTAMP = 1299;
  
  public static final int ER_INVALID_CHARACTER_STRING = 1300;
  
  public static final int ER_WARN_ALLOWED_PACKET_OVERFLOWED = 1301;
  
  public static final int ER_CONFLICTING_DECLARATIONS = 1302;
  
  public static final int ER_SP_NO_RECURSIVE_CREATE = 1303;
  
  public static final int ER_SP_ALREADY_EXISTS = 1304;
  
  public static final int ER_SP_DOES_NOT_EXIST = 1305;
  
  public static final int ER_SP_DROP_FAILED = 1306;
  
  public static final int ER_SP_STORE_FAILED = 1307;
  
  public static final int ER_SP_LILABEL_MISMATCH = 1308;
  
  public static final int ER_SP_LABEL_REDEFINE = 1309;
  
  public static final int ER_SP_LABEL_MISMATCH = 1310;
  
  public static final int ER_SP_UNINIT_VAR = 1311;
  
  public static final int ER_SP_BADSELECT = 1312;
  
  public static final int ER_SP_BADRETURN = 1313;
  
  public static final int ER_SP_BADSTATEMENT = 1314;
  
  public static final int ER_UPDATE_LOG_DEPRECATED_IGNORED = 1315;
  
  public static final int ER_UPDATE_LOG_DEPRECATED_TRANSLATED = 1316;
  
  public static final int ER_QUERY_INTERRUPTED = 1317;
  
  public static final int ER_SP_WRONG_NO_OF_ARGS = 1318;
  
  public static final int ER_SP_COND_MISMATCH = 1319;
  
  public static final int ER_SP_NORETURN = 1320;
  
  public static final int ER_SP_NORETURNEND = 1321;
  
  public static final int ER_SP_BAD_CURSOR_QUERY = 1322;
  
  public static final int ER_SP_BAD_CURSOR_SELECT = 1323;
  
  public static final int ER_SP_CURSOR_MISMATCH = 1324;
  
  public static final int ER_SP_CURSOR_ALREADY_OPEN = 1325;
  
  public static final int ER_SP_CURSOR_NOT_OPEN = 1326;
  
  public static final int ER_SP_UNDECLARED_VAR = 1327;
  
  public static final int ER_SP_WRONG_NO_OF_FETCH_ARGS = 1328;
  
  public static final int ER_SP_FETCH_NO_DATA = 1329;
  
  public static final int ER_SP_DUP_PARAM = 1330;
  
  public static final int ER_SP_DUP_VAR = 1331;
  
  public static final int ER_SP_DUP_COND = 1332;
  
  public static final int ER_SP_DUP_CURS = 1333;
  
  public static final int ER_SP_CANT_ALTER = 1334;
  
  public static final int ER_SP_SUBSELECT_NYI = 1335;
  
  public static final int ER_STMT_NOT_ALLOWED_IN_SF_OR_TRG = 1336;
  
  public static final int ER_SP_VARCOND_AFTER_CURSHNDLR = 1337;
  
  public static final int ER_SP_CURSOR_AFTER_HANDLER = 1338;
  
  public static final int ER_SP_CASE_NOT_FOUND = 1339;
  
  public static final int ER_FPARSER_TOO_BIG_FILE = 1340;
  
  public static final int ER_FPARSER_BAD_HEADER = 1341;
  
  public static final int ER_FPARSER_EOF_IN_COMMENT = 1342;
  
  public static final int ER_FPARSER_ERROR_IN_PARAMETER = 1343;
  
  public static final int ER_FPARSER_EOF_IN_UNKNOWN_PARAMETER = 1344;
  
  public static final int ER_VIEW_NO_EXPLAIN = 1345;
  
  public static final int ER_FRM_UNKNOWN_TYPE = 1346;
  
  public static final int ER_WRONG_OBJECT = 1347;
  
  public static final int ER_NONUPDATEABLE_COLUMN = 1348;
  
  public static final int ER_VIEW_SELECT_DERIVED = 1349;
  
  public static final int ER_VIEW_SELECT_CLAUSE = 1350;
  
  public static final int ER_VIEW_SELECT_VARIABLE = 1351;
  
  public static final int ER_VIEW_SELECT_TMPTABLE = 1352;
  
  public static final int ER_VIEW_WRONG_LIST = 1353;
  
  public static final int ER_WARN_VIEW_MERGE = 1354;
  
  public static final int ER_WARN_VIEW_WITHOUT_KEY = 1355;
  
  public static final int ER_VIEW_INVALID = 1356;
  
  public static final int ER_SP_NO_DROP_SP = 1357;
  
  public static final int ER_SP_GOTO_IN_HNDLR = 1358;
  
  public static final int ER_TRG_ALREADY_EXISTS = 1359;
  
  public static final int ER_TRG_DOES_NOT_EXIST = 1360;
  
  public static final int ER_TRG_ON_VIEW_OR_TEMP_TABLE = 1361;
  
  public static final int ER_TRG_CANT_CHANGE_ROW = 1362;
  
  public static final int ER_TRG_NO_SUCH_ROW_IN_TRG = 1363;
  
  public static final int ER_NO_DEFAULT_FOR_FIELD = 1364;
  
  public static final int ER_DIVISION_BY_ZERO = 1365;
  
  public static final int ER_TRUNCATED_WRONG_VALUE_FOR_FIELD = 1366;
  
  public static final int ER_ILLEGAL_VALUE_FOR_TYPE = 1367;
  
  public static final int ER_VIEW_NONUPD_CHECK = 1368;
  
  public static final int ER_VIEW_CHECK_FAILED = 1369;
  
  public static final int ER_PROCACCESS_DENIED_ERROR = 1370;
  
  public static final int ER_RELAY_LOG_FAIL = 1371;
  
  public static final int ER_PASSWD_LENGTH = 1372;
  
  public static final int ER_UNKNOWN_TARGET_BINLOG = 1373;
  
  public static final int ER_IO_ERR_LOG_INDEX_READ = 1374;
  
  public static final int ER_BINLOG_PURGE_PROHIBITED = 1375;
  
  public static final int ER_FSEEK_FAIL = 1376;
  
  public static final int ER_BINLOG_PURGE_FATAL_ERR = 1377;
  
  public static final int ER_LOG_IN_USE = 1378;
  
  public static final int ER_LOG_PURGE_UNKNOWN_ERR = 1379;
  
  public static final int ER_RELAY_LOG_INIT = 1380;
  
  public static final int ER_NO_BINARY_LOGGING = 1381;
  
  public static final int ER_RESERVED_SYNTAX = 1382;
  
  public static final int ER_WSAS_FAILED = 1383;
  
  public static final int ER_DIFF_GROUPS_PROC = 1384;
  
  public static final int ER_NO_GROUP_FOR_PROC = 1385;
  
  public static final int ER_ORDER_WITH_PROC = 1386;
  
  public static final int ER_LOGGING_PROHIBIT_CHANGING_OF = 1387;
  
  public static final int ER_NO_FILE_MAPPING = 1388;
  
  public static final int ER_WRONG_MAGIC = 1389;
  
  public static final int ER_PS_MANY_PARAM = 1390;
  
  public static final int ER_KEY_PART_0 = 1391;
  
  public static final int ER_VIEW_CHECKSUM = 1392;
  
  public static final int ER_VIEW_MULTIUPDATE = 1393;
  
  public static final int ER_VIEW_NO_INSERT_FIELD_LIST = 1394;
  
  public static final int ER_VIEW_DELETE_MERGE_VIEW = 1395;
  
  public static final int ER_CANNOT_USER = 1396;
  
  public static final int ER_XAER_NOTA = 1397;
  
  public static final int ER_XAER_INVAL = 1398;
  
  public static final int ER_XAER_RMFAIL = 1399;
  
  public static final int ER_XAER_OUTSIDE = 1400;
  
  public static final int ER_XA_RMERR = 1401;
  
  public static final int ER_XA_RBROLLBACK = 1402;
  
  public static final int ER_NONEXISTING_PROC_GRANT = 1403;
  
  public static final int ER_PROC_AUTO_GRANT_FAIL = 1404;
  
  public static final int ER_PROC_AUTO_REVOKE_FAIL = 1405;
  
  public static final int ER_DATA_TOO_LONG = 1406;
  
  public static final int ER_SP_BAD_SQLSTATE = 1407;
  
  public static final int ER_STARTUP = 1408;
  
  public static final int ER_LOAD_FROM_FIXED_SIZE_ROWS_TO_VAR = 1409;
  
  public static final int ER_CANT_CREATE_USER_WITH_GRANT = 1410;
  
  public static final int ER_WRONG_VALUE_FOR_TYPE = 1411;
  
  public static final int ER_TABLE_DEF_CHANGED = 1412;
  
  public static final int ER_SP_DUP_HANDLER = 1413;
  
  public static final int ER_SP_NOT_VAR_ARG = 1414;
  
  public static final int ER_SP_NO_RETSET = 1415;
  
  public static final int ER_CANT_CREATE_GEOMETRY_OBJECT = 1416;
  
  public static final int ER_FAILED_ROUTINE_BREAK_BINLOG = 1417;
  
  public static final int ER_BINLOG_UNSAFE_ROUTINE = 1418;
  
  public static final int ER_BINLOG_CREATE_ROUTINE_NEED_SUPER = 1419;
  
  public static final int ER_EXEC_STMT_WITH_OPEN_CURSOR = 1420;
  
  public static final int ER_STMT_HAS_NO_OPEN_CURSOR = 1421;
  
  public static final int ER_COMMIT_NOT_ALLOWED_IN_SF_OR_TRG = 1422;
  
  public static final int ER_NO_DEFAULT_FOR_VIEW_FIELD = 1423;
  
  public static final int ER_SP_NO_RECURSION = 1424;
  
  public static final int ER_TOO_BIG_SCALE = 1425;
  
  public static final int ER_TOO_BIG_PRECISION = 1426;
  
  public static final int ER_M_BIGGER_THAN_D = 1427;
  
  public static final int ER_WRONG_LOCK_OF_SYSTEM_TABLE = 1428;
  
  public static final int ER_CONNECT_TO_FOREIGN_DATA_SOURCE = 1429;
  
  public static final int ER_QUERY_ON_FOREIGN_DATA_SOURCE = 1430;
  
  public static final int ER_FOREIGN_DATA_SOURCE_DOESNT_EXIST = 1431;
  
  public static final int ER_FOREIGN_DATA_STRING_INVALID_CANT_CREATE = 1432;
  
  public static final int ER_FOREIGN_DATA_STRING_INVALID = 1433;
  
  public static final int ER_CANT_CREATE_FEDERATED_TABLE = 1434;
  
  public static final int ER_TRG_IN_WRONG_SCHEMA = 1435;
  
  public static final int ER_STACK_OVERRUN_NEED_MORE = 1436;
  
  public static final int ER_TOO_LONG_BODY = 1437;
  
  public static final int ER_WARN_CANT_DROP_DEFAULT_KEYCACHE = 1438;
  
  public static final int ER_TOO_BIG_DISPLAYWIDTH = 1439;
  
  public static final int ER_XAER_DUPID = 1440;
  
  public static final int ER_DATETIME_FUNCTION_OVERFLOW = 1441;
  
  public static final int ER_CANT_UPDATE_USED_TABLE_IN_SF_OR_TRG = 1442;
  
  public static final int ER_VIEW_PREVENT_UPDATE = 1443;
  
  public static final int ER_PS_NO_RECURSION = 1444;
  
  public static final int ER_SP_CANT_SET_AUTOCOMMIT = 1445;
  
  public static final int ER_MALFORMED_DEFINER = 1446;
  
  public static final int ER_VIEW_FRM_NO_USER = 1447;
  
  public static final int ER_VIEW_OTHER_USER = 1448;
  
  public static final int ER_NO_SUCH_USER = 1449;
  
  public static final int ER_FORBID_SCHEMA_CHANGE = 1450;
  
  public static final int ER_ROW_IS_REFERENCED_2 = 1451;
  
  public static final int ER_NO_REFERENCED_ROW_2 = 1452;
  
  public static final int ER_SP_BAD_VAR_SHADOW = 1453;
  
  public static final int ER_TRG_NO_DEFINER = 1454;
  
  public static final int ER_OLD_FILE_FORMAT = 1455;
  
  public static final int ER_SP_RECURSION_LIMIT = 1456;
  
  public static final int ER_SP_PROC_TABLE_CORRUPT = 1457;
  
  public static final int ER_SP_WRONG_NAME = 1458;
  
  public static final int ER_TABLE_NEEDS_UPGRADE = 1459;
  
  public static final int ER_SP_NO_AGGREGATE = 1460;
  
  public static final int ER_MAX_PREPARED_STMT_COUNT_REACHED = 1461;
  
  public static final int ER_VIEW_RECURSIVE = 1462;
  
  public static final int ER_NON_GROUPING_FIELD_USED = 1463;
  
  public static final int ER_TABLE_CANT_HANDLE_SPKEYS = 1464;
  
  public static final int ER_NO_TRIGGERS_ON_SYSTEM_SCHEMA = 1465;
  
  public static final int ER_REMOVED_SPACES = 1466;
  
  public static final int ER_AUTOINC_READ_FAILED = 1467;
  
  public static final int ER_USERNAME = 1468;
  
  public static final int ER_HOSTNAME = 1469;
  
  public static final int ER_WRONG_STRING_LENGTH = 1470;
  
  public static final int ER_NON_INSERTABLE_TABLE = 1471;
  
  public static final int ER_ADMIN_WRONG_MRG_TABLE = 1472;
  
  public static final int ER_TOO_HIGH_LEVEL_OF_NESTING_FOR_SELECT = 1473;
  
  public static final int ER_NAME_BECOMES_EMPTY = 1474;
  
  public static final int ER_AMBIGUOUS_FIELD_TERM = 1475;
  
  public static final int ER_FOREIGN_SERVER_EXISTS = 1476;
  
  public static final int ER_FOREIGN_SERVER_DOESNT_EXIST = 1477;
  
  public static final int ER_ILLEGAL_HA_CREATE_OPTION = 1478;
  
  public static final int ER_PARTITION_REQUIRES_VALUES_ERROR = 1479;
  
  public static final int ER_PARTITION_WRONG_VALUES_ERROR = 1480;
  
  public static final int ER_PARTITION_MAXVALUE_ERROR = 1481;
  
  public static final int ER_PARTITION_SUBPARTITION_ERROR = 1482;
  
  public static final int ER_PARTITION_SUBPART_MIX_ERROR = 1483;
  
  public static final int ER_PARTITION_WRONG_NO_PART_ERROR = 1484;
  
  public static final int ER_PARTITION_WRONG_NO_SUBPART_ERROR = 1485;
  
  public static final int ER_WRONG_EXPR_IN_PARTITION_FUNC_ERROR = 1486;
  
  public static final int ER_NO_CONST_EXPR_IN_RANGE_OR_LIST_ERROR = 1487;
  
  public static final int ER_FIELD_NOT_FOUND_PART_ERROR = 1488;
  
  public static final int ER_LIST_OF_FIELDS_ONLY_IN_HASH_ERROR = 1489;
  
  public static final int ER_INCONSISTENT_PARTITION_INFO_ERROR = 1490;
  
  public static final int ER_PARTITION_FUNC_NOT_ALLOWED_ERROR = 1491;
  
  public static final int ER_PARTITIONS_MUST_BE_DEFINED_ERROR = 1492;
  
  public static final int ER_RANGE_NOT_INCREASING_ERROR = 1493;
  
  public static final int ER_INCONSISTENT_TYPE_OF_FUNCTIONS_ERROR = 1494;
  
  public static final int ER_MULTIPLE_DEF_CONST_IN_LIST_PART_ERROR = 1495;
  
  public static final int ER_PARTITION_ENTRY_ERROR = 1496;
  
  public static final int ER_MIX_HANDLER_ERROR = 1497;
  
  public static final int ER_PARTITION_NOT_DEFINED_ERROR = 1498;
  
  public static final int ER_TOO_MANY_PARTITIONS_ERROR = 1499;
  
  public static final int ER_SUBPARTITION_ERROR = 1500;
  
  public static final int ER_CANT_CREATE_HANDLER_FILE = 1501;
  
  public static final int ER_BLOB_FIELD_IN_PART_FUNC_ERROR = 1502;
  
  public static final int ER_UNIQUE_KEY_NEED_ALL_FIELDS_IN_PF = 1503;
  
  public static final int ER_NO_PARTS_ERROR = 1504;
  
  public static final int ER_PARTITION_MGMT_ON_NONPARTITIONED = 1505;
  
  public static final int ER_FOREIGN_KEY_ON_PARTITIONED = 1506;
  
  public static final int ER_DROP_PARTITION_NON_EXISTENT = 1507;
  
  public static final int ER_DROP_LAST_PARTITION = 1508;
  
  public static final int ER_COALESCE_ONLY_ON_HASH_PARTITION = 1509;
  
  public static final int ER_REORG_HASH_ONLY_ON_SAME_NO = 1510;
  
  public static final int ER_REORG_NO_PARAM_ERROR = 1511;
  
  public static final int ER_ONLY_ON_RANGE_LIST_PARTITION = 1512;
  
  public static final int ER_ADD_PARTITION_SUBPART_ERROR = 1513;
  
  public static final int ER_ADD_PARTITION_NO_NEW_PARTITION = 1514;
  
  public static final int ER_COALESCE_PARTITION_NO_PARTITION = 1515;
  
  public static final int ER_REORG_PARTITION_NOT_EXIST = 1516;
  
  public static final int ER_SAME_NAME_PARTITION = 1517;
  
  public static final int ER_NO_BINLOG_ERROR = 1518;
  
  public static final int ER_CONSECUTIVE_REORG_PARTITIONS = 1519;
  
  public static final int ER_REORG_OUTSIDE_RANGE = 1520;
  
  public static final int ER_PARTITION_FUNCTION_FAILURE = 1521;
  
  public static final int ER_PART_STATE_ERROR = 1522;
  
  public static final int ER_LIMITED_PART_RANGE = 1523;
  
  public static final int ER_PLUGIN_IS_NOT_LOADED = 1524;
  
  public static final int ER_WRONG_VALUE = 1525;
  
  public static final int ER_NO_PARTITION_FOR_GIVEN_VALUE = 1526;
  
  public static final int ER_FILEGROUP_OPTION_ONLY_ONCE = 1527;
  
  public static final int ER_CREATE_FILEGROUP_FAILED = 1528;
  
  public static final int ER_DROP_FILEGROUP_FAILED = 1529;
  
  public static final int ER_TABLESPACE_AUTO_EXTEND_ERROR = 1530;
  
  public static final int ER_WRONG_SIZE_NUMBER = 1531;
  
  public static final int ER_SIZE_OVERFLOW_ERROR = 1532;
  
  public static final int ER_ALTER_FILEGROUP_FAILED = 1533;
  
  public static final int ER_BINLOG_ROW_LOGGING_FAILED = 1534;
  
  public static final int ER_BINLOG_ROW_WRONG_TABLE_DEF = 1535;
  
  public static final int ER_BINLOG_ROW_RBR_TO_SBR = 1536;
  
  public static final int ER_EVENT_ALREADY_EXISTS = 1537;
  
  public static final int ER_EVENT_STORE_FAILED = 1538;
  
  public static final int ER_EVENT_DOES_NOT_EXIST = 1539;
  
  public static final int ER_EVENT_CANT_ALTER = 1540;
  
  public static final int ER_EVENT_DROP_FAILED = 1541;
  
  public static final int ER_EVENT_INTERVAL_NOT_POSITIVE_OR_TOO_BIG = 1542;
  
  public static final int ER_EVENT_ENDS_BEFORE_STARTS = 1543;
  
  public static final int ER_EVENT_EXEC_TIME_IN_THE_PAST = 1544;
  
  public static final int ER_EVENT_OPEN_TABLE_FAILED = 1545;
  
  public static final int ER_EVENT_NEITHER_M_EXPR_NOR_M_AT = 1546;
  
  public static final int ER_COL_COUNT_DOESNT_MATCH_CORRUPTED = 1547;
  
  public static final int ER_CANNOT_LOAD_FROM_TABLE = 1548;
  
  public static final int ER_EVENT_CANNOT_DELETE = 1549;
  
  public static final int ER_EVENT_COMPILE_ERROR = 1550;
  
  public static final int ER_EVENT_SAME_NAME = 1551;
  
  public static final int ER_EVENT_DATA_TOO_LONG = 1552;
  
  public static final int ER_DROP_INDEX_FK = 1553;
  
  public static final int ER_WARN_DEPRECATED_SYNTAX_WITH_VER = 1554;
  
  public static final int ER_CANT_WRITE_LOCK_LOG_TABLE = 1555;
  
  public static final int ER_CANT_LOCK_LOG_TABLE = 1556;
  
  public static final int ER_FOREIGN_DUPLICATE_KEY = 1557;
  
  public static final int ER_COL_COUNT_DOESNT_MATCH_PLEASE_UPDATE = 1558;
  
  public static final int ER_TEMP_TABLE_PREVENTS_SWITCH_OUT_OF_RBR = 1559;
  
  public static final int ER_STORED_FUNCTION_PREVENTS_SWITCH_BINLOG_FORMAT = 1560;
  
  public static final int ER_NDB_CANT_SWITCH_BINLOG_FORMAT = 1561;
  
  public static final int ER_PARTITION_NO_TEMPORARY = 1562;
  
  public static final int ER_PARTITION_CONST_DOMAIN_ERROR = 1563;
  
  public static final int ER_PARTITION_FUNCTION_IS_NOT_ALLOWED = 1564;
  
  public static final int ER_DDL_LOG_ERROR = 1565;
  
  public static final int ER_NULL_IN_VALUES_LESS_THAN = 1566;
  
  public static final int ER_WRONG_PARTITION_NAME = 1567;
  
  public static final int ER_CANT_CHANGE_TX_ISOLATION = 1568;
  
  public static final int ER_DUP_ENTRY_AUTOINCREMENT_CASE = 1569;
  
  public static final int ER_EVENT_MODIFY_QUEUE_ERROR = 1570;
  
  public static final int ER_EVENT_SET_VAR_ERROR = 1571;
  
  public static final int ER_PARTITION_MERGE_ERROR = 1572;
  
  public static final int ER_CANT_ACTIVATE_LOG = 1573;
  
  public static final int ER_RBR_NOT_AVAILABLE = 1574;
  
  public static final int ER_BASE64_DECODE_ERROR = 1575;
  
  public static final int ER_EVENT_RECURSION_FORBIDDEN = 1576;
  
  public static final int ER_EVENTS_DB_ERROR = 1577;
  
  public static final int ER_ONLY_INTEGERS_ALLOWED = 1578;
  
  public static final int ER_UNSUPORTED_LOG_ENGINE = 1579;
  
  public static final int ER_BAD_LOG_STATEMENT = 1580;
  
  public static final int ER_CANT_RENAME_LOG_TABLE = 1581;
  
  public static final int ER_WRONG_PARAMCOUNT_TO_NATIVE_FCT = 1582;
  
  public static final int ER_WRONG_PARAMETERS_TO_NATIVE_FCT = 1583;
  
  public static final int ER_WRONG_PARAMETERS_TO_STORED_FCT = 1584;
  
  public static final int ER_NATIVE_FCT_NAME_COLLISION = 1585;
  
  public static final int ER_DUP_ENTRY_WITH_KEY_NAME = 1586;
  
  public static final int ER_BINLOG_PURGE_EMFILE = 1587;
  
  public static final int ER_EVENT_CANNOT_CREATE_IN_THE_PAST = 1588;
  
  public static final int ER_EVENT_CANNOT_ALTER_IN_THE_PAST = 1589;
  
  @Deprecated
  public static final int ER_SLAVE_INCIDENT = 1590;
  
  public static final int ER_REPLICA_INCIDENT = 1590;
  
  public static final int ER_NO_PARTITION_FOR_GIVEN_VALUE_SILENT = 1591;
  
  public static final int ER_BINLOG_UNSAFE_STATEMENT = 1592;
  
  @Deprecated
  public static final int ER_SLAVE_FATAL_ERROR = 1593;
  
  public static final int ER_REPLICA_FATAL_ERROR = 1593;
  
  @Deprecated
  public static final int ER_SLAVE_RELAY_LOG_READ_FAILURE = 1594;
  
  public static final int ER_REPLICA_RELAY_LOG_READ_FAILURE = 1594;
  
  @Deprecated
  public static final int ER_SLAVE_RELAY_LOG_WRITE_FAILURE = 1595;
  
  public static final int ER_REPLICA_RELAY_LOG_WRITE_FAILURE = 1595;
  
  @Deprecated
  public static final int ER_SLAVE_CREATE_EVENT_FAILURE = 1596;
  
  public static final int ER_REPLICA_CREATE_EVENT_FAILURE = 1596;
  
  @Deprecated
  public static final int ER_SLAVE_MASTER_COM_FAILURE = 1597;
  
  public static final int ER_REPLICA_SOURCE_COM_FAILURE = 1597;
  
  public static final int ER_BINLOG_LOGGING_IMPOSSIBLE = 1598;
  
  public static final int ER_VIEW_NO_CREATION_CTX = 1599;
  
  public static final int ER_VIEW_INVALID_CREATION_CTX = 1600;
  
  public static final int ER_SR_INVALID_CREATION_CTX = 1601;
  
  public static final int ER_TRG_CORRUPTED_FILE = 1602;
  
  public static final int ER_TRG_NO_CREATION_CTX = 1603;
  
  public static final int ER_TRG_INVALID_CREATION_CTX = 1604;
  
  public static final int ER_EVENT_INVALID_CREATION_CTX = 1605;
  
  public static final int ER_TRG_CANT_OPEN_TABLE = 1606;
  
  public static final int ER_CANT_CREATE_SROUTINE = 1607;
  
  public static final int ER_NEVER_USED = 1608;
  
  public static final int ER_NO_FORMAT_DESCRIPTION_EVENT_BEFORE_BINLOG_STATEMENT = 1609;
  
  @Deprecated
  public static final int ER_SLAVE_CORRUPT_EVENT = 1610;
  
  public static final int ER_REPLICA_CORRUPT_EVENT = 1610;
  
  public static final int ER_LOAD_DATA_INVALID_COLUMN = 1611;
  
  public static final int ER_LOG_PURGE_NO_FILE = 1612;
  
  public static final int ER_XA_RBTIMEOUT = 1613;
  
  public static final int ER_XA_RBDEADLOCK = 1614;
  
  public static final int ER_NEED_REPREPARE = 1615;
  
  public static final int ER_DELAYED_NOT_SUPPORTED = 1616;
  
  @Deprecated
  public static final int WARN_NO_MASTER_INFO = 1617;
  
  public static final int WARN_NO_SOURCE_INFO = 1617;
  
  public static final int WARN_OPTION_IGNORED = 1618;
  
  public static final int WARN_PLUGIN_DELETE_BUILTIN = 1619;
  
  public static final int WARN_PLUGIN_BUSY = 1620;
  
  public static final int ER_VARIABLE_IS_READONLY = 1621;
  
  public static final int ER_WARN_ENGINE_TRANSACTION_ROLLBACK = 1622;
  
  @Deprecated
  public static final int ER_SLAVE_HEARTBEAT_FAILURE = 1623;
  
  public static final int ER_REPLICA_HEARTBEAT_FAILURE = 1623;
  
  @Deprecated
  public static final int ER_SLAVE_HEARTBEAT_VALUE_OUT_OF_RANGE = 1624;
  
  public static final int ER_REPLICA_HEARTBEAT_VALUE_OUT_OF_RANGE = 1624;
  
  public static final int ER_NDB_REPLICATION_SCHEMA_ERROR = 1625;
  
  public static final int ER_CONFLICT_FN_PARSE_ERROR = 1626;
  
  public static final int ER_EXCEPTIONS_WRITE_ERROR = 1627;
  
  public static final int ER_TOO_LONG_TABLE_COMMENT = 1628;
  
  public static final int ER_TOO_LONG_FIELD_COMMENT = 1629;
  
  public static final int ER_FUNC_INEXISTENT_NAME_COLLISION = 1630;
  
  public static final int ER_DATABASE_NAME = 1631;
  
  public static final int ER_TABLE_NAME = 1632;
  
  public static final int ER_PARTITION_NAME = 1633;
  
  public static final int ER_SUBPARTITION_NAME = 1634;
  
  public static final int ER_TEMPORARY_NAME = 1635;
  
  public static final int ER_RENAMED_NAME = 1636;
  
  public static final int ER_TOO_MANY_CONCURRENT_TRXS = 1637;
  
  public static final int WARN_NON_ASCII_SEPARATOR_NOT_IMPLEMENTED = 1638;
  
  public static final int ER_DEBUG_SYNC_TIMEOUT = 1639;
  
  public static final int ER_DEBUG_SYNC_HIT_LIMIT = 1640;
  
  public static final int ER_DUP_SIGNAL_SET = 1641;
  
  public static final int ER_SIGNAL_WARN = 1642;
  
  public static final int ER_SIGNAL_NOT_FOUND = 1643;
  
  public static final int ER_SIGNAL_EXCEPTION = 1644;
  
  public static final int ER_RESIGNAL_WITHOUT_ACTIVE_HANDLER = 1645;
  
  public static final int ER_SIGNAL_BAD_CONDITION_TYPE = 1646;
  
  public static final int WARN_COND_ITEM_TRUNCATED = 1647;
  
  public static final int ER_COND_ITEM_TOO_LONG = 1648;
  
  public static final int ER_UNKNOWN_LOCALE = 1649;
  
  @Deprecated
  public static final int ER_SLAVE_IGNORE_SERVER_IDS = 1650;
  
  public static final int ER_REPLICA_IGNORE_SERVER_IDS = 1650;
  
  public static final int ER_QUERY_CACHE_DISABLED = 1651;
  
  public static final int ER_SAME_NAME_PARTITION_FIELD = 1652;
  
  public static final int ER_PARTITION_COLUMN_LIST_ERROR = 1653;
  
  public static final int ER_WRONG_TYPE_COLUMN_VALUE_ERROR = 1654;
  
  public static final int ER_TOO_MANY_PARTITION_FUNC_FIELDS_ERROR = 1655;
  
  public static final int ER_MAXVALUE_IN_VALUES_IN = 1656;
  
  public static final int ER_TOO_MANY_VALUES_ERROR = 1657;
  
  public static final int ER_ROW_SINGLE_PARTITION_FIELD_ERROR = 1658;
  
  public static final int ER_FIELD_TYPE_NOT_ALLOWED_AS_PARTITION_FIELD = 1659;
  
  public static final int ER_PARTITION_FIELDS_TOO_LONG = 1660;
  
  public static final int ER_BINLOG_ROW_ENGINE_AND_STMT_ENGINE = 1661;
  
  public static final int ER_BINLOG_ROW_MODE_AND_STMT_ENGINE = 1662;
  
  public static final int ER_BINLOG_UNSAFE_AND_STMT_ENGINE = 1663;
  
  public static final int ER_BINLOG_ROW_INJECTION_AND_STMT_ENGINE = 1664;
  
  public static final int ER_BINLOG_STMT_MODE_AND_ROW_ENGINE = 1665;
  
  public static final int ER_BINLOG_ROW_INJECTION_AND_STMT_MODE = 1666;
  
  public static final int ER_BINLOG_MULTIPLE_ENGINES_AND_SELF_LOGGING_ENGINE = 1667;
  
  public static final int ER_BINLOG_UNSAFE_LIMIT = 1668;
  
  public static final int ER_BINLOG_UNSAFE_INSERT_DELAYED = 1669;
  
  public static final int ER_BINLOG_UNSAFE_SYSTEM_TABLE = 1670;
  
  public static final int ER_BINLOG_UNSAFE_AUTOINC_COLUMNS = 1671;
  
  public static final int ER_BINLOG_UNSAFE_UDF = 1672;
  
  public static final int ER_BINLOG_UNSAFE_SYSTEM_VARIABLE = 1673;
  
  public static final int ER_BINLOG_UNSAFE_SYSTEM_FUNCTION = 1674;
  
  public static final int ER_BINLOG_UNSAFE_NONTRANS_AFTER_TRANS = 1675;
  
  public static final int ER_MESSAGE_AND_STATEMENT = 1676;
  
  @Deprecated
  public static final int ER_SLAVE_CONVERSION_FAILED = 1677;
  
  public static final int ER_REPLICA_CONVERSION_FAILED = 1677;
  
  @Deprecated
  public static final int ER_SLAVE_CANT_CREATE_CONVERSION = 1678;
  
  public static final int ER_REPLICA_CANT_CREATE_CONVERSION = 1678;
  
  public static final int ER_INSIDE_TRANSACTION_PREVENTS_SWITCH_BINLOG_FORMAT = 1679;
  
  public static final int ER_PATH_LENGTH = 1680;
  
  public static final int ER_WARN_DEPRECATED_SYNTAX_NO_REPLACEMENT = 1681;
  
  public static final int ER_WRONG_NATIVE_TABLE_STRUCTURE = 1682;
  
  public static final int ER_WRONG_PERFSCHEMA_USAGE = 1683;
  
  public static final int ER_WARN_I_S_SKIPPED_TABLE = 1684;
  
  public static final int ER_INSIDE_TRANSACTION_PREVENTS_SWITCH_BINLOG_DIRECT = 1685;
  
  public static final int ER_STORED_FUNCTION_PREVENTS_SWITCH_BINLOG_DIRECT = 1686;
  
  public static final int ER_SPATIAL_MUST_HAVE_GEOM_COL = 1687;
  
  public static final int ER_TOO_LONG_INDEX_COMMENT = 1688;
  
  public static final int ER_LOCK_ABORTED = 1689;
  
  public static final int ER_DATA_OUT_OF_RANGE = 1690;
  
  public static final int ER_WRONG_SPVAR_TYPE_IN_LIMIT = 1691;
  
  public static final int ER_BINLOG_UNSAFE_MULTIPLE_ENGINES_AND_SELF_LOGGING_ENGINE = 1692;
  
  public static final int ER_BINLOG_UNSAFE_MIXED_STATEMENT = 1693;
  
  public static final int ER_INSIDE_TRANSACTION_PREVENTS_SWITCH_SQL_LOG_BIN = 1694;
  
  public static final int ER_STORED_FUNCTION_PREVENTS_SWITCH_SQL_LOG_BIN = 1695;
  
  public static final int ER_FAILED_READ_FROM_PAR_FILE = 1696;
  
  public static final int ER_VALUES_IS_NOT_INT_TYPE_ERROR = 1697;
  
  public static final int ER_ACCESS_DENIED_NO_PASSWORD_ERROR = 1698;
  
  public static final int ER_SET_PASSWORD_AUTH_PLUGIN = 1699;
  
  public static final int ER_GRANT_PLUGIN_USER_EXISTS = 1700;
  
  public static final int ER_TRUNCATE_ILLEGAL_FK = 1701;
  
  public static final int ER_PLUGIN_IS_PERMANENT = 1702;
  
  @Deprecated
  public static final int ER_SLAVE_HEARTBEAT_VALUE_OUT_OF_RANGE_MIN = 1703;
  
  public static final int ER_REPLICA_HEARTBEAT_VALUE_OUT_OF_RANGE_MIN = 1703;
  
  @Deprecated
  public static final int ER_SLAVE_HEARTBEAT_VALUE_OUT_OF_RANGE_MAX = 1704;
  
  public static final int ER_REPLICA_HEARTBEAT_VALUE_OUT_OF_RANGE_MAX = 1704;
  
  public static final int ER_STMT_CACHE_FULL = 1705;
  
  public static final int ER_MULTI_UPDATE_KEY_CONFLICT = 1706;
  
  public static final int ER_TABLE_NEEDS_REBUILD = 1707;
  
  public static final int WARN_OPTION_BELOW_LIMIT = 1708;
  
  public static final int ER_INDEX_COLUMN_TOO_LONG = 1709;
  
  public static final int ER_ERROR_IN_TRIGGER_BODY = 1710;
  
  public static final int ER_ERROR_IN_UNKNOWN_TRIGGER_BODY = 1711;
  
  public static final int ER_INDEX_CORRUPT = 1712;
  
  public static final int ER_UNDO_RECORD_TOO_BIG = 1713;
  
  public static final int ER_BINLOG_UNSAFE_INSERT_IGNORE_SELECT = 1714;
  
  public static final int ER_BINLOG_UNSAFE_INSERT_SELECT_UPDATE = 1715;
  
  public static final int ER_BINLOG_UNSAFE_REPLACE_SELECT = 1716;
  
  public static final int ER_BINLOG_UNSAFE_CREATE_IGNORE_SELECT = 1717;
  
  public static final int ER_BINLOG_UNSAFE_CREATE_REPLACE_SELECT = 1718;
  
  public static final int ER_BINLOG_UNSAFE_UPDATE_IGNORE = 1719;
  
  public static final int ER_PLUGIN_NO_UNINSTALL = 1720;
  
  public static final int ER_PLUGIN_NO_INSTALL = 1721;
  
  public static final int ER_BINLOG_UNSAFE_WRITE_AUTOINC_SELECT = 1722;
  
  public static final int ER_BINLOG_UNSAFE_CREATE_SELECT_AUTOINC = 1723;
  
  public static final int ER_BINLOG_UNSAFE_INSERT_TWO_KEYS = 1724;
  
  public static final int ER_TABLE_IN_FK_CHECK = 1725;
  
  public static final int ER_UNSUPPORTED_ENGINE = 1726;
  
  public static final int ER_BINLOG_UNSAFE_AUTOINC_NOT_FIRST = 1727;
  
  public static final int ER_CANNOT_LOAD_FROM_TABLE_V2 = 1728;
  
  @Deprecated
  public static final int ER_MASTER_DELAY_VALUE_OUT_OF_RANGE = 1729;
  
  public static final int ER_SOURCE_DELAY_VALUE_OUT_OF_RANGE = 1729;
  
  public static final int ER_ONLY_FD_AND_RBR_EVENTS_ALLOWED_IN_BINLOG_STATEMENT = 1730;
  
  public static final int ER_PARTITION_EXCHANGE_DIFFERENT_OPTION = 1731;
  
  public static final int ER_PARTITION_EXCHANGE_PART_TABLE = 1732;
  
  public static final int ER_PARTITION_EXCHANGE_TEMP_TABLE = 1733;
  
  public static final int ER_PARTITION_INSTEAD_OF_SUBPARTITION = 1734;
  
  public static final int ER_UNKNOWN_PARTITION = 1735;
  
  public static final int ER_TABLES_DIFFERENT_METADATA = 1736;
  
  public static final int ER_ROW_DOES_NOT_MATCH_PARTITION = 1737;
  
  public static final int ER_BINLOG_CACHE_SIZE_GREATER_THAN_MAX = 1738;
  
  public static final int ER_WARN_INDEX_NOT_APPLICABLE = 1739;
  
  public static final int ER_PARTITION_EXCHANGE_FOREIGN_KEY = 1740;
  
  public static final int ER_NO_SUCH_KEY_VALUE = 1741;
  
  public static final int ER_RPL_INFO_DATA_TOO_LONG = 1742;
  
  public static final int ER_NETWORK_READ_EVENT_CHECKSUM_FAILURE = 1743;
  
  public static final int ER_BINLOG_READ_EVENT_CHECKSUM_FAILURE = 1744;
  
  public static final int ER_BINLOG_STMT_CACHE_SIZE_GREATER_THAN_MAX = 1745;
  
  public static final int ER_CANT_UPDATE_TABLE_IN_CREATE_TABLE_SELECT = 1746;
  
  public static final int ER_PARTITION_CLAUSE_ON_NONPARTITIONED = 1747;
  
  public static final int ER_ROW_DOES_NOT_MATCH_GIVEN_PARTITION_SET = 1748;
  
  public static final int ER_NO_SUCH_PARTITION__UNUSED = 1749;
  
  public static final int ER_CHANGE_RPL_INFO_REPOSITORY_FAILURE = 1750;
  
  public static final int ER_WARNING_NOT_COMPLETE_ROLLBACK_WITH_CREATED_TEMP_TABLE = 1751;
  
  public static final int ER_WARNING_NOT_COMPLETE_ROLLBACK_WITH_DROPPED_TEMP_TABLE = 1752;
  
  public static final int ER_MTS_FEATURE_IS_NOT_SUPPORTED = 1753;
  
  public static final int ER_MTS_UPDATED_DBS_GREATER_MAX = 1754;
  
  public static final int ER_MTS_CANT_PARALLEL = 1755;
  
  public static final int ER_MTS_INCONSISTENT_DATA = 1756;
  
  public static final int ER_FULLTEXT_NOT_SUPPORTED_WITH_PARTITIONING = 1757;
  
  public static final int ER_DA_INVALID_CONDITION_NUMBER = 1758;
  
  public static final int ER_INSECURE_PLAIN_TEXT = 1759;
  
  @Deprecated
  public static final int ER_INSECURE_CHANGE_MASTER = 1760;
  
  public static final int ER_INSECURE_CHANGE_SOURCE = 1760;
  
  public static final int ER_FOREIGN_DUPLICATE_KEY_WITH_CHILD_INFO = 1761;
  
  public static final int ER_FOREIGN_DUPLICATE_KEY_WITHOUT_CHILD_INFO = 1762;
  
  @Deprecated
  public static final int ER_SQLTHREAD_WITH_SECURE_SLAVE = 1763;
  
  public static final int ER_SQLTHREAD_WITH_SECURE_REPLICA = 1763;
  
  public static final int ER_TABLE_HAS_NO_FT = 1764;
  
  public static final int ER_VARIABLE_NOT_SETTABLE_IN_SF_OR_TRIGGER = 1765;
  
  public static final int ER_VARIABLE_NOT_SETTABLE_IN_TRANSACTION = 1766;
  
  public static final int ER_GTID_NEXT_IS_NOT_IN_GTID_NEXT_LIST = 1767;
  
  public static final int ER_CANT_CHANGE_GTID_NEXT_IN_TRANSACTION_WHEN_GTID_NEXT_LIST_IS_NULL = 1768;
  
  public static final int ER_SET_STATEMENT_CANNOT_INVOKE_FUNCTION = 1769;
  
  public static final int ER_GTID_NEXT_CANT_BE_AUTOMATIC_IF_GTID_NEXT_LIST_IS_NON_NULL = 1770;
  
  public static final int ER_SKIPPING_LOGGED_TRANSACTION = 1771;
  
  public static final int ER_MALFORMED_GTID_SET_SPECIFICATION = 1772;
  
  public static final int ER_MALFORMED_GTID_SET_ENCODING = 1773;
  
  public static final int ER_MALFORMED_GTID_SPECIFICATION = 1774;
  
  public static final int ER_GNO_EXHAUSTED = 1775;
  
  @Deprecated
  public static final int ER_BAD_SLAVE_AUTO_POSITION = 1776;
  
  public static final int ER_BAD_REPLICA_AUTO_POSITION = 1776;
  
  public static final int ER_AUTO_POSITION_REQUIRES_GTID_MODE_ON = 1777;
  
  public static final int ER_CANT_DO_IMPLICIT_COMMIT_IN_TRX_WHEN_GTID_NEXT_IS_SET = 1778;
  
  public static final int ER_GTID_MODE_2_OR_3_REQUIRES_ENFORCE_GTID_CONSISTENCY_ON = 1779;
  
  public static final int ER_GTID_MODE_REQUIRES_BINLOG = 1780;
  
  public static final int ER_CANT_SET_GTID_NEXT_TO_GTID_WHEN_GTID_MODE_IS_OFF = 1781;
  
  public static final int ER_CANT_SET_GTID_NEXT_TO_ANONYMOUS_WHEN_GTID_MODE_IS_ON = 1782;
  
  public static final int ER_CANT_SET_GTID_NEXT_LIST_TO_NON_NULL_WHEN_GTID_MODE_IS_OFF = 1783;
  
  public static final int ER_FOUND_GTID_EVENT_WHEN_GTID_MODE_IS_OFF = 1784;
  
  public static final int ER_GTID_UNSAFE_NON_TRANSACTIONAL_TABLE = 1785;
  
  public static final int ER_GTID_UNSAFE_CREATE_SELECT = 1786;
  
  public static final int ER_GTID_UNSAFE_CREATE_DROP_TEMPORARY_TABLE_IN_TRANSACTION = 1787;
  
  public static final int ER_GTID_MODE_CAN_ONLY_CHANGE_ONE_STEP_AT_A_TIME = 1788;
  
  @Deprecated
  public static final int ER_MASTER_HAS_PURGED_REQUIRED_GTIDS = 1789;
  
  public static final int ER_SOURCE_HAS_PURGED_REQUIRED_GTIDS = 1789;
  
  public static final int ER_CANT_SET_GTID_NEXT_WHEN_OWNING_GTID = 1790;
  
  public static final int ER_UNKNOWN_EXPLAIN_FORMAT = 1791;
  
  public static final int ER_CANT_EXECUTE_IN_READ_ONLY_TRANSACTION = 1792;
  
  public static final int ER_TOO_LONG_TABLE_PARTITION_COMMENT = 1793;
  
  @Deprecated
  public static final int ER_SLAVE_CONFIGURATION = 1794;
  
  public static final int ER_REPLICA_CONFIGURATION = 1794;
  
  public static final int ER_INNODB_FT_LIMIT = 1795;
  
  public static final int ER_INNODB_NO_FT_TEMP_TABLE = 1796;
  
  public static final int ER_INNODB_FT_WRONG_DOCID_COLUMN = 1797;
  
  public static final int ER_INNODB_FT_WRONG_DOCID_INDEX = 1798;
  
  public static final int ER_INNODB_ONLINE_LOG_TOO_BIG = 1799;
  
  public static final int ER_UNKNOWN_ALTER_ALGORITHM = 1800;
  
  public static final int ER_UNKNOWN_ALTER_LOCK = 1801;
  
  @Deprecated
  public static final int ER_MTS_CHANGE_MASTER_CANT_RUN_WITH_GAPS = 1802;
  
  public static final int ER_MTS_CHANGE_SOURCE_CANT_RUN_WITH_GAPS = 1802;
  
  public static final int ER_MTS_RECOVERY_FAILURE = 1803;
  
  public static final int ER_MTS_RESET_WORKERS = 1804;
  
  public static final int ER_COL_COUNT_DOESNT_MATCH_CORRUPTED_V2 = 1805;
  
  @Deprecated
  public static final int ER_SLAVE_SILENT_RETRY_TRANSACTION = 1806;
  
  public static final int ER_REPLICA_SILENT_RETRY_TRANSACTION = 1806;
  
  public static final int ER_DISCARD_FK_CHECKS_RUNNING = 1807;
  
  public static final int ER_TABLE_SCHEMA_MISMATCH = 1808;
  
  public static final int ER_TABLE_IN_SYSTEM_TABLESPACE = 1809;
  
  public static final int ER_IO_READ_ERROR = 1810;
  
  public static final int ER_IO_WRITE_ERROR = 1811;
  
  public static final int ER_TABLESPACE_MISSING = 1812;
  
  public static final int ER_TABLESPACE_EXISTS = 1813;
  
  public static final int ER_TABLESPACE_DISCARDED = 1814;
  
  public static final int ER_INTERNAL_ERROR = 1815;
  
  public static final int ER_INNODB_IMPORT_ERROR = 1816;
  
  public static final int ER_INNODB_INDEX_CORRUPT = 1817;
  
  public static final int ER_INVALID_YEAR_COLUMN_LENGTH = 1818;
  
  public static final int ER_NOT_VALID_PASSWORD = 1819;
  
  public static final int ER_MUST_CHANGE_PASSWORD = 1820;
  
  public static final int ER_FK_NO_INDEX_CHILD = 1821;
  
  public static final int ER_FK_NO_INDEX_PARENT = 1822;
  
  public static final int ER_FK_FAIL_ADD_SYSTEM = 1823;
  
  public static final int ER_FK_CANNOT_OPEN_PARENT = 1824;
  
  public static final int ER_FK_INCORRECT_OPTION = 1825;
  
  public static final int ER_FK_DUP_NAME = 1826;
  
  public static final int ER_PASSWORD_FORMAT = 1827;
  
  public static final int ER_FK_COLUMN_CANNOT_DROP = 1828;
  
  public static final int ER_FK_COLUMN_CANNOT_DROP_CHILD = 1829;
  
  public static final int ER_FK_COLUMN_NOT_NULL = 1830;
  
  public static final int ER_DUP_INDEX = 1831;
  
  public static final int ER_FK_COLUMN_CANNOT_CHANGE = 1832;
  
  public static final int ER_FK_COLUMN_CANNOT_CHANGE_CHILD = 1833;
  
  public static final int ER_FK_CANNOT_DELETE_PARENT = 1834;
  
  public static final int ER_MALFORMED_PACKET = 1835;
  
  public static final int ER_READ_ONLY_MODE = 1836;
  
  public static final int ER_GTID_NEXT_TYPE_UNDEFINED_GROUP = 1837;
  
  public static final int ER_VARIABLE_NOT_SETTABLE_IN_SP = 1838;
  
  public static final int ER_CANT_SET_GTID_PURGED_WHEN_GTID_MODE_IS_OFF = 1839;
  
  public static final int ER_CANT_SET_GTID_PURGED_WHEN_GTID_EXECUTED_IS_NOT_EMPTY = 1840;
  
  public static final int ER_CANT_SET_GTID_PURGED_WHEN_OWNED_GTIDS_IS_NOT_EMPTY = 1841;
  
  public static final int ER_GTID_PURGED_WAS_CHANGED = 1842;
  
  public static final int ER_GTID_EXECUTED_WAS_CHANGED = 1843;
  
  public static final int ER_BINLOG_STMT_MODE_AND_NO_REPL_TABLES = 1844;
  
  public static final int ER_ALTER_OPERATION_NOT_SUPPORTED = 1845;
  
  public static final int ER_ALTER_OPERATION_NOT_SUPPORTED_REASON = 1846;
  
  public static final int ER_ALTER_OPERATION_NOT_SUPPORTED_REASON_COPY = 1847;
  
  public static final int ER_ALTER_OPERATION_NOT_SUPPORTED_REASON_PARTITION = 1848;
  
  public static final int ER_ALTER_OPERATION_NOT_SUPPORTED_REASON_FK_RENAME = 1849;
  
  public static final int ER_ALTER_OPERATION_NOT_SUPPORTED_REASON_COLUMN_TYPE = 1850;
  
  public static final int ER_ALTER_OPERATION_NOT_SUPPORTED_REASON_FK_CHECK = 1851;
  
  public static final int ER_ALTER_OPERATION_NOT_SUPPORTED_REASON_IGNORE = 1852;
  
  public static final int ER_ALTER_OPERATION_NOT_SUPPORTED_REASON_NOPK = 1853;
  
  public static final int ER_ALTER_OPERATION_NOT_SUPPORTED_REASON_AUTOINC = 1854;
  
  public static final int ER_ALTER_OPERATION_NOT_SUPPORTED_REASON_HIDDEN_FTS = 1855;
  
  public static final int ER_ALTER_OPERATION_NOT_SUPPORTED_REASON_CHANGE_FTS = 1856;
  
  public static final int ER_ALTER_OPERATION_NOT_SUPPORTED_REASON_FTS = 1857;
  
  @Deprecated
  public static final int ER_SQL_SLAVE_SKIP_COUNTER_NOT_SETTABLE_IN_GTID_MODE = 1858;
  
  public static final int ER_SQL_REPLICA_SKIP_COUNTER_NOT_SETTABLE_IN_GTID_MODE = 1858;
  
  public static final int ER_DUP_UNKNOWN_IN_INDEX = 1859;
  
  public static final int ER_IDENT_CAUSES_TOO_LONG_PATH = 1860;
  
  public static final int ER_ALTER_OPERATION_NOT_SUPPORTED_REASON_NOT_NULL = 1861;
  
  public static final int ER_MUST_CHANGE_PASSWORD_LOGIN = 1862;
  
  public static final int ER_ROW_IN_WRONG_PARTITION = 1863;
  
  public static final int ER_MTS_EVENT_BIGGER_PENDING_JOBS_SIZE_MAX = 1864;
  
  public static final int ER_INNODB_NO_FT_USES_PARSER = 1865;
  
  public static final int ER_BINLOG_LOGICAL_CORRUPTION = 1866;
  
  public static final int ER_WARN_PURGE_LOG_IN_USE = 1867;
  
  public static final int ER_WARN_PURGE_LOG_IS_ACTIVE = 1868;
  
  public static final int ER_AUTO_INCREMENT_CONFLICT = 1869;
  
  public static final int WARN_ON_BLOCKHOLE_IN_RBR = 1870;
  
  @Deprecated
  public static final int ER_SLAVE_MI_INIT_REPOSITORY = 1871;
  
  public static final int ER_REPLICA_MI_INIT_REPOSITORY = 1871;
  
  @Deprecated
  public static final int ER_SLAVE_RLI_INIT_REPOSITORY = 1872;
  
  public static final int ER_REPLICA_RLI_INIT_REPOSITORY = 1872;
  
  public static final int ER_ACCESS_DENIED_CHANGE_USER_ERROR = 1873;
  
  public static final int ER_INNODB_READ_ONLY = 1874;
  
  @Deprecated
  public static final int ER_STOP_SLAVE_SQL_THREAD_TIMEOUT = 1875;
  
  public static final int ER_STOP_REPLICA_SQL_THREAD_TIMEOUT = 1875;
  
  @Deprecated
  public static final int ER_STOP_SLAVE_IO_THREAD_TIMEOUT = 1876;
  
  public static final int ER_STOP_REPLICA_IO_THREAD_TIMEOUT = 1876;
  
  public static final int ER_TABLE_CORRUPT = 1877;
  
  public static final int ER_TEMP_FILE_WRITE_FAILURE = 1878;
  
  public static final int ER_INNODB_FT_AUX_NOT_HEX_ID = 1879;
  
  public static final int ER_OLD_TEMPORALS_UPGRADED = 1880;
  
  public static final int ER_INNODB_FORCED_RECOVERY = 1881;
  
  public static final int ER_AES_INVALID_IV = 1882;
  
  public static final int ER_FILE_CORRUPT = 1883;
  
  @Deprecated
  public static final int ER_ERROR_ON_MASTER = 1884;
  
  public static final int ER_ERROR_ON_SOURCE = 1884;
  
  public static final int ER_INCONSISTENT_ERROR = 1885;
  
  public static final int ER_STORAGE_ENGINE_NOT_LOADED = 1886;
  
  public static final int ER_GET_STACKED_DA_WITHOUT_ACTIVE_HANDLER = 1887;
  
  public static final int ER_WARN_LEGACY_SYNTAX_CONVERTED = 1888;
  
  public static final int ER_BINLOG_UNSAFE_FULLTEXT_PLUGIN = 1889;
  
  public static final int ER_CANNOT_DISCARD_TEMPORARY_TABLE = 1890;
  
  public static final int ER_FK_DEPTH_EXCEEDED = 1891;
  
  public static final int ER_COL_COUNT_DOESNT_MATCH_PLEASE_UPDATE_V2 = 1892;
  
  public static final int ER_WARN_TRIGGER_DOESNT_HAVE_CREATED = 1893;
  
  public static final int ER_REFERENCED_TRG_DOES_NOT_EXIST = 1894;
  
  public static final int ER_EXPLAIN_NOT_SUPPORTED = 1895;
  
  public static final int ER_INVALID_FIELD_SIZE = 1896;
  
  public static final int ER_MISSING_HA_CREATE_OPTION = 1897;
  
  public static final int ER_ENGINE_OUT_OF_MEMORY = 1898;
  
  public static final int ER_PASSWORD_EXPIRE_ANONYMOUS_USER = 1899;
  
  @Deprecated
  public static final int ER_SLAVE_SQL_THREAD_MUST_STOP = 1900;
  
  public static final int ER_REPLICA_SQL_THREAD_MUST_STOP = 1900;
  
  public static final int ER_NO_FT_MATERIALIZED_SUBQUERY = 1901;
  
  public static final int ER_INNODB_UNDO_LOG_FULL = 1902;
  
  public static final int ER_INVALID_ARGUMENT_FOR_LOGARITHM = 1903;
  
  @Deprecated
  public static final int ER_SLAVE_IO_THREAD_MUST_STOP = 1904;
  
  public static final int ER_REPLICA_IO_THREAD_MUST_STOP = 1904;
  
  public static final int ER_WARN_OPEN_TEMP_TABLES_MUST_BE_ZERO = 1905;
  
  @Deprecated
  public static final int ER_WARN_ONLY_MASTER_LOG_FILE_NO_POS = 1906;
  
  public static final int ER_WARN_ONLY_SOURCE_LOG_FILE_NO_POS = 1906;
  
  public static final int ER_QUERY_TIMEOUT = 1907;
  
  public static final int ER_NON_RO_SELECT_DISABLE_TIMER = 1908;
  
  public static final int ER_DUP_LIST_ENTRY = 1909;
  
  public static final int ER_SQL_MODE_NO_EFFECT = 1910;
  
  public static final int ER_SESSION_WAS_KILLED = 3169;
  
  public static final int ER_CLIENT_INTERACTION_TIMEOUT = 4031;
  
  public static final int ER_X_BAD_MESSAGE = 5000;
  
  public static final int ER_X_CAPABILITIES_PREPARE_FAILED = 5001;
  
  public static final int ER_X_CAPABILITY_NOT_FOUND = 5002;
  
  public static final int ER_X_INVALID_PROTOCOL_DATA = 5003;
  
  public static final int ER_X_BAD_CONNECTION_SESSION_ATTRIBUTE_VALUE_LENGTH = 5004;
  
  public static final int ER_X_BAD_CONNECTION_SESSION_ATTRIBUTE_KEY_LENGTH = 5005;
  
  public static final int ER_X_BAD_CONNECTION_SESSION_ATTRIBUTE_EMPTY_KEY = 5006;
  
  public static final int ER_X_BAD_CONNECTION_SESSION_ATTRIBUTE_LENGTH = 5007;
  
  public static final int ER_X_BAD_CONNECTION_SESSION_ATTRIBUTE_TYPE = 5008;
  
  public static final int ER_X_CAPABILITY_SET_NOT_ALLOWED = 5009;
  
  public static final int ER_X_SERVICE_ERROR = 5010;
  
  public static final int ER_X_SESSION = 5011;
  
  public static final int ER_X_INVALID_ARGUMENT = 5012;
  
  public static final int ER_X_MISSING_ARGUMENT = 5013;
  
  public static final int ER_X_BAD_INSERT_DATA = 5014;
  
  public static final int ER_X_CMD_NUM_ARGUMENTS = 5015;
  
  public static final int ER_X_CMD_ARGUMENT_TYPE = 5016;
  
  public static final int ER_X_CMD_ARGUMENT_VALUE = 5017;
  
  public static final int ER_X_BAD_UPSERT_DATA = 5018;
  
  public static final int ER_X_DUPLICATED_CAPABILITIES = 5019;
  
  public static final int ER_X_CMD_ARGUMENT_OBJECT_EMPTY = 5020;
  
  public static final int ER_X_CMD_INVALID_ARGUMENT = 5021;
  
  public static final int ER_X_BAD_UPDATE_DATA = 5050;
  
  public static final int ER_X_BAD_TYPE_OF_UPDATE = 5051;
  
  public static final int ER_X_BAD_COLUMN_TO_UPDATE = 5052;
  
  public static final int ER_X_BAD_MEMBER_TO_UPDATE = 5053;
  
  public static final int ER_X_BAD_STATEMENT_ID = 5110;
  
  public static final int ER_X_BAD_CURSOR_ID = 5111;
  
  public static final int ER_X_BAD_SCHEMA = 5112;
  
  public static final int ER_X_BAD_TABLE = 5113;
  
  public static final int ER_X_BAD_PROJECTION = 5114;
  
  public static final int ER_X_DOC_ID_MISSING = 5115;
  
  public static final int ER_X_DUPLICATE_ENTRY = 5116;
  
  public static final int ER_X_DOC_REQUIRED_FIELD_MISSING = 5117;
  
  public static final int ER_X_PROJ_BAD_KEY_NAME = 5120;
  
  public static final int ER_X_BAD_DOC_PATH = 5121;
  
  public static final int ER_X_CURSOR_EXISTS = 5122;
  
  public static final int ER_X_PREPARED_STATMENT_CAN_HAVE_ONE_CURSOR = 5131;
  
  public static final int ER_X_PREPARED_EXECUTE_ARGUMENT_NOT_SUPPORTED = 5133;
  
  public static final int ER_X_PREPARED_EXECUTE_ARGUMENT_CONSISTENCY = 5134;
  
  public static final int ER_X_CURSOR_REACHED_EOF = 5123;
  
  public static final int ER_X_EXPR_BAD_OPERATOR = 5150;
  
  public static final int ER_X_EXPR_BAD_NUM_ARGS = 5151;
  
  public static final int ER_X_EXPR_MISSING_ARG = 5152;
  
  public static final int ER_X_EXPR_BAD_TYPE_VALUE = 5153;
  
  public static final int ER_X_EXPR_BAD_VALUE = 5154;
  
  public static final int ER_X_EXPR_BAD_REGEX = 5155;
  
  public static final int ER_X_INVALID_COLLECTION = 5156;
  
  public static final int ER_X_INVALID_ADMIN_COMMAND = 5157;
  
  public static final int ER_X_EXPECT_NOT_OPEN = 5158;
  
  public static final int ER_X_EXPECT_NO_ERROR_FAILED = 5159;
  
  public static final int ER_X_EXPECT_BAD_CONDITION = 5160;
  
  public static final int ER_X_EXPECT_BAD_CONDITION_VALUE = 5161;
  
  public static final int ER_X_INVALID_NAMESPACE = 5162;
  
  public static final int ER_X_BAD_NOTICE = 5163;
  
  public static final int ER_X_CANNOT_DISABLE_NOTICE = 5164;
  
  public static final int ER_X_BAD_CONFIGURATION = 5165;
  
  public static final int ER_X_MYSQLX_ACCOUNT_MISSING_PERMISSIONS = 5167;
  
  public static final int ER_X_EXPECT_FIELD_EXISTS_FAILED = 5168;
  
  public static final int ER_X_BAD_LOCKING = 5169;
  
  public static final int ER_X_FRAME_COMPRESSION_DISABLED = 5170;
  
  public static final int ER_X_DECOMPRESSION_FAILED = 5171;
  
  public static final int ER_X_BAD_COMPRESSED_FRAME = 5174;
  
  public static final int ER_X_CAPABILITY_COMPRESSION_INVALID_ALGORITHM = 5175;
  
  public static final int ER_X_CAPABILITY_COMPRESSION_INVALID_SERVER_STYLE = 5176;
  
  public static final int ER_X_CAPABILITY_COMPRESSION_INVALID_CLIENT_STYLE = 5177;
  
  public static final int ER_X_CAPABILITY_COMPRESSION_INVALID_OPTION = 5178;
  
  public static final int ER_X_CAPABILITY_COMPRESSION_MISSING_REQUIRED_FIELDS = 5179;
  
  public static final int ER_X_DOCUMENT_DOESNT_MATCH_EXPECTED_SCHEMA = 5180;
  
  public static final int ER_X_COLLECTION_OPTION_DOESNT_EXISTS = 5181;
  
  public static final int ER_X_INVALID_VALIDATION_SCHEMA = 5182;
  
  public static final int ERROR_CODE_NULL_LOAD_BALANCED_CONNECTION = 1000001;
  
  public static final int ERROR_CODE_REPLICATION_CONNECTION_WITH_NO_HOSTS = 1000002;
  
  public static final String SQL_STATE_WARNING = "01000";
  
  public static final String SQL_STATE_DISCONNECT_ERROR = "01002";
  
  public static final String SQL_STATE_DATE_TRUNCATED = "01004";
  
  public static final String SQL_STATE_PRIVILEGE_NOT_REVOKED = "01006";
  
  public static final String SQL_STATE_NO_DATA = "02000";
  
  public static final String SQL_STATE_WRONG_NO_OF_PARAMETERS = "07001";
  
  public static final String SQL_STATE_UNABLE_TO_CONNECT_TO_DATASOURCE = "08001";
  
  public static final String SQL_STATE_CONNECTION_IN_USE = "08002";
  
  public static final String SQL_STATE_CONNECTION_NOT_OPEN = "08003";
  
  public static final String SQL_STATE_CONNECTION_REJECTED = "08004";
  
  public static final String SQL_STATE_CONNECTION_FAILURE = "08006";
  
  public static final String SQL_STATE_TRANSACTION_RESOLUTION_UNKNOWN = "08007";
  
  public static final String SQL_STATE_COMMUNICATION_LINK_FAILURE = "08S01";
  
  public static final String SQL_STATE_FEATURE_NOT_SUPPORTED = "0A000";
  
  public static final String SQL_STATE_CARDINALITY_VIOLATION = "21000";
  
  public static final String SQL_STATE_INSERT_VALUE_LIST_NO_MATCH_COL_LIST = "21S01";
  
  public static final String SQL_STATE_STRING_DATA_RIGHT_TRUNCATION = "22001";
  
  public static final String SQL_STATE_NUMERIC_VALUE_OUT_OF_RANGE = "22003";
  
  public static final String SQL_STATE_INVALID_DATETIME_FORMAT = "22007";
  
  public static final String SQL_STATE_DATETIME_FIELD_OVERFLOW = "22008";
  
  public static final String SQL_STATE_DIVISION_BY_ZERO = "22012";
  
  public static final String SQL_STATE_INVALID_CHARACTER_VALUE_FOR_CAST = "22018";
  
  public static final String SQL_STATE_INTEGRITY_CONSTRAINT_VIOLATION = "23000";
  
  public static final String SQL_STATE_INVALID_CURSOR_STATE = "24000";
  
  public static final String SQL_STATE_INVALID_TRANSACTION_STATE = "25000";
  
  public static final String SQL_STATE_INVALID_AUTH_SPEC = "28000";
  
  public static final String SQL_STATE_INVALID_TRANSACTION_TERMINATION = "2D000";
  
  public static final String SQL_STATE_INVALID_CONDITION_NUMBER = "35000";
  
  public static final String SQL_STATE_INVALID_CATALOG_NAME = "3D000";
  
  public static final String SQL_STATE_ROLLBACK_SERIALIZATION_FAILURE = "40001";
  
  public static final String SQL_STATE_SYNTAX_ERROR = "42000";
  
  public static final String SQL_STATE_ER_TABLE_EXISTS_ERROR = "42S01";
  
  public static final String SQL_STATE_BASE_TABLE_OR_VIEW_NOT_FOUND = "42S02";
  
  public static final String SQL_STATE_ER_NO_SUCH_INDEX = "42S12";
  
  public static final String SQL_STATE_ER_DUP_FIELDNAME = "42S21";
  
  public static final String SQL_STATE_ER_BAD_FIELD_ERROR = "42S22";
  
  public static final String SQL_STATE_INVALID_CONNECTION_ATTRIBUTE = "01S00";
  
  public static final String SQL_STATE_ERROR_IN_ROW = "01S01";
  
  public static final String SQL_STATE_NO_ROWS_UPDATED_OR_DELETED = "01S03";
  
  public static final String SQL_STATE_MORE_THAN_ONE_ROW_UPDATED_OR_DELETED = "01S04";
  
  public static final String SQL_STATE_RESIGNAL_WHEN_HANDLER_NOT_ACTIVE = "0K000";
  
  public static final String SQL_STATE_STACKED_DIAGNOSTICS_ACCESSED_WITHOUT_ACTIVE_HANDLER = "0Z002";
  
  public static final String SQL_STATE_CASE_NOT_FOUND_FOR_CASE_STATEMENT = "20000";
  
  public static final String SQL_STATE_NULL_VALUE_NOT_ALLOWED = "22004";
  
  public static final String SQL_STATE_INVALID_LOGARITHM_ARGUMENT = "2201E";
  
  public static final String SQL_STATE_ACTIVE_SQL_TRANSACTION = "25001";
  
  public static final String SQL_STATE_READ_ONLY_SQL_TRANSACTION = "25006";
  
  public static final String SQL_STATE_SRE_PROHIBITED_SQL_STATEMENT_ATTEMPTED = "2F003";
  
  public static final String SQL_STATE_SRE_FUNCTION_EXECUTED_NO_RETURN_STATEMENT = "2F005";
  
  public static final String SQL_STATE_ER_QUERY_INTERRUPTED = "70100";
  
  public static final String SQL_STATE_BASE_TABLE_OR_VIEW_ALREADY_EXISTS = "S0001";
  
  public static final String SQL_STATE_BASE_TABLE_NOT_FOUND = "S0002";
  
  public static final String SQL_STATE_INDEX_ALREADY_EXISTS = "S0011";
  
  public static final String SQL_STATE_INDEX_NOT_FOUND = "S0012";
  
  public static final String SQL_STATE_COLUMN_ALREADY_EXISTS = "S0021";
  
  public static final String SQL_STATE_COLUMN_NOT_FOUND = "S0022";
  
  public static final String SQL_STATE_NO_DEFAULT_FOR_COLUMN = "S0023";
  
  public static final String SQL_STATE_GENERAL_ERROR = "S1000";
  
  public static final String SQL_STATE_MEMORY_ALLOCATION_FAILURE = "S1001";
  
  public static final String SQL_STATE_INVALID_COLUMN_NUMBER = "S1002";
  
  public static final String SQL_STATE_ILLEGAL_ARGUMENT = "S1009";
  
  public static final String SQL_STATE_DRIVER_NOT_CAPABLE = "S1C00";
  
  public static final String SQL_STATE_TIMEOUT_EXPIRED = "S1T00";
  
  public static final String SQL_STATE_CLI_SPECIFIC_CONDITION = "HY000";
  
  public static final String SQL_STATE_MEMORY_ALLOCATION_ERROR = "HY001";
  
  public static final String SQL_STATE_XA_RBROLLBACK = "XA100";
  
  public static final String SQL_STATE_XA_RBDEADLOCK = "XA102";
  
  public static final String SQL_STATE_XA_RBTIMEOUT = "XA106";
  
  public static final String SQL_STATE_XA_RMERR = "XAE03";
  
  public static final String SQL_STATE_XAER_NOTA = "XAE04";
  
  public static final String SQL_STATE_XAER_INVAL = "XAE05";
  
  public static final String SQL_STATE_XAER_RMFAIL = "XAE07";
  
  public static final String SQL_STATE_XAER_DUPID = "XAE08";
  
  public static final String SQL_STATE_XAER_OUTSIDE = "XAE09";
  
  public static final String SQL_STATE_BAD_SSL_PARAMS = "08000";
  
  private static Map<String, String> sqlStateMessages = new HashMap<>();
  
  static {
    sqlStateMessages.put("01002", Messages.getString("SQLError.35"));
    sqlStateMessages.put("01004", Messages.getString("SQLError.36"));
    sqlStateMessages.put("01006", Messages.getString("SQLError.37"));
    sqlStateMessages.put("01S00", Messages.getString("SQLError.38"));
    sqlStateMessages.put("01S01", Messages.getString("SQLError.39"));
    sqlStateMessages.put("01S03", Messages.getString("SQLError.40"));
    sqlStateMessages.put("01S04", Messages.getString("SQLError.41"));
    sqlStateMessages.put("07001", Messages.getString("SQLError.42"));
    sqlStateMessages.put("08001", Messages.getString("SQLError.43"));
    sqlStateMessages.put("08002", Messages.getString("SQLError.44"));
    sqlStateMessages.put("08003", Messages.getString("SQLError.45"));
    sqlStateMessages.put("08004", Messages.getString("SQLError.46"));
    sqlStateMessages.put("08007", Messages.getString("SQLError.47"));
    sqlStateMessages.put("08S01", Messages.getString("SQLError.48"));
    sqlStateMessages.put("21S01", Messages.getString("SQLError.49"));
    sqlStateMessages.put("22003", Messages.getString("SQLError.50"));
    sqlStateMessages.put("22008", Messages.getString("SQLError.51"));
    sqlStateMessages.put("22012", Messages.getString("SQLError.52"));
    sqlStateMessages.put("40001", Messages.getString("SQLError.53"));
    sqlStateMessages.put("28000", Messages.getString("SQLError.54"));
    sqlStateMessages.put("42000", Messages.getString("SQLError.55"));
    sqlStateMessages.put("42S02", Messages.getString("SQLError.56"));
    sqlStateMessages.put("S0001", Messages.getString("SQLError.57"));
    sqlStateMessages.put("S0002", Messages.getString("SQLError.58"));
    sqlStateMessages.put("S0011", Messages.getString("SQLError.59"));
    sqlStateMessages.put("S0012", Messages.getString("SQLError.60"));
    sqlStateMessages.put("S0021", Messages.getString("SQLError.61"));
    sqlStateMessages.put("S0022", Messages.getString("SQLError.62"));
    sqlStateMessages.put("S0023", Messages.getString("SQLError.63"));
    sqlStateMessages.put("S1000", Messages.getString("SQLError.64"));
    sqlStateMessages.put("S1001", Messages.getString("SQLError.65"));
    sqlStateMessages.put("S1002", Messages.getString("SQLError.66"));
    sqlStateMessages.put("S1009", Messages.getString("SQLError.67"));
    sqlStateMessages.put("S1C00", Messages.getString("SQLError.68"));
    sqlStateMessages.put("S1T00", Messages.getString("SQLError.69"));
  }
  
  public static Map<Integer, String> mysqlToSql99State = new HashMap<>();
  
  static {
    mysqlToSql99State.put(Integer.valueOf(1249), "01000");
    mysqlToSql99State.put(Integer.valueOf(1261), "01000");
    mysqlToSql99State.put(Integer.valueOf(1262), "01000");
    mysqlToSql99State.put(Integer.valueOf(1265), "01000");
    mysqlToSql99State.put(Integer.valueOf(1263), "22004");
    mysqlToSql99State.put(Integer.valueOf(1264), "22003");
    mysqlToSql99State.put(Integer.valueOf(1311), "01000");
    mysqlToSql99State.put(Integer.valueOf(1642), "01000");
    mysqlToSql99State.put(Integer.valueOf(1329), "02000");
    mysqlToSql99State.put(Integer.valueOf(1643), "02000");
    mysqlToSql99State.put(Integer.valueOf(1040), "08004");
    mysqlToSql99State.put(Integer.valueOf(1251), "08004");
    mysqlToSql99State.put(Integer.valueOf(1042), "08S01");
    mysqlToSql99State.put(Integer.valueOf(1043), "08S01");
    mysqlToSql99State.put(Integer.valueOf(1047), "08S01");
    mysqlToSql99State.put(Integer.valueOf(1053), "08S01");
    mysqlToSql99State.put(Integer.valueOf(1080), "08S01");
    mysqlToSql99State.put(Integer.valueOf(1081), "08S01");
    mysqlToSql99State.put(Integer.valueOf(1152), "08S01");
    mysqlToSql99State.put(Integer.valueOf(1153), "08S01");
    mysqlToSql99State.put(Integer.valueOf(1154), "08S01");
    mysqlToSql99State.put(Integer.valueOf(1155), "08S01");
    mysqlToSql99State.put(Integer.valueOf(1156), "08S01");
    mysqlToSql99State.put(Integer.valueOf(1157), "08S01");
    mysqlToSql99State.put(Integer.valueOf(1158), "08S01");
    mysqlToSql99State.put(Integer.valueOf(1159), "08S01");
    mysqlToSql99State.put(Integer.valueOf(1160), "08S01");
    mysqlToSql99State.put(Integer.valueOf(1161), "08S01");
    mysqlToSql99State.put(Integer.valueOf(1184), "08S01");
    mysqlToSql99State.put(Integer.valueOf(1189), "08S01");
    mysqlToSql99State.put(Integer.valueOf(1190), "08S01");
    mysqlToSql99State.put(Integer.valueOf(1218), "08S01");
    mysqlToSql99State.put(Integer.valueOf(1312), "0A000");
    mysqlToSql99State.put(Integer.valueOf(1314), "0A000");
    mysqlToSql99State.put(Integer.valueOf(1335), "0A000");
    mysqlToSql99State.put(Integer.valueOf(1336), "0A000");
    mysqlToSql99State.put(Integer.valueOf(1415), "0A000");
    mysqlToSql99State.put(Integer.valueOf(1845), "0A000");
    mysqlToSql99State.put(Integer.valueOf(1846), "0A000");
    mysqlToSql99State.put(Integer.valueOf(1044), "42000");
    mysqlToSql99State.put(Integer.valueOf(1049), "42000");
    mysqlToSql99State.put(Integer.valueOf(1055), "42000");
    mysqlToSql99State.put(Integer.valueOf(1056), "42000");
    mysqlToSql99State.put(Integer.valueOf(1057), "42000");
    mysqlToSql99State.put(Integer.valueOf(1059), "42000");
    mysqlToSql99State.put(Integer.valueOf(1061), "42000");
    mysqlToSql99State.put(Integer.valueOf(1063), "42000");
    mysqlToSql99State.put(Integer.valueOf(1064), "42000");
    mysqlToSql99State.put(Integer.valueOf(1065), "42000");
    mysqlToSql99State.put(Integer.valueOf(1066), "42000");
    mysqlToSql99State.put(Integer.valueOf(1067), "42000");
    mysqlToSql99State.put(Integer.valueOf(1068), "42000");
    mysqlToSql99State.put(Integer.valueOf(1069), "42000");
    mysqlToSql99State.put(Integer.valueOf(1070), "42000");
    mysqlToSql99State.put(Integer.valueOf(1071), "42000");
    mysqlToSql99State.put(Integer.valueOf(1072), "42000");
    mysqlToSql99State.put(Integer.valueOf(1073), "42000");
    mysqlToSql99State.put(Integer.valueOf(1074), "42000");
    mysqlToSql99State.put(Integer.valueOf(1075), "42000");
    mysqlToSql99State.put(Integer.valueOf(1083), "42000");
    mysqlToSql99State.put(Integer.valueOf(1084), "42000");
    mysqlToSql99State.put(Integer.valueOf(1090), "42000");
    mysqlToSql99State.put(Integer.valueOf(1091), "42000");
    mysqlToSql99State.put(Integer.valueOf(1101), "42000");
    mysqlToSql99State.put(Integer.valueOf(1102), "42000");
    mysqlToSql99State.put(Integer.valueOf(1103), "42000");
    mysqlToSql99State.put(Integer.valueOf(1104), "42000");
    mysqlToSql99State.put(Integer.valueOf(1106), "42000");
    mysqlToSql99State.put(Integer.valueOf(1107), "42000");
    mysqlToSql99State.put(Integer.valueOf(1110), "42000");
    mysqlToSql99State.put(Integer.valueOf(1112), "42000");
    mysqlToSql99State.put(Integer.valueOf(1113), "42000");
    mysqlToSql99State.put(Integer.valueOf(1115), "42000");
    mysqlToSql99State.put(Integer.valueOf(1118), "42000");
    mysqlToSql99State.put(Integer.valueOf(1120), "42000");
    mysqlToSql99State.put(Integer.valueOf(1121), "42000");
    mysqlToSql99State.put(Integer.valueOf(1131), "42000");
    mysqlToSql99State.put(Integer.valueOf(1132), "42000");
    mysqlToSql99State.put(Integer.valueOf(1133), "42000");
    mysqlToSql99State.put(Integer.valueOf(1139), "42000");
    mysqlToSql99State.put(Integer.valueOf(1140), "42000");
    mysqlToSql99State.put(Integer.valueOf(1141), "42000");
    mysqlToSql99State.put(Integer.valueOf(1142), "42000");
    mysqlToSql99State.put(Integer.valueOf(1143), "42000");
    mysqlToSql99State.put(Integer.valueOf(1144), "42000");
    mysqlToSql99State.put(Integer.valueOf(1145), "42000");
    mysqlToSql99State.put(Integer.valueOf(1147), "42000");
    mysqlToSql99State.put(Integer.valueOf(1148), "42000");
    mysqlToSql99State.put(Integer.valueOf(1149), "42000");
    mysqlToSql99State.put(Integer.valueOf(1162), "42000");
    mysqlToSql99State.put(Integer.valueOf(1163), "42000");
    mysqlToSql99State.put(Integer.valueOf(1164), "42000");
    mysqlToSql99State.put(Integer.valueOf(1166), "42000");
    mysqlToSql99State.put(Integer.valueOf(1167), "42000");
    mysqlToSql99State.put(Integer.valueOf(1170), "42000");
    mysqlToSql99State.put(Integer.valueOf(1171), "42000");
    mysqlToSql99State.put(Integer.valueOf(1172), "42000");
    mysqlToSql99State.put(Integer.valueOf(1173), "42000");
    mysqlToSql99State.put(Integer.valueOf(1176), "42000");
    mysqlToSql99State.put(Integer.valueOf(1177), "42000");
    mysqlToSql99State.put(Integer.valueOf(1178), "42000");
    mysqlToSql99State.put(Integer.valueOf(1203), "42000");
    mysqlToSql99State.put(Integer.valueOf(1211), "42000");
    mysqlToSql99State.put(Integer.valueOf(1226), "42000");
    mysqlToSql99State.put(Integer.valueOf(1227), "42000");
    mysqlToSql99State.put(Integer.valueOf(1230), "42000");
    mysqlToSql99State.put(Integer.valueOf(1231), "42000");
    mysqlToSql99State.put(Integer.valueOf(1232), "42000");
    mysqlToSql99State.put(Integer.valueOf(1234), "42000");
    mysqlToSql99State.put(Integer.valueOf(1235), "42000");
    mysqlToSql99State.put(Integer.valueOf(1239), "42000");
    mysqlToSql99State.put(Integer.valueOf(1248), "42000");
    mysqlToSql99State.put(Integer.valueOf(1250), "42000");
    mysqlToSql99State.put(Integer.valueOf(1252), "42000");
    mysqlToSql99State.put(Integer.valueOf(1253), "42000");
    mysqlToSql99State.put(Integer.valueOf(1280), "42000");
    mysqlToSql99State.put(Integer.valueOf(1281), "42000");
    mysqlToSql99State.put(Integer.valueOf(1286), "42000");
    mysqlToSql99State.put(Integer.valueOf(1304), "42000");
    mysqlToSql99State.put(Integer.valueOf(1305), "42000");
    mysqlToSql99State.put(Integer.valueOf(1308), "42000");
    mysqlToSql99State.put(Integer.valueOf(1309), "42000");
    mysqlToSql99State.put(Integer.valueOf(1310), "42000");
    mysqlToSql99State.put(Integer.valueOf(1313), "42000");
    mysqlToSql99State.put(Integer.valueOf(1315), "42000");
    mysqlToSql99State.put(Integer.valueOf(1316), "42000");
    mysqlToSql99State.put(Integer.valueOf(1318), "42000");
    mysqlToSql99State.put(Integer.valueOf(1319), "42000");
    mysqlToSql99State.put(Integer.valueOf(1320), "42000");
    mysqlToSql99State.put(Integer.valueOf(1322), "42000");
    mysqlToSql99State.put(Integer.valueOf(1323), "42000");
    mysqlToSql99State.put(Integer.valueOf(1324), "42000");
    mysqlToSql99State.put(Integer.valueOf(1327), "42000");
    mysqlToSql99State.put(Integer.valueOf(1330), "42000");
    mysqlToSql99State.put(Integer.valueOf(1331), "42000");
    mysqlToSql99State.put(Integer.valueOf(1332), "42000");
    mysqlToSql99State.put(Integer.valueOf(1333), "42000");
    mysqlToSql99State.put(Integer.valueOf(1337), "42000");
    mysqlToSql99State.put(Integer.valueOf(1338), "42000");
    mysqlToSql99State.put(Integer.valueOf(1370), "42000");
    mysqlToSql99State.put(Integer.valueOf(1403), "42000");
    mysqlToSql99State.put(Integer.valueOf(1407), "42000");
    mysqlToSql99State.put(Integer.valueOf(1410), "42000");
    mysqlToSql99State.put(Integer.valueOf(1413), "42000");
    mysqlToSql99State.put(Integer.valueOf(1414), "42000");
    mysqlToSql99State.put(Integer.valueOf(1425), "42000");
    mysqlToSql99State.put(Integer.valueOf(1426), "42000");
    mysqlToSql99State.put(Integer.valueOf(1427), "42000");
    mysqlToSql99State.put(Integer.valueOf(1437), "42000");
    mysqlToSql99State.put(Integer.valueOf(1439), "42000");
    mysqlToSql99State.put(Integer.valueOf(1453), "42000");
    mysqlToSql99State.put(Integer.valueOf(1458), "42000");
    mysqlToSql99State.put(Integer.valueOf(1460), "42000");
    mysqlToSql99State.put(Integer.valueOf(1461), "42000");
    mysqlToSql99State.put(Integer.valueOf(1463), "42000");
    mysqlToSql99State.put(Integer.valueOf(1582), "42000");
    mysqlToSql99State.put(Integer.valueOf(1583), "42000");
    mysqlToSql99State.put(Integer.valueOf(1584), "42000");
    mysqlToSql99State.put(Integer.valueOf(1630), "42000");
    mysqlToSql99State.put(Integer.valueOf(1641), "42000");
    mysqlToSql99State.put(Integer.valueOf(1687), "42000");
    mysqlToSql99State.put(Integer.valueOf(1701), "42000");
    mysqlToSql99State.put(Integer.valueOf(1222), "21000");
    mysqlToSql99State.put(Integer.valueOf(1241), "21000");
    mysqlToSql99State.put(Integer.valueOf(1242), "21000");
    mysqlToSql99State.put(Integer.valueOf(1022), "23000");
    mysqlToSql99State.put(Integer.valueOf(1048), "23000");
    mysqlToSql99State.put(Integer.valueOf(1052), "23000");
    mysqlToSql99State.put(Integer.valueOf(1062), "23000");
    mysqlToSql99State.put(Integer.valueOf(1169), "23000");
    mysqlToSql99State.put(Integer.valueOf(1216), "23000");
    mysqlToSql99State.put(Integer.valueOf(1217), "23000");
    mysqlToSql99State.put(Integer.valueOf(1451), "23000");
    mysqlToSql99State.put(Integer.valueOf(1452), "23000");
    mysqlToSql99State.put(Integer.valueOf(1557), "23000");
    mysqlToSql99State.put(Integer.valueOf(1586), "23000");
    mysqlToSql99State.put(Integer.valueOf(1761), "23000");
    mysqlToSql99State.put(Integer.valueOf(1762), "23000");
    mysqlToSql99State.put(Integer.valueOf(1859), "23000");
    mysqlToSql99State.put(Integer.valueOf(1406), "22001");
    mysqlToSql99State.put(Integer.valueOf(1416), "22003");
    mysqlToSql99State.put(Integer.valueOf(1690), "22003");
    mysqlToSql99State.put(Integer.valueOf(1292), "22007");
    mysqlToSql99State.put(Integer.valueOf(1367), "22007");
    mysqlToSql99State.put(Integer.valueOf(1441), "22008");
    mysqlToSql99State.put(Integer.valueOf(1365), "22012");
    mysqlToSql99State.put(Integer.valueOf(1325), "24000");
    mysqlToSql99State.put(Integer.valueOf(1326), "24000");
    mysqlToSql99State.put(Integer.valueOf(1179), "25000");
    mysqlToSql99State.put(Integer.valueOf(1207), "25000");
    mysqlToSql99State.put(Integer.valueOf(1045), "28000");
    mysqlToSql99State.put(Integer.valueOf(1698), "28000");
    mysqlToSql99State.put(Integer.valueOf(1873), "28000");
    mysqlToSql99State.put(Integer.valueOf(1758), "35000");
    mysqlToSql99State.put(Integer.valueOf(1046), "3D000");
    mysqlToSql99State.put(Integer.valueOf(1645), "0K000");
    mysqlToSql99State.put(Integer.valueOf(1887), "0Z002");
    mysqlToSql99State.put(Integer.valueOf(1339), "20000");
    mysqlToSql99State.put(Integer.valueOf(1058), "21S01");
    mysqlToSql99State.put(Integer.valueOf(1136), "21S01");
    mysqlToSql99State.put(Integer.valueOf(1138), "22004");
    mysqlToSql99State.put(Integer.valueOf(1903), "2201E");
    mysqlToSql99State.put(Integer.valueOf(1568), "25001");
    mysqlToSql99State.put(Integer.valueOf(1792), "25006");
    mysqlToSql99State.put(Integer.valueOf(1303), "2F003");
    mysqlToSql99State.put(Integer.valueOf(1321), "2F005");
    mysqlToSql99State.put(Integer.valueOf(1050), "42S01");
    mysqlToSql99State.put(Integer.valueOf(1051), "42S02");
    mysqlToSql99State.put(Integer.valueOf(1109), "42S02");
    mysqlToSql99State.put(Integer.valueOf(1146), "42S02");
    mysqlToSql99State.put(Integer.valueOf(1082), "42S12");
    mysqlToSql99State.put(Integer.valueOf(1060), "42S21");
    mysqlToSql99State.put(Integer.valueOf(1054), "42S22");
    mysqlToSql99State.put(Integer.valueOf(1247), "42S22");
    mysqlToSql99State.put(Integer.valueOf(1317), "70100");
    mysqlToSql99State.put(Integer.valueOf(1037), "HY001");
    mysqlToSql99State.put(Integer.valueOf(1038), "HY001");
    mysqlToSql99State.put(Integer.valueOf(1402), "XA100");
    mysqlToSql99State.put(Integer.valueOf(1614), "XA102");
    mysqlToSql99State.put(Integer.valueOf(1613), "XA106");
    mysqlToSql99State.put(Integer.valueOf(1401), "XAE03");
    mysqlToSql99State.put(Integer.valueOf(1397), "XAE04");
    mysqlToSql99State.put(Integer.valueOf(1398), "XAE05");
    mysqlToSql99State.put(Integer.valueOf(1399), "XAE07");
    mysqlToSql99State.put(Integer.valueOf(1440), "XAE08");
    mysqlToSql99State.put(Integer.valueOf(1400), "XAE09");
    mysqlToSql99State.put(Integer.valueOf(1205), "40001");
    mysqlToSql99State.put(Integer.valueOf(1213), "40001");
  }
  
  public static String get(String stateCode) {
    return sqlStateMessages.get(stateCode);
  }
  
  public static String mysqlToSql99(int errno) {
    Integer err = Integer.valueOf(errno);
    if (mysqlToSql99State.containsKey(err))
      return mysqlToSql99State.get(err); 
    return "HY000";
  }
  
  public static String mysqlToSqlState(int errno) {
    return mysqlToSql99(errno);
  }
}
