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

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet that adds a new comment to Datastore. */
@WebServlet("/new-comment")
public class NewCommentServlet extends HttpServlet {

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String commentText = getCommentText(request);
    String commentAuthor = getCommentAuthor(request);
    long commentTime = System.currentTimeMillis();

    Entity commentEntity = new Entity("Comment");
    commentEntity.setProperty("text", commentText);
    commentEntity.setProperty("author", commentAuthor);
    commentEntity.setProperty("time", commentTime);

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    datastore.put(commentEntity);
  }

  /** Returns the text entered by the user. */
  private String getCommentText(HttpServletRequest request) {
    return request.getParameter("comment-text");
  }

  /** Returns the author entered by the user, or the user's email if left blank. */
  private String getCommentAuthor(HttpServletRequest request) {
    // Get the input from the form.
    String author = request.getParameter("comment-author");
    
    // Account for a blank response.
    if (author.equals("")) {
      UserService userService = UserServiceFactory.getUserService();
      String email = userService.getCurrentUser().getEmail();
      if (email == null) {
        return "Anonymous";
      } else {
        return email;
      }
    }

    return author;
  }
}
