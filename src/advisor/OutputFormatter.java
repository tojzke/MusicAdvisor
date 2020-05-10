package advisor;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

public class OutputFormatter {

    private OutputFormatter() {}


    public static void printResponse(Collection<?> response) {
        response.forEach(System.out::println);
    }
}
