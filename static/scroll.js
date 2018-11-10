var hasClicc = false;

setTimeout(function(){
  if(!hasClicc){
    new Android_Toast({
      content: "Touch the planet!",
      duration: 3000,
      position: "bottom"
    });
  }
}, 1000);

// offset is because of fixedtop navbar !!

$("#bigimage").click(function(){
  hasClicc = true;
  $.scrollTo(document.getElementById("maincontent"), {duration: 1000, offset: 0});
});
