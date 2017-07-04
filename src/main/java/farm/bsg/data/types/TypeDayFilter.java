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

    public static enum Day {
        Sunday((byte) 0x01, 's', DateTimeConstants.SUNDAY), //
        Monday((byte) 0x02, 'm', DateTimeConstants.MONDAY), //
        Tuesday((byte) 0x04, 't', DateTimeConstants.TUESDAY), //
        Wednesday((byte) 0x08, 'w', DateTimeConstants.WEDNESDAY), //
        Thursday((byte) 0x10, 'h', DateTimeConstants.THURSDAY), //
        Friday((byte) 0x20, 'f', DateTimeConstants.FRIDAY), //
        Saturday((byte) 0x40, 'a', DateTimeConstants.SATURDAY),;
        public static Set<Day> decode(final int value) {
            final HashSet<Day> set = new HashSet<>();
            for (final Day day : Day.values()) {
                if ((day.bitmask & value) > 0) {
                    set.add(day);
                }
            }
            return set;
        }

        public static Set<Day> decode(final String value) {
            try {
                return decode(Integer.parseInt(value));
            } catch (final NumberFormatException nfe) {
                return decode(encode(value));
            }
        }

        public static int encode(final String value) {
            int asInt = 0;
            for (final Day day : Day.values()) {
                if (value.contains(day.letter)) {
                    asInt += day.bitmask;
                }
            }
            return asInt;
        }

        public final byte   bitmask;

        public final String letter;

        public final int    ordinal;

        private Day(final byte bitmask, final char letter, final int ordinal) {
            this.bitmask = bitmask;
            this.letter = "" + letter;
            this.ordinal = ordinal;
        }
    }

    public static SingleCharacterBitmaskProvider PROVIDER = new SingleCharacterBitmaskProvider() {
        @Override
        public Map<String, String> asMap() {
            final LinkedHashMap<String, String> map = new LinkedHashMap<>();
            for (final Day day : Day.values()) {
                map.put(day.name(), day.letter);
            }
            return map;
        }

        @Override
        public Set<String> valuesOf(final String data) {
            final HashSet<String> values = new HashSet<>();
            // no data provided means no override, so set all
            if (data == null || data.length() == 0) {
                for (final Day day : Day.values()) {
                    values.add(day.letter);
                }
                return values;
            }
            final Set<Day> daysSet = Day.decode(data);
            for (final Day day : daysSet) {
                values.add(day.letter);
            }
            return values;
        }
    };

    public static Set<Integer> ordinalsOf(final String data) {
        final HashSet<Integer> values = new HashSet<>();
        // no data provided means no override, so set all
        if (data == null || data.length() == 0) {
            for (final Day day : Day.values()) {
                values.add(day.ordinal);
            }
            return values;
        }
        final Set<Day> daysSet = Day.decode(data);
        for (final Day day : daysSet) {
            values.add(day.ordinal);
        }
        return values;

    }

    public static String project(final ProjectionProvider provider, final String key) {
        final StringBuilder sb = new StringBuilder();
        for (final Day day : Day.values()) {
            if (provider.first(key + "_" + day.letter) != null) {
                sb.append(day.letter);
            }
        }
        final String joined = sb.toString();
        if (joined.length() == 0) {
            return null;
        }
        final int encoded = Day.encode(joined);
        return Integer.toString(encoded);
    }

    public TypeDayFilter(final String name) {
        super(name);
    }

    @Override
    public String defaultValue() {
        return null;
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
        } catch (final NumberFormatException nfe) {
            return Integer.toString(Day.encode(value));
        }
    }

    @Override
    public String type() {
        return "day-filter";
    }

    @Override
    public boolean validate(final String value) {
        return true;
    }

}
