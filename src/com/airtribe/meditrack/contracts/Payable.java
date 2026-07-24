package com.airtribe.meditrack.contracts;

import com.airtribe.meditrack.constants.Constants;

/**
 * Implemented by anything that produces a payable total, such as a Bill.
 */
public interface Payable {

    double generateBill();

    void pay();

    boolean isPaid();

    default double calculateTax(double amount) {
        return amount * Constants.TAX_RATE;
    }
}
