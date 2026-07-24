package com.airtribe.meditrack.service;

import com.airtribe.meditrack.contracts.AppointmentObserver;
import com.airtribe.meditrack.entity.Appointment;
import com.airtribe.meditrack.util.DateUtil;

/**
 * Observer pattern (Bonus B): prints a console reminder whenever an
 * appointment's lifecycle changes, decoupled from AppointmentService itself.
 */
public class ConsoleReminderObserver implements AppointmentObserver {

    @Override
    public void onAppointmentEvent(Appointment appointment, String eventType) {
        System.out.println("[REMINDER] Appointment " + appointment.getId() + " " + eventType
                + " -> " + appointment.getPatient().getName() + " with Dr. " + appointment.getDoctor().getName()
                + " at " + DateUtil.format(appointment.getDateTime()));
    }
}
