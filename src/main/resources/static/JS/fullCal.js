//Red's Connection
var redsconnection= "http://ukl5cg6195g1q:8080/";
//Mike's Connection
var mikesconnection = "http://UKL5CG6195GRV:8080/";
//LocalHost Connection
var localhost = "http://localhost:8080/";
//Choose a Connection
//var defaultConnection=redsconnection;
var defaultConnection=localhost;

$(document).ready(function() {
	sessionStorage.setItem('newEdit',"");
	sessionStorage.setItem('bookingID', -1);
	sessionStorage.setItem(sessionStorage.getItem('location'), "");
	sessionStorage.setItem(sessionStorage.getItem('seatsDate'), null);
	sessionStorage.setItem('startDate', "");
  sessionStorage.setItem('endDate', "");
	var myBooking = new Array();

	$.getJSON(defaultConnection+"booking/user/"+ sessionStorage.getItem('userID'), function(result) {
		$.each(result, function(key,val){
			var booking = new Object();
			booking.title = val.location;
			booking.start = val.startDate;
			var dateEnd=new Date(val.endDate);
			dateEnd.setTime(dateEnd.getTime() + (8*60*60*1000));
			booking.end = dateEnd;
			booking.bookingID = val.bookingID;
			myBooking.push(booking);
		})
		startCal(myBooking);
	});

	//Code that will be executed when the user selects the "Edit" button
	$('#editBooking').click(function(){
		//Set the value of the current session variable to EDIT, so it is usable in the future to check if it is a new booking or an edit one
		sessionStorage.setItem('newEdit',"Edit");
		//Open this link in the same window
		window.open('Booking.html', "_self");
	});

	//When the selected booking has been closed, clear the session
	$('#closeBooking').click(function(){
		//Set the value of the current session variable to NULL, so it is usable in the future to check if it is a new booking or an edit one
		sessionStorage.setItem('newEdit',"Close");
		sessionStorage.setItem("bookingID", -1);
	});


	$('#searchAvailability').click(function(){
		//Set the value of the current session variable to NULL, so it is usable in the future to check if it is a new booking or an edit one
		sessionStorage.setItem('newEdit',"");
		//Add data to the sessionStorage
		sessionStorage.setItem('bookingID', -1);
		sessionStorage.setItem('location', $('#select').val());
		sessionStorage.setItem('startDate', $('#sdate').val());
		sessionStorage.setItem('endDate', $('#edate').val());

		if(sessionStorage.getItem('endDate') == ""){
			//Hide the current modal in order to show the error
			$('#myModal').modal('hide');
			//Instantiate the modal and its data
			$('#insertTextErrors').empty();
			$('#insertTextErrors').append("You must select and End Date for your booking!");
			$('#errorModals').modal({backdrop: "static"});
			setTimeout(function() { $('#errorModals').modal('hide');$('#myModal').modal('show'); },1500);
		}
		else{
			//Verify that the booking is no longer than 14 days
			var startDateSel = new Date($('#sdate').val());
			var endDateSel = new Date($('#edate').val());
			var oneDay = 24*60*60*1000; // hours*minutes*seconds*milliseconds
			var numDaysBetweenDates =  Math.round(Math.abs((startDateSel.getTime() - endDateSel.getTime())/(oneDay)));
			//Verify if the date range is valid
			if(numDaysBetweenDates >= 0 && numDaysBetweenDates < 14){
				//Open this link in the same window
				window.open('Booking.html', "_self");
			}
			else{
				//Hide the current modal in order to show the error
				$('#myModal').modal('hide');
				//Instantiate the modal and its data
				$('#insertTextErrors').empty();
				$('#insertTextErrors').append("A booking cannot have a length greater than 14 days!");
				$('#errorModals').modal({backdrop: "static"});
				setTimeout(function() { $('#errorModals').modal('hide');$('#myModal').modal('show'); },1500);
			}
		}
	});



	function startCal(bk){
		var calendar = $('#calendar').fullCalendar({
			weekends: false, // hides saturdays and sundays
			contentHeight: 450,
			editable: true,
			eventLimit: true, // allow "more" link when too many events
			events:bk,
			header: {
				left: 'prev,next',
				center: 'title',
				right: 'today',
			},

			eventClick: function(event, jsEvent, view){
				eventHasBeenSelected(event.bookingID);
			},

			dayClick: function(date, jsEvent, view){

				//Once the user clicks on the day of the calendar, verify if there is booking for that day
				var eventBookingID=0;
				$('#calendar').fullCalendar('clientEvents', function (event) {
					if(event.start <= date && event.end >= date){
						//if the events match, retrieve the bookingID
						eventBookingID=event.bookingID;
					}
				});
				//Verify if an event has been found
				if(eventBookingID>0){
					//Bring up the booking details
					eventHasBeenSelected(eventBookingID);
				}
				else{
					//Bring up a create a booing popup window
					$('#sdate').val(date.format("YYYY-MM-DD"));
					$('#edate').val("");
					//$('#edate').datepicker('setStartDate', new Date(date.format("YYYY-MM-DD")));
					FromEndDate = new Date(date.format("YYYY-MM-DD"));
					if (moment().format('YYYY-MM-DD') === date.format('YYYY-MM-DD') || date.isAfter(moment())) {
						// This allows today and future date
						$('#myModal').modal({backdrop: "static"});
					} else {
						// Else part is for past dates
						$('#errorModalB').modal({backdrop: "static"});
					}
				}

				$('#edate').datepicker({
					weekStart: 1,
					daysOfWeekDisabled: [0,6],
					format: "yyyy-mm-dd",
					todayHighlight: true,
					autoclose: true,
					todayBtn: true,
					startDate: new Date(date)
				});
			},
		});

		function eventHasBeenSelected(bookingIDRef){
			$('#bookingDetails').empty();
			//Reset the session Variables
			sessionStorage.setItem(sessionStorage.getItem('bookingID'), -1);
			sessionStorage.setItem(sessionStorage.getItem('location'), "");
			sessionStorage.setItem(sessionStorage.getItem('seatsDate'), null);
			//Retrieve data from the API
			$.ajax({
				dataType: "json",
				url:defaultConnection+"/booking/"+sessionStorage.getItem('userID')+"/ref/"+bookingIDRef,
				method: "GET",
				success: function(data){
					$('#bookingDetails').append("<h3> Booking Reference: "+ data.bookingID + "</h3>");
					$('#bookingDetails').append("<h4> Location: "+ data.location + "</h4>");
					//Set the correct data
					sessionStorage.setItem('bookingID', data.bookingID);
					sessionStorage.setItem('location', data.location);
					sessionStorage.setItem('seatsDate', data.seats);
					sessionStorage.setItem('startDate', data.startDate);
					sessionStorage.setItem('endDate', data.endDate);
					//Add each seat to the detail window
					$('#bookingDetails').append("<h4> Desk Details: </h4>\n");
					$('#bookingDetails').append("<table>");
					$('#bookingDetails').append("<tr>");
					$('#bookingDetails').append("<th><center><h4> Date </h4></th>");
					$('#bookingDetails').append("<th><center><h4> Desk Block </h4></th>");
					$('#bookingDetails').append("<th><center><h4>  - Letter </h4></th>");
					$('#bookingDetails').append("</tr>");
					$.each(data.seats, function(){
						$('#bookingDetails').append("<tr>");
						$('#bookingDetails').append("<td class='text-right'><center><h5>"+$(this).attr('date')+"</h5></td>");
						$('#bookingDetails').append("<td class='text-right'><center><h5>"+$(this).attr('deskBlock')+"</h5></td>");
						$('#bookingDetails').append("<td class='text-right'><center><h5>"+$(this).attr('deskLetter')+"</h5></td>");
						$('#bookingDetails').append("</tr>");
						$('#bookingDetails').append("</table>");
					});
				},
				fail: function(){
					$('#insertTextErrors').empty();
					$('#insertTextErrors').append("Error While Retrieving the Information!");
					$('#errorModals').modal({backdrop: "static"});
					setTimeout(function() { $('#errorModals').modal('hide'); },1500);
				}
			});
			$('#eventModal').modal({backdrop: "static"});
		}

		//Code that will be executed when the user selects the "Delete" button
		$('#del').click(function(){
			$('#eventModal').modal('hide');
			$('#deleteModal').modal({backdrop: "static"});
		});

		//Action to remove a booking from the system
		$('#confirmationDeleteBooking').click(function(){
			$.ajax({
				type: "DELETE",
				url: defaultConnection+"booking/" + sessionStorage.getItem('bookingID'),
				success:function(reply){
					//Reset the session variable for the bookingID
					sessionStorage.setItem('bookingID',-1);
					$('#deleteModal').modal('hide');
					$('#insertTextConfirms').empty();
					$('#insertTextConfirms').append("Booking Deleted Successfully!");
					$('#confirmationModals').modal({backdrop: "static"});
					setTimeout(function() { $('#confirmationModals').modal('hide'); window.location.reload()},1500);
				},
				fail: function(error){
					$('#insertTextErrors').empty();
					$('#insertTextErrors').append(error);
					$('#errorModals').modal({backdrop: "static"});
					setTimeout(function() { $('#errorModals').modal('hide'); },1500);
				}
			});
		});

		//Action to decline the deletion of a booking and bring up the booking details once again
		$('#declineDeleteBooking').click(function(){
			$('#deleteModal').modal('hide');
			$('#eventModal').modal('show');
		});
	}
});
