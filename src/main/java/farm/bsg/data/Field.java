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

    public static Type BOOL(final String name) {
        return new TypeBoolean(name);
    }

    public static TypeBytesInBase64 BYTESB64(final String name) {
        return new TypeBytesInBase64(name);
    }

    public static Type DATETIME(final String name) {
        return new TypeDateTime(name);
    }

    public static Type DAYFILTER(final String name) {
        return new TypeDayFilter(name);
    }

    public static Type MONTHFILTER(final String name) {
        return new TypeMonthFilter(name);
    }

    public static TypeNumber NUMBER(final String name) {
        return new TypeNumber(name);
    }

    public static TypeString STRING(final String name) {
        return new TypeString(name);
    }

    public static Type TOKEN_STRING_LIST(final String name) {
        return new TypeStringTokenList(name);
    }

    public static Type UUID(final String name) {
        return new TypeUUID(name);
    }
}
