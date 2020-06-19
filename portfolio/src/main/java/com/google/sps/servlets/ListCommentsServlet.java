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
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gson.Gson;
import com.google.sps.data.Comment;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet that lists the comments from Datastore. */
@WebServlet("/list-comments")
public class ListCommentsServlet extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    Query query = new Query("Comment").addSort("time", SortDirection.DESCENDING);
    int maxComments = getMaxComments(request);
    double maxToxicity = getMaxToxicity(request);

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    PreparedQuery results = datastore.prepare(query);

    List<Comment> comments = new ArrayList<>();
    for (Entity entity : results.asIterable()) {
      String commentText = (String) entity.getProperty("text");
      String id = (String) entity.getProperty("author");
      String commentAuthor = getNickname(id);
      long commentTimeMS = (long) entity.getProperty("time");
      Date commentTime = new Date(commentTimeMS);
      double commentToxicity = (double) entity.getProperty("toxicity");

      if (commentToxicity <= maxToxicity) {
        Comment comment = new Comment(commentText, commentAuthor, commentTime, commentToxicity);
        comments.add(comment);
      }

      if (comments.size() == maxComments) {
        break;
      }
    }

    // Convert the comments to JSON
    String json = convertToJson(comments);

    // Send the JSON as the response
    response.setContentType("application/json;");
    response.getWriter().println(json);
  }

  private String convertToJson(List comments) {
    Gson gson = new Gson();
    String json = gson.toJson(comments);
    return json;
  }

  /** Returns the maximum number of comments entered by the user. */
  private int getMaxComments(HttpServletRequest request) {
    // Get the input from the user.
    String maxCommentsString = request.getParameter("max-comments");

    // Convert the input to an int.
    int maxComments;
    try {
      maxComments = Integer.parseInt(maxCommentsString);
    } catch (NumberFormatException e) {
      System.err.println("Could not convert to int: " + maxCommentsString);
      return -1;
    }

    return maxComments;
  }

  /** Returns the maximum toxicity entered by the user. */
  private double getMaxToxicity(HttpServletRequest request) {
    // Get the input from the user.
    String maxToxicityString = request.getParameter("max-toxicity");

    // Convert the input to a double.
    double maxToxicity;
    try {
      maxToxicity = Double.parseDouble(maxToxicityString);
    } catch (NumberFormatException e) {
      System.err.println("Could not convert to double: " + maxToxicityString);
      return -1;
    }

    return maxToxicity;
  }

  private String getNickname(String id) {
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    Query query =
        new Query("UserInfo")
            .setFilter(new Query.FilterPredicate("id", Query.FilterOperator.EQUAL, id));
    PreparedQuery results = datastore.prepare(query);
    Entity entity = results.asSingleEntity();

    // Account for a blank response.
    if (entity == null) {
      return "Anonymous";
    }

    String nickname = (String) entity.getProperty("nickname");
    return nickname;
  }
}
