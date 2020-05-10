package advisor.spotifyobjects;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import javax.print.attribute.standard.MediaSize;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SpotifyResponseParser {

    // Spotify json object constants
    private static final String ALBUMS = "albums";
    private static final String CATEGORIES = "categories";
    private static final String PLAYLISTS = "playlists";

    private static final String ITEMS = "items";
    private static final String NAME = "name";
    private static final String ID = "id";
    private static final String URL = "external_urls";
    private static final String URL_TYPE = "spotify";
    private static final String ARTISTS = "artists";
    private static final String HREF = "href";


    public List<Album> parseAlbums(JsonObject albumsJsonResponse) {
        List<Album> albums = new ArrayList<>();

        var albumArray = albumsJsonResponse.get(ALBUMS).getAsJsonObject().get(ITEMS).getAsJsonArray();
        var albumIterator = albumArray.iterator();

        while (albumIterator.hasNext()) {
            var albumJson = albumIterator.next().getAsJsonObject();
            String name = albumJson.get(NAME).getAsString();
            String uri = albumJson.get(URL).getAsJsonObject().get(URL_TYPE).getAsString();
            List<String> artists = parseArtists(albumJson.get(ARTISTS).getAsJsonArray());
            albums.add(new Album(name, artists, uri));
        }

        return albums;
    }

    private List<String> parseArtists(JsonArray artists) {
        List<String> artistsNames = new ArrayList<>();

        var artistsIt = artists.iterator();
        while (artistsIt.hasNext()) {
            var artist = artistsIt.next().getAsJsonObject();
            artistsNames.add(artist.get(NAME).getAsString());
        }

        return artistsNames;
    }


    public Set<Category> parseCategories(JsonObject categoriesJsonResponse) {
        Set<Category> categories = new HashSet<>();

        var categoriesArray = categoriesJsonResponse.get(CATEGORIES).getAsJsonObject().get(ITEMS).getAsJsonArray();
        var categoryIterator = categoriesArray.iterator();

        while (categoryIterator.hasNext()) {
            var categoryJson = categoryIterator.next().getAsJsonObject();
            String name = categoryJson.get(NAME).getAsString();
            String id = categoryJson.get(ID).getAsString();
            String href = categoryJson.get(HREF).getAsString();
            categories.add(new Category(id, name, href));
        }

        return categories;
    }

    public List<Playlist> parsePlaylists(JsonObject featuredJsonResponse) {
        List<Playlist> featured = new ArrayList<>();

        var featuredPlaylistsArray = featuredJsonResponse.get(PLAYLISTS).getAsJsonObject().get(ITEMS).getAsJsonArray();
        var featuredPlaylistsIterator = featuredPlaylistsArray.iterator();

        while (featuredPlaylistsIterator.hasNext()) {
            var playlistsJson = featuredPlaylistsIterator.next().getAsJsonObject();
            String name = playlistsJson.get(NAME).getAsString();
            String uri = playlistsJson.get(URL).getAsJsonObject().get(URL_TYPE).getAsString();
            featured.add(new Playlist(name, uri));
        }

        return featured;
    }
}
