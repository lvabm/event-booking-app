package com.eventbooking.util;

import org.mindrot.jbcrypt.BCrypt;

public class GeneratePasswordHash {
  public static void main(String[] args) {
    String password = "password123";
    String hash = BCrypt.hashpw(password, BCrypt.gensalt());
    System.out.println("Password: " + password);
    System.out.println("BCrypt Hash: " + hash);
    
    // Verify
    boolean matches = BCrypt.checkpw(password, hash);
    System.out.println("Verify: " + matches);
  }
}

