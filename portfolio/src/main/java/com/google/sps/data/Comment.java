package com.google.sps.data;

import java.util.Date;

/** Class representing a comment */
public class Comment {

  private final long id;
  private final String text;
  private final String author;
  private final Date time;

  public Comment(long id, String text, String author) {
    this.id = id;
    this.text = text;
    this.author = author;
    this.time = new Date();
  }

  public Comment(long id, String text, String author, Date time) {
    this.id = id;
    this.text = text;
    this.author = author;
    this.time = time;
  }

  public long getID() {
      return id;
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
