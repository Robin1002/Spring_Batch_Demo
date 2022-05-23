package com.javatechie.spring.batch.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "CUSTOMERS_INFO")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Customer {

    @Id
    int issuerMemberId;
    String oldPan;
    String oldExpirationDate;
    
    
    String newPan;
    String newExpirationDate;
    String reasonCode;
    String effectiveDate;


}
