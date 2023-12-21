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
import taskmanager.dao.Task;
import taskmanager.dao.User;
import taskmanager.exception.InternalProcessException;
import taskmanager.model.task.*;
import taskmanager.service.TaskService;
import taskmanager.service.UserService;
import taskmanager.utility.JsonSerializer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api/task")
@RequiredArgsConstructor
public class TaskController {

    private final UserService userService;
    private final TaskService taskService;

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

    @PostMapping("/create")
    public ResponseEntity<TaskResponse> createTask(
            @RequestBody @Valid CreateTaskRequest req
    ) {
        req.setAuthor(getCurrentUser().getId());
        try {
            return new ResponseEntity<>(
                    new TaskResponse(
                            taskService.create(req)
                    ), HttpStatus.OK);
        } catch (Exception e) {
            throw new InternalProcessException("Task not saved, " + e.toString());
        }
    }

    @PutMapping("/update")
    public ResponseEntity<TaskResponse> updateTask(
            @RequestBody @Valid EditTaskRequest req
    ) {
        req.setAuthor(getCurrentUser().getId());
        try {
            return new ResponseEntity<>(
                    new TaskResponse(
                            taskService.update(req, getCurrentUser().getId())
                    ), HttpStatus.OK);
        } catch (Exception e) {
            throw new InternalProcessException("Task update failed, " + e.toString());
        }
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Map> deleteTask(
            @RequestBody @Valid DeleteTaskRequest req
    ) {
        Boolean b = null;
        req.setAuthor(getCurrentUser().getId());
        try {
            b = taskService.delete(req, getCurrentUser().getId());
            if (b) {
                Map<String, String> map = new HashMap<>();
                map.put("message", "Task deleted successful.");
                return new ResponseEntity<>(map, HttpStatus.OK);
            } else {
                throw new InternalProcessException("");
            }
        } catch (Exception e) {
            throw new InternalProcessException("Task not deleted, " + e.toString());
        }
    }

    @GetMapping("/getTasks")
    public ResponseEntity<Page<TaskResponse>> findPaginated(
            @RequestParam @NotNull int page,
            @RequestParam @NotNull int size
    ) {
        try {
            return new ResponseEntity<>(
                    taskService.getAllTasksPaged(
                            page,
                            size)
                    , HttpStatus.OK);
        } catch (Exception e) {
            throw new InternalProcessException("Task request failed, " + e.toString());
        }
    }

    @GetMapping("/getTasksByAuthor")
    public ResponseEntity<Page<TaskResponse>> findPaginatedByAuthor(
            @RequestParam @NotNull int page,
            @RequestParam @NotNull int size,
            @RequestParam @NotNull long authorId
    ) {
        try {
            return new ResponseEntity<>(
                    taskService.getTasksByAuthorPaged(
                            authorId,
                            page,
                            size)
                    , HttpStatus.OK);
        } catch (Exception e) {
            throw new InternalProcessException("Task request failed, " + e.toString());
        }
    }

    @GetMapping("/getTasksByRespPers")
    public ResponseEntity<Page<TaskResponse>> findPaginatedByRespPers(
            @RequestParam @NotNull int page,
            @RequestParam @NotNull int size,
            @RequestParam @NotNull long respPersId
    ) {
        try {
            return new ResponseEntity<>(
                    taskService.getTasksByRespPersonPaged(
                            respPersId,
                            page,
                            size)
                    , HttpStatus.OK);
        } catch (Exception e) {
            throw new InternalProcessException("Task request failed, " + e.toString());
        }
    }
}
