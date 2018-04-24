var $selectableTables,

initSelectableTables = function() {
	$selectableTables.on('click', selectTableRow);
},
selectTableRow = function() {
	var $el = $(this);
	var $input = $el.find("input");
	var form = $input[0].form;
	
	if ($input.prop("checked") == false) {
		$input.prop('checked',true);
	} else {
		$input.prop("checked", false);	
	}		
	
	for (i = 0; i < form.elements.length; i++) {
		el = form.elements[i];		
		if ( (el.type=="radio" || el.type=="checkbox") && !el.checked ) $(el.parentElement.parentElement).removeClass("selected");
	}
	
	$el.addClass("selected");
},

resultIndicator,

initResultIndicators = function() {
	$resultIndicator.each(setResultIndicatorColor);
}
setResultIndicatorColor = function () {
	var $this = $(this), r, g, b, score;
	score = parseInt($this.data("score"));
	
	if (score > 0) {
       
       b = 0;
       g = parseInt( (255 * (score / 50)) );
       r = parseInt( (255 * (1 - (score - 50) / 50)) );

	   $this.css('border-color','rgb('+r+','+g+','+b+')' );
   }
},

// Bubblesort
tableSorter = function(tbody, index, attr, type, asc) {
	// In Vanilla JS for performance	
	
	switching = true;
	j = 0;
	while (switching) {
		j++; if (j > 250000) { break; } // safety stop to avoid endless loops, max 500 items to sort
		
		switching = false;
				
		tr = tbody.getElementsByTagName("TR");
		
		for (i = 0; i < tr.length; i++) {
			shouldSwitch = false;
			
			row1 = tr[i];
			row2 = tr[i+1];
			if (!row2) break;
			
			val1 = 0;
			val2 = 0;
			if (row1.children[index].firstChild) val1 = row1.children[index].firstChild.dataset[attr]; 
			if (row2.children[index].firstChild) val2 = row2.children[index].firstChild.dataset[attr];
				
			console.log(val1+" - "+val2); 
					
			if (type == "string") {
				if ( (!asc && val2.localeCompare(val1) < 0) || (asc && val2.localeCompare(val1) > 0) ) { shouldSwitch = true; break; }
			} else {
				if ( (!asc && val2 < val1) || (asc && val2 > val1) ) { shouldSwitch = true; break; }
			}
						
		}		
		if (shouldSwitch == true) {	
			console.log("switch");		
			if (asc) tr[i].parentNode.insertBefore(tr[i + 1], tr[i]);
			else {
				(tr[i].parentNode).insertBefore(tr[i], tr[i+1].nextSibling);
			}
			switching = true;
		}
	}
	return;
},



$(document).ready(function(){ 
	
	$selectableTables = $("table.selectable tr");
	if ($selectableTables) initSelectableTables();
	
	$resultIndicator = $(".resultIndicator");
	if ($resultIndicator) initResultIndicators();
	
	
	/* UGLY STUFF below */
	
	$('link[rel=stylesheet][href="https://teuniz.dwo.nl/gwtclient/gwtclient/gwt/clean/clean.css"]').remove();
	
	$(".tablewrap").scroll(function() {
		var $this = $(this), left = $this.scrollLeft();
		if (left == 0) $this.removeClass('active');
		else $this.addClass('active');
		$(this).find('td:first-child span').css('left',left+'px');
	});
	
	$("#help h2").click(function() { $(this).parent().toggleClass('active'); $(this).parent().css('z-index','9999');});
	
	//$("#accountMenuToggle").click( function() { $("#accountMenuBox").toggle(); });
	
	$("tr.headers th span").hover(function() { 
		$this = $(this);
		if ($this.hasClass('active')) {
			$this.removeClass('active');
			$this.find('a').remove();
		} else {
			$this.addClass('active');
			$link = ('<a href="javascript:void(0);">Activiteiten</a>');
			$this.append($link);
		}
	});
	
	$('.sortButton').click(function() {		
		$this = $(this);
		$table = $this.parents('table');
		tbody = $table.find('tbody').get(0);
		index = $this.parent().index();
		
		asc = true;
		if ($this.data("order") == "desc") asc = false;
		
		type = "int";
		if ($this.data("type") == "string") type = "string";
		
		attr = "score";
		if ($this.data("attr")) attr = $this.data("attr");
			
		tableSorter(tbody, index, attr, type, asc);	
		
		$('.sortButton').removeClass("active");
		$this.addClass("active");				
	});
		
	$(window).resize( function() {
		if ($(window).outerWidth() > (1366)) {
			$("#help").addClass('active desktop');			
			if ($(window).outerWidth() > (1366 + 158)) $("#help").addClass('col-3'); else $("#help").removeClass('col-3');
			if ($(window).outerWidth() > (1366 + 158 + 1 * 79)) $("#help").addClass('col-4'); else $("#help").removeClass('col-4');
			if ($(window).outerWidth() > (1366 + 158 + 2 * 79)) $("#help").addClass('col-5'); else $("#help").removeClass('col-5');
			if ($(window).outerWidth() > (1366 + 158 + 3 * 79)) $("#help").addClass('col-6'); else $("#help").removeClass('col-6');
			if ($(window).outerWidth() > (1366 + 158 + 4 * 79)) $("#help").addClass('col-7'); else $("#help").removeClass('col-7');
			if ($(window).outerWidth() > (1366 + 158 + 5 * 79)) $("#help").addClass('col-8'); else $("#help").removeClass('col-8');
			if ($(window).outerWidth() > (1366 + 158 + 6 * 79)) $("#help").addClass('col-9'); else $("#help").removeClass('col-9');
			if ($(window).outerWidth() > (1366 + 158 + 7 * 79)) $("#help").addClass('col-10'); else $("#help").removeClass('col-10');
			if ($(window).outerWidth() > (1366 + 158 + 8 * 79)) $("#help").addClass('col-11'); else $("#help").removeClass('col-11');		
		}		
		else $("#help").removeClass('active desktop col-3 col-4 col-5 col-6');
	} );
	$(window).trigger('resize');
	
	
	// Test stuff below
	$(".panel").hide();
	$("#mainPanel").show();
	$("#demoDisplayPanel").show();
	$("#libraryDisplayPanel").hide();
	$("#libraryPageNav").hide(); $(".libraryPageIcon").hide(); $("nav h2").hide();
	if (window.location.hash == "#bibliotheek") { $("body").addClass("libraryPage"); $("#demoDisplayPanel").hide(); $("#libraryDisplayPanel").show(); $("#libraryPageNav").show(); $(".libraryPageIcon").show(); $("nav h2").show(); return; } 
	if (window.location.hash == "#lightbox") { $("body").addClass("overlay"); $(".lightbox").show(); return; }
	if (window.location.hash == "#dialog") { $("body").addClass("overlay"); $(".dialog").show(); return; }
	if (window.location.hash == "#login") { $("#mainPanel").hide(); $("#loginDisplayPanel").show(); return; }
	
	
});