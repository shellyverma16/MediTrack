package com.airtribe.meditrack.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Immutable snapshot of a Bill: every field is final, there are no setters,
 * and all constructor arguments are themselves immutable (String, double,
 * LocalDateTime), so instances are safe to share across threads.
 */
public final class BillSummary implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String billId;
    private final String patientName;
    private final String doctorName;
    private final double totalAmount;
    private final LocalDateTime billedAt;

    public BillSummary(String billId, String patientName, String doctorName, double totalAmount,
                        LocalDateTime billedAt) {
        this.billId = billId;
        this.patientName = patientName;
        this.doctorName = doctorName;
        this.totalAmount = totalAmount;
        this.billedAt = billedAt;
    }

    public String getBillId() {
        return billId;
    }

    public String getPatientName() {
        return patientName;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public LocalDateTime getBilledAt() {
        return billedAt;
    }

    @Override
    public String toString() {
        return "BillSummary{" + billId + ", patient=" + patientName + ", doctor=" + doctorName
                + ", total=" + totalAmount + ", billedAt=" + billedAt + '}';
    }
}
