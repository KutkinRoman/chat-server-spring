package chat.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;

@RestController
public class MainController {

    @Value("${server.name}")
    private String serverName;

    @GetMapping
    public ResponseEntity<?> index () {
        HashMap<String, String> response = new HashMap<> ();
        response.put ("server", serverName);
        response.put ("dateTime", LocalDateTime.now ().toString ());
        return ResponseEntity.ok (response);
    }
}
