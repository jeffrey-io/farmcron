package farm.bsg.models;

import farm.bsg.data.Field;
import farm.bsg.data.ObjectSchema;
import farm.bsg.data.RawObject;
import farm.bsg.ops.CounterCodeGen;

public class Check extends RawObject {
    public static final ObjectSchema SCHEMA = ObjectSchema.persisted("checks/", //
            Field.STRING("ref"), // made; inserted
            Field.STRING("person").makeIndex(false), // made; copied into
            Field.DATETIME("generated"), // made
            Field.STRING("fiscal_day").makeIndex(false), // made
            Field.NUMBER("payment"), // made
            Field.STRING("ready").makeIndex(false), // made
            Field.NUMBER("checksum") // made
    );

    public static String fiscalQuarterFromFiscalDay(final String day) {
        final String year = day.substring(0, 4);
        final String month = day.substring(4, 6);
        switch (month) {
            case "01":
            case "02":
            case "03":
                return year + "Q1";
            case "04":
            case "05":
            case "06":
                return year + "Q2";
            case "07":
            case "08":
            case "09":
                return year + "Q3";
            case "10":
            case "11":
            case "12":
                return year + "Q4";
        }
        return year + "?";
    }

    public static void link(final CounterCodeGen c) {
        c.section("Data: Check");
    }

    private String cachedFiscalQuarter;

    public Check() {
        super(SCHEMA);
    }

    public String getFiscalQuarter() {
        if (this.cachedFiscalQuarter == null) {
            this.cachedFiscalQuarter = fiscalQuarterFromFiscalDay(get("fiscal_day"));
        }
        return this.cachedFiscalQuarter;
    }

    @Override
    protected void invalidateCache() {
        this.cachedFiscalQuarter = null;
    }
}
