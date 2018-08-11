package tdt4140.gr18nn.web.server.javalin;

import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        // create a LatLongApp and start its Javalin instance
        new LatLongApp(new ArrayList<>()).javalin.start(7070);
    }
}
