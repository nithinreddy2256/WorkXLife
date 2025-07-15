package com.workxlife.employer_service.model;

import jakarta.persistence.*;

@Entity
@Table(name = "employers")
public class Employer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String companyName;
    private String industry;
    private String website;
    private String location;
    private String size;
    private String establishedYear;
    private String about;

    private String contactName;
    private String contactEmail;
    private String contactPhone;

    private String recruiterName;
    private String recruiterRole;
    private String recruiterBio;

    public Employer() {
    }

    public Employer(Long id, String companyName, String industry, String website, String location, String size,
                    String establishedYear, String about, String contactName, String contactEmail,
                    String contactPhone, String recruiterName, String recruiterRole, String recruiterBio) {
        this.id = id;
        this.companyName = companyName;
        this.industry = industry;
        this.website = website;
        this.location = location;
        this.size = size;
        this.establishedYear = establishedYear;
        this.about = about;
        this.contactName = contactName;
        this.contactEmail = contactEmail;
        this.contactPhone = contactPhone;
        this.recruiterName = recruiterName;
        this.recruiterRole = recruiterRole;
        this.recruiterBio = recruiterBio;
    }

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getIndustry() {
        return industry;
    }

    public void setIndustry(String industry) {
        this.industry = industry;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getEstablishedYear() {
        return establishedYear;
    }

    public void setEstablishedYear(String establishedYear) {
        this.establishedYear = establishedYear;
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        this.about = about;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public String getRecruiterName() {
        return recruiterName;
    }

    public void setRecruiterName(String recruiterName) {
        this.recruiterName = recruiterName;
    }

    public String getRecruiterRole() {
        return recruiterRole;
    }

    public void setRecruiterRole(String recruiterRole) {
        this.recruiterRole = recruiterRole;
    }

    public String getRecruiterBio() {
        return recruiterBio;
    }

    public void setRecruiterBio(String recruiterBio) {
        this.recruiterBio = recruiterBio;
    }
}
