package hello.bookingwebservice;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class BookingTable {

	//Constants
	private final String DATE_PATTERN="yyyy-MM-dd";
	private final SimpleDateFormat dateFormatter=new SimpleDateFormat(DATE_PATTERN);

	private int deskID;
	private ArrayList<String> dates;

	public BookingTable(){}
	
	public BookingTable(int id, ArrayList<String> dates){
		this.deskID=id;
		this.dates=dates;
	}

	public void setDeskID(int id){
		this.deskID=id;
	}
	public int getDeskID(){
		return deskID;
	}
	public void setDates(ArrayList<String> dates){
		this.dates=dates;
	}

	
	public ArrayList<String> getDates(){
		return dates;
	}
	
	public void addDate(String date){
	if(dates==null){
		dates=new ArrayList<String>();
	}
	dates.add(date);
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
	public boolean datesValidity(){
		return (dates!=null)? true:false;
	}
}
