package com.airtribe.meditrack.entity;

import com.airtribe.meditrack.contracts.Payable;

/**
 * generateBill() is overridden per subclass so each bill type computes its
 * total differently while sharing the same tax logic from {@link Payable}.
 */
public abstract class Bill extends MedicalEntity implements Payable {

    private static final long serialVersionUID = 1L;

    protected final Appointment appointment;
    protected final double baseAmount;
    protected boolean paid;

    protected Bill(String id, Appointment appointment, double baseAmount) {
        super(id);
        this.appointment = appointment;
        this.baseAmount = baseAmount;
        this.paid = false;
    }

    public Appointment getAppointment() {
        return appointment;
    }

    public double getBaseAmount() {
        return baseAmount;
    }

    @Override
    public void pay() {
        this.paid = true;
    }

    @Override
    public boolean isPaid() {
        return paid;
    }

    @Override
    public String describe() {
        return "Bill [" + getId() + "] for appointment " + appointment.getId()
                + ", total: " + generateBill() + ", paid: " + paid;
    }

    @Override
    public String toString() {
        return describe();
    }
}
