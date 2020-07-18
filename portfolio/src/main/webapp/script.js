

function post(path, params, method='post') {
  const form = document.createElement('form');
  form.method = method;
  form.action = path;

  for (const key in params) {
    if (params.hasOwnProperty(key)) {
      const hiddenField = document.createElement('input');
      hiddenField.type = 'hidden';
      hiddenField.name = key;
      hiddenField.value = params[key];

      form.appendChild(hiddenField);
    }
  }

  document.body.appendChild(form);
  form.submit();
}

function redirectTo(sUrl) {
  window.location = sUrl
}

function getStatus() {
  fetch('/get_status').then(response => response.json()).then((status) => {
    if(status.logged_in){
      
    }
    else{
      document.getElementById("comment").disbaled = true;
    }
  });
}


window.onload = () => {
  getStatus();
}
