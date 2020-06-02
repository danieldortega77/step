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
 * Fetches comments from the servers and adds them to the DOM.
 */
async function getComments() {
  const maxComments = document.getElementById('max-comments').value;
  const response = await fetch('/list-comments?max-comments=' + maxComments);
  const comments = await response.json();
  const commentsElement = document.getElementById('comments-container');
  commentsElement.innerHTML = '';

  for (var comment of comments) {
    commentsElement.appendChild(createCommentElement(comment))
  }
}

/**
 * Creates an element containing a comment.
 */
function createCommentElement(comment) {
  const commentElement = document.createElement('div');
  commentElement.innerHTML = '';
  commentElement.setAttribute("id", comment.id);

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
