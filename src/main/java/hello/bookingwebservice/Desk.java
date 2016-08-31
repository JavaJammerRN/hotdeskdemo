package hello.bookingwebservice;

public class Desk {
	private int deskID, deskBlock;
	private String deskLetter, location;
	
	//Empty Constructor
	public Desk(){
		deskID=0;
		deskBlock=0;
		deskLetter="";
		location="";
	}
	
	//Constructor with parameters
	public Desk(int deskId, int deskB, String deskL, String loc){
		deskID=deskId;
		deskBlock=deskB;
		deskLetter=deskL;
		location=loc;
	}
	
	public int getDeskID() {
		return deskID;
	}
	public void setDeskID(int deskID) {
		this.deskID = deskID;
	}
	public int getDeskBlock() {
		return deskBlock;
	}
	public void setDeskBlock(int deskBlock) {
		this.deskBlock = deskBlock;
	}
	public String getDeskLetter() {
		return deskLetter;
	}
	public void setDeskLetter(String deskLetter) {
		this.deskLetter = deskLetter;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	
	public String toString(){
		String s="";
		s+="DeskID: "+deskID+"\nDesk Block: "+deskBlock+"\nDesk Letter: "+deskLetter+"\nLocation: "+location;
		return s;
	}
	

}
