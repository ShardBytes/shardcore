(window.webpackJsonp=window.webpackJsonp||[]).push([[0],{12:function(e,t,n){e.exports=n(23)},20:function(e,t,n){},23:function(e,t,n){"use strict";n.r(t);var a=n(1),r=n.n(a),o=n(9),c=n.n(o),l=(n(17),n(18),n(20),n(2)),i=n(3),u=n(5),s=n(4),m=n(6),h=function(e){function t(e){var n;return Object(l.a)(this,t),(n=Object(u.a)(this,Object(s.a)(t).call(this,e))).state={data:null},n}return Object(m.a)(t,e),Object(i.a)(t,[{key:"componentDidMount",value:function(){var e=this;fetch("https://api.memeload.us/v1/random.php").then(function(e){return e.json()}).then(function(t){e.setState({data:t})}).catch(function(e){return console.log(e)})}},{key:"render",value:function(){var e=this.state.data;return e?r.a.createElement("div",{className:"text-center"},r.a.createElement("div",{className:"align-center"},r.a.createElement("h1",null,e.title),r.a.createElement("p",null,"/r/",e.subreddit),r.a.createElement("p",null,"Author: ",e.author),r.a.createElement("img",{className:"w-100",alt:"meme",src:e.image}))):r.a.createElement("h1",null,"Loading...")}}]),t}(r.a.Component),d=n(25),p=function(e){function t(){return Object(l.a)(this,t),Object(u.a)(this,Object(s.a)(t).apply(this,arguments))}return Object(m.a)(t,e),Object(i.a)(t,[{key:"render",value:function(){return r.a.createElement(d.a,null,r.a.createElement(h,null))}}]),t}(r.a.Component);Boolean("localhost"===window.location.hostname||"[::1]"===window.location.hostname||window.location.hostname.match(/^127(?:\.(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)){3}$/));c.a.render(r.a.createElement(p,null),document.getElementById("root")),"serviceWorker"in navigator&&navigator.serviceWorker.ready.then(function(e){e.unregister()})}},[[12,1,2]]]);
//# sourceMappingURL=main.fe94e66c.chunk.js.map