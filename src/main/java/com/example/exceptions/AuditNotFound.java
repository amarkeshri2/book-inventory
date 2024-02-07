package com.example.exceptions;

public class AuditNotFound extends RuntimeException{
     public AuditNotFound(String message) {
         super(message);
     }
}
