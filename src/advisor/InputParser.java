package advisor;

import advisor.DataHandler;
import advisor.DataStub;

import javax.naming.AuthenticationException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class InputParser {

    public DataHandler dataStorage;

    public InputParser(DataHandler dataStorage) {
        this.dataStorage = dataStorage;
    }

    public Collection<?> processInput(String input) throws IllegalArgumentException, AuthenticationException, IOException, InterruptedException {
        switch (input) {
            case "featured":
                return dataStorage.getFeatured();
            case "new":
                return dataStorage.getNewReleases();
            case "categories":
                return dataStorage.getCategories();
            default:
                String[] inputParts = input.split("\\s+");
                if ("playlists".equals(inputParts[0])) {
                    String playlist = String.join(" ", Arrays.stream(inputParts).skip(1).collect(Collectors.toList()));
                    return dataStorage.getByCategory(playlist);
                } else {
                    throw new IllegalArgumentException();
                }
        }
    }

}
