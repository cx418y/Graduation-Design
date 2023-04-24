package fudan.design.clone.controller;


import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
@Controller
public class cloneController {
	@GetMapping("/getCloneModule")
	public ResponseEntity<?> getAllModule() {

		return null;
	}

	@GetMapping("/test")
	public ResponseEntity<String>test() {
		String s = "hello";
		return ResponseEntity.ok(s);
	}
}
