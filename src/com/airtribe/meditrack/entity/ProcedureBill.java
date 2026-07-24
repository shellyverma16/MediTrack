package com.airtribe.meditrack.entity;

public class ProcedureBill extends Bill {

    private static final long serialVersionUID = 1L;

    private final double procedureCharge;

    public ProcedureBill(String id, Appointment appointment, double baseAmount, double procedureCharge) {
        super(id, appointment, baseAmount);
        this.procedureCharge = procedureCharge;
    }

    public double getProcedureCharge() {
        return procedureCharge;
    }

    @Override
    public double generateBill() {
        double subtotal = baseAmount + procedureCharge;
        return subtotal + calculateTax(subtotal);
    }
}
