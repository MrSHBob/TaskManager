package taskmanager.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import taskmanager.dao.Comment;
import taskmanager.dao.Task;
import taskmanager.dao.User;
import taskmanager.model.CommentRequest;
import taskmanager.model.PaginatedTasksRequest;
import taskmanager.model.TaskRequest;
import taskmanager.model.TaskResponse;
import taskmanager.service.CommentService;
import taskmanager.service.TaskService;
import taskmanager.service.UserService;
import taskmanager.utility.JsonSerializer;

import java.util.List;


@RestController
@RequestMapping("/api/task")
@RequiredArgsConstructor
public class TaskController {

    private final UserService userService;
    private final CommentService commentService;
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

    @PostMapping("/new")
    public ResponseEntity<String> newTask(
            @RequestBody TaskRequest req
    ) {
        TaskResponse t = null;
        req.setAuthor(getCurrentUser().getId());
        try {
            t = new TaskResponse(taskService.create(req));
        } catch (Exception e) {
            return ResponseEntity.status(400).body(e.toString());
        }

        if ((t != null) && (t.getId() > 0)) {
            String jsonResponse = JsonSerializer.gson().toJson(t);

            return ResponseEntity
                    .ok()
                    .body(jsonResponse);
        }
        return ResponseEntity.status(400).body("Add new task failed");
    }

    @PostMapping("/update")
    public ResponseEntity<String> updateTask(
            @RequestBody TaskRequest req
    ) {
        TaskResponse t = null;
        try {
            t = new TaskResponse(taskService.update(req, getCurrentUser().getId()));
        } catch (Exception e) {
            return ResponseEntity.status(400).body(e.toString());
        }

        if ((t != null) && (t.getId() > 0)) {
            String jsonResponse = JsonSerializer.gson().toJson(t);

            return ResponseEntity
                    .ok()
                    .body(jsonResponse);
        }
        return ResponseEntity.status(400).body("Add new task failed");
    }

    @PostMapping("/getTasks")
    public ResponseEntity<String> findPaginated(
            @RequestBody PaginatedTasksRequest req
    ) {
        if ((req.getPage() != null) && (req.getSize() != null)) {
            Page<Task> resultPage = null;
            if (req.getAuthorFilter() != null) {
                resultPage = taskService.getTasksByAuthorPaged(
                        req.getAuthorFilter(),
                        req.getPage(),
                        req.getSize());
            } else if (req.getResponsiblePersonFilter() != null) {
                resultPage = taskService.getTasksByRespPersonPaged(
                        req.getResponsiblePersonFilter(),
                        req.getPage(),
                        req.getSize()
                );
            } else {
                resultPage = taskService.getAllTasksPaged(req.getPage(), req.getSize());
            }

            if (resultPage != null) {
                if (req.getPage() > resultPage.getTotalPages()) {
                    return ResponseEntity.status(400).body("Page not found.");
                }

                String jsonResponse = JsonSerializer.gson().toJson(resultPage);
                return ResponseEntity
                        .ok()
                        .body(jsonResponse);
            }
        } else {
            List<Task> tasks = null;
            if (req.getAuthorFilter() != null) {
                tasks = taskService.getTasksByAuthor(req.getAuthorFilter());
            } else if (req.getResponsiblePersonFilter() != null) {
                tasks = taskService.getTasksByRespPerson(req.getResponsiblePersonFilter());
            } else {
                tasks = taskService.getAllTasks();
            }

            if (tasks != null) {
                if (tasks.size() == 0) {
                    return ResponseEntity.status(400).body("Tasks not found.");
                }

                String jsonResponse = JsonSerializer.gson().toJson(tasks);
                return ResponseEntity
                        .ok()
                        .body(jsonResponse);
            }
        }
        return ResponseEntity.status(400).body("Unexpected error happens");
    }

    @PostMapping("/delete")
    public ResponseEntity<String> deleteTask(
            @RequestBody TaskRequest req
    ) {
        Boolean t = null;
        try {
            t = taskService.delete(req, getCurrentUser().getId());
        } catch (Exception e) {
            return ResponseEntity.status(400).body(e.toString());
        }

        if (t) {
            return ResponseEntity.status(200).body("Task successfully deleted.");
        }
        return ResponseEntity.status(400).body("Task deletion failed");
    }
}
