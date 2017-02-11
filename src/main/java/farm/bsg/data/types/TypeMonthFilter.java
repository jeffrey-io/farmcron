package farm.bsg.data.types;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import farm.bsg.data.Type;
import farm.bsg.data.contracts.ProjectionProvider;
import farm.bsg.data.contracts.SingleCharacterBitmaskProvider;

public class TypeMonthFilter extends Type {
    public static SingleCharacterBitmaskProvider PROVIDER = new SingleCharacterBitmaskProvider() {
        @Override
        public Map<String, String> asMap() {
            LinkedHashMap<String, String> map = new LinkedHashMap<>();
            for (Month month : Month.values()) {
                map.put(month.name(), month.letter);
            }
            return map;
        }

        @Override
        public Set<String> valuesOf(String data) {
            HashSet<String> values = new HashSet<>();
            // no data provided means no override, so set all
            if (data == null || data.length() == 0) {
                for (Month month : Month.values()) {
                    values.add(month.letter);
                }
                return values;
            }
            Set<Month> daysSet = Month.decode(data);
            for (Month month : daysSet) {
                values.add(month.letter);
            }
            return values;
        }
    };

    public static Set<Integer> ordinalsOf(String data) {
        HashSet<Integer> values = new HashSet<>();
        // no data provided means no override, so set all
        if (data == null || data.length() == 0) {
            for (Month month : Month.values()) {
                values.add(month.ordinal);
            }
            return values;
        }
        Set<Month> daysSet = Month.decode(data);
        for (Month month : daysSet) {
            values.add(month.ordinal);
        }
        return values;

    }

    public static enum Month {
        January(0x0001, 'j', 1), //
        February(0x0002, 'f', 2), //
        March(0x0004, 'm', 3), //
        April(0x0008, 'a', 4), //
        May(0x0010, 'y', 5), //
        June(0x0020, 'u', 6), //
        July(0x0040, 'l', 7), //
        August(0x0080, 'g', 8), //
        September(0x0100, 's', 9), //
        October(0x0200, 'o', 10), //
        November(0x0400, 'n', 11), //
        December(0x0800, 'd', 12);

        public final int    bitmask;
        public final String letter;
        public final int    ordinal;

        private Month(int bitmask, char letter, int ordinal) {
            this.bitmask = bitmask;
            this.letter = "" + letter;
            this.ordinal = ordinal;
        }

        public static int encode(String value) {
            int asInt = 0;
            for (Month month : Month.values()) {
                if (value.contains(month.letter)) {
                    asInt += month.bitmask;
                }
            }
            return asInt;
        }

        public static Set<Month> decode(String value) {
            try {
                return decode(Integer.parseInt(value));
            } catch (NumberFormatException nfe) {
                return decode(encode(value));
            }
        }

        public static Set<Month> decode(int value) {
            HashSet<Month> set = new HashSet<>();
            for (Month month : Month.values()) {
                if ((month.bitmask & value) > 0) {
                    set.add(month);
                }
            }
            return set;
        }
    }

    public TypeMonthFilter(String name) {
        super(name);
    }

    @Override
    public String normalize(String value) {
        if (value == null) {
            return null;
        }
        value = value.trim();
        if (value.equals("")) {
            return null;
        }

        try {
            Integer.parseInt(value);
            return value;
        } catch (NumberFormatException nfe) {
            return Integer.toString(Month.encode(value));
        }
    }

    @Override
    public boolean validate(String value) {
        return true;
    }

    @Override
    public String defaultValue() {
        return null;
    }

    @Override
    public String type() {
        return "month-filter";
    }

    public static String project(ProjectionProvider provider, String key) {
        StringBuilder sb = new StringBuilder();
        for (Month month : Month.values()) {
            if (provider.first(key + "_" + month.letter) != null) {
                sb.append(month.letter);
            }
        }
        String joined = sb.toString();
        if (joined.length() == 0) {
            return null;
        }
        int encoded = Month.encode(joined);
        return Integer.toString(encoded);
    }
}
