
//Red's Connection
var redsconnection= "http://ukl5cg6195g1q:8080/";
//Mike's Connection
var mikesconnection = "http://UKL5CG6195GRV:8080/";
//LocalHost Connection
var localhost = "http://localhost:8080/";
//Choose a Connection
//var defaultConnection=redsconnection;
var defaultConnection=localhost;
var daysCounter = 0;
var desksCounter = 0;

$(document).ready(function() {
  var FromEndDate = new Date();
  var ToEndDate = new Date();

  //Update GIS table
  putRequestForGIS(new Array());

  //Check if the user wants to edit a current booking or create a new one
  if(sessionStorage.getItem('newEdit') == "Edit"){
    //Send request for seats availability
    searchAvailableSeats(sessionStorage.getItem('startDate'), sessionStorage.getItem('endDate'));
    //Add the start and the end dates to the datepickers
    $('#sdate').val(sessionStorage.getItem('startDate'));
    $('#edate').val(sessionStorage.getItem('endDate'));
    $('#select').val(sessionStorage.getItem('location'));
  }
  else if(sessionStorage.getItem('newEdit') == "Close"){
    sessionStorage.setItem('newEdit', "");
    $('#select').val(sessionStorage.getItem('location'));
  }
  else{
    $('#sdate').val(sessionStorage.getItem('startDate'));
    $('#edate').val(sessionStorage.getItem('endDate'));
    $('#select').val(sessionStorage.getItem('location'));
    sessionStorage.setItem('bookingID',-1);
    //Send request for seats availability
    if($('#sdate').val() != ""){
      searchAvailableSeats(sessionStorage.getItem('startDate'), sessionStorage.getItem('endDate'));
    }
  }

  ToEndDate.setDate(ToEndDate.getDate()+365);

  $('#sdate').datepicker({
    weekStart: 1,
    daysOfWeekDisabled: [0, 6],
    format: "yyyy-mm-dd",
    todayHighlight: true,
    autoclose: true,
    todayBtn: true,
    startDate: new Date(),
  }).on('changeDate', function(selected){
    startDate = new Date(selected.date.valueOf());
    startDate.setDate(startDate.getDate(new Date(selected.date.valueOf())));
    $('#edate').datepicker('setStartDate', startDate);
  });


  // Datepicker appears when enddate input box is selected
  $('#edate').datepicker({
    weekStart: 1,
    daysOfWeekDisabled: [0,6],
    format: "yyyy-mm-dd",
    todayHighlight: true,
    autoclose: true,
    todayBtn: true,
    startDate: new Date(),
  }).on('changeDate', function(selected){
    FromEndDate = new Date(selected.date.valueOf());
    FromEndDate.setDate(FromEndDate.getDate(new Date(selected.date.valueOf())));
    $('#sdate').datepicker('setEndDate', FromEndDate);
  });

  $('#search').click(function(){
    //Verify that the booking is no longer than 14 days
    var startD = new Date($('#sdate').val());
    var endD = new Date($('#edate').val());
    var oneDay = 24*60*60*1000; // hours*minutes*seconds*milliseconds
    var numDaysBetweenDates =  Math.round(Math.abs((startD.getTime() - endD.getTime())/(oneDay)));
    if($('#sdate').val() == "" || $('#edate').val() == ""){
      $('#insertTextError').empty();
      $('#insertTextError').append("Please select a valid date!");
      $('#errorModal').modal({backdrop: "static"});
      setTimeout(function() { $('#errorModal').modal('hide'); },1500);
      //alert("Please select a valid date!");
    }
    else{
      if(numDaysBetweenDates >= 0 && numDaysBetweenDates<14){
        searchAvailableSeats($('#sdate').val(),$('#edate').val());
      }
      else{
        $('#insertTextError').empty();
        $('#insertTextError').append("A booking cannot have a length greater than 14 days!");
        $('#errorModal').modal({backdrop: "static"});
        setTimeout(function() { $('#errorModal').modal('hide'); },1500);
      }
    }
  });

  $('.book').click(function(){
    var checked = $('#resultsTable').find(":radio:checked");
    //Create and instantiate an object that will store all the deskid and dates for the booking
    var dataForBooking = [];
    //Verify if the user has picked a seat for each day of the booking
    if(checked.length != daysCounter){
      //In this case, the user has not made a selection for each datepicker
      $('#insertTextError').empty();
      $('#insertTextError').append("Please select a seat for each day of the booking!");
      $('#errorModal').modal({backdrop: "static"});
      setTimeout(function() { $('#errorModal').modal('hide'); },1500);
    }
    else{
      //Add all the data related to the booking to the var bookingData
      checked.each(function(){
        var tempData = new BookingTable();
        //Extract the deskID and the date on each selection
        var stringContainingAllTheData = $(this).attr('id');
        var arrayElementsString =stringContainingAllTheData.split("_");
        //Add the data to the BookingTable tempo object
        tempData.deskID=arrayElementsString[1];
        tempData.dates.push(arrayElementsString[0]);
        //Now add the temp object to the array that stores the booking
        dataForBooking.push(tempData);
      });

      //Tidy the data
      var tidy = tidyDataBookingBeforeSending(dataForBooking);
      //Wrap the object into a BookingTableWrapper to pass to the API
      var bookingTableWrapper = { "bookingTables":tidy }
      //Verify if it is a new booking or an Edit booking
      if(sessionStorage.getItem('newEdit') == "Edit"){
        //Send an UPDATE request to update the selected booking
        $.ajax({
          headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json'
          },
          dataType: "json",
          url:defaultConnection+"booking/"+sessionStorage.getItem('bookingID')+"/user/"+sessionStorage.getItem('userID'),
          method: "PUT",
          data: JSON.stringify(bookingTableWrapper),
          complete: function(event, xhr, settings){
            if(xhr!="parserror"){
              //Request executed successfully
              //Load the home page with all the bookingDetails after resetting the session object to undefined
              sessionStorage.setItem('newEdit',"");
              //Update text to confirmation modal
              $('#insertTextConfirm').empty();
              $('#insertTextConfirm').append("Booking Updated Correctly!");
              //Show a confirmation message for 1 sec
              //Open this link in the same window
              $('#confirmationModal').modal({backdrop: "static"});
              setTimeout(function() { $('#confirmationModal').modal('hide');
              window.location = "home.html"},1500);
            }
            else{
              //Update text to error modal
              $('#insertTextError').empty();
              $('#insertTextError').append("Error While Updating the Record!");
              $('#errorModal').modal({backdrop: "static"});
              setTimeout(function() { $('#errorModal').modal('hide'); },1500);
            }
          },
        });
      }
      else{
        //Create a NEW booking request
        $.ajax({
          headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json'
          },
          dataType: "json",
          url:defaultConnection+"booking/"+sessionStorage.getItem('userID'),
          method: "POST",
          data: JSON.stringify(bookingTableWrapper),
          complete: function(event, xhr, settings){
            if(xhr!="parserror"){
              //Request executed successfully
              //Load the home page with all the bookingDetails after resetting the session object to undefined
              sessionStorage.setItem('newEdit',"");
              //Show a confirmation message for 1 sec
              //Open this link in the same window
              $('#insertTextConfirm').empty();
              $('#insertTextConfirm').append("Booking Inserted Correctly!");
              //Show a confirmation message for 1 sec
              //Open this link in the same window
              $('#confirmationModal').modal({backdrop: "static"});
              setTimeout(function() { $('#confirmationModal').modal('hide');
              window.location = "home.html"},1500);
            }
            else{
              $('#insertTextError').empty();
              $('#insertTextError').append("Error While Creating the Record!");
              $('#errorModal').modal({backdrop: "static"});
              setTimeout(function() { $('#errorModal').modal('hide'); },1500);
            }
          },
        });
      }
    }
  });
});

