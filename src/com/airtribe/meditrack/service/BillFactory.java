package com.airtribe.meditrack.service;

import com.airtribe.meditrack.entity.Appointment;
import com.airtribe.meditrack.entity.Bill;
import com.airtribe.meditrack.entity.ConsultationBill;
import com.airtribe.meditrack.entity.ProcedureBill;
import com.airtribe.meditrack.enums.BillType;

/**
 * Factory pattern (Bonus B): hides which concrete Bill subclass gets built
 * from a given BillType, so callers never construct bills directly.
 */
public final class BillFactory {

    private BillFactory() {
    }

    public static Bill createBill(BillType type, String id, Appointment appointment, double baseAmount,
                                   double procedureCharge) {
        return switch (type) {
            case CONSULTATION -> new ConsultationBill(id, appointment, baseAmount);
            case PROCEDURE -> new ProcedureBill(id, appointment, baseAmount, procedureCharge);
        };
    }
}
