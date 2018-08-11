package tdt4140.gr18nn.web.server.javalin;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import io.javalin.Javalin;
import io.javalin.json.JavalinJson;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import tdt4140.gr18nn.app.core.LatLong;

public class LatLongAppTest {

    private Javalin javalin;
    private String endpoint;

    private List<LatLong> serverLatLongs = new ArrayList<>(Arrays.asList(
        new LatLong(1, -1),
        new LatLong(2, -2),
        new LatLong(3, -3)
    ));

    private LatLong testLatLong = new LatLong(4, -4);

    @Before
    public void setup() { // each test creates a new instance of the app and starts it on a random port
        javalin = new LatLongApp(serverLatLongs).javalin.start(0);
        endpoint = "http://localhost:" + javalin.port() + "/latLong";
    }

    @After
    public void tearDown() {
        javalin.stop();
    }

    @Test
    public void create_LatLong_returns_size_of_LatLong_list_before_adding() throws Exception {
        String postBody = JavalinJson.toJson(Collections.singletonList(testLatLong));
        String expectedSize = String.valueOf(serverLatLongs.size());
        HttpResponse<String> response = Unirest.post(endpoint).body(postBody).asString();
        Assert.assertEquals(expectedSize, response.getBody()); // server returns the correct index
        Assert.assertEquals(serverLatLongs.get(3), testLatLong); // the object is now stored on the server
    }

    @Test
    public void get_LatLong_by_index_returns_LatLong() throws Exception {
        String responseBody = Unirest.get(endpoint + "/" + 2).asString().getBody();
        LatLong retrievedLatLong = JavalinJson.fromJson(responseBody, LatLong.class);
        Assert.assertEquals(serverLatLongs.get(2), retrievedLatLong); // the retrieved LatLong matches the one on the server
    }

    @Test
    public void get_LatLong_by_negative_index_returns_LatLong() throws Exception {
        String responseBody = Unirest.get(endpoint + "/" + -1).asString().getBody();
        LatLong retrievedLatLong = JavalinJson.fromJson(responseBody, LatLong.class);
        Assert.assertEquals(serverLatLongs.get(serverLatLongs.size() - 1), retrievedLatLong); // negative index counts from the back
    }

    @Test
    public void replace_LatLong_by_index_replaces_old_LatLong_and_returns_new_LatLong() throws Exception {
        HttpResponse<String> response = Unirest.put(endpoint + "/" + 2).body(JavalinJson.toJson(testLatLong)).asString();
        LatLong newLatLong = JavalinJson.fromJson(response.getBody(), LatLong.class);
        Assert.assertEquals(newLatLong, testLatLong); // the server returns the newly created LatLong
        Assert.assertEquals(newLatLong, serverLatLongs.get(2)); // the new element at index 2 is the one we put there
    }

    @Test
    public void delete_LatLong_by_index_deletes_LatLong() throws Exception {
        LatLong oldLatLongAtIndex2 = serverLatLongs.get(2);
        HttpResponse<String> response = Unirest.delete(endpoint + "/" + 2).asString();
        LatLong deletedLatLong = JavalinJson.fromJson(response.getBody(), LatLong.class);
        Assert.assertEquals(deletedLatLong, oldLatLongAtIndex2); // the server returns the deleted LatLong
        Assert.assertFalse(serverLatLongs.contains(oldLatLongAtIndex2)); // the deleted LatLong is in fact gone from the server
    }

}