function searchAvailableSeats(sD, eD){
  $('.headers').empty();
  $('.tableDetails').empty();
  $('.book').empty();

  //Reset the counters every time the Search button is pressed
  daysCounter = 0;
  desksCounter = 0;
  var startString=sD;
  var endString=eD;
  sD=new Date(sD);
  eD=new Date(eD);
  //add if statement to ensure that both dates have been selected
  $('.headers').append("<th scope='col'><center>Desk Number</center></th>");
  var location = "Edinburgh";

  for(var d = sD; d <= eD; d.setDate(d.getDate() +1)){
    if(d.getDay()!=6 && d.getDay()!=0){
      $('.headers').append("<th scope='col'><center>" + formatDate(d) + "</center></th>")
    }
  }
  var desksAvailable=new Array();
  $.getJSON(defaultConnection+"booking/checkSingleAvailability", {
    location:location, startDate: startString, endDate: endString, bookingId: sessionStorage.getItem('bookingID')})
    .success(function(result) {
      $.each(result, function(key,val){
        //Add the desk number to the array
        desksAvailable.push(val.deskID);
        $('.tableDetails').append("<tr id='desk"+ key +"'><th scope='row'><center>"+ val.deskID +"</center></th></tr>");
        desksCounter++;
        sD = new Date(startString);
        for(var d = sD; d <= eD; d.setDate(d.getDate() +1)){
          //Check whether the date is a Saturday or a Sunday
          if(d.getDay()!=6 && d.getDay()!=0){
            daysCounter++;
          }
          if(contains (formatDate(d),val.dates)){
            $('#desk'+ key).append("<td style='text-align:center'><label class='btn btn-success'><input type='radio' name='"+ formatDate(d) +"' id ='"+ formatDate(d) +"_"+(key+1) +"' required</label></td>");
          }
          else if(d.getDay()!=6 && d.getDay()!=0){
            $('#desk'+ key).append("<td style='text-align:center'><label class='btn btn-default'><input type='radio' name='"+ formatDate(d) +"' id ='"+ formatDate(d) +"_"+(key+1) +"' disabled='true'></label></td>");
          }
        }
      })
      $('.book').append("<center><button type='button' class='btn btn-primary' id='book'>Book Hot Desk</button></center>");
      daysCounter=daysCounter/desksCounter;
      //Request to update GIS database
      putRequestForGIS(desksAvailable);
    }).fail(function(d, status, error) {
      $('#insertTextError').empty();
      $('#insertTextError').append("No Dates Selected!");
      $('#errorModal').modal({backdrop: "static"});
      setTimeout(function() { $('#errorModal').modal('hide'); },1500);
    });
  }

  function putRequestForGIS(dataObj){
    $.ajax({
      headers: {
        'Accept': 'application/json',
        'Content-Type': 'application/json'
      },
      dataType: "json",
      url:defaultConnection+"gis",
      method: "PUT",
      data: JSON.stringify({"deskIDs": dataObj}),
    });
  }


  function formatDate(date){
    date=new Date(date);
    var year = date.getFullYear();
    var day = formatDayMonth(date.getDate());
    var month = formatDayMonth((date.getMonth() + 1));
    return year + "-" + month + "-" + day;
  }

  function formatDayMonth(dayMonth){
    if(dayMonth < 10){
      return 0 + "" + dayMonth;
    }else{
      return dayMonth;
    }
  }

  function contains(value, list){
    var isContained = false;
    for(var i = 0; i < list.length; i++ ){
      if(list[i] == value){
        return true;
      }
    }
    return false;
  }

  function getStartEndDate(){
    var gettingDate = $('#sdate').datepicker('getDate');
    return gettingDate.setDate(gettingDate.getDate() + 13);
  }

  function tidyDataBookingBeforeSending(data){
    for(var i=0; i<data.length-1; i++){
      for(var j=(i+1); j<data.length; j++){
        if(data[i].deskID == data[j].deskID){
          data[i].dates.push(data[j].dates[0]);
          //now set the DeskID to an invalid number
          data[j].deskID=-1;
        }
      }
    }
    //Remove all the useless elements
    var correctArray=[];
    for(var i=0; i<data.length; i++){
      if(data[i].deskID!=-1){
        correctArray.push(data[i]);
      }
    }
    return correctArray;
  }

  //Define an Object BookingTable
  function BookingTable(){
    this.deskID=0;
    this.dates=[];
  }
