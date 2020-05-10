package advisor;

import advisor.spotifyobjects.Album;
import advisor.spotifyobjects.Category;
import advisor.spotifyobjects.Playlist;

import javax.naming.AuthenticationException;
import java.io.IOException;
import java.util.List;
import java.util.Set;

public interface DataHandler {

    void authorize() throws AuthenticationException;

    List<Playlist> getFeatured() throws AuthenticationException, IOException, InterruptedException;

    List<Album> getNewReleases() throws AuthenticationException, IOException, InterruptedException;

    Set<Category> getCategories() throws AuthenticationException, IOException, InterruptedException;

    List<Playlist> getByCategory(String category) throws AuthenticationException, IOException, InterruptedException;

}
