var $selectableTables,

initSelectableTables = function() {
	$selectableTables.on('click', selectTableRow);
},
selectTableRow = function() {
	$el = $(this);
	$input = $el.find("input");
	console.log($input.prop("checked"));
	
	if ($input.prop("checked") == false) {
		$input.prop('checked',true);
		$el.addClass("selected");
	} else {
		$input.prop("checked", false);
		$el.removeClass("selected");
	}
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
}



$(document).ready(function(){ 
	$selectableTables = $("table.selectable tr");
	if ($selectableTables) initSelectableTables();
	
	$resultIndicator = $(".resultIndicator");
	if($resultIndicator) initResultIndicators();
	
	
	/* UGLY STUFF below */
	
	$(".tablewrap").scroll(function() {
		var $this = $(this), left = $this.scrollLeft();
		if (left == 0) $this.removeClass('active');
		else $this.addClass('active');
		$(this).find('td:first-child span').css('left',left+'px');
	});
	
	$("#help h2").click(function() { $(this).parent().toggleClass('active'); $(this).parent().css('z-index','9999');});
	
	$(window).resize( function() {
		console.log($(window).outerWidth());
		if ($(window).outerWidth() > (1342 + 200)) $("#help").addClass('active');
		else $("#help").removeClass('active');
	} );
	$(window).trigger('resize');
	
});