package taskmanager.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import taskmanager.config.JwtUtils;
import taskmanager.dao.User;
import taskmanager.model.AuthenticationRequest;
import taskmanager.service.UserService;
import taskmanager.utility.JsonSerializer;


@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtUtils jwtUtils;

    @PostMapping("/authenticate")
    public ResponseEntity<String> authenticate(
            @RequestBody AuthenticationRequest req
    ) {
        UserDetails user = null;
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(req.getUsername(), req.getPassword())
            );
            user = userService.findByName(req.getUsername());
        } catch (Exception e) {
            return ResponseEntity.status(400).body(e.toString());
        }
        if (user != null) {
            return ResponseEntity
                    .ok()
                    .body(jwtUtils.generateToken(user));
        }
        return ResponseEntity.status(400).body("Some error has occurred");
    }

    @PostMapping("/registration")
    public ResponseEntity<String> registration(
            @RequestBody AuthenticationRequest req
    ) {
        User user = null;
        try {
            user = userService.create(
                req.getUsername(), req.getPassword(), "user"
            );
        } catch (Exception e) {
            return ResponseEntity.status(400).body(e.toString());
        }

        if ((user != null) && (user.getId() > 0)) {
            user.setPassword(null);
            String jsonResponse = JsonSerializer.gson().toJson(user);

            return ResponseEntity
                    .ok()
                    .body(jsonResponse);
        }
        return ResponseEntity.status(400).body("Registration failed");
    }

    //TODO logoff

}
