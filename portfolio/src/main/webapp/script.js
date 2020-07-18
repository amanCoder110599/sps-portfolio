
function getStatus() {
  fetch('/get_status').then(response => response.json()).then((status) => {
    if(status.logged_in){
      console.log("Hola");
      console.log(status.logoutUrl);
    }
    else{
      console.log("Hello")
      console.log(status.loginUrl);
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
  getStatus();
}
