package advisor.spotifyobjects;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.StringJoiner;

@AllArgsConstructor
@Getter
public class Album {

    private String name;
    private List<String> authors;
    private String link;

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append(name + "\n");
        StringJoiner authorsJoiner = new StringJoiner(", ", "[", "]");
        authors.stream().forEach(authorsJoiner::add);
        sb.append(authorsJoiner.toString() + "\n");
        sb.append(link + "\n");

        return sb.toString();
    }
}
