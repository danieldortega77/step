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

/**
 * Loads all elements of the page that are factored out originally.
 */
async function loadElements() {
  await htmlInject('navbar.html', 'navbar-container');
  await htmlInject('socials.html', 'socials-container');
  await htmlInject('comments.html', 'comments-container');
  getComments();
}

/**
 * Replaces the inner HTML of the targetID element with the HTML in templatePath.
 */
async function htmlInject(templatePath, targetID) {
  const target = document.getElementById(targetID);
  if (target) {
    const response = await fetch(templatePath);
    const htmlText = await response.text();
    target.innerHTML = htmlText;
  }
}

/**
 * Fetches comments from the servers and adds them to the DOM.
 */
async function getComments() {
  const maxComments = document.getElementById('max-comments').value;
  const response = await fetch('/list-comments?max-comments=' + maxComments);
  const comments = await response.json();
  const commentsElement = document.getElementById('comments-list');
  commentsElement.innerHTML = '';

  for (var comment of comments) {
    commentsElement.appendChild(createCommentElement(comment));
  }

  if (commentsElement.innerHTML === '') {
    document.getElementById("comment-section").style.visibility="hidden";
  } else {
    document.getElementById("comment-section").style.visibility="visible";
  }
}

/**
 * Creates an element containing a comment.
 */
function createCommentElement(comment) {
  const commentElement = document.createElement('div');
  commentElement.innerHTML = '';

  commentElement.appendChild(createAnyElement('h5', comment.author));
  commentElement.appendChild(createAnyElement('h6', comment.time));
  commentElement.appendChild(createAnyElement('p',  comment.text));

  return commentElement;
}

function createAnyElement(tag, text) {
  const textElement = document.createElement(tag);
  textElement.innerText = text;
  return textElement;
}

/**
 * Deletes all comments from the database.
 */
async function deleteComments() {
  var confirmation = confirm("Are you sure you want to delete all comments?");
  if (confirmation == true) {
    const response = await fetch('/delete-comments', {method: 'POST'});
    getComments();
    return;
  } else {
    return;
  }
}
