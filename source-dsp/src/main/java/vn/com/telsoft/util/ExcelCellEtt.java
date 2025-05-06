 /*
  * To change this license header, choose License Headers in Project Properties.
  * To change this template file, choose Tools | Templates
  * and open the template in the editor.
  */
 package vn.com.telsoft.util;

 import java.io.Serializable;
 import java.util.Date;

 /**
  *
  * @author TungLM
  */
 public class ExcelCellEtt implements Serializable {

     String stringValue;
     double numberValue;
     Date dateValue;

     public ExcelCellEtt() {
     }

     public ExcelCellEtt(ExcelCellEtt ett) {
         this.stringValue = ett.stringValue;
         this.numberValue = ett.numberValue;
         this.dateValue = ett.dateValue;
     }

     public String getStringValue() {
         return stringValue;
     }

     public void setStringValue(String stringValue) {
         this.stringValue = stringValue;
     }

     public double getNumberValue() {
         return numberValue;
     }

     public void setNumberValue(double numberValue) {
         this.numberValue = numberValue;
     }

     public Date getDateValue() {
         return dateValue;
     }

     public void setDateValue(Date dateValue) {
         this.dateValue = dateValue;
     }
 }
