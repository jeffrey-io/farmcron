package farm.bsg.models;

import java.util.List;

import farm.bsg.QueryEngine;
import farm.bsg.data.Field;
import farm.bsg.data.ObjectSchema;
import farm.bsg.data.RawObject;
import farm.bsg.ops.CounterCodeGen;

public class PayrollEntry extends RawObject {

    public static final ObjectSchema SCHEMA = ObjectSchema.persisted("payroll/", //
            Field.STRING("person").makeIndex(false), // done; copied
            Field.DATETIME("reported"), // done; generated
            Field.STRING("fiscal_day"), // done; generated

            Field.NUMBER("mileage").addProjection("edit"), // done; used
            Field.NUMBER("hours_worked").addProjection("edit"), // done; used
            Field.NUMBER("pto_used").addProjection("edit"), // done; used
            Field.NUMBER("sick_leave_used").addProjection("edit"), // done; used

            Field.NUMBER("hourly_wage_compesation"), // done; copied
            Field.NUMBER("mileage_compensation"), // done; copied

            Field.NUMBER("pto_change"), // done; computed

            Field.NUMBER("owed"), // done; computed
            Field.NUMBER("tax_withholding"), // done; copied
            Field.NUMBER("taxes"), // done; computed
            Field.NUMBER("benefits"), // done; used (NEED INPUT)

            Field.STRING("check").makeIndex(false), // indicates a payment was made
            Field.STRING("unpaid").makeIndex(false) // if not "paid", then it is the employee id; DONE; indexed
    );

    public static List<PayrollEntry> getUnpaidEntries(final QueryEngine engine, final String id) {
        return engine.select_payrollentry().where_unpaid_eq(id).to_list().inline_order_lexographically_asc_by("reported").done();
    }

    public static void link(final CounterCodeGen c) {
        c.section("Data: PayrollEntry");
    }

    public PayrollEntry() {
        super(SCHEMA);
    }

    public boolean executeBenefits(final Person person, final int dMonth) {
        set("person", person.getId());
        final String monthly_benefits = person.get("monthly_benefits");
        if (monthly_benefits == null) {
            return false;
        }
        set("benefits", monthly_benefits);
        sharedNormalize(person);
        set("reported", RawObject.isoTimestamp());
        set("fiscal_day", person.getFutureMonth(dMonth) + "01");
        set("unpaid", person.getId());
        return true;
    }

    public double getOwed() {
        final String owedRaw = get("owed");
        if (owedRaw == null) {
            return 0.0;
        }
        return Double.parseDouble(owedRaw);
    }

    @Override
    protected void invalidateCache() {
    }

    public boolean isOutstanding() {
        final String unpaid = get("unpaid");
        if (unpaid == null) {
            return false;
        }
        if (unpaid.equals(get("person"))) {
            return true;
        }
        return false;
    }

    public void normalize(final Person person) {
        set("person", person.getId());
        if (get("unpaid") == null) {
            set("unpaid", person.getId());
        }
        if (getAsDouble("hours_worked") > 0) {
            if (getAsDouble("mileage") <= 0.01) {
                set("mileage", person.get("default_mileage"));
            }
        } else {
            set("mileage", "0");
        }

        sharedNormalize(person);
    }

    private void sharedNormalize(final Person person) {
        copyFrom(person, "hourly_wage_compesation", "mileage_compensation", "tax_withholding");
        final double pto_earning_rate = person.getAsDouble("pto_earning_rate");
        final double hoursWorked = getAsDouble("hours_worked");

        final double ptoUsed = getAsDouble("pto_used");
        if (pto_earning_rate > 0.0001) {
            set("pto_change", pto_earning_rate * hoursWorked - ptoUsed);
        } else {
            set("pto_change", -ptoUsed);
        }
        final double mileageReported = getAsDouble("mileage");
        final double wageRate = getAsDouble("hourly_wage_compesation");
        final double mileageRate = getAsDouble("mileage_compensation");
        final double sickLeaveUsed = getAsDouble("sick_leave_used");
        final double hoursOwed = hoursWorked + ptoUsed + sickLeaveUsed;
        final double benefitsOwed = getAsDouble("benefits");
        final double taxWithholding = getAsDouble("tax_withholding");
        
        final double taxableBeforeTaxes = hoursOwed * wageRate;
        final double nonTaxableBeforeTaxes = mileageReported * mileageRate + benefitsOwed; 
        final double beforeTaxes = taxableBeforeTaxes + nonTaxableBeforeTaxes;
        double taxes = taxableBeforeTaxes * taxWithholding;
        taxes = Math.ceil(taxes * 100) / 100.0;
        double owed = beforeTaxes - taxes;
        owed = Math.ceil(owed * 10) / 10.0;
        set("owed", owed);
        set("taxes", taxes);
    }

}
