package farm.bsg.pages.common;

import farm.bsg.route.RequestResponseWrapper;

public class ParameterHelper {

    public static int getIntParamWithDefault(RequestResponseWrapper wrapper, String key, int defaultValue) {
        String value = wrapper.getParam(key);
        if (value == null) {
            return defaultValue;
        }
        value = value.trim();
        if (value.length() == 0) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException nfe) {
            return defaultValue;
        }
    }
}
