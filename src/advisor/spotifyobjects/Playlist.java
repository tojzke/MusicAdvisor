package advisor.spotifyobjects;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class Playlist {

    private String name;
    private String uri;

    @Override
    public String toString() {
        return name + "\n" + uri + "\n";
    }
}
