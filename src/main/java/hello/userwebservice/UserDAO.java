package hello.userwebservice;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import com.mysql.jdbc.Connection;

import hello.DBSettings;

public class UserDAO {

	public static  ResponseEntity<User> getUserDataByUsername(String userInfo) throws SQLException{
		//Create a temporary user object to store the user information
		if(!userInfo.equals("")){
			//Establish a connection with the database
			Connection connectionDB=DBSettings.establishConnection();
			if(connectionDB!=null){
				try{
					Statement stmt = connectionDB.createStatement();
					//Adjust the query
					String query = "select * from user where username='"+userInfo+"'";
					//Execute the query
					boolean status = stmt.execute(query);
					if(status){
						//Extract the data from the resultset object
						ResultSet rs = stmt.getResultSet();
//						User userTemp=new User(userInfo);
						User userTemp = null;
						while(rs.next()){
							int userID = rs.getInt("userID");
							String forename = rs.getString("forename");
							String surname = rs.getString("surname");
							String username = rs.getString("username");
//							userTemp.setUserID((Integer.parseInt(rs.getString("userID"))));
//							userTemp.setForename(rs.getString("forename"));
//							userTemp.setSurname(rs.getString("surname"));
//							userTemp.setUsername(rs.getString("username"));
							userTemp = new User(userID, forename, surname, username);
						}
						//Close the connection with the database
						rs.close();
						//Return all the user information
						if(userTemp != null){
							return ResponseEntity.ok(userTemp);
						} else {
							throw new SQLException();
						}
					}
				}
				catch(SQLException SQLe){
//					return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
					HttpHeaders headers = new HttpHeaders();
					headers.add("Error", "ID not found");

					return new ResponseEntity<User>(null, headers, HttpStatus.NOT_FOUND);
					
				}
			}
		}
		return null;
	}
}
