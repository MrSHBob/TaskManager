package taskmanager.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import taskmanager.config.JwtUtils;
import taskmanager.dao.Comment;
import taskmanager.dao.User;
import taskmanager.model.AuthenticationRequest;
import taskmanager.model.CommentRequest;
import taskmanager.model.CommentResponse;
import taskmanager.service.CommentService;
import taskmanager.service.UserService;
import taskmanager.utility.JsonSerializer;


@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CommentController {

    private final UserService userService;
    private final CommentService commentService;

    private User getCurrentUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username;
        if (principal instanceof UserDetails) {
            username = ((UserDetails)principal).getUsername();
        } else {
            username = principal.toString();
        }
        return userService.findUserByName(username);
    }

    @PostMapping("/newComment")
    public ResponseEntity<String> newComment(
            @RequestBody CommentRequest req
    ) {
        CommentResponse comment = null;
        req.setAuthor(getCurrentUser().getId());
        try {
            comment = new CommentResponse(commentService.create(req));
        } catch (Exception e) {
            return ResponseEntity.status(400).body(e.toString());
        }

        if ((comment != null) && (comment.getId() > 0)) {
            String jsonResponse = JsonSerializer.gson().toJson(comment);

            return ResponseEntity
                    .ok()
                    .body(jsonResponse);
        }
        return ResponseEntity.status(400).body("Add new comment failed");
    }

}
