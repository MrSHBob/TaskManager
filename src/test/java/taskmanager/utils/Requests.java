package taskmanager.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.json.JSONArray;
import org.json.JSONObject;
import taskmanager.model.CommentRequest;
import taskmanager.model.PaginatedTasksRequest;
import taskmanager.model.TaskRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class Requests {
    private String baseUrl = null;

    public Requests(String url) {
        this.baseUrl = url;
    }

    public Response register(
            String email,
            String passwd
    ) {
        JSONObject requestParams = new JSONObject();
        try {
            requestParams.put("username", email);
            requestParams.put("password", passwd);
        } catch (Exception e) {}

        return RestAssured.given()
                .contentType(ContentType.JSON)
                .body(requestParams.toString())
                .post(baseUrl + "/api/auth/registration");
    }

    public Response authenticate(
            String email,
            String passwd
    ) {
        JSONObject requestParams = new JSONObject();
        try {
            requestParams.put("username", email);
            requestParams.put("password", passwd);
        } catch (Exception e) {}

        return RestAssured.given()
                .contentType(ContentType.JSON)
                .body(requestParams.toString())
                .post(baseUrl + "/api/auth/authenticate");
    }

    public Response createTask(
            TaskRequest tr,
            String token
    ) {
        JSONObject requestParams = new JSONObject();
        try {
            requestParams.put("title", tr.getTitle());
            requestParams.put("description", tr.getDescription());
            requestParams.put("priority", tr.getPriority());
            requestParams.put("responsiblePerson", tr.getResponsiblePerson());
        } catch (Exception e) {}

        return RestAssured.given()
                .auth().oauth2(token)
                .contentType(ContentType.JSON)
                .body(requestParams.toString())
                .post(baseUrl + "/api/task/new");
    }

    public Response updateTask(
            TaskRequest tr,
            String token
    ) {
        JSONObject requestParams = new JSONObject();
        try {
            requestParams.put("id", tr.getId());
            requestParams.put("title", tr.getTitle());
            requestParams.put("description", tr.getDescription());
            requestParams.put("status", tr.getStatus());
            requestParams.put("priority", tr.getPriority());
            requestParams.put("responsiblePerson", tr.getResponsiblePerson());
            requestParams.put("author", tr.getAuthor());
        } catch (Exception e) {}

        return RestAssured.given()
                .auth().oauth2(token)
                .contentType(ContentType.JSON)
                .body(requestParams.toString())
                .post(baseUrl + "/api/task/update");
    }

    public Response deleteTask(
            TaskRequest tr,
            String token
    ) {
        JSONObject requestParams = new JSONObject();
        try {
            requestParams.put("id", tr.getId());
        } catch (Exception e) {}

        return RestAssured.given()
                .auth().oauth2(token)
                .contentType(ContentType.JSON)
                .body(requestParams.toString())
                .post(baseUrl + "/api/task/delete");
    }

    public Response getTasksPaginated(
            PaginatedTasksRequest ptr,
            String token
    ) {
        JSONObject requestParams = new JSONObject();
        try {
            requestParams.put("page", ptr.getPage());
            requestParams.put("size", ptr.getSize());
            requestParams.put("authorFilter", ptr.getAuthorFilter());
            requestParams.put("responsiblePersonFilter", ptr.getResponsiblePersonFilter());
        } catch (Exception e) {}

        return RestAssured.given()
                .auth().oauth2(token)
                .contentType(ContentType.JSON)
                .body(requestParams.toString())
                .post(baseUrl + "/api/task/getTasks");
    }

    public Response createComment(
            CommentRequest cr,
            String token
    ) {
        JSONObject requestParams = new JSONObject();
        try {
            requestParams.put("parentTaskId", cr.getParentTaskId());
            requestParams.put("text", cr.getText());
        } catch (Exception e) {}

        return RestAssured.given()
                .auth().oauth2(token)
                .contentType(ContentType.JSON)
                .body(requestParams.toString())
                .post(baseUrl + "/api/newComment");
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
