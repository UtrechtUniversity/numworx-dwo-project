	function NewWindow(mypage) 
	{	
		parent.bodyframe.location.href=mypage;
		parent.top.document.body.rows = "100, *"
	}

	function CloseWindow() 
	{
			
		parent.bodyframe.location.href="about:blank";
		parent.top.document.body.rows = "100%, *"
	}
	
	var win;
		
	function NewPopUp(mypage, myname, w, h, scroll) 
	{	var winl = (screen.width - w) / 2;
		var wint = (screen.height - h) / 2;
		winprops = 'height='+h+',width='+w+',top='+wint+',left='+winl+',scrollbars='+scroll+',resizable,status=no';
		win = window.open(mypage, myname, winprops);
		if (parseInt(navigator.appVersion) >= 4) 
		{	win.window.focus(); 
		}
		else
		{ win.window.focus();
		}

		
	}
	
	function FocusPopUp() 
	{	win.window.focus();
	}
	
	function ClosePopUp(mypage) 
	{	win.window.close(mypage);
	}
