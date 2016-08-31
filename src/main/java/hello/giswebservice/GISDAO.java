package hello.giswebservice;

import org.springframework.http.ResponseEntity;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.mysql.jdbc.Connection;
import hello.DBSettings;
import hello.Validate;
import hello.Validate.Validator;


public class GISDAO {

	//Method to manipulate GIS table
	public static ResponseEntity<String> updateMap(GISWrapper deskIDs) {
		Validator val = Validate.validateUpdateMap(deskIDs);
		if(!val.pass){
			return ResponseEntity.badRequest().body(val.message);
		}
		try{
			//Get connection & disable auto commit for batch execution
			Connection conn = DBSettings.establishConnection();
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
			Connection conn = DBSettings.establishConnection();
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


}
