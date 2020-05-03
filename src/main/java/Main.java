import io.javalin.Javalin;

public class Main {
    public static void main(String[] args) {
        Javalin app = Javalin.create(config -> {
            config.addStaticFiles("/static");
        }).start(4567);

        endpoints.Home home_endpoint = new endpoints.Home();
        app.get("/", home_endpoint);
    }
}
