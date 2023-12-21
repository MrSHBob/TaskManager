package taskmanager.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import taskmanager.dao.Comment;
import taskmanager.dao.Task;
import taskmanager.dao.User;
import taskmanager.exception.InternalProcessException;
import taskmanager.model.comment.CommentRequest;
import taskmanager.model.comment.CommentResponse;
import taskmanager.service.CommentService;
import taskmanager.service.UserService;


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

    @PostMapping("/comment/add")
    public ResponseEntity<CommentResponse> addComment(
            @RequestBody @Valid CommentRequest req
    ) {
        req.setAuthor(getCurrentUser().getId());

        try {
            return new ResponseEntity<>(
                    new CommentResponse(
                            commentService.create(req)
                    ), HttpStatus.OK);
        } catch (Exception e) {
            throw new InternalProcessException("Comment not saved, " + e.toString());
        }
    }

    @GetMapping("/getComments")
    public ResponseEntity<Page<CommentResponse>> findPaginated(
            @RequestParam @NotNull int page,
            @RequestParam @NotNull int size,
            @RequestParam @NotNull long taskId
    ) {
        try {
            return new ResponseEntity<>(
                    commentService.getAllCommentsByTaskIdPaged(
                            page,
                            size,
                            taskId)
                    , HttpStatus.OK);
        } catch (Exception e) {
            throw new InternalProcessException("Comment request failed, " + e.toString());
        }
    }

}
