package com.tiger.erp.po;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.tiger.erp.common.Excel;
import com.tiger.erp.common.FuzzyQuery;

@Entity
@Table(name = "EMP")
public class Emp {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="ID")
    private Integer id;
    
    @Excel(columnName="label.staffNo")
    @FuzzyQuery
    @Column(name="STAFF_NO")
    private String staffNo;
    
    @Excel(columnName="label.staffName")
    @FuzzyQuery
    @Column(name="STAFF_NAME")
    private String staffName;
    
    @Excel(columnName="label.phone")
    @FuzzyQuery
    @Column(name="PHONE")
    private String phone;
    
    @Excel(columnName="label.address")
    @Column(name="ADDRESS")
    private String address;
    
    @Excel(columnName="label.status")
    @Column(name="STATUS")
    private String status;
    
    @Excel(columnName="label.entryDate",isConvert = true)
    @Column(name="ENTRY_DATE")
    private Date entryDate;
    
    @Column(name="OUT_DATE")
    private Date outDate;
    
    @Excel(columnName="label.emergencyContact")
    @Column(name="EMERGENCY_CONTACT")
    private String emergencyContact;
    
    @Excel(columnName="label.emergencyContactPhone")
    @Column(name="EMERGENCY_CONTACT_PHONE")
    private String emergencyContactPhone;
    
    @Excel(columnName="label.email")
    @Column(name="EMAIL")
    private String email;
    
    @Column(name="DEPT_ID")
    private Integer deptId;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getStaffNo() {
        return staffNo;
    }

    public void setStaffNo(String staffNo) {
        this.staffNo = staffNo;
    }

    public String getStaffName() {
        return staffName;
    }

    public void setStaffName(String staffName) {
        this.staffName = staffName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getEntryDate() {
        return entryDate;
    }

    public void setEntryDate(Date entryDate) {
        this.entryDate = entryDate;
    }

    public Date getOutDate() {
        return outDate;
    }

    public void setOutDate(Date outDate) {
        this.outDate = outDate;
    }

    public String getEmergencyContact() {
        return emergencyContact;
    }

    public void setEmergencyContact(String emergencyContact) {
        this.emergencyContact = emergencyContact;
    }

    public String getEmergencyContactPhone() {
        return emergencyContactPhone;
    }

    public void setEmergencyContactPhone(String emergencyContactPhone) {
        this.emergencyContactPhone = emergencyContactPhone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getDeptId() {
        return deptId;
    }

    public void setDeptId(Integer deptId) {
        this.deptId = deptId;
    }
   
}
