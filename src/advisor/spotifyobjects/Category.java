package advisor.spotifyobjects;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class Category {

    private String id;
    private String name;
    private String href;

    @Override
    public String toString() {
        return name;
    }
}
