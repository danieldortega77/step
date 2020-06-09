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
async function loadElements(page) {
  // Insert the navbar
  await htmlInject('navbar.html', 'navbar-container');
  var navbarOptions = document.querySelectorAll('.nav-item')
  // Select and then highlight the current page's name in the navbar
  navbarOptions[page].classList.add('active');
  
  // Insert social media and comment section, if present in page
  await htmlInject('socials.html', 'socials-container');
  await htmlInject('comments.html', 'comments-container');

  const loginResponse = await fetch('/login');
  const userInfo = await loginResponse.json();

  if (!(userInfo.isLoggedIn)) {
    document.getElementById('comment-submission').innerHTML = '';
    document.getElementById('dropdown-nickname').remove();
    document.getElementById('dropdown-logout').remove();
  } else {
    document.getElementById('dropdown-login').remove();
    displayNickname();
  }

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
function updateCommentSection() {
  // Wait for the form to submit before resetting
  setTimeout(function(){ 
    document.getElementById('comment-form').reset();
    getComments();
    }, 1000);
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
  const response = await fetch('/list-comments?max-comments=' + maxComments);
  const comments = await response.json();
  const commentsElement = document.getElementById('comments-list');
  commentsElement.innerHTML = '';

  if (comments.length == 0) {
    document.getElementById('comment-section').style.visibility = 'hidden';
  } else {
    document.getElementById('comment-section').style.visibility = 'visible';
    for (var comment of comments) {
      commentsElement.appendChild(createCommentElement(comment));
    }
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
  const confirmation = confirm('Are you sure you want to delete all comments?');
  if (confirmation) {
    const response = await fetch('/delete-comments', {method: 'POST'});
    getComments();
  }
}

/**
 * Displays user's nickname on the 'change nickname' webpage
 */
async function displayNickname() {
  const response = await fetch('/nickname');
  const nickname = await response.text();
  const element = document.getElementById('nickname-greeting');
  if (element) {
    element.innerHTML = 'Your current nickname is ' + nickname + '.';
  }
}

/**
 * Creates a map of my favorite places in Orange County
 */
function createMap() {
  const map = new google.maps.Map(document.getElementById('map'), {
    center: {lat: 33.6, lng: -117.77},
    zoom: 10
  });

  addLandmark(map, 33.649397, -117.742604, 'Irvine Spectrum Center', 'places/spectrum.html');
  addLandmark(map, 33.574302, -117.840324, 'Crystal Cove State Park', 'places/crystalcove.html');
  addLandmark(map, 33.476702, -117.720383, 'Salt Creek Beach', 'places/saltcreek.html');
  addLandmark(map, 33.558982, -117.668982, 'The Shops at Mission Viejo', 'places/mvmall.html');
  addLandmark(map, 33.576321, -117.726727, 'Regal Edwards Aliso Viejo & IMAX', 'places/movies.html');
  addLandmark(map, 33.690977, -117.888958, 'South Coast Plaza', 'places/scplaza.html');
  addLandmark(map, 33.812095, -117.918980, 'Disneyland Park', 'places/disney.html');
  addLandmark(map, 33.637634, -117.592207, 'Mathnasium of RSM', 'places/mathnasium.html');
  
}

/**
 * Adds a marker that shows an info window when clicked.
 */
function addLandmark(map, lat, lng, title, URL) {
  const marker = new google.maps.Marker({
    position: {lat: lat, lng: lng},
    map: map,
    animation: google.maps.Animation.DROP});

  const infoWindow = new google.maps.InfoWindow({content: title});
  marker.addListener('click', async () => {
    infoWindow.open(map, marker);
    await htmlInject(URL, 'description-container');
    setTimeout(() => { infoWindow.close(map, marker); }, 4000);
  });
}
