package com.eventbooking.util;

import org.mindrot.jbcrypt.BCrypt;

/**
 * Utility class để generate BCrypt hash cho password
 * Chạy main method để generate hash
 */
public class PasswordHashGenerator {
  public static void main(String[] args) {
    String password = "password123";
    String hash = BCrypt.hashpw(password, BCrypt.gensalt());
    System.out.println("Password: " + password);
    System.out.println("BCrypt Hash: " + hash);
    
    // Test verify
    boolean matches = BCrypt.checkpw(password, hash);
    System.out.println("Verify result: " + matches);
  }
}

