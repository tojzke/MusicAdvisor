package advisor;

import advisor.spotifyobjects.Album;
import advisor.spotifyobjects.Category;
import advisor.spotifyobjects.Playlist;
import advisor.spotifyobjects.SpotifyResponseParser;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import jdk.net.SocketFlow;

import javax.naming.AuthenticationException;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Set;

public class RequestDataHandler implements DataHandler {


    private Authorizer authorizer;
    private SpotifyResponseParser parser;

    private final String BASE_REQUEST_URI;
    private String accessToken = "";

    private HttpClient httpClient = HttpClient.newHttpClient();
    private HttpResponse.BodyHandler<String> bodyHandler = HttpResponse.BodyHandlers.ofString();

    private static final  String CATEEGORIES_ENDPOINT = "/v1/browse/categories";
    private static final String NEW_RELEASES_ENDPOINT = "/v1/browse/new-releases";
    private static final String FEATURED_ENDPOINT = "/v1/browse/featured-playlists";
    private static final String PLAYLIST_BY_CATEGORY_FORMAT_ENDPOINT =
            "/v1/browse/categories/%s/playlists"; // %s stands for id placeholder for str format

    private final String CATEGORIES_URI;
    private final String NEW_RELEASES_URI;
    private final String FEATURED_URI;
    private final String PLAYLIST_BY_CATEGORY_FORMAT_URI;

    public RequestDataHandler(String requestUri, Authorizer authorizer, SpotifyResponseParser parser) {
        this.authorizer = authorizer;
        this.parser = parser;
        BASE_REQUEST_URI = requestUri;
        CATEGORIES_URI = BASE_REQUEST_URI + CATEEGORIES_ENDPOINT;
        NEW_RELEASES_URI = BASE_REQUEST_URI + NEW_RELEASES_ENDPOINT;
        FEATURED_URI = BASE_REQUEST_URI + FEATURED_ENDPOINT;
        PLAYLIST_BY_CATEGORY_FORMAT_URI = BASE_REQUEST_URI + PLAYLIST_BY_CATEGORY_FORMAT_ENDPOINT;
    }

    private boolean isAuthorized() {
        return !("".equals(accessToken));
    }


    @Override
    public void authorize() throws AuthenticationException {
        if (isAuthorized()) {
            System.out.println("You are already authorized!");
            return;
        }

        try {
            accessToken = authorizer.authorize();
            System.out.println("---SUCCESS---");
        } catch (Exception e) {
            throw new AuthenticationException();
        }
    }

    private JsonObject getJsonResponse(String from) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .header("Authorization", "Bearer " + accessToken)
                .uri(URI.create(from))
                .GET()
                .build();

        HttpResponse response = httpClient.send(request, bodyHandler);
        if (response.statusCode() == 200) {
            return JsonParser.parseString(response.body().toString()).getAsJsonObject();
        } else {
            String errorMsg = JsonParser.parseString(
                    response.body().toString()).getAsJsonObject().get("error").getAsJsonObject().get("message").getAsString();
            throw new IllegalStateException(errorMsg);
        }
    }

    @Override
    public List<Playlist> getFeatured() throws AuthenticationException, IOException, InterruptedException {
        if (isAuthorized()) {
            var featuredJsonResponse = getJsonResponse(FEATURED_URI);
            return parser.parsePlaylists(featuredJsonResponse);
        } else {
            throw new AuthenticationException();
        }
    }

    @Override
    public List<Album> getNewReleases() throws AuthenticationException, IOException, InterruptedException {
        if (isAuthorized()) {
            var albumsJsonResponse = getJsonResponse(NEW_RELEASES_URI);
            return parser.parseAlbums(albumsJsonResponse);
        } else {
            throw new AuthenticationException();
        }
    }

    @Override
    public Set<Category> getCategories() throws AuthenticationException, IOException, InterruptedException {
        if (isAuthorized()) {
            var categoriesJsonResponse = getJsonResponse(CATEGORIES_URI);
            return parser.parseCategories(categoriesJsonResponse);
        } else {
            throw new AuthenticationException();
        }
    }

    @Override
    public List<Playlist> getByCategory(String categoryName) throws AuthenticationException, IOException, InterruptedException {
        if (isAuthorized()) {
            var categoriesJsonResponse = getJsonResponse(CATEGORIES_URI);
            Set<Category> categories = parser.parseCategories(categoriesJsonResponse);
            var category = categories.stream()
                    .filter(c -> categoryName.equals(c.getName()))
                    .findFirst()
                    .orElseThrow(() -> new IllegalCallerException("Unknown category name."));

            var playlistsByCategoryURI = String.format(PLAYLIST_BY_CATEGORY_FORMAT_URI, category.getId());
            var playlistsResponse = getJsonResponse(playlistsByCategoryURI);
            return parser.parsePlaylists(playlistsResponse);
        } else {
            throw new AuthenticationException();
        }
    }
}
