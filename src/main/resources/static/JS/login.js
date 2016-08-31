//Red's Connection
var redsconnection= "http://ukl5cg6195g1q:8080/";
//Mike's Connection
var mikesconnection = "http://UKL5CG6195GRV:8080/";
//LocalHost Connection
var localhost = "http://localhost:8080/";
//Choose a Connection
//var defaultConnection=redsconnection;
var defaultConnection=localhost;

$(function() {
	//Clear previous sessionStorage
	sessionStorage.clear();
	//Get function to retrieve and populate gpNames in select picker.
	$("#login").click(function() {
		var inputname = $("#username").val();
		$.getJSON(defaultConnection+"/user/"+ inputname).success(function(result) {
			if(result.userID >-1){
			$('.uNameReturn').append(" " + result.forename + " " );
			//Add the current user id to the sessionStorage
			sessionStorage.setItem('userID', result.userID);
			if (inputname == (result.username)) {
				$('#username') == result.forename;
				$('#successModal').modal({backdrop: "static"});
				setTimeout(function() { $('#successModal').modal('hide');
				window.location = "home.html"},1000);
			}
			}
			else{
				$('#errorModal').modal({backdrop: "static"});
			}
		}).fail(function(d, status, error) {
			$('#errorModal').modal({backdrop: "static"});
		});
	});
});
