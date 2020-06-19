package com.google.sps.data;

import java.util.Date;

/** Class representing a comment */
public class Comment {

  private final String text;
  private final String author;
  private final Date time;
  private final double toxicity;

  public Comment(String text, String author, double toxicity) {
    this.text = text;
    this.author = author;
    this.time = new Date();
    this.toxicity = toxicity;
  }

  public Comment(String text, String author, Date time, double toxicity) {
    this.text = text;
    this.author = author;
    this.time = time;
    this.toxicity = toxicity;
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

  public double getToxicity() {
    return toxicity;
  }
}
