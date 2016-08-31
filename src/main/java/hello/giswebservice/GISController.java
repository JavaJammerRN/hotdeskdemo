package hello.giswebservice;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/gis")
public class GISController {

	// PUT - update map with all available seats
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "", method = RequestMethod.PUT)
	public @ResponseBody ResponseEntity<String> updateMap(@RequestBody GISWrapper desksAvailable) {
		return GISDAO.updateMap(desksAvailable);
	}

	// PUT - delete existing booking
	@CrossOrigin(origins = "*")
	@RequestMapping(value = "/{deskSelected}", method = RequestMethod.PUT)
	public @ResponseBody ResponseEntity<String> updateMapSelected(@PathVariable int deskSelected) {
		return GISDAO.updateMapSelected(deskSelected);
	}
}
