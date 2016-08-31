package hello.bookingwebservice;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.springframework.http.ResponseEntity;
import java.time.DateTimeException;
import java.time.format.DateTimeParseException;
import java.sql.PreparedStatement;
import com.mysql.jdbc.Connection;
import hello.Validate;
import hello.Validate.Validator;
import hello.giswebservice.GISWrapper;;


public class BookingDAO {

	//Declare and Initiate the Constants used within this class
	private final static String DATE_PATTERN="yyyy-MM-dd";
	private final static String ERROR_CONNECTIONDB="DB Connection Error";
	private final static String ERROR_APPLICATION="Application Error";
	private final static String ERROR_DBREQUEST="DB Request Error";
	private final static String ERROR_INVALIDLOCATION="Invalid Location";

	/*
	 * This method returns all the bookings within the system
	 */
	/*public static List<Booking> getAllBookings(){
			Connection connectionDB=BookingDAO.establishConnection();
			//Create and initialise an object that will store all the bookings within the system
			List<Booking> allBookings=new ArrayList<Booking>();
			if(connectionDB!=null){
				try{
					Statement stmt = connectionDB.createStatement();
					//Select query
					String query = "SELECT * FROM `booking` LEFT JOIN desk on booking.deskID=desk.deskID";
					//Execute the query
					boolean status = stmt.execute(query);
					if(status){
						//Extract the data from the resultset object
						ResultSet rs = stmt.getResultSet();
						//Loop around the resultset to extract the data needed for each booking
						while(rs.next()){
							int bookingId=(Integer.parseInt(rs.getString("bookingID")));
							int userId=(Integer.parseInt(rs.getString("userID")));
							int deskId=(Integer.parseInt(rs.getString("deskID")));
							int deskBlockN=(Integer.parseInt(rs.getString("deskBlock")));
							String deskLetter=rs.getString("deskLetter");
							String location=rs.getString("location");
							//Create a temporary booking object that will be added to the vector
							//The start and end date of the booking are currently set to null
							Booking bookingTemp=new Booking(bookingId,userId,deskId,null,null,deskBlockN,deskLetter,location);
							//Add the temporary booking to the vector
							allBookings.add(bookingTemp);
						}
						//Close the connection with the database
						rs.close();
						//Return all the user information
						return allBookings;
					}
				}catch(Exception e){}
			}
			return null;
		}*/

	/*
	 * This methods retrieves all the bookings linked to a specific userID.
	 * Future Developments: Retrieve only the bookings within a date range to limit the amount of data to elaborate
	 */
	public static List<Booking> getAllBookingsForSpecificUser(int userId)throws Exception{
		//Validate the userId
		if(userId>0){
			List<Integer> userBookingIDs=new ArrayList<Integer>();
			List<Booking> userBookings=new ArrayList<Booking>();
			//Instantiate a connection with the database
			Connection connectionDB=BookingDAO.establishConnection();
			try{
				Statement stmt = connectionDB.createStatement();
				//Select query
				String query = "SELECT * FROM `booking` WHERE userID='"+userId+"'";
				//Execute the query
				boolean status = stmt.execute(query);
				if(status){
					//Extract the data from the resultset object
					ResultSet rs = stmt.getResultSet();
					//Loop around the resultset to extract the data needed for each booking
					while(rs.next()){
						int bookingId=(Integer.parseInt(rs.getString("bookingID")));
						//Add bookingID to the List
						userBookingIDs.add(bookingId);
					}
					//Close the connection with the database
					rs.close();
					connectionDB.close();
					//Now that all the bookings IDs have been found for a specific user, let's group them into single bookings with a start and end date
					for(int i=0; i<userBookingIDs.size(); i++){
						userBookings.add(getSingleBookingForSpecificUser(userId, userBookingIDs.get(i)));
					}
					//Return all the bookings
					return userBookings;
				}
			}catch(SQLException sqlE){
				throw sqlE;
			}catch(IndexOutOfBoundsException indexE){
				throw indexE;
			}catch(Exception e){
				throw e;
			}
		}
		return null;
	}

	/*
	 * This method retrieves a specific booking provided a user and a booking id
	 */
	public static Booking getSingleBookingForSpecificUser(int userId, int bookingId) throws Exception{
		//Validate the data
		if(userId>0 && bookingId>0){
			//Create a temporary booking object
			Booking userBooking;
			//This list of dates will be used later on to find the start and end date of the booking
			List<java.sql.Date> datesBookingTemp=new ArrayList<java.sql.Date>();
			//Create a connection with the database
			Connection connectionDB=BookingDAO.establishConnection();
			try{
				Statement stmt = connectionDB.createStatement();
				//Select query
				String query = "SELECT booking.bookingID, userID, date, desk.deskID, deskBlock, deskLetter, location FROM `booking` "
						+ "LEFT JOIN `bookingdate` on booking.bookingID=bookingdate.bookingID "
						+ "LEFT JOIN `desk` on bookingdate.deskID=desk.deskID "
						+ "WHERE userID='"+userId+"' AND booking.bookingID='"+bookingId+"'";
				//Execute the query
				boolean status = stmt.execute(query);
				if(status){
					//Extract the data from the resultset object
					ResultSet rs = stmt.getResultSet();
					//Initialise the booking object
					userBooking=new Booking(bookingId, userId, "");
					//Loop around the resultset to extract the data needed for each booking
					while(rs.next()){
						String location=rs.getString("location");
						userBooking.setLocation(location);
						int deskId=(Integer.parseInt(rs.getString("deskID")));
						int deskBlockN=(Integer.parseInt(rs.getString("deskBlock")));
						String deskLetter=rs.getString("deskLetter");
						String date=rs.getString("date");
						//Add this date related to 1 day of the booking
						SeatBooked seatTemp=new SeatBooked(deskId,deskBlockN,deskLetter,date);
						//Add the date to the list of dates
						datesBookingTemp.add(seatTemp.getDate());
						//Add the seat reservation to the userBooking object
						userBooking.addSeatBooked(seatTemp);
					}
					//Close the connection with the database
					rs.close();
					connectionDB.close();
					//The following code will find the first and last date for the given booking id, add them to the Booking object
					//and return it to the user
					//Only do this if the booking is longer than 1 day
					//Create temporary start and end date;
					java.sql.Date startDTemp=null, endDTemp=null;
					if(datesBookingTemp.size()>1){
						//Add 20 years to the current date
						Calendar cal=Calendar.getInstance();
						DateFormat dateFormat = new SimpleDateFormat(DATE_PATTERN);
						Date date = new Date();
						dateFormat.format(date);
						cal.setTime(date);
						//This specific line adds the 20 years to the current date
						cal.add(Calendar.YEAR, 20);
						//Convert the Date created to a SQL Date 
						startDTemp=new java.sql.Date(cal.getTime().getTime());

						//Loop that finds the start date of the booking
						for(int i=0; i<datesBookingTemp.size(); i++){
							if(datesBookingTemp.get(i).before(startDTemp))
								startDTemp=datesBookingTemp.get(i);
						}
						//Loop that finds the end date of the booking
						for(int i=0; i<datesBookingTemp.size(); i++){
							if(datesBookingTemp.get(i).after(startDTemp)){
								endDTemp=datesBookingTemp.get(i);
							}
						}
					}
					else{
						//Otherwise if the booking is for 1 day only, the start and end date match
						startDTemp=endDTemp=datesBookingTemp.get(0);
					}

					//Now that the start and end date of the booking have been found,
					//pass the data to the booking object
					userBooking.setStartDate(startDTemp.toString());
					userBooking.setEndDate(endDTemp.toString());

					//The booking object now contains all the information required.
					//Return it to the user
					return userBooking;
				}
			}catch(SQLException sqlE){
				throw sqlE;
			}catch(IndexOutOfBoundsException indexE){
				throw indexE;
			}catch(DateTimeParseException dateParserE){
				throw dateParserE;
			}catch(DateTimeException dateE){
				throw dateE;
			}catch(Exception e){
				throw e;
			}
		}
		return null;
	}

	/*
	 * This method returns all the available seats provided a location, a start date and an end date
	 * The results are stored into a List of Integers, since each seat is identified with an unique number
	 */
	//		public static List<BookingTable> getIndividualSeatsAvailabilityForLocationDateRange(String location, String startD, String endD) throws Exception{
	//			if(!location.equals("")){
	//				//Convert the given strings into Date object
	//				SimpleDateFormat dateFormatter=new SimpleDateFormat(DATE_PATTERN);
	//				List<BookingTable> tableBookings=new ArrayList<BookingTable>();
	//				Calendar cal=new GregorianCalendar();
	//				//Re-format the location string
	//				String loc=location.substring(0,1).toUpperCase()+location.substring(1);
	//				try{
	//					//Retrieve a list of all the seats for the given location
	//					List<Integer> allSeatsLocation=getSeatsLocation(loc);
	//					//Convert the String into Date objects
	//					Date startDateConverted=dateFormatter.parse(startD);
	//					Date endDateConverted=dateFormatter.parse(endD);
	//					//Find the number of days between the dates
	//					int daysBetweenDates=getBookingLength(startDateConverted, endDateConverted);
	//
	//					//Establish a connection with the DataBase
	//					Connection connectionDB=BookingDAO.establishConnection();
	//					Statement stmt = connectionDB.createStatement();
	//					//Select query
	//					String query= "SELECT booking.bookingID, userID, desk.deskID, deskBlock, deskLetter, location, date FROM booking "
	//							+ "LEFT JOIN desk INNER JOIN bookingdate "
	//							+ "ON desk.deskID=bookingdate.deskID "
	//							+ "ON booking.bookingID=bookingdate.bookingID "
	//							+ "WHERE date BETWEEN '"+startD+"' AND '"+endD+"'"
	//							+ "AND location='"+loc+"'";
	//					//Execute query and store the result
	//					stmt.execute(query);
	//					//Extract the data from the statement object and store in a resultset object
	//					ResultSet rs = stmt.getResultSet();
	//					//Work with the data received from the DataBase
	//					for(int i=0; i<allSeatsLocation.size(); i++){
	//						//Add the date to a calendar
	//						cal.setTime(startDateConverted);
	//						//Create the BookingTable objects and add the deskIDs
	//						BookingTable temp=new BookingTable();
	//						temp.setDeskID(allSeatsLocation.get(i));
	//						//Retrieve all the bookings for a seat ID
	//						List<java.sql.Date> datesSeat=getDatesInResultSet(rs,allSeatsLocation.get(i));
	//						//Now, check the availability of each BookingTable element
	//						for(int j=0; j<daysBetweenDates+1; j++){
	//							//Verify if the day is a Saturday or a Sunday
	//							int dayOfTheWeek=cal.get(Calendar.DAY_OF_WEEK);
	//							//If the day is a Saturday or a Sunday, skip it
	//							//Convert the Java Date object to a SQL Date object
	//							java.sql.Date selectedDate=new java.sql.Date(cal.getTime().getTime());
	//							//Make sure the selected date is not part of the weekend and is not contained within the list
	//							if(dayOfTheWeek!=Calendar.SATURDAY && dayOfTheWeek!=Calendar.SUNDAY && !datesSeat.contains(selectedDate)){
	//								temp.addDate(selectedDate.toString());
	//							}
	//							//Add a day to the current date
	//							cal.add(Calendar.DATE,1);
	//						}
	//						//Add the element created to the tableBookings only if it contains dates
	//						if(temp.datesValidity()){
	//							tableBookings.add(temp);
	//						}
	//					}
	//					//Close the connection with the database
	//					rs.close();
	//					connectionDB.close();
	//					return tableBookings;
	//				}catch(ParseException ex){
	//					throw ex;
	//				}catch(SQLException  mysqlE){
	//					throw mysqlE;
	//				}catch(Exception e){
	//					throw e;
	//				}
	//			}
	//			return null;
	//		}

	public static List<BookingTable> getIndividualSeatsAvailabilityForLocationDateRange(String location, String startD, String endD, int bookingID) throws Exception{
		if(!location.equals("")){
			//Convert the given strings into Date object
			SimpleDateFormat dateFormatter=new SimpleDateFormat(DATE_PATTERN);
			List<BookingTable> tableBookings=new ArrayList<BookingTable>();
			Calendar cal=new GregorianCalendar();
			//Re-format the location string
			String loc=location.substring(0,1).toUpperCase()+location.substring(1);
			try{
				//Retrieve a list of all the seats for the given location
				List<Integer> allSeatsLocation=getSeatsLocation(loc);
				//Convert the String into Date objects
				Date startDateConverted=dateFormatter.parse(startD);
				Date endDateConverted=dateFormatter.parse(endD);
				//Find the number of days between the dates
				int daysBetweenDates=getBookingLength(startDateConverted, endDateConverted);

				//Establish a connection with the DataBase
				Connection connectionDB=BookingDAO.establishConnection();
				Statement stmt = connectionDB.createStatement();
				//Select query
				String query="";
				if(bookingID==-1){
					query= "SELECT booking.bookingID, userID, desk.deskID, deskBlock, deskLetter, location, date FROM booking "
							+ "LEFT JOIN desk INNER JOIN bookingdate "
							+ "ON desk.deskID=bookingdate.deskID "
							+ "ON booking.bookingID=bookingdate.bookingID "
							+ "WHERE date BETWEEN '"+startD+"' AND '"+endD+"'"
							+ "AND location='"+loc+"'";
				}
				else{
					query= "SELECT booking.bookingID, userID, desk.deskID, deskBlock, deskLetter, location, date FROM booking "
							+ "LEFT JOIN desk INNER JOIN bookingdate "
							+ "ON desk.deskID=bookingdate.deskID "
							+ "ON booking.bookingID=bookingdate.bookingID "
							+ "WHERE date BETWEEN '"+startD+"' AND '"+endD+"'"
							+ "AND location='"+loc+"'"
							+ "AND booking.bookingID!='"+bookingID+"'";
				}
				//Execute query and store the result
				stmt.execute(query);
				//Extract the data from the statement object and store in a resultset object
				ResultSet rs = stmt.getResultSet();
				//Work with the data received from the DataBase
				for(int i=0; i<allSeatsLocation.size(); i++){
					//Add the date to a calendar
					cal.setTime(startDateConverted);
					//Create the BookingTable objects and add the deskIDs
					BookingTable temp=new BookingTable();
					temp.setDeskID(allSeatsLocation.get(i));
					//Retrieve all the bookings for a seat ID
					List<java.sql.Date> datesSeat=getDatesInResultSet(rs,allSeatsLocation.get(i));
					//Now, check the availability of each BookingTable element
					for(int j=0; j<daysBetweenDates+1; j++){
						//Verify if the day is a Saturday or a Sunday
						int dayOfTheWeek=cal.get(Calendar.DAY_OF_WEEK);
						//If the day is a Saturday or a Sunday, skip it
						//Convert the Java Date object to a SQL Date object
						java.sql.Date selectedDate=new java.sql.Date(cal.getTime().getTime());
						//Make sure the selected date is not part of the weekend and is not contained within the list
						if(dayOfTheWeek!=Calendar.SATURDAY && dayOfTheWeek!=Calendar.SUNDAY && !datesSeat.contains(selectedDate)){
							temp.addDate(selectedDate.toString());
						}
						//Add a day to the current date
						cal.add(Calendar.DATE,1);
					}
					//Add the element created to the tableBookings only if it contains dates
					if(temp.datesValidity()){
						tableBookings.add(temp);
					}
				}
				//Close the connection with the database
				rs.close();
				connectionDB.close();
				return tableBookings;
			}catch(ParseException ex){
				throw ex;
			}catch(SQLException  mysqlE){
				throw mysqlE;
			}catch(Exception e){
				throw e;
			}
		}
		return null;
	}


	/*
	 * This method returns a list of seats which are available for each day of the date range at the location provided
	 */
	//	public static List<Integer> getAvailableSeatsLocationDateRange(String location, String startD, String endD) throws Exception{
	//		//Re-format the location string
	//		String loc=location.substring(0,1).toUpperCase()+location.substring(1);
	//		//Get the seats available for each day of the date range
	//		List<List<Integer>> availableSeats=getIndividualSeatsAvailabilityForLocationDateRange(loc, startD, endD);
	//		//Get all the seats for the location
	//		List<Integer> allSeatsLocation=getSeatsLocation(loc);
	//		//The application will return only the common seats available for the given date range
	//		//therefore, we need to create an array which contains the common seats number
	//		List<Integer> commonSeats=new ArrayList<Integer>();
	//		for(int i=0; i<allSeatsLocation.size(); i++){
	//			boolean isInserted=true;
	//			//Check if the element is contained in any of the array
	//			for(int j=0; j<availableSeats.size(); j++){
	//				//If the element is not contained, change the value of the flag
	//				if(!isElementInArray(allSeatsLocation.get(i), availableSeats.get(j)))
	//					isInserted=false;
	//			}
	//			//If the value of the flag is not changed, it means the value is in all of the arrays and can be added to the common list
	//			if(isInserted){
	//				commonSeats.add(allSeatsLocation.get(i));
	//			}
	//		}
	//		return commonSeats;
	//	}

	/*
	 * This methods returns all the information for each seat on the location provided
	 */
	public static ResponseEntity<?> getSeatsInfoLocation(String location){
		//Retrieve a list of all the seats for the given location
		List<Desk> allSeats=new ArrayList<Desk>();
		//Instantiate a connection with the database
		Connection connectionDB=BookingDAO.establishConnection();
		try{
			Statement stmt = connectionDB.createStatement();
			//Re-format the location string
			String loc=location.substring(0,1).toUpperCase()+location.substring(1);
			//Select query
			String query = "SELECT * FROM `desk` WHERE location='"+loc+"'";
			//Execute the query
			boolean status = stmt.execute(query);
			if(status){
				//Extract the data from the resultset object
				ResultSet rs = stmt.getResultSet();
				//Loop around the resultset
				while(rs.next()){
					Desk temp=new Desk();
					temp.setDeskID(Integer.parseInt(rs.getString("deskID")));
					temp.setDeskBlock(Integer.parseInt(rs.getString("deskBlock")));
					temp.setDeskLetter(rs.getString("deskLetter"));
					temp.setLocation(rs.getString("location"));
					//Add the desk to the List
					allSeats.add(temp);
				}
				//Close the connection with the database
				rs.close();
				connectionDB.close();
			}
			else{
				return ResponseEntity.badRequest().body(ERROR_DBREQUEST);
			}
			return (allSeats.size()>0)? ResponseEntity.ok(allSeats) : ResponseEntity.badRequest().body(ERROR_INVALIDLOCATION);
		}catch(SQLException  mysqlE){
			return ResponseEntity.badRequest().body(ERROR_CONNECTIONDB);
		}catch(Exception e){
			return ResponseEntity.badRequest().body(ERROR_APPLICATION);
		}
	}





	/*
	 * This method returns info for a selected seat ID
	 */
	public static ResponseEntity<?> retrieveDeskInfo(String id){
		int idConverted;
		Desk deskData=new Desk();
		try{
			idConverted=Integer.parseInt(id);
			if(idConverted>0){
				//Instantiate a connection with the database
				Connection connectionDB=BookingDAO.establishConnection();
				Statement stmt = connectionDB.createStatement();
				//Select query
				String query = "SELECT * FROM `desk` WHERE deskID='"+idConverted+"'";
				//Execute the query
				boolean status = stmt.execute(query);
				if(status){
					//Extract the data from the resultset object
					ResultSet rs = stmt.getResultSet();
					//Loop around the resultset
					if(rs.next()){
						deskData.setDeskID(Integer.parseInt(rs.getString("deskID")));
						deskData.setDeskBlock(Integer.parseInt(rs.getString("deskBlock")));
						deskData.setDeskLetter(rs.getString("deskLetter"));
						deskData.setLocation(rs.getString("location"));
					}
					//Close the connection with the database
					rs.close();
					connectionDB.close();
				}
			}
			else{
				return ResponseEntity.badRequest().body("DeskID must be a value > 0");
			}
		}catch(SQLException  mysqlE){
			return ResponseEntity.badRequest().body(ERROR_CONNECTIONDB);
		}catch(NumberFormatException numE){
			return ResponseEntity.badRequest().body("Invalid Desk ID");
		}catch(Exception e){
			return ResponseEntity.badRequest().body("The given DeskID contains invalid characters");
		}
		return ResponseEntity.ok(deskData);
	}


	//	/*
	//	 * This Method verify if an element is contained within an array and returns TRUE or FALSE
	//	 */
	//	private static boolean isElementInArray(int value, List<Integer>range){
	//		//If the array is empty, it may be because the selected day is a SaturdaySunday or because all the seats are taken,
	//		// in any case, the application 
	//		if(range.isEmpty())
	//			return true;
	//		else
	//			return range.contains((Integer)value);
	//	}

	/*
	 * This method is used in the front end to calculate the number of days the booking is for
	 */
	public static int getBookingLengthPublic(String startDate, String endDate){
		//Convert the given strings into Date object
		SimpleDateFormat dateFormatter=new SimpleDateFormat(DATE_PATTERN);
		try{
			Date startDateConverted=dateFormatter.parse(startDate);
			Date endDateConverted=dateFormatter.parse(endDate);
			//Now that the strings are converted, let's find the numbers of days between them
			return getBookingLength(startDateConverted, endDateConverted)+1;
		}catch(Exception e){
			return -1;
		}
	}


