       $(document).ready(function() {
        // $('#mapArea').hide();
        

      var FromEndDate = new Date();
      var ToEndDate = new Date();

      ToEndDate.setDate(ToEndDate.getDate()+365);

        // Datepicker appears when startdate input box is selected  
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

               $('.headers').empty();
               $('.tableDetails').empty();
               $('.book').empty();
               //add if statement to ensure that both dates have been selected
               $('.headers').append("<th scope='col'><center>Desk Number</center></th>");
              var startdate = $('#sdate').val();
              var enddate = $('#edate').val();
              var location = "Edinburgh";

              var start = new Date(startdate);
              var end = new Date(enddate);

              for(var d = start; d <= end; d.setDate(d.getDate() +1)){
                $('.headers').append("<th scope='col'><center>" + formatDate(d) + "</center></th>")
                console.log(formatDate(d));
              }


              $.getJSON("http://UKL5CG6195G1Q:8080/booking/checkSingleAvailability", {location:location, startDate: startdate, endDate: enddate}).success(function(result) {

                  $.each(result, function(key,val){
                    
                    $('.tableDetails').append("<tr id='desk"+ key +"'><th scope='row'><center>"+ val.deskID +"</center></th></tr>");

                    start = new Date(startdate);

                    for(var d = start; d <= end; d.setDate(d.getDate() +1)){
                      if(contains (formatDate(d),val.dates)){
                        
                        $('#desk'+ key).append("<td id ='"+ formatDate(d) +"' style='text-align:center'><label class='btn btn-success'><input type='radio' id='"+ formatDate(d) +"' required</label></td>");

                      }else{
                  $('#desk'+ key).append("<td style='text-align:center'><label class='btn btn-default'><input type='radio' name='"+ formatDate(d) +"' disabled='true'></label></td>");
                      }
                  }       
    
                  })
                  $('.book').append("<center><button type='button' class='btn btn-primary' id='book'>Book Hot Desk</button></center>");
                


                 }).fail(function(d, status, error) {
                 alert("\nError: No Dates Selected");
             });
              
          });

          function formatDate(date){
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

          $('.book').click(function(){
              var checked = $('#resultsTable').find(":radio:checked");
              alert(checked.length);
              console.log(checked);
  //             $('#resultsTable').each(function(){
  //                 $(this).find('td').each(function(){
  //                   if($(this).attr('id').getElementById('2016-10-10').checked){
  //                     alert("hello");
  //                   }
  //                 var x = $(this).attr('id');
                  
        //do your stuff, you can use $(this) to get current cell
//          })
//             })

            // var table = document.getElementById('#resultsTable');
            // console.log(resultsTable);
            // for(var i = 0, row; row = table.rows[i]; i++){
            //   alert(row);
            //   for(var j = 0, col; col = row.cells[j]; j++){
            //     alert(col);
            //   }
            // }
          });

});