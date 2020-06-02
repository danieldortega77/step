// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps.servlets;

import com.google.sps.data.Comment;
import com.google.gson.Gson;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet that returns some example content. TODO: modify this file to handle comments data */
@WebServlet("/data")
public class DataServlet extends HttpServlet {

  private ArrayList<Comment> comments;

  @Override
  public void init() {
    comments = new ArrayList<Comment>();
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    // Convert the comments to JSON
    String json = convertToJson(comments);

    // Send the JSON as the response
    response.setContentType("application/json;");
    response.getWriter().println(json);
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String commentText = getCommentText(request);
    String commentAuthor = getCommentAuthor(request);
    Comment comment = new Comment(commentText, commentAuthor);

    comments.add(comment);

    response.sendRedirect("/index.html");
  }

  /** Returns the text entered by the user. */
  private String getCommentText(HttpServletRequest request) {
    return request.getParameter("comment-text");
  }

  /** Returns the author entered by the user, or "Anonymous" if left blank. */
  private String getCommentAuthor(HttpServletRequest request) {
    // Get the input from the form.
    String author = request.getParameter("comment-author");
    
    // Account for a blank response.
    if (author.equals("")) {
      return "Anonymous";
    }

    return author;
  }

  private String convertToJson(ArrayList comments) {
    Gson gson = new Gson();
    String json = gson.toJson(comments);
    return json;
  }
}
