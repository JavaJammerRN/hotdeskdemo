package hello.userwebservice;

import java.sql.SQLException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController{

	@CrossOrigin(origins = "*")
	@RequestMapping(value="/{userName}", method = RequestMethod.GET)
	  public ResponseEntity<User> users(@PathVariable String userName) throws SQLException  {
		return UserDAO.getUserDataByUsername(userName);
    }
	

}
