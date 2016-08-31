
	$(document).ready(function() {
		var myBooking = new Array();
		var myUser = sessionStorage.getItem('userID');
		$.getJSON("http://UKL5CG6195G1Q:8080/booking/user/"+ myUser, function(result) {
			$.each(result, function(key,val){
				var booking = new Object();
				booking.title = val.location;
				booking.start = val.startDate;
				booking.end = val.endDate;
				booking.bookingID = val.bookingID;
				//booking.rendering = "background";
				//booking.backgroundColor = "99CCFF";
				myBooking.push(booking);


			})
			startCal(myBooking);
		});


		$('#del').click(function(){
			//alert($('#something').text());
			var bID = $('#something').text();
				$.ajax({
					type: "DELETE",
					url: "http://ukl5cg6195g1q:8080/booking/" + bID,
					success:function(reply){
						$('#eventModal').modal('hide');
						window.location.reload();
						alert(reply);
					}
				})

			});


		function startCal(bk){

		var calendar = $('#calendar').fullCalendar({
			weekends: false, // hides saturdays and sundays
			contentHeight: 450,
			editable: true,
			eventLimit: true, // allow "more" link when too many events
			events:myBooking,

			header: {
				left: 'prev,next',
				center: 'title',
				right: 'today',
			},

			
			eventClick: function(event, jsEvent, view){
				$('#bookingDetails').empty();
				$('#bookingDetails').append("<h1 id = 'something'>"+ event.bookingID + "</h1>")
				$('#eventModal').modal({backdrop: "static"});
			},

			dayClick: function(date, jsEvent, view){

				$('#sdate').val(date.format("DD/MM/YYYY")); //gets the date clicked on calendar
				

    			if (moment().format('YYYY-MM-DD') === date.format('YYYY-MM-DD') || date.isAfter(moment())) {
        		// This allows today and future date
        		$('#myModal').modal({backdrop: "static"});
    			} else {
        		// Else part is for past dates
        		$('#errorModal').modal({backdrop: "static"});
    			}
				
				// Datepicker appears when enddate input box is selected	
				$('#edate').datepicker({
             		weekStart: 1,
              		daysOfWeekDisabled: [0,6],           
              		format: "yyyy-mm-dd",
              		todayHighlight: true,
              		autoclose: true,
              		todayBtn: true,
              		startDate: new Date()
						});
				           
					},

			
			

		
		});
		}	
});

//