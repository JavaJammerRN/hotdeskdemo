package hello.bookingwebservice;

import java.sql.SQLException;
import java.text.ParseException;
import java.time.DateTimeException;
import java.time.format.DateTimeParseException;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/booking")
public class BookingController {

	//Declare and Initiate the Constants used within this class
	private final static String ERROR_APPLICATION="Application Error";
	private final static String ERROR_DBREQUEST="DB Request Error";
	private final static String ERROR_INDEXOUTOFBOUND="Internal Error (Index Error)";
	private final static String ERROR_INVALIDUSERID="Invalid User ID";
	private final static String ERROR_INVALIDBOOKINGID="Invalid Booking ID";
	private final static String ERROR_GENERAL="General Exception";
	private final static String ERROR_DATECONVERSION="Date Conversion Error";
	private final static String ERROR_INVALIDDATE="Invalid Date";
	private final static String ERROR_INVALIDLOCATION="Invalid Location";


	@CrossOrigin(origins = "*")
	@RequestMapping(value="/user/{userID}", method=RequestMethod.GET)
	public ResponseEntity<?> userBookings(@PathVariable String userID){
		try{
			int userIdentification=Integer.parseInt(userID);
			List<Booking> obj = BookingDAO.getAllBookingsForSpecificUser(userIdentification);
			return (obj!=null)? ResponseEntity.ok(obj) : ResponseEntity.badRequest().body(ERROR_INVALIDUSERID);
		}catch(SQLException sqlE){
			return ResponseEntity.badRequest().body(ERROR_DBREQUEST);
		}catch(IndexOutOfBoundsException indexE){
			return ResponseEntity.badRequest().body(ERROR_INDEXOUTOFBOUND);
		}catch(NumberFormatException numE){
			return ResponseEntity.badRequest().body(ERROR_INVALIDUSERID);
		}catch(Exception e){
			return ResponseEntity.badRequest().body(ERROR_GENERAL);
		}
	}

	@CrossOrigin(origins = "*")
	@RequestMapping(value="/{userID}/ref/{bookingID}", method=RequestMethod.GET)
	public ResponseEntity<?> userSpecificBooking(
			@PathVariable("userID") String userID, 
			@PathVariable("bookingID") String bookingID){
		try {
			int userIdentification=Integer.parseInt(userID);
			int bookingIdentification=Integer.parseInt(bookingID);
			Booking obj = BookingDAO.getSingleBookingForSpecificUser(userIdentification, bookingIdentification);
			return (obj!=null)? ResponseEntity.ok(obj) : ResponseEntity.badRequest().body(ERROR_INVALIDLOCATION);
			//Catch any exception (mysql, numconversion) threw by the method and output them into a bad request  
		}catch(SQLException sqlE){
			return ResponseEntity.badRequest().body(ERROR_DBREQUEST);
		}catch(IndexOutOfBoundsException indexE){
			return ResponseEntity.badRequest().body(ERROR_INDEXOUTOFBOUND);
		}catch(DateTimeParseException dateParserE){
			return ResponseEntity.badRequest().body(ERROR_DATECONVERSION);
		}catch(DateTimeException dateE){
			return ResponseEntity.badRequest().body(ERROR_INVALIDDATE);
		}catch(NumberFormatException numE){
			return ResponseEntity.badRequest().body(ERROR_INVALIDBOOKINGID +" OR "+ ERROR_INVALIDUSERID);
		}catch(Exception e){
			return ResponseEntity.badRequest().body(ERROR_APPLICATION);
		}

	}

	@CrossOrigin(origins = "*")
	@RequestMapping(value="/checkSingleAvailability", method=RequestMethod.GET)
	public ResponseEntity<?> retrieveSingleSeatsAvailableOnPeriodOfTime(
			@RequestParam(value="location") String location, 
			@RequestParam(value="startDate") String startDate, 
			@RequestParam(value="endDate") String endDate){
		try {
			List<BookingTable> obj=BookingDAO.getIndividualSeatsAvailabilityForLocationDateRange(location, startDate, endDate);
			return (obj!=null && obj.size()>0)? ResponseEntity.ok(obj) : ResponseEntity.badRequest().body(ERROR_INVALIDLOCATION);

			//Catch any exception (mysql, numconversion) threw by the method and output them into a bad request  
		}catch(ParseException ex){
			return ResponseEntity.badRequest().body(ERROR_APPLICATION);
		}catch(SQLException  mysqlE){
			return ResponseEntity.badRequest().body(ERROR_DBREQUEST);
		}catch(Exception e){
			return ResponseEntity.badRequest().body(ERROR_APPLICATION);
		}
	}

	@CrossOrigin(origins = "*")
	@RequestMapping(value="/bookingLength", method=RequestMethod.GET)
	public ResponseEntity<?> retrieveBookingLength(
			@RequestParam(value="startDate") String startDate, 
			@RequestParam(value="endDate") String endDate){
		try{
			int length=BookingDAO.getBookingLengthPublic(startDate, endDate);
			//Verify the integer value retrieved
			return (length>0)? ResponseEntity.ok(length) : ResponseEntity.badRequest().body(ERROR_INVALIDDATE);
		}catch(Exception e){
			return ResponseEntity.badRequest().body(ERROR_APPLICATION);
		}
	}

	@CrossOrigin(origins = "*")
	@RequestMapping(value="/seatsLocation", method=RequestMethod.GET)
	public ResponseEntity<?> retrieveAllSeatsLocation(
			@RequestParam(value="location") String location){
		try{
			return BookingDAO.getSeatsInfoLocation(location);
		}catch(Exception e){
			return null;
		}
	}

	@CrossOrigin(origins = "*")
	@RequestMapping(value="/seatInfo", method=RequestMethod.GET)
	public ResponseEntity<?> retrieveSeatData(
			@RequestParam(value="deskId") String deskId){
		try{
			return BookingDAO.retrieveDeskInfo(deskId);
		}catch(Exception e){
			return null;
		}
	}


	// POST - create new booking
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/{userID}", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<String> test(@PathVariable int userID, @RequestBody BookingTableWrapper bookingTableWrapper) throws SQLException, ParseException {
		return BookingDAO.createBooking(userID, bookingTableWrapper);
	}

	// PUT - update existing booking
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/{bookingID}/user/{userID}", method = RequestMethod.PUT)
	public @ResponseBody ResponseEntity<String> updateBooking(@PathVariable int userID, @PathVariable int bookingID, @RequestBody BookingTableWrapper bookingTableWrapper) throws SQLException, ParseException {
		return BookingDAO.updateBooking(userID, bookingID, bookingTableWrapper);
	}

	// DELETE - delete existing booking
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/{bookingID}", method = RequestMethod.DELETE)
	public @ResponseBody ResponseEntity<String> deleteBooking(@PathVariable int bookingID) {
		return BookingDAO.deleteBooking(bookingID);
	}


		


}
