package advisor;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.ref.SoftReference;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URL;
import java.net.URLDecoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Authorizer {


    public static final String OK_MSG = "Got the code. Return back to your program.";
    public static final String ERR_MSG = "Not found authorization code. Try again.";

    private static final int LOCAL_PORT = 8080;

    private static final String GRANT_TYPE_VALUE = "authorization_code";
    private static final String GRANT_TYPE_KEY = "grant_type";
    private static final String CODE_KEY = "code";
    private static final String REDIRECT_KEY = "redirect_uri";
    private static final String CLIENT_ID_KEY = "client_id";
    private static final String CLIENT_ID = "5bc32bf6899c4dfc9c48250111c642be";
    private static final String CLIENT_SECRET_KEY = "client_secret";
    private static final String CLIENT_SECRET = "590f170fd32246a69518e63ceacfb530";
    private static final String ACCESS_TOKEN = "access_token";

    private String accessUri;
    private String accessCode = "";
    private String accessToken;
    private String tokenAccessUri;
    private String authLink;

    private static final String REDIRECT_URI = "http://localhost:8080";

    public Authorizer(String accessUri) {
        this.accessUri = accessUri;
        this.tokenAccessUri = this.accessUri + "/api/token";
        this.authLink = this.accessUri + "/authorize?client_id="+ CLIENT_ID + "&" +
                "redirect_uri="+ REDIRECT_URI + "&response_type=code";
    }

    private void getAccessToken() throws IOException, InterruptedException {
        System.out.println("making http request for access_token...");

        HttpClient client = HttpClient.newBuilder().build();
        StringBuilder postBody = new StringBuilder();
        postBody.append(GRANT_TYPE_KEY + "=" + GRANT_TYPE_VALUE);
        postBody.append("&" + CODE_KEY + "=" + accessCode);
        postBody.append("&" + REDIRECT_KEY + "=" + REDIRECT_URI);

        String secret = CLIENT_ID + ":" + CLIENT_SECRET;
        String encodedSecret = Base64.getEncoder().encodeToString(secret.getBytes());
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(tokenAccessUri))
                .headers("Content-Type", "application/x-www-form-urlencoded",
                        "Authorization", "Basic " + encodedSecret)
                .POST(HttpRequest.BodyPublishers.ofString(postBody.toString()))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println("response:");
        System.out.println(response.body());
        JsonObject jo = JsonParser.parseString(response.body()).getAsJsonObject();
        accessToken = jo.get(ACCESS_TOKEN).getAsString();
        System.out.println(accessToken);
    }

    public static Map<String, String> splitQuery(String query) throws UnsupportedEncodingException {
        Map<String, String> query_pairs = new LinkedHashMap<String, String>();
        String[] pairs = query.split("&");
        for (String pair : pairs) {
            int idx = pair.indexOf("=");
            query_pairs.put(URLDecoder.decode(pair.substring(0, idx), "UTF-8"), URLDecoder.decode(pair.substring(idx + 1), "UTF-8"));
        }
        return query_pairs;
    }


    private void getAccessCode() throws Exception {

        HttpServer server = HttpServer.create();
        server.bind(new InetSocketAddress(LOCAL_PORT), 0);
        server.createContext("/",
                new HttpHandler() {
                    @Override
                    public void handle(HttpExchange exchange) throws IOException {
                        String message = "";
                        if (exchange.getRequestURI().getQuery() != null) {
                            Map<String, String> queryParams = splitQuery(exchange.getRequestURI().getQuery());
                            if (queryParams.containsKey("code")) {
                                message = OK_MSG;
                                accessCode = queryParams.get("code");
                            } else {
                                message = ERR_MSG;
                            }
                        } else {
                            message = ERR_MSG;
                        }
                        exchange.sendResponseHeaders(200, message.length());
                        exchange.getResponseBody().write(message.getBytes());
                        exchange.getResponseBody().close();
                    }
                });
        server.start();
        System.out.println("use this link to request the access code:\n" + authLink);
        System.out.println("waiting for code...");
        while ("".equals(accessCode)) {
            Thread.sleep(10);
        }
        System.out.println("code received");
        server.stop(1);
    }

    public String authorize() throws Exception {
        getAccessCode();
        getAccessToken();
        return accessToken;
    }

}
