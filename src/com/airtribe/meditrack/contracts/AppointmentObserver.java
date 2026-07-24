package com.airtribe.meditrack.contracts;

import com.airtribe.meditrack.entity.Appointment;

/**
 * Observer pattern participant notified of appointment lifecycle events.
 */
public interface AppointmentObserver {

    void onAppointmentEvent(Appointment appointment, String eventType);
}
