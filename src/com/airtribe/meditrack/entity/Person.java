package com.airtribe.meditrack.entity;

import com.airtribe.meditrack.exception.InvalidDataException;
import com.airtribe.meditrack.util.Validator;

/**
 * Common identity fields for the humans in the system. Doctor and Patient
 * both extend this class and chain into {@link MedicalEntity} via super().
 */
public abstract class Person extends MedicalEntity {

    private static final long serialVersionUID = 1L;

    private String name;
    private int age;
    private String gender;
    private String contactNumber;
    private String email;

    protected Person(String id, String name, int age, String gender, String contactNumber, String email)
            throws InvalidDataException {
        super(id);
        setName(name);
        setAge(age);
        this.gender = gender;
        setContactNumber(contactNumber);
        setEmail(email);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) throws InvalidDataException {
        Validator.validateNonEmpty(name, "Name");
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) throws InvalidDataException {
        Validator.validateAge(age);
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) throws InvalidDataException {
        Validator.validatePhone(contactNumber);
        this.contactNumber = contactNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) throws InvalidDataException {
        Validator.validateEmail(email);
        this.email = email;
    }

    public abstract String getRole();

    @Override
    public String describe() {
        return getRole() + " [" + getId() + "] " + name + ", Age: " + age;
    }
}
