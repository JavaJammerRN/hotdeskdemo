package hello.userwebservice;

public class User {
	
	//Global Variables
	private int userID;
	private String forename,surname, username;
	
	//Empty Constructor
	public User(){
		userID=-1;
		forename="null";
		surname="null";
		username="";
	}
	
	//Constructor with Parameters
	public User(int userID, String forename, String surname, String username){
		this.userID = userID;
		this.forename = forename;
		this.surname = surname;
		this.username = username;
	}
	
	//Constructor with Parameters
	public User(int id){
		//Verify that the given ID is valid
		this.setUserID(id);
		forename="null";
		surname="null";
		username="";
	}
	
	//Constructor with Parameters
	public User(String givenUsername){
		if(!givenUsername.equals("")){
			this.username=givenUsername;
			userID=-1;
			forename="";
			surname="";
		}
	}
	
	//Getters and Setters
	public void setUserID(int id){
		if(id>0){
			userID=id;
		}
		else{
			userID=-1;
		}
	}
	public int getUserID(){
		return userID;
	}
	public void setForename(String name){
		if(name!=null && !name.equals("")){
			forename=name;
		}
	}
	public String getForename(){
		return forename;
	}
	public void setSurname(String sur){
		if(sur!=null && !sur.equals("")){
			surname=sur;
		}
	}
	public String getSurname(){
		return surname;
	}
	public void setUsername(String s){
		if(s!=null && !s.equals("")){
			username=s;
		}
	}
	public String getUsername(){
		return username;
	}
	public String toString(){
		String s="";
		s+="User ID: "+userID+"\nForename: "+forename+"\nSurname: "+surname+"\nUsername: "+username;
		return s;
	}

}
