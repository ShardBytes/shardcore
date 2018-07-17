var hasClicc = false;

setTimeout(function(){
  if(!hasClicc){
    new Android_Toast({
      content: "Click or tap anywhere!",
      duration: 3000,
      position: "bottom"
    });
  }
}, 5000);

$("#bigimage").click(function(){
  hasClicc = true;
  $.scrollTo(document.getElementById("maincontent"), {duration: 1000});
});
