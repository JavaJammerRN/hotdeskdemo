package hello;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import javax.validation.Valid;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import com.mysql.jdbc.Connection;
import hello.bookingwebservice.*;
import hello.giswebservice.*;

public class Validate {



	private enum idType{
		USER,
		DESK,
		BOOKING
	}


	public static Validator validateCreateBooking(int userID, BookingTableWrapper bookingTableWrapper){
		Validator val = null;
		
		val = validateID(idType.USER, userID);
		if(!val.pass)
			return val;
		
		val = validateBookingTableWrapper(0, bookingTableWrapper);
		if(!val.pass)
			return val;
		
		return new Validator(true,"");
	}
	
	public static Validator validateUpdateBooking(int userID, int bookingID, BookingTableWrapper bookingTableWrapper){
		Validator val = null;	
		
		val = validateID(idType.BOOKING, bookingID);
		if(!val.pass)
			return val;
		
		val = validateBookingTableWrapper(bookingID, bookingTableWrapper);
		if(!val.pass)
			return val;
		
		return new Validator(true,"");
	}
	
	public static Validator validateDeleteBooking(int bookingID){
		Validator val = null;
		val = validateID(idType.BOOKING, bookingID);
		if(!val.pass)
			return val;

		return new Validator(true,"");
	}
	
	public static Validator validateUpdateMap(GISWrapper deskIDs){
		Validator val = null;
		
		for(int ID : deskIDs.getDeskIDs()){
			val = validateID(idType.DESK, ID);
			if(!val.pass)
				return val;
		}
		return new Validator(true,"");
	}
	
	public static Validator validateUpdateMapSelected(int deskID){
		Validator val = null;
		val = validateID(idType.DESK, deskID);
		if(!val.pass)
			return val;

		return new Validator(true,"");
	}
	
	private static Validator validateBookingTableWrapper(int bookingID, BookingTableWrapper bookingTableWrapper){
		Validator val = null;
		HashMap<Integer, ArrayList<String>> bookingTableMap = new HashMap<>();
		
		for(BookingTable bookingTable : bookingTableWrapper.getBookingTables()){
				bookingTableMap.put(bookingTable.getDeskID(), bookingTable.getDates());
		}
		
		ArrayList<Integer> deskIDList = new ArrayList<>(bookingTableMap.keySet());
		
		for(int ID : deskIDList){
			validateID(idType.DESK, ID);
			val = validateID(idType.DESK, ID);
			if(!val.pass)
				return val;
			}
		
		val = validateDates(bookingID, bookingTableMap);
		if(!val.pass)
			return val;

		return new Validator(true,"");
	}
	
	
	private static Validator validateDates(int bookingID, HashMap<Integer, ArrayList<String>> bookingTableMap){
		Validator val = null;
		val = checkWeekends(bookingTableMap);
		if(!val.pass)
			return val;
		
		Map<Integer, ArrayList<String>> DBBookingMap = getBookings(bookingID);
		
		for(Entry<Integer, ArrayList<String>> booking : bookingTableMap.entrySet()){
			int key = booking.getKey();
			
			if(!DBBookingMap.containsKey(key)){
				continue;
			}
			
			ArrayList<String> DBDates = DBBookingMap.get(key);
			for(String d : booking.getValue()){
				if(DBDates.contains(d)){
					return new Validator(false,"There is already a booking in this period. Please try another.");
				}
			}
			
		}

		return new Validator(true,"");
		
	}
	
	
	private static Validator checkWeekends(HashMap<Integer, ArrayList<String>> bookingTableMap) {
		for(ArrayList<String> dates : bookingTableMap.values()){
			for(String date : dates){
				LocalDate d = LocalDate.parse(date);
				if(d.getDayOfWeek() == DayOfWeek.SATURDAY || d.getDayOfWeek() == DayOfWeek.SUNDAY){
					return new Validator(false,"Bookings can not be made on weekends");
				}
			}
		}
		return new Validator(true,"");
	}

	private static Validator validateID(idType type, int ID){
		
		ArrayList<Integer> DBIDList = getIDList(type);
		
		if(DBIDList == null){
			return new Validator(false,"Something went wrong, please try again later.");
		}
		
		if(!DBIDList.contains(ID)){
			return new Validator(false,"This " + type + "ID does not exist.");
		}
		
		return new Validator(true,"");
		
	}

	
	private static ArrayList<Integer> getIDList(idType type){
		ArrayList<Integer> dBIDList = new ArrayList<>();
		try{
			Connection conn = DBSettings.establishConnection();
			PreparedStatement stmt = null;
			switch(type){
			case USER:
				stmt = conn.prepareStatement("SELECT userID FROM user");
				break;
			case DESK:
				stmt = conn.prepareStatement("SELECT deskID FROM desk");
				break;
			case BOOKING:
				stmt = conn.prepareStatement("SELECT bookingID FROM booking");
				break;
			}
			ResultSet rs = stmt.executeQuery();
			while(rs.next()){
				dBIDList.add(rs.getInt(1));
				}
			return dBIDList;
		}catch(SQLException SLQe){
			return null;
		}
	}
	
	private static Map<Integer,ArrayList<String>> getBookings(int bookingID){
		Map<Integer, ArrayList<String>> DBTableMap = new HashMap<>();
		try{
			Connection conn = DBSettings.establishConnection();
			PreparedStatement stmt =  conn.prepareStatement("SELECT * FROM bookingdate");
			ResultSet rs = stmt.executeQuery();
			while(rs.next()){
				int bID = rs.getInt("bookingID");
				int deskID = rs.getInt("deskID");
				String date = rs.getString("date");
				
				if(bookingID == bID){
					continue;
				}
				
				if(DBTableMap.containsKey(deskID)){
					DBTableMap.get(deskID).add(date);
				}else{
					DBTableMap.put(deskID, new ArrayList<String>(Arrays.asList(new String[]{date})));
				}
			}
			return DBTableMap;			
		}catch(SQLException SLQe){
			return null;
		}		
	}
	
	
	public static class Validator{
		public boolean pass;
		public String message;
		
		public Validator(boolean pass, String message){
			this.pass = pass;
			this.message = message;
		}
		
		public Validator(){
			this.pass = true;
			this.message = "";
		}
		
		public void setValidator(boolean pass, String message){
			this.pass = pass;
			this.message = message;
		}
	}
	
}

