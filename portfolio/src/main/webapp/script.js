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
 * 'page' is the index of the current page according to the order in the navbar.
 */
async function loadElements(page) {
  // Insert the navbar
  await htmlInject('navbar.html', 'navbar-container');
  var navbarOptions = document.querySelectorAll('.nav-item')
  // Select and then highlight the current page's name in the navbar
  navbarOptions[page].classList.add("active");

  // Insert social media and comment section, if present in page
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
 * Updates the comment section after a new comment is submitted
 */
async function updateCommentSection() {
  const textElement = document.getElementById("comment-text");
  const authorElement = document.getElementById("comment-author");

  if (textElement && authorElement) {
    const text = textElement.value;
    const author = authorElement.value;
    const toxicity = await getToxicity(text);
    const response = await fetch('/new-comment', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({text: text, author: author, toxicity: toxicity})});
    textElement.value = '';
    authorElement.value = '';
  }

  await getComments();
}

/**
 * Fetches comments from the servers and adds them to the DOM.
 */
async function getComments() {
  const maxCommentsElement = document.getElementById('max-comments');
  if (!maxCommentsElement) {
    return;
  }
  const maxComments = maxCommentsElement.value;
  const maxToxicityElement = document.getElementById('max-toxicity');
  if (!maxToxicityElement) {
    return;
  }
  const maxToxicity = maxToxicityElement.value;
  const response = await fetch('/list-comments?max-comments=' + maxComments + '&max-toxicity=' + maxToxicity);
  const comments = await response.json();
  const commentsElement = document.getElementById('comments-list');
  commentsElement.innerHTML = '';

  for (var comment of comments) {
    commentsElement.appendChild(createCommentElement(comment));
  }

  if (commentsElement.innerHTML === '') {
    document.getElementById('comment-section').style.visibility = 'hidden';
  } else {
    document.getElementById('comment-section').style.visibility = 'visible';
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
  commentElement.appendChild(createAnyElement('p',  comment.toxicity));

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
  const confirmation = confirm('Are you sure you want to delete all comments?');
  if (confirmation) {
    const response = await fetch('/delete-comments', {method: 'POST'});
    getComments();
  }
}

/**
 * Returns the toxicity of the text input.
 */
async function getToxicity(text) {
  const response = await fetch(
  'https://commentanalyzer.googleapis.com/v1alpha1/comments:analyze?key=API_KEY_HERE',
  {
    method: 'POST',
    headers: {'Content-Type': 'application/json'},
    body: JSON.stringify({comment: {text: text}, languages: [], requestedAttributes: { TOXICITY: {} }})
  });
  const data = await response.json();
  console.log(data);
  console.log(data.attributeScores.TOXICITY.summaryScore.value);
  return data.attributeScores.TOXICITY.summaryScore.value
}
