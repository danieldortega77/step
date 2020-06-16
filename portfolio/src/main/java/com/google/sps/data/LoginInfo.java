package com.google.sps.data;

/** Class that holds user's login information */
public class LoginInfo {

  private final boolean isLoggedIn;
  private final String loginUrl;
  private final String logoutUrl;

  public LoginInfo(boolean isLoggedIn, String loginUrl, String logoutUrl) {
    this.isLoggedIn = isLoggedIn;
    this.loginUrl = loginUrl;
    this.logoutUrl = logoutUrl;
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
