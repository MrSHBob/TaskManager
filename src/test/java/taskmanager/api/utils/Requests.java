package taskmanager.api.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.json.JSONArray;
import org.json.JSONObject;
import taskmanager.model.TaskRequest;
import taskmanager.model.comment.CommentRequest;
import taskmanager.model.task.CreateTaskRequest;
import taskmanager.model.task.DeleteTaskRequest;
import taskmanager.model.task.EditTaskRequest;
import taskmanager.model.task.PaginatedTasksRequest;
import taskmanager.model.user.LogonRequest;
import taskmanager.model.user.RegisterRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class Requests {
    private String baseUrl = null;

    public Requests(String url) {
        this.baseUrl = url;
    }

    public Map<String, Object> register(
            RegisterRequest rr
    ) {
        JSONObject requestParams = new JSONObject();
        try {
            requestParams.put("username", rr.getUsername());
            requestParams.put("password", rr.getPassword());
        } catch (Exception e) {}

         Response resp = RestAssured.given()
                .contentType(ContentType.JSON)
                .body(requestParams.toString())
                .post(baseUrl + "/api/auth/register");

        return Requests.getMapFromJsonBody(resp.getBody().asString());
    }

    public Map<String, Object> authenticate(
            LogonRequest lr
    ) {
        JSONObject requestParams = new JSONObject();
        try {
            requestParams.put("username", lr.getUsername());
            requestParams.put("password", lr.getPassword());
        } catch (Exception e) {}

        Response resp = RestAssured.given()
                .contentType(ContentType.JSON)
                .body(requestParams.toString())
                .post(baseUrl + "/api/auth/logon");

        return Requests.getMapFromJsonBody(resp.getBody().asString());
    }

    public Map<String, Object> createTask(
            CreateTaskRequest ctr,
            String token
    ) {
        JSONObject requestParams = new JSONObject();
        try {
            requestParams.put("title", ctr.getTitle());
            requestParams.put("description", ctr.getDescription());
            requestParams.put("priority", ctr.getPriority());
            requestParams.put("responsiblePerson", ctr.getResponsiblePerson());
        } catch (Exception e) {}

        Response resp = RestAssured.given()
                .auth().oauth2(token)
                .contentType(ContentType.JSON)
                .body(requestParams.toString())
                .post(baseUrl + "/api/task/create");

        return Requests.getMapFromJsonBody(resp.getBody().asString());
    }










    public Map<String, Object> updateTask(
            EditTaskRequest etr,
            String token
    ) {
        JSONObject requestParams = new JSONObject();
        try {
            requestParams.put("id", etr.getId());
            requestParams.put("title", etr.getTitle());
            requestParams.put("description", etr.getDescription());
            requestParams.put("status", etr.getStatus());
            requestParams.put("priority", etr.getPriority());
            requestParams.put("responsiblePerson", etr.getResponsiblePerson());
            requestParams.put("author", etr.getAuthor());
        } catch (Exception e) {}

        Response resp = RestAssured.given()
                .auth().oauth2(token)
                .contentType(ContentType.JSON)
                .body(requestParams.toString())
                .put(baseUrl + "/api/task/update");

        return Requests.getMapFromJsonBody(resp.getBody().asString());
    }

    public Map<String, Object> deleteTask(
            DeleteTaskRequest dtr,
            String token
    ) {
        JSONObject requestParams = new JSONObject();
        try {
            requestParams.put("id", dtr.getId());
        } catch (Exception e) {}

        Response resp = RestAssured.given()
                .auth().oauth2(token)
                .contentType(ContentType.JSON)
                .body(requestParams.toString())
                .delete(baseUrl + "/api/task/delete");

        return Requests.getMapFromJsonBody(resp.getBody().asString());
    }

    public Map<String, Object> getTasksPaginated(
            int page,
            int size,
            String token
    ) {
        StringBuilder params = new StringBuilder();
        params.append("?page=" + page);
        params.append("&size=" + size);

        Response resp = RestAssured.given()
                .auth().oauth2(token)
                .contentType(ContentType.JSON)
                .get(baseUrl + "/api/task/getTasks" + params);

        return Requests.getMapFromJsonBody(resp.getBody().asString());
    }

    public Map<String, Object> getTasksPaginatedByAuthor(
            int page,
            int size,
            long authorId,
            String token
    ) {
        StringBuilder params = new StringBuilder();
        params.append("?page=" + page);
        params.append("&size=" + size);
        params.append("&authorId=" + authorId);

        Response resp = RestAssured.given()
                .auth().oauth2(token)
                .contentType(ContentType.JSON)
                .get(baseUrl + "/api/task/getTasksByAuthor" + params);

        return Requests.getMapFromJsonBody(resp.getBody().asString());
    }

    public Map<String, Object> getTasksPaginatedByRespPers(
            int page,
            int size,
            long respPersId,
            String token
    ) {
        StringBuilder params = new StringBuilder();
        params.append("?page=" + page);
        params.append("&size=" + size);
        params.append("&respPersId=" + respPersId);

        Response resp = RestAssured.given()
                .auth().oauth2(token)
                .contentType(ContentType.JSON)
                .get(baseUrl + "/api/task/getTasksByRespPers" + params);

        return Requests.getMapFromJsonBody(resp.getBody().asString());
    }

    public Map<String, Object> createComment(
            CommentRequest cr,
            String token
    ) {
        JSONObject requestParams = new JSONObject();
        try {
            requestParams.put("parentTaskId", cr.getParentTaskId());
            requestParams.put("text", cr.getText());
        } catch (Exception e) {}

        Response resp = RestAssured.given()
                .auth().oauth2(token)
                .contentType(ContentType.JSON)
                .body(requestParams.toString())
                .post(baseUrl + "/api/comment/add");

        return Requests.getMapFromJsonBody(resp.getBody().asString());
    }

    public Map<String, Object> getCommentsByTaskId(
            int page,
            int size,
            long taskId,
            String token
    ) {
        StringBuilder params = new StringBuilder();
        params.append("?page=" + page);
        params.append("&size=" + size);
        params.append("&taskId=" + taskId);

        Response resp = RestAssured.given()
                .auth().oauth2(token)
                .contentType(ContentType.JSON)
                .get(baseUrl + "/api/getComments" + params);

        String body = resp.getBody().asString();

        return Requests.getMapFromJsonBody(resp.getBody().asString());
    }


    static public Map<String, Object> getMapFromJsonBody(String json) {
        Map<String, Object> map = null;

        try {
            map = new ObjectMapper().readValue(json, Map.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        };

        return map;
    }

    static public List<Map> getListOfMapFromJsonBody(String json) {
        List<Map> result = new ArrayList<>();

        try {
            JSONArray jsonArray = new JSONArray(json);
            for(int i=0; i< jsonArray.length(); i++) {
                result.add(getMapFromJsonBody(jsonArray.get(i).toString()));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        };

        return result;
    }

}
