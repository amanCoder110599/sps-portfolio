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
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import java.io.IOException;
import java.io.PrintWriter;
import com.google.gson.Gson;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.cloud.language.v1.Document;
import com.google.cloud.language.v1.LanguageServiceClient;
import com.google.cloud.language.v1.Sentiment;
import java.util.ArrayList;


@WebServlet("/comment")
public class AddCommentServlet extends HttpServlet {

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

    UserService userService = UserServiceFactory.getUserService();
    String comment = request.getParameter("comment");
    if(comment.length() > 0){
      String email = userService.getCurrentUser().getEmail();
      double score = getScore(comment);
      DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
      Entity commentEntity = new Entity("Comment");
      commentEntity.setProperty("comment", comment);
      commentEntity.setProperty("commentScore", score);
      commentEntity.setProperty("email", email);
      commentEntity.setProperty("timestamp", System.currentTimeMillis());
      
      
      datastore.put(commentEntity);
    }
    
    response.sendRedirect("/index.html");
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    Query query = new Query("Comment").addSort("timestamp", SortDirection.DESCENDING);

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    PreparedQuery results = datastore.prepare(query);

    ArrayList<Comment> comments = new ArrayList<>();
    for (Entity entity : results.asIterable()) {
      long id = entity.getKey().getId();
      String email = entity.getProperty("email").toString();
      String comment = entity.getProperty("comment").toString();
      long timestamp = Long.parseLong(entity.getProperty("timestamp").toString());
      double commentScore = Double.parseDouble(entity.getProperty("commentScore").toString());
      Comment currComment = new Comment(id, comment, commentScore, email, timestamp);
      comments.add(currComment);
    }

    Gson gson = new Gson();

    response.setContentType("application/json;");
    response.getWriter().println(gson.toJson(comments));
  }

  private double getScore(String comment) throws IOException{
    Document doc =
        Document.newBuilder().setContent(comment).setType(Document.Type.PLAIN_TEXT).build();
    LanguageServiceClient languageService = LanguageServiceClient.create();
    Sentiment sentiment = languageService.analyzeSentiment(doc).getDocumentSentiment();
    double score = sentiment.getScore();
    languageService.close();

    return score;
  }
}