	//Method to create booking for a specified user
	public static ResponseEntity<String> createBooking(int userID, BookingTableWrapper bookingTableWrapper) throws SQLException, ParseException{
		Validator val = Validate.validateCreateBooking(userID, bookingTableWrapper);
		if(!val.pass){
			return ResponseEntity.badRequest().body(val.message);
		}
		try{
			//Get connection & disable auto commit for batch execution
			Connection conn = BookingDAO.establishConnection();
			conn.setAutoCommit(false);

			//Insert into booking table, return auto generated ID
			PreparedStatement stmt = conn.prepareStatement("INSERT INTO booking (userID) VALUES(?)", Statement.RETURN_GENERATED_KEYS);
			stmt.setInt(1, userID);
			stmt.execute();

			//get auto generated ID for bookingdate table
			int generatedUserID = 0;
			try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
				if (generatedKeys.next()) {
					generatedUserID = generatedKeys.getInt(1);
				}
			}

			//Insert booking dates records and commit
			insertBookingDates(conn, stmt, generatedUserID, bookingTableWrapper);
			conn.commit();
			return ResponseEntity.ok("Booking has been created");
		} catch(SQLException SQLe) {
			return ResponseEntity.badRequest().body("Please make sure the data you have entered is correct.");
		}

	}


	//Method to update booking for a specified user
	public static ResponseEntity<String> updateBooking(int userID, int bookingID, BookingTableWrapper bookingTableWrapper) throws SQLException, ParseException{
		Validator val = Validate.validateUpdateBooking(userID, bookingID, bookingTableWrapper);
		if(!val.pass){
			return ResponseEntity.badRequest().body(val.message);
		}
		try{
			//Get connection & disable auto commit for batch execution
			Connection conn = BookingDAO.establishConnection();
			conn.setAutoCommit(false);

			PreparedStatement stmt = conn.prepareStatement("DELETE FROM bookingdate WHERE bookingID = ?");
			stmt.setInt(1, bookingID);
			stmt.executeUpdate();

			//Insert booking dates records an commit
			insertBookingDates(conn, stmt, bookingID, bookingTableWrapper);
			conn.commit();
			return ResponseEntity.ok("Booking: " + bookingID + " has been updated.");
		}catch(SQLException SQLe){
			return ResponseEntity.badRequest().body(SQLe.toString());
		}

	}

	//Method to delete a booking
	public static ResponseEntity<String> deleteBooking(int bookingID) {
		Validator val = Validate.validateDeleteBooking(bookingID);
		if(!val.pass){
			return ResponseEntity.badRequest().body(val.message);
		}
		try{
			//Get connection & disable auto commit for batch execution
			Connection conn = BookingDAO.establishConnection();
			//			conn.setAutoCommit(false);

			PreparedStatement stmt = conn.prepareStatement("DELETE FROM booking WHERE bookingID = ?");
			stmt.setInt(1, bookingID);
			stmt.executeUpdate();
			//			conn.commit();
			return ResponseEntity.ok("Booking " + bookingID + " has been deleted.");
		}catch(SQLException SQLe){
			return ResponseEntity.badRequest().body(SQLe.toString());
		}
	}


	//Method to manipulate GIS table
	public static ResponseEntity<String> updateMap(GISWrapper deskIDs) {
		Validator val = Validate.validateUpdateMap(deskIDs);
		if(!val.pass){
			return ResponseEntity.badRequest().body(val.message);
		}
		try{
			//Get connection & disable auto commit for batch execution
			Connection conn = BookingDAO.establishConnection();
			conn.setAutoCommit(false);
			PreparedStatement stmt = conn.prepareStatement("UPDATE qgis SET booked=? WHERE ID=?");
			for(int i = 1; i <= 16; i++){
				stmt.setInt(2, i);
				if(deskIDs.getDeskIDs().contains(i)) {
					stmt.setInt(1, 1);
				}else{
					stmt.setInt(1, 0);
				}
				stmt.addBatch();
			}
			stmt.executeBatch();
			conn.commit();
			return ResponseEntity.ok("Map has been updated.");
		}catch(SQLException SQLe){
			return ResponseEntity.badRequest().body(SQLe.toString());
		}

	}
	
	
	public static ResponseEntity<String> updateMapSelected(int deskID) {
		Validator val = Validate.validateUpdateMapSelected(deskID);
		if(!val.pass){
			return ResponseEntity.badRequest().body(val.message);
		}
		try{
			//Get connection & disable auto commit for batch execution
			Connection conn = BookingDAO.establishConnection();
			conn.setAutoCommit(false);

			PreparedStatement stmt = conn.prepareStatement("UPDATE qgis SET booked=2 WHERE ID=?");
			stmt.setInt(1, deskID);
			stmt.executeUpdate();
			conn.commit();
			return ResponseEntity.ok("Map has been updated.");
		}catch(SQLException SQLe){
			return ResponseEntity.badRequest().body(SQLe.toString());
		}

	}

	private static void insertBookingDates(Connection conn, PreparedStatement stmt, int generatedUserID, BookingTableWrapper bookingTableWrapper) throws SQLException, ParseException{
		stmt = conn.prepareStatement("INSERT INTO bookingdate (bookingID, date, deskID) VALUES(?,?, ?)");
		for(BookingTable bookingTable : bookingTableWrapper.getBookingTables()){
			for(String date : bookingTable.getDates()){
				stmt.setInt(1, generatedUserID);
				stmt.setDate(2, stringToSQLDate(date));
				stmt.setInt(3, bookingTable.getDeskID());
				stmt.addBatch();
			}

		}
		stmt.executeBatch();
	}

	private static java.sql.Date stringToSQLDate(String stringDate) throws ParseException{
		SimpleDateFormat formatter = new SimpleDateFormat(DATE_PATTERN);
		java.util.Date dateStr = formatter.parse(stringDate);
		return  new java.sql.Date(dateStr.getTime());
	}



	/*
	 * This method is used to calculate how many days the booking is for
	 */
	private static int getBookingLength(Date startDate, Date endDate){
		int counter=0;
		long diff=endDate.getTime()-startDate.getTime();
		counter=(int) TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
		return counter;
	}


	/*
	 * This method returns a list of seats ID for the location provided
	 */
	private static List<Integer> getSeatsLocation (String location){
		//Re-format the location string
		String loc=location.substring(0,1).toUpperCase()+location.substring(1);
		//Retrieve a list of all the seats for the given location
		List<Integer> allSeatsLocation=new ArrayList<Integer>();
		//Instantiate a connection with the database
		Connection connectionDB=BookingDAO.establishConnection();
		if(connectionDB!=null){
			try{
				Statement stmt = connectionDB.createStatement();
				//Select query
				String query = "SELECT * FROM `desk` WHERE location='"+loc+"'";
				//Execute the query
				boolean status = stmt.execute(query);
				if(status){
					//Extract the data from the resultset object
					ResultSet rs = stmt.getResultSet();
					//Loop around the resultset
					while(rs.next()){
						int deskId=(Integer.parseInt(rs.getString("deskID")));
						//Add the desk to the List
						allSeatsLocation.add(deskId);
					}
					//Close the connection with the database
					rs.close();
					connectionDB.close();
				}
				return allSeatsLocation;
			}catch(Exception e){
				return null;
			}
		}
		return null;
	}

	/*
	 * This method extract the dates from the resultset passed as a parameter to match the results
	 * to the deskID
	 */
	private static List<java.sql.Date> getDatesInResultSet(ResultSet res, int deskID){
		List<java.sql.Date> dates=new ArrayList<java.sql.Date>();
		try{
			//Convert the resultSet into an arrayList
			while (res.next()) {
				if(res.getInt("deskID")==deskID){
					//Retrieve the booking date and convert it into a java.sql.Date object
					dates.add(stringToSQLDate(res.getString("date")));
				}
			}
			//Re-position the resultset object to index 0
			res.first();
			return dates;
		}catch(Exception e){
			return null;
		}
	}

	static Connection establishConnection(){
		Connection conn = null;
		try {
			// The newInstance() call is a work around for some broken Java implementations
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			//Use for Michael's DB
			conn = (Connection) DriverManager.getConnection("jdbc:mysql://UKL5CG6195GRV:3306/hotdesk?" +"user=hotdesk&password=hotdesk");
			//Use for Red's DB
			//conn = (Connection) DriverManager.getConnection("jdbc:mysql://UKL5CG6195G1Q:3306/hotdesk?" +"user=hotdesk&password=hotdesk");
			//Use for local
			//conn = (Connection) DriverManager.getConnection("jdbc:mysql://localhost/hotdesk?" +"user=root&password=");

		} catch (Exception error) {
			System.err.println("Could not establish a connection with the DataBase! "+error);
		}
		return conn;
	}


}
