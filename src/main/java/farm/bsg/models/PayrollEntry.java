package farm.bsg.models;

import farm.bsg.data.RawObject;
import farm.bsg.ops.CounterCodeGen;
import java.util.List;
import farm.bsg.ProductEngine;
import farm.bsg.data.Field;

public class PayrollEntry extends RawObject {

    public PayrollEntry() {
        super("payroll/", //
                Field.STRING("person"), // done; copied
                Field.DATETIME("reported"), // done; generated
                Field.STRING("fiscal_day"), // done; generated

                Field.NUMBER("mileage").addProjection("edit"), // done; used
                Field.NUMBER("hours_worked").addProjection("edit"), // done; used
                Field.NUMBER("pto_used").addProjection("edit"), // done; used
                Field.NUMBER("sick_leave_used").addProjection("edit"), // done; used

                Field.NUMBER("hourly_wage_compesation"), // done; copied
                Field.NUMBER("mileage_compensation"), // done; copied
                Field.NUMBER("owed"), // done; computed
                Field.NUMBER("tax_withholding"), // done; copied
                Field.NUMBER("taxes"), Field.NUMBER("benefits"), // done; used (NEED INPUT)
                Field.STRING("check").makeIndex(false), // indicates a payment was made
                Field.STRING("unpaid").makeIndex(false) // if not "paid", then it is the employee id; DONE; indexed

        );
    }

    public boolean isOutstanding() {
        String unpaid = get("unpaid");
        if (unpaid == null) {
            return false;
        }
        if (unpaid.equals(get("person"))) {
            return true;
        }
        return false;
    }

    public double getOwed() {
        String owedRaw = get("owed");
        if (owedRaw == null) {
            return 0.0;
        }
        return Double.parseDouble(owedRaw);
    }

    public boolean executeBenefits(Person person, int dMonth) {
        set("person", person.getId());
        String monthly_benefits = person.get("monthly_benefits");
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

    private void sharedNormalize(Person person) {
        copyFrom(person, "hourly_wage_compesation", "mileage_compensation", "tax_withholding");
        double hoursWorked = getAsDouble("hours_worked");
        double mileageReported = getAsDouble("mileage");
        double wageRate = getAsDouble("hourly_wage_compesation");
        double mileageRate = getAsDouble("mileage_compensation");
        double ptoUsed = getAsDouble("pto_used");
        double sickLeaveUsed = getAsDouble("sick_leave_used");
        double hoursOwed = hoursWorked + ptoUsed + sickLeaveUsed;
        double benefitsOwed = getAsDouble("benefits");
        double taxWithholding = getAsDouble("tax_withholding");
        double beforeTaxes = hoursOwed * wageRate + mileageReported * mileageRate + benefitsOwed;
        double taxes = taxWithholding * beforeTaxes;
        taxes = Math.ceil(taxes * 100) / 100.0;
        double owed = beforeTaxes - taxes;
        owed = Math.ceil(owed * 10) / 10.0;
        set("owed", owed);
        set("taxes", taxes);
    }

    public void normalize(Person person) {
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

    public static List<PayrollEntry> getUnpaidEntries(ProductEngine engine, String id) {
        return engine.select_payrollentry().where_unpaid_eq(id).to_list().inline_order_lexographically_asc_by("reported").done();
    }

    public static void link(CounterCodeGen c) {
        c.section("Data: PayrollEntry");
    }

    @Override
    protected void invalidateCache() {
    }

}
