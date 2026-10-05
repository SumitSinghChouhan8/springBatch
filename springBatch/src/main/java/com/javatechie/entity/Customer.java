package com.javatechie.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "Customer_Info")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Customer {

    //id	firstname	lastname	email	contactno	gender	country	dob
 @Id
 @Column(name = "CUSTOMER_ID")
private Integer id;
 @Column(name = "FIRST_NAME")
private String firstname;
 @Column(name ="LAST_NAME" )
private String lastname;
 @Column(name = "EMAIL")
private String email;
 @Column(name="CONTACT")
private String contactno;
 @Column(name="GENDER")
private String gender;
 @Column(name = "COUNTRY")
private String country;
 @Column(name="DOB")
private String dob;

}
