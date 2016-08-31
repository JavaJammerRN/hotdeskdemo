$(function() {

	//Get function to retrieve and populate gpNames in select picker.
	$("#login").click(function() {
		var inputname = $("#username").val();
		$.getJSON("http://UKL5CG6195G1Q:8080/user/"+ inputname).success(function(result) {
				$('.uNameReturn').append(" " + result.forename + " " );
				sessionStorage.setItem('userID', result.userID);

					if (inputname == (result.username)) {

						$('#username') == result.forename;
						
						$('#successModal').modal({backdrop: "static"});
						
						setTimeout(function() { $('#successModal').modal('hide');
							window.location = "home.html"},1000);
						}


		}).fail(function(d, status, error) {
		$('#errorModal').modal({backdrop: "static"});
	});

	});

	

});



