package hello.bookingwebservice;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SeatBooked {
	//Constants
	private final String DATE_PATTERN="yyyy-MM-dd";
	private final SimpleDateFormat dateFormatter=new SimpleDateFormat(DATE_PATTERN);
	
	//Global Variables
	private int deskId;
	private int deskBlock;
	private String deskLetter;
	private java.sql.Date date;
	
	//Empty Constructor
	public SeatBooked(){
		deskId=0;
		deskBlock=0;
		deskLetter="";
		date=null;
	}
	//Contructor with parameters
	public SeatBooked(int deskID, int deskB, String deskL, String stringDate){
		this.deskId=deskID;
		this.deskBlock=deskB;
		this.deskLetter=deskL;
		//Convert the String date into a java.sql.Date object
		date=this.convertStringToSQLDate(stringDate);
	}
	
	public void setDeskID(int value){
		deskId=value;
	}
	public int getDeskID(){
		return deskId;
	}
	public void setDeskBlock(int desk){
		deskBlock=desk;
	}
	public int getDeskBlock(){
		return deskBlock;
	}
	public void setDeskLetter(String letter){
		deskLetter=letter;
	}
	public String getDeskLetter(){
		return deskLetter;
	}
	public void setDate(java.sql.Date date){
		this.date=date;
	}
	public java.sql.Date getDate(){
		return date;
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
		s+="Desk ID: "+deskId+"\nDesk Block: "+deskBlock+"\nDesk Letter: "+deskLetter+"\nDate: "+date.toString();
		return s;
	}

}
