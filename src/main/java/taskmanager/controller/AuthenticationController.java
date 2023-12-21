package taskmanager.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import taskmanager.config.JwtUtils;
import taskmanager.exception.InternalProcessException;
import taskmanager.model.user.LoggedOnResponse;
import taskmanager.model.user.LogonRequest;
import taskmanager.model.user.RegisterRequest;
import taskmanager.model.user.RegisterResponse;
import taskmanager.service.UserService;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtUtils jwtUtils;

    @PostMapping("/logon")
    public ResponseEntity<LoggedOnResponse> logon(
            @RequestBody @Valid LogonRequest req
    ) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(req.getUsername(), req.getPassword())
            );
            return new ResponseEntity<>(new LoggedOnResponse(
                    jwtUtils.generateToken(userService.findByName(req.getUsername()))
            ), HttpStatus.OK);
        } catch (Exception e) {
            throw new InternalProcessException("Logon failed, " + e.toString());
        }

    }

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(
            @RequestBody @Valid RegisterRequest req
    ) {
        try {
            return new ResponseEntity<>(
                    new RegisterResponse(
                        userService.create(req.getUsername(), req.getPassword(), "user")
            ), HttpStatus.OK);
        } catch (Exception e) {
            throw new InternalProcessException("Registration failed, " + e.toString());
        }
    }

    //TODO logoff

}
