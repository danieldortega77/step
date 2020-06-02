package com.google.sps.data;

import java.util.Date;

/** Class representing a comment */
public class Comment {

  private final String text;
  private final String author;
  private final Date time = new Date();

  public Comment(String text, String author) {
    this.text = text;
    this.author = author;
  }
  
  public String getText() {
      return text;
  }

  public String getAuthor() {
      return author;
  }

  public Date getTime() {
      return time;
  }
}