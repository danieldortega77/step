package com.google.sps.data;

/** Class that holds user's login information */
public class UserInfo {

  private final String email;
  private final boolean isLoggedIn;
  private final String loginUrl;
  private final String logoutUrl;

  public UserInfo(String email, boolean isLoggedIn, String loginUrl, String logoutUrl) {
    this.email = email;
    this.isLoggedIn = isLoggedIn;
    this.loginUrl = loginUrl;
    this.logoutUrl = logoutUrl;
  }
  
  public String getEmail() {
      return email;
  }

  public boolean isLoggedIn() {
      return isLoggedIn;
  }

  public String getLoginUrl() {
      return loginUrl;
  }

  public String getLogoutUrl() {
      return logoutUrl;
  }
}
