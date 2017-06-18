package farm.bsg.data;

import farm.bsg.data.types.TypeBoolean;
import farm.bsg.data.types.TypeBytesInBase64;
import farm.bsg.data.types.TypeDateTime;
import farm.bsg.data.types.TypeDayFilter;
import farm.bsg.data.types.TypeMonthFilter;
import farm.bsg.data.types.TypeNumber;
import farm.bsg.data.types.TypeString;
import farm.bsg.data.types.TypeStringTokenList;
import farm.bsg.data.types.TypeUUID;

public class Field {
    
    public static Type UUID(String name) {
        return new TypeUUID(name);
    }

    public static TypeString STRING(String name) {
        return new TypeString(name);
    }

    public static TypeBytesInBase64 BYTESB64(String name) {
        return new TypeBytesInBase64(name);
    }

    public static Type BOOL(String name) {
        return new TypeBoolean(name);
    }

    public static Type TOKEN_STRING_LIST(String name) {
        return new TypeStringTokenList(name);
    }

    public static Type DATETIME(String name) {
        return new TypeDateTime(name);
    }

    public static TypeNumber NUMBER(String name) {
        return new TypeNumber(name);
    }
    
    public static Type DAYFILTER(String name) {
        return new TypeDayFilter(name);
    }
    
    
    public static Type MONTHFILTER(String name) {
        return new TypeMonthFilter(name);
    }
}
