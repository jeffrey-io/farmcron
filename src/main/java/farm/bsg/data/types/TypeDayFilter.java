package farm.bsg.data.types;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.joda.time.DateTimeConstants;

import farm.bsg.data.Type;
import farm.bsg.data.contracts.ProjectionProvider;
import farm.bsg.data.contracts.SingleCharacterBitmaskProvider;

public class TypeDayFilter extends Type {

    public static SingleCharacterBitmaskProvider PROVIDER = new SingleCharacterBitmaskProvider() {
        @Override
        public Map<String, String> asMap() {
            LinkedHashMap<String, String> map = new LinkedHashMap<>();
            for (Day day : Day.values()) {
                map.put(day.name(), day.letter);
            }
            return map;
        }

        @Override
        public Set<String> valuesOf(String data) {
            HashSet<String> values = new HashSet<>();
            // no data provided means no override, so set all
            if (data == null || data.length() == 0) {
                for (Day day : Day.values()) {
                    values.add(day.letter);
                }
                return values;
            }
            Set<Day> daysSet = Day.decode(data);
            for (Day day : daysSet) {
                values.add(day.letter);
            }
            return values;
        }
    };

    public static Set<Integer> ordinalsOf(String data) {
        HashSet<Integer> values = new HashSet<>();
        // no data provided means no override, so set all
        if (data == null || data.length() == 0) {
            for (Day day : Day.values()) {
                values.add(day.ordinal);
            }
            return values;
        }
        Set<Day> daysSet = Day.decode(data);
        for (Day day : daysSet) {
            values.add(day.ordinal);
        }
        return values;

    }

    public static enum Day {
        Sunday((byte) 0x01, 's', DateTimeConstants.SUNDAY), //
        Monday((byte) 0x02, 'm', DateTimeConstants.MONDAY), //
        Tuesday((byte) 0x04, 't', DateTimeConstants.TUESDAY), //
        Wednesday((byte) 0x08, 'w', DateTimeConstants.WEDNESDAY), //
        Thursday((byte) 0x10, 'h', DateTimeConstants.THURSDAY), //
        Friday((byte) 0x20, 'f', DateTimeConstants.FRIDAY), //
        Saturday((byte) 0x40, 'a', DateTimeConstants.SATURDAY),;
        public final byte   bitmask;
        public final String letter;
        public final int    ordinal;

        private Day(byte bitmask, char letter, int ordinal) {
            this.bitmask = bitmask;
            this.letter = "" + letter;
            this.ordinal = ordinal;
        }

        public static int encode(String value) {
            int asInt = 0;
            for (Day day : Day.values()) {
                if (value.contains(day.letter)) {
                    asInt += day.bitmask;
                }
            }
            return asInt;
        }

        public static Set<Day> decode(String value) {
            try {
                return decode(Integer.parseInt(value));
            } catch (NumberFormatException nfe) {
                return decode(encode(value));
            }
        }

        public static Set<Day> decode(int value) {
            HashSet<Day> set = new HashSet<>();
            for (Day day : Day.values()) {
                if ((day.bitmask & value) > 0) {
                    set.add(day);
                }
            }
            return set;
        }
    }

    public TypeDayFilter(String name) {
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
            return Integer.toString(Day.encode(value));
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
        return "day-filter";
    }

    public static String project(ProjectionProvider provider, String key) {
        StringBuilder sb = new StringBuilder();
        for (Day day : Day.values()) {
            if (provider.first(key + "_" + day.letter) != null) {
                sb.append(day.letter);
            }
        }
        String joined = sb.toString();
        if (joined.length() == 0) {
            return null;
        }
        int encoded = Day.encode(joined);
        return Integer.toString(encoded);
    }

}
