package advisor;

import advisor.spotifyobjects.Album;
import advisor.spotifyobjects.Category;
import advisor.spotifyobjects.Playlist;

import javax.naming.AuthenticationException;
import java.util.*;

public class DataStub implements DataHandler {

    private Set<String> accessTokens = new HashSet<>();
    private Authorizer authorizer;


    public DataStub(Authorizer authorizer) {
        this.authorizer = authorizer;
    }

    private List<String> newReleases =
           new ArrayList<>(Arrays.asList(
                   "Mountains [Sia, Diplo, Labirinth]",
                   "Runaway [Lil Peep]",
                   "The Greatest Show [Panic! At The Disco]",
                   "All Out Life [Slipknot]"));

    private List<String> featured =
           new ArrayList<>(Arrays.asList(
                   "Mellow Morning",
                   "Wake Up and Smell the Coffee",
                   "Monday Motivation"
           ));

    private List<String> topLists =
            new ArrayList<>(Arrays.asList(
                    "Wake up and work hard",
                    "Train!",
                    "Chill"
            ));

    private List<String> popLists =
            new ArrayList<>(Arrays.asList(
                    "Drake",
                    "Kanye West",
                    "The weeknd"
            ));

    private List<String> moodLists =
            new ArrayList<>(Arrays.asList(
                    "Angry",
                    "Plesure",
                    "Sex"
            ));


    private Map<String, List<String>> categories =
           Map.of(
                   "Top Lists", topLists,
                   "Pop", popLists,
                   "Mood", moodLists
           );


    @Override
    public void authorize() throws AuthenticationException {
        try {
           accessTokens.add(authorizer.authorize());
           System.out.println("---SUCCESS---");
       } catch (Exception e) {
           throw new AuthenticationException();
       }

    }


    @Override
    public List<Playlist> getFeatured() throws AuthenticationException {
        if (isAuthorized()) {
            throw  new UnsupportedOperationException();
        } else {
            throw new AuthenticationException();
        }
    }

    private boolean isAuthorized() {
        return accessTokens.size() != 0;
    }

    @Override
    public List<Album> getNewReleases() throws AuthenticationException {
        if (isAuthorized()) {
            return null;
        } else {
            throw new AuthenticationException();
        }
    }

    @Override
    public Set<Category> getCategories() throws AuthenticationException {
        if (isAuthorized()) {
            return null;
        } else {
            throw new AuthenticationException();
        }
    }

    @Override
    public List<Playlist> getByCategory(String category) throws AuthenticationException {
        if (isAuthorized()) {
            return  null;
        } else {
            throw new AuthenticationException();
        }
    }
}

