function Helpers() {

}

Helpers.selectTableRow = function(event) {
	if (event.type == "keypress" && event.keyCode != 32) return;

	var $el = $(event.target);
	$el = $el.first();
	
	while ($el.get(0).tagName != "TR") {
		$el = $el.parent();
	}
	
	var $input = $el.find("input");
	var form = $input[0].form;
	
	if ($input[0].checked == false) $input[0].checked = true;
	else $input[0].checked = false;		
		
	for (i = 0; i < form.elements.length; i++) {
		el = form.elements[i];		
		if ( (el.type=="radio" || el.type=="checkbox") && el.checked ) $(el.parentElement.parentElement).addClass("selected");
		else $(el.parentElement.parentElement).removeClass("selected");
	}
}


Helpers.resizeHelpSection = function(event) {
	var $help = $(".help");
	if ($(window).outerWidth() > (1366)) {
		$help.addClass('active desktop');			
		if ($(window).outerWidth() > (1366 + 158)) $help.addClass('col-3'); else $help.removeClass('col-3');
		if ($(window).outerWidth() > (1366 + 158 + 1 * 79)) $help.addClass('col-4'); else $help.removeClass('col-4');
		if ($(window).outerWidth() > (1366 + 158 + 2 * 79)) $help.addClass('col-5'); else $help.removeClass('col-5');
		if ($(window).outerWidth() > (1366 + 158 + 3 * 79)) $help.addClass('col-6'); else $help.removeClass('col-6');
		if ($(window).outerWidth() > (1366 + 158 + 4 * 79)) $help.addClass('col-7'); else $help.removeClass('col-7');
		if ($(window).outerWidth() > (1366 + 158 + 5 * 79)) $help.addClass('col-8'); else $help.removeClass('col-8');
		if ($(window).outerWidth() > (1366 + 158 + 6 * 79)) $help.addClass('col-9'); else $help.removeClass('col-9');
		if ($(window).outerWidth() > (1366 + 158 + 7 * 79)) $help.addClass('col-10'); else $help.removeClass('col-10');
		if ($(window).outerWidth() > (1366 + 158 + 8 * 79)) $help.addClass('col-11'); else $help.removeClass('col-11');		
	}		
	else $help.removeClass('active desktop col-3 col-4 col-5 col-6');
}

Helpers.toggleHelpSection = function() {
	$(this).parent().toggleClass('active'); 
	$(this).parent().css('z-index','9999');
}

Helpers.stretchHeight = function(elements) {
	console.log("STRETCH!");
	if (elements.length < 1) return;
	
	subpanel = elements[0].closest('.subpanel');
	subpanelHeight = subpanel.outerHeight();
	bodyHeight = $(document.body).outerHeight();
	freeSpace = bodyHeight - subpanelHeight;
	
	console.log(bodyHeight);
	
	for(i=0; i<elements.length; i++) {
		newHeight = elements[i].height() + freeSpace;
		elements[i].height(newHeight+"px");
	}
}
