
function getComments() {
  fetch('/data').then(response => response.json()).then((comments) => {

    const commentsListElement = document.getElementById('server-comment-container');
    commentsListElement.innerHTML = '';
    for (index = 0; index < comments.length; index++) { 
      console.log(comments[index]);
      commentsListElement.appendChild(
        createListElement(comments[index]));
    } 
  });
}

/** Creates an <li> element containing text. */
function createListElement(text) {
  const liElement = document.createElement('li');
  liElement.innerText = text;
  return liElement;
}

window.onload = () => {
  getComments();
}
