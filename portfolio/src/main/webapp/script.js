//fetch all comments in DB
function getComments() {
  fetch('/comment').then(response => response.json()).then(comments => {
    loadAllComments(comments);
  });
}

// hide/unhide comments form to the user based on the status of login
function loadCommentForm() {
  fetch('/get_status').then(response => response.json()).then(status => {
    if (status.logged_in) {
      CommentForm(status);
    } else {
      SignInLink(status);
    }
  });
}

//parse all elements from the JSON and add to DOM

function loadAllComments(comments){
  let commentList = document.querySelector('div.' + 'commentList');
  
  for (Comment of comments) {
    let UserEmail = document.createElement('p');
    UserEmail.innerText = Comment.email;
    let UserComment = document.createElement('li');
    UserComment.innerText = Comment.comment;
    let commentItem = document.createElement('div');
    commentItem.appendChild(UserEmail);
    commentItem.appendChild(UserComment);
    commentList.appendChild(commentItem);
    var br = document.createElement("br");
    commentList.appendChild(br);
  }
}

// if user is logged in show the comment form
function CommentForm(status) {
  let loggedinSay = document.createElement('p');
  loggedinSay.innerHTML = 'You are logged in as <strong>';
  loggedinSay.innerHTML += status.email;
  loggedinSay.innerHTML += '<strong>. <a href =\"' + status.logoutUrl + '\">Log out</a>.';

  let comment = document.createElement('textarea');
  comment.className = 'text';
  comment.name = 'comment';

  let commentSubmit = document.createElement('input');
  commentSubmit.className = 'button_color'
  commentSubmit.type = 'submit';
  commentSubmit.value = 'Post';

  let commentForm = document.createElement('form');
  commentForm.method = 'POST';
  commentForm.action = '/comment';
  commentForm.appendChild(loggedinSay);
  commentForm.appendChild(comment);
  commentForm.appendChild(commentSubmit);
  document.querySelector('div.' + 'commentDiv')
          .appendChild(commentForm);
}

// if user is not logged in show the sign-in link
function SignInLink(status) {
  let loginAsk = document.createElement('p');
  loginAsk.innerText = 'Sign in to post comments';

  let loginButton = document.createElement('button');
  loginButton.innerHTML = '<a href=\"' + status.loginUrl + '\">Login</a>';

  let loginDiv = document.createElement('div');
  loginDiv.appendChild(loginAsk);
  loginDiv.appendChild(loginButton);

  document.querySelector('div.' + 'commentDiv')
          .appendChild(loginDiv);
}


window.onload = () => {
  getComments();
  loadCommentForm();
}
