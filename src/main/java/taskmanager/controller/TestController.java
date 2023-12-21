//package taskmanager.controller;
//
//import jakarta.validation.Valid;
//import lombok.RequiredArgsConstructor;
//import org.springframework.data.domain.Page;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//import taskmanager.dao.Task;
//import taskmanager.dao.User;
//import taskmanager.exception.InternalProcessException;
//import taskmanager.model.task.PaginatedTasksRequest;
//import taskmanager.model.task.TaskResponse;
//import taskmanager.model.tests.Test1Request;
//import taskmanager.service.TaskService;
//import taskmanager.service.UserService;
//
//
//@RestController
//@RequestMapping("/api/tests")
//@RequiredArgsConstructor
//public class TestController {
//
//    private final TaskService taskService;
//    private final UserService userService;
//
//    private User getCurrentUser() {
//        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//        String username;
//        if (principal instanceof UserDetails) {
//            username = ((UserDetails)principal).getUsername();
//        } else {
//            username = principal.toString();
//        }
//        return userService.findUserByName(username);
//    }
//
//    @PostMapping("/test1")
//    public ResponseEntity<String> newTask(
//            @RequestBody @Valid Test1Request req
//    ) {
//        throw new InternalProcessException("Something went wrong");
//    }
//
//    @PostMapping("/getTasks")
//    public ResponseEntity<Page<TaskResponse>> findPaginated(
//            @RequestBody @Valid PaginatedTasksRequest req
//    ) {
//        try {
//            return new ResponseEntity<>(
//                    taskService.getAllTasksPaged(
//                            req.getPage(),
//                            req.getSize())
//                    , HttpStatus.OK);
//        } catch (Exception e) {
//            throw new InternalProcessException("Task request failed, " + e.toString());
//        }
//    }
//
//}
