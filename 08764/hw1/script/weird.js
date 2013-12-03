/**
* @file weird.js
* @description: for whole site
* @author: Yue Xing, yuexing@andrew.cmu.edu, 08764
*/
weird = (function() {
	weird = window.weird || {};
	/*
    * get element by id
    * @param string
    * @return HTMLElement
    */
	weird.gEl = function(el) {
		//string 
		if ('string' == typeof el || el instanceof String) {
			return document.getElementById(el);
		} else if (el && el.nodeName && (el.nodeType == 1 || el.nodeType == 9)) {
			return el;
		}
		return null;
    };

	//trim string
	(function() {
		var trimer = new RegExp("(^[\t\xa0\u3000]+)|([\u3000\xa0\t]+$)", "g");
		weird.trim = function(source) {
			return String(source).replace(trimer, "");
		};
	})();

	/**
    * escape for RegExp： .*+?^=!:${}()|[\]/\
    * @param {string} source 
    * @return {string} 
    */
	weird.escapeReg = function(source) {
		return String(source).replace(new RegExp("([.*+?^=!:\x24{}()|[\\]\/\\\\])", "g"), '\\\x241');
	};

	/**
     * get Element by classname
     * 
     * @param {string}             className        
     * @param {HTMLElement|string} element optional start element, default document
     * @param {string}             tagName optional 
     * @return {Array} 
     */
	weird.qEl = function(className, element, tagName) {
		var result = [],
		trim = weird.trim,
		len,
		i,
		elements,
		node;

		if (! (className = trim(className))) {
			return null;
		}
        
		if ('undefined' == typeof element) {
			element = document;
		} else {
			element = weird.gEl(element);
			if (!element) {
				return result;
			}
		} //if

		// tagName
		tagName && (tagName = trim(tagName).toUpperCase());

		// 查询元素
		if (element.getElementsByClassName) {
			elements = element.getElementsByClassName(className);
			len = elements.length;
			for (i = 0; i < len; i++) {
				node = elements[i];
				if (tagName && node.tagName != tagName) {
					continue;
				} //if
				result[result.length] = node;
			} //for
		} else {
			className = new RegExp("(^|\\s)" + weird.escapeReg(className) + "(\\s|\x24)");
			elements = tagName ? element.getElementsByTagName(tagName) : (element.all || element.getElementsByTagName("*"));
			len = elements.length;
			for (i = 0; i < len; i++) {
				node = elements[i];
				className.test(node.className) && (result[result.length] = node);
			} //for
		} //if
        
		return result;
	}
    
    /*
     * get children by id or El
     *
     * @param string|HTMLElement
     * @return Array
     */
    weird.children=function(el){
	    el = weird.gEl(el);
	    // children for html elements & childnodes for text also
	    var children = el.children;        
	    return children;
	}
    /*
     * firstChild by id or El
     *
     * @param string|HTMLElement
     * @return HTMLElement
     */
    weird.first=function(el){
	    el = weird.gEl(el);
	    for(var tmpChild=el.firstChild; null != tmpChild; tmpChild=tmpChild.nextSibling){
		    if(tmpChild.nodeType==1){
			    return tmpChild;
			    }
		    }
	    return null;
	}
    /*
    * @param string|HTMLElement string
    * @return bool
    */
    weird.hasClass=function(el,className){
	    el = weird.gEl(el);
        var classArray = weird.trim(className).split(/\s+/), 
    	len = classArray.length;

        className = el.className.split(/\s+/).join(" ");

        while (len--) {
            if(!(new RegExp("(^| )" + classArray[len] + "( |\x24)")).test(className)){
                return false;
            }
        }
        return true;
	    }
    /*
    * @param string|HTMLElement
    * @param string className
    * @return HTMLElement
    */
    weird.addClass = function(el,className){
	    el = weird.gEl(el);
        el.className += ' ' + className;
        return el;
	}

    /*
     * @param string|HTMLElement
    * @param string
    * @return HTMLElement
    */
    weird.removeClass=function(el,className){
	    el = weird.gEl(el);
        var classArray = weird.trim(el.className).split(/\s+/), 
    	len = classArray.length,
		clazz = weird.trim(className),
		i,
		item;
		
	    for(i=0;i<len;i++){
		    item = classArray[i];
		    if(clazz == item){
			    //移除
			    classArray.splice(i,1);
			    }
		    }
	    el.className=classArray.join(" ");	
	    return el;
	}
    /*
     * foreach
     * 
     * @param {Array}    array   
     * @param {Function} fun    
     * @return {Array} 
     */
    weird.each = function (array, fun) {
	    var returnValue, item, i, len = array.length;
    
        if ('function' == typeof fun) {
            for (i = 0; i < len; i++) {
                item = array[i];
                returnValue = fun.call(array, item, i);
    		    //允许终止
                if (returnValue === false) {
                break;
                }
            }
        }
        return array;
    }
    
	function underBuild(ev){
        ev = ev || window.event;
        ev.preventDefault&&ev.preventDefault();
        ev.returnValue = false;
        ev.cancelBubble = true;
        ev.stopPropagation&&ev.stopPropagation();
        alert("Still Under Build... :)");        
    }
    function goPage(ev, page){
        ev = ev || window.event;
        ev.preventDefault&&ev.preventDefault();
        ev.returnValue = false;
        ev.cancelBubble = true;
        ev.stopPropagation&&ev.stopPropagation();
        window.location.href=page;
    }
    
    function onSignUp(form, ev, prop){    
        ev = ev || window.event;//IE
        //alert(form.story.value)
        //ev.preventDefault();
        if(form.email&&weird.trim(form.email.value)==""){
            alert("Email can't be empty T T...");
            return false;
        }else if(form.email&&!/^\w+([\.-]?\w+)*@\w+([\.-]?\w+)*(\.\w{2,3})+$/.test(form.email.value)){
            alert("Email Format Is Wrong TT...");
            return false;
        }else if(form.fname&&weird.trim(form.fname.value)==""){
            alert("First Name can't be empty TT...");
            return false;
        }else if(form.lname&&weird.trim(form.lname.value)==""){
            alert("Last Name can't be empty TT...");
            return false;
        }else if(form.tzone&&form.tzone.selectedIndex==0){
            alert("Time Zone can't be empty TT...");
            return false;
        }else if(form.password&&weird.trim(form.password.value)==""){
            alert("Password can't be empty TT...");
            return false;
        }else if(form.story&&weird.trim(form.story.value)==""){
            alert("Story can't be empty TT...");
            return false;
        }else if(form.comment&&weird.trim(form.comment.value)==""){
            alert("Comment can't be empty TT...");
            return false;
        }else{
            alert(prop +" Success! And Jump To Target Now!");
        }
        return true;
    }
    
    // nav building...
    var nav = weird.qEl("nav")[0];
    var nav_lis = weird.children(nav);    
    weird.each(nav_lis,function(li){
        if(li.className=="hotweird"){
            weird.first(li).onclick=underBuild;            
        }else if(li.className=="chicks"){
            weird.first(li).onclick=underBuild;
        }else if(li.className=="groups"){
            weird.first(li).onclick=underBuild;
        }else if(li.className=="aboutus"){
            weird.first(li).onclick=underBuild;
        }
    });
    
    //go to signup.htm
    var joinuss = weird.qEl("joinus");
    weird.each(joinuss, function(joinus){
        joinus.onclick=function(ev){goPage(ev,"register.html")};
    });
   //go to index.htm
    var indexs = weird.qEl("gindex");
    weird.each(indexs, function(index){
        index.onclick=function(ev){goPage(ev,"welcome.html")};
    });
    //go to home.htm
    var ghomes = weird.qEl("ghome");
    weird.each(ghomes, function(ghome){
        ghome.onclick=function(ev){goPage(ev,"home.html")};
    });
    //go to edit.htm
    var gedits = weird.qEl("gedit");
    weird.each(gedits, function(gedit){
        gedit.onclick=function(ev){goPage(ev,"profile.html")};
    });
    //go to comment.htm
    var gcomments = weird.qEl("gcomment");
    weird.each(gcomments, function(gcomment){
        gcomment.onclick=function(ev){goPage(ev,"comment.html")};
    });
    //go to story.htm
    var gstorys = weird.qEl("gstory");
    weird.each(gstorys, function(gstory){
        gstory.onclick=function(ev){goPage(ev,"story.html")};
    });
    //go to search.htm
    var gsearchs = weird.qEl("gsearch");
    weird.each(gsearchs, function(gsearch){
        gsearch.onclick=function(ev){goPage(ev,"search.html")};
    });
    
   //signin
   var signin_form = weird.qEl("signin")[0];
   if(signin_form)signin_form.onsubmit = function(ev){return onSignUp(this, ev, "Log In")};
   
   //signup
   var signups = weird.qEl("signup");   
   weird.each(signups, function(signup){
       var signup_form = signup.getElementsByTagName("FORM")[0];
       //alert(signup_form)
       if(!signup_form)return;
       if(signup_form.name=="edit"){
            signup_form.onsubmit = function(ev){return onSignUp(this, ev, "Edit")};
       }else if(signup_form.name=="joinus"){
           signup_form.onsubmit = function(ev){return onSignUp(this, ev, "Sign Up")};
       }else if(signup_form.name=="story"){
           signup_form.onsubmit = function(ev){return onSignUp(this, ev, "Post Weird")};
       }else if(signup_form.name=="comment"){
           signup_form.onsubmit = function(ev){return onSignUp(this, ev, "Post Comment")};
       }
       
   });
   
   //home tab
   var xbar = weird.qEl("xbar")[0];
   if(xbar){
       var xbar_as = xbar.getElementsByTagName("a");
       weird.each(xbar_as, function(xbar_a, num){
      
      xbar_a.onclick = function(ev){
         ev = ev || window.event;
         ev.preventDefault&&ev.preventDefault();
         ev.returnValue = false;
         //now
         if(weird.hasClass(this,"now")){
             return;
         }else{
             var len = xbar_as.length;             
             for(var i=0; i<len; i++){
                var xbar_tmp = xbar_as[i];
                var frame = xbar_tmp.getAttribute("target");
                if(num == i){
                    weird.addClass(xbar_tmp,"now");
                    weird.qEl(frame)[0].style.display="block";
                }else{
                    weird.removeClass(xbar_tmp,"now");
                    weird.qEl(frame)[0].style.display="none";
                    }
             }
             }
      }; //onclick
   });
       }
   
   
   //friend kickout
   var kickouts = weird.qEl("kickout");
   weird.each(kickouts, function(kickout){
       kickout.onclick = function(ev){
           ev = ev || window.event;
           ev.preventDefault&&ev.preventDefault();
           ev.returnValue = false;
           kickout_li =  kickout.parentNode.parentNode;
           kickout_li.parentNode.removeChild(kickout_li);
           }
           });   
   return weird;
} ());