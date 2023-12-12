package taskmanager.task;

import io.restassured.response.Response;
import org.json.JSONArray;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import taskmanager.TaskManagerApplication;
import taskmanager.enums.TaskPriorityEnum;
import taskmanager.enums.TaskStatusEnum;
import taskmanager.model.CommentRequest;
import taskmanager.model.PaginatedTasksRequest;
import taskmanager.model.TaskRequest;
import taskmanager.repo.CommentRepo;
import taskmanager.repo.TaskRepo;
import taskmanager.repo.UserRepo;
import taskmanager.service.CommentService;
import taskmanager.service.TaskService;
import taskmanager.service.UserService;
import taskmanager.utils.Requests;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@SpringBootTest
@ContextConfiguration(classes = TaskManagerApplication.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class RequestTasksWithCommentTests {
    StringBuilder errors = null;

    String url = "http://localhost:8080";

    @Autowired
    private UserService userService;
    @Autowired
    private TaskService taskService;
    @Autowired
    private CommentService commentService;
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private TaskRepo taskRepo;
    @Autowired
    private CommentRepo commentRepo;

    void before() {
        errors = new StringBuilder();
    }
    void after() {
        if ((errors != null) && (!errors.isEmpty())) {
            Assertions.assertEquals("", errors.toString());
        }
    }

    @Test
    @Order(1)
    public void getTasksTest() {
        before();
        String testUser1 = "usrGT1";
        String testUser2 = "usrGT1A";

        Requests req = new Requests(url);
        req.register(testUser1, testUser1);
        req.register(testUser2, testUser2);
        String token1 = req.authenticate(testUser1, testUser1).getBody().asString();

        List<TaskRequest> trs = new ArrayList<>();

        // add tasks
        for (int i = 0; i < 20; i++) {
            TaskRequest tr = new TaskRequest();
            tr.setTitle("TitleForRequest" + (i + 1));
            tr.setDescription("DescriptionForRequest" + (i + 1));
            tr.setPriority(TaskPriorityEnum.LOW.value);
            if (i > 14) {
                tr.setResponsiblePerson(userRepo.findByUsername(testUser2).getId());
            } else {
                tr.setResponsiblePerson(userRepo.findByUsername(testUser1).getId());
            }

            Response resp = req.createTask(tr, token1);
            Map map = Requests.getMapFromJsonBody(resp.getBody().asString());
            tr.setId((long) (int) map.get("id"));
            trs.add(tr);
        }

        // get all Tasks paged ()
        PaginatedTasksRequest ptr1 = new PaginatedTasksRequest();
        ptr1.setPage(1);
        ptr1.setSize(5);
        Response resp1 = req.getTasksPaginated(ptr1, token1);
        Map map1 = Requests.getMapFromJsonBody(resp1.getBody().asString());

        Assertions.assertTrue((int) map1.get("total") > 19, "Not enough amount of tasks");

        // get Tasks by Author (check that amount is 20)
        PaginatedTasksRequest ptr2 = new PaginatedTasksRequest();
        ptr2.setAuthorFilter(userRepo.findByUsername(testUser1).getId());
        Response resp2 = req.getTasksPaginated(ptr2, token1);
        List<Map> list2 = Requests.getListOfMapFromJsonBody(resp2.getBody().asString());

        Assertions.assertEquals(20, list2.size());

        // get by RespPerson TestUser2 - amount = 5
        PaginatedTasksRequest ptr3 = new PaginatedTasksRequest();
        ptr3.setResponsiblePersonFilter(userRepo.findByUsername(testUser2).getId());
        Response resp3 = req.getTasksPaginated(ptr3, token1);
        List<Map> list3 = Requests.getListOfMapFromJsonBody(resp3.getBody().asString());

        Assertions.assertEquals(5, list3.size());

        trs.forEach(taskRequest -> {
            taskRepo.delete(taskRepo.findById(taskRequest.getId()).get());
        });
        userRepo.delete(userRepo.findByUsername(testUser1));
        userRepo.delete(userRepo.findByUsername(testUser2));
        after();
    }

    @Test
    @Order(2)
    public void getCommentTest() {
        before();
        String testUser1 = "usrGT2";
        String testUser2 = "usrGT2A";
        String testUser3 = "usrGT2B";

        Requests req = new Requests(url);
        req.register(testUser1, testUser1);
        req.register(testUser2, testUser2);
        req.register(testUser3, testUser3);
        String token1 = req.authenticate(testUser1, testUser1).getBody().asString();
        String token2 = req.authenticate(testUser1, testUser1).getBody().asString();
        String token3 = req.authenticate(testUser1, testUser1).getBody().asString();

        TaskRequest tr = new TaskRequest();
        tr.setTitle("TitleForCommentsRequest");
        tr.setDescription("DescriptionForCommentsRequest");
        tr.setPriority(TaskPriorityEnum.LOW.value);
        tr.setResponsiblePerson(userRepo.findByUsername(testUser2).getId());

        Response resp = req.createTask(tr, token1);
        Map map = Requests.getMapFromJsonBody(resp.getBody().asString());
        tr.setId((long) (int) map.get("id"));

        // add tasks
        for (int i = 0; i < 20; i++) {
            CommentRequest cr = new CommentRequest();
            cr.setParentTaskId(tr.getId());
            cr.setText("SomeCommentText - " + (i + 1));

            if (i < 6) {
                req.createComment(cr, token1);
            } else if (i > 14) {
                req.createComment(cr, token2);
            } else {
                req.createComment(cr, token3);
            }
        }

        // get Tasks by Author (check that amount is 20)
        PaginatedTasksRequest ptr1 = new PaginatedTasksRequest();
        ptr1.setAuthorFilter(userRepo.findByUsername(testUser1).getId());
        Response resp1 = req.getTasksPaginated(ptr1, token1);
        List<Map> list1 = Requests.getListOfMapFromJsonBody(resp1.getBody().asString());
        Map map1 = list1.get(0);
        List<Map> list2 = (List) map1.get("comments");

        Assertions.assertEquals(20, list2.size());

        list2.forEach(comment -> {
            Assertions.assertTrue(comment.get("text").toString().contains("SomeCommentText - "));
        });

        Response resp3 = req.deleteTask(tr, token1);
        Assertions.assertEquals(200, resp3.getStatusCode());
        String body = resp3.getBody().asString();
        Assertions.assertTrue(body.contains("Task successfully deleted."), "Message missed.");


        userRepo.delete(userRepo.findByUsername(testUser1));
        userRepo.delete(userRepo.findByUsername(testUser2));
        userRepo.delete(userRepo.findByUsername(testUser3));
        after();
    }
}
