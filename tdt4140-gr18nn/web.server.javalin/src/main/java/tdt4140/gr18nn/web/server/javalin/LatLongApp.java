package tdt4140.gr18nn.web.server.javalin;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import io.javalin.Context;
import io.javalin.Javalin;
import io.javalin.json.JavalinJackson;
import java.util.Arrays;
import java.util.List;
import tdt4140.gr18nn.app.core.LatLong;
import tdt4140.gr18nn.app.json.LatLongDeserializer;
import tdt4140.gr18nn.app.json.LatLongSerializer;

// This class shows an example implementation
// using Javalin (https://javalin.io/documentation)
public class LatLongApp {

    private final List<LatLong> latLongs;
    public final Javalin javalin = Javalin.create().enableCaseSensitiveUrls();

    public LatLongApp(List<LatLong> latLongs) {

        this.latLongs = latLongs;

        javalin.post("/latLong", ctx -> {
            int latLongNum = latLongs.size();
            LatLong[] newLatLongs = ctx.bodyAsClass(LatLong[].class);
            latLongs.addAll(Arrays.asList(newLatLongs));
            ctx.json(latLongNum);
        });

        javalin.get("/latLong/:num", ctx -> {
            ctx.json(latLongs.get(num(ctx)));
        });

        javalin.put("/latLong/:num", ctx -> {
            LatLong newLatLong = ctx.bodyAsClass(LatLong.class);
            latLongs.set(num(ctx), newLatLong);
            ctx.json(newLatLong);
        });

        javalin.delete("/latLong/:num", ctx -> {
            LatLong removedLatLong = latLongs.remove(num(ctx));
            ctx.json(removedLatLong);
        });

        JavalinJackson.configure(new ObjectMapper().registerModule(
            new SimpleModule()
                .addSerializer(LatLong.class, new LatLongSerializer())
                .addDeserializer(LatLong.class, new LatLongDeserializer())
        ));

    }

    private int num(Context ctx) {
        int num = Integer.parseInt(ctx.pathParam(":num")); // parse number from url
        return num > 0 ? num : latLongs.size() + num; // count from end if negative
    }

}
