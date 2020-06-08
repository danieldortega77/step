package com.google.sps.data;

/** Class that holds user's login information */
public class UserInfo {

  private final String email;
  private final boolean isLoggedIn;

  public UserInfo(String email, boolean isLoggedIn) {
    this.email = email;
    this.isLoggedIn = isLoggedIn;
  }
  
  public String getEmail() {
      return email;
  }

  public boolean isLoggedIn() {
      return isLoggedIn;
  }
}
