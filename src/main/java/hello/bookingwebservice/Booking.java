package hello.bookingwebservice;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Booking {
	
	//Constants
	private final String DATE_PATTERN="yyyy-MM-dd";
	private final SimpleDateFormat dateFormatter=new SimpleDateFormat(DATE_PATTERN);
	
	//Global Variables
	private int bookingID, userID;
	private String location;
	private java.sql.Date startDate, endDate;
	private List<SeatBooked> seats;
	
	//Empty Constructor
	public Booking(){
		bookingID=0;
		userID=0;
		location="";
		startDate=null;
		endDate=null;
	}
	//Constructor with Parameters
	public Booking(int bookingId, int userId, String loc, List<SeatBooked> seats){
		this.setBookingID(bookingId);
		this.setUserIDFK(userId);
		this.setLocation(loc);
		this.seats=seats;
		startDate=null;
		endDate=null;
	}
	//Constructor with Parameters
		public Booking(int bookingId, int userId, String loc){
			this.setBookingID(bookingId);
			this.setUserIDFK(userId);
			this.setLocation(loc);
			startDate=null;
			endDate=null;
		}
	
	//Gets and Setters
	public void setBookingID(int id){
		if(id>0)
			bookingID=id;
		else
			bookingID=-1;
	}
	public int getBookingID(){
		return bookingID;
	}
	public void setUserIDFK(int userId){
		if(userId>0)
			userID=userId;
		else
			userID=-1;
	}
	public int getUserID(){
		return userID;
	}
	
	public void setLocation(String loc){
		if(!loc.equals(""))
			location=loc;
		else
			location="InvalidLocation";
	}
	public String getLocation(){
		return location;
	}
	public void setStartDate(String stringDate){
		startDate=this.convertStringToSQLDate(stringDate);
	}
	public java.sql.Date getStartDate(){
		return startDate;
	}public void setEndDate(String stringDate){
		endDate=this.convertStringToSQLDate(stringDate);
	}
	public java.sql.Date getEndDate(){
		return endDate;
	}
	public void setSeats(List<SeatBooked> seats){
		this.seats=seats;
	}
	public List<SeatBooked> getSeats(){
		return seats;
	}
	public void addSeatBooked (SeatBooked seat){
		if(seats==null)
			seats=new ArrayList<SeatBooked>();
		if(seat!=null)
			seats.add(seat);
	}
	/*
	 * This method defines the structure of the Date variables
	 */
	private java.sql.Date convertStringToSQLDate(String date){
		try{
			Date dateConverted=dateFormatter.parse(date);
			return new java.sql.Date(dateConverted.getTime());
		}
		catch(ParseException ex){}
		return null;
	}
	public String toString(){
		String s="";
		s+="BookingID: "+this.getBookingID()+"\nUserID: "+this.getUserID()+"\nLocation: "+this.getLocation()+"\nStart Date: "+startDate.toString()+"\nEnd Date: "+endDate.toString();
		for(SeatBooked element: seats){
			s+="\n"+element.toString();
		}
		s+="\n---------------------------------";
		return s;
	}
	
	public static Booking cloneBooking(Booking b){
		Booking obj=new Booking();
		obj.setBookingID(b.getBookingID());
		obj.setUserIDFK(b.getUserID());
		obj.setLocation(b.getLocation());
		obj.setSeats(b.getSeats());
		return obj;
	}

}
