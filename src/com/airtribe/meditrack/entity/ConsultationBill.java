package com.airtribe.meditrack.entity;

public class ConsultationBill extends Bill {

    private static final long serialVersionUID = 1L;

    public ConsultationBill(String id, Appointment appointment, double baseAmount) {
        super(id, appointment, baseAmount);
    }

    @Override
    public double generateBill() {
        return baseAmount + calculateTax(baseAmount);
    }
}
