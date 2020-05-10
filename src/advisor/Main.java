package advisor;

import advisor.spotifyobjects.Album;
import advisor.spotifyobjects.SpotifyResponseParser;

import javax.naming.AuthenticationException;
import java.io.IOException;
import java.util.Collection;
import java.util.Scanner;

public class Main {


    private static String ACCESS_URI = "https://accounts.spotify.com";
    private static String RESOURCE_URI = "https://api.spotify.com";

    private static final String USAGE = "Usage:\n" +
            "\tauth (then enter auth URI) -- to authenticate yourself\n" +
            "\tnew -- to get very new music\n" +
            "\tfeatured -- to get your featured music\n" +
            "\tcategories -- to list categories\n" +
            "\tplaylists <category name> -- to list playlists by category\n";


    public static void main(String[] args) {

        if (args.length != 0) {
            for (int i = 0; i < args.length; ++i) {
                if ("-access".equals(args[i])) {
                    ++i;
                    ACCESS_URI = args[i];
                } else if ("-resource".equals(args[i])) {
                    ++i;
                    RESOURCE_URI = args[i];
                }
            }
        }

        Scanner scanner = new Scanner(System.in);
        DataHandler dataHandler = new RequestDataHandler(RESOURCE_URI, new Authorizer(ACCESS_URI), new SpotifyResponseParser());
//        DataHandler dataHandler = new DataStub(new Authorizer(ACCESS_URI));
        InputParser parser = new InputParser(dataHandler);

        boolean isRunning = true;
        String input;
        while (isRunning) {
            input = scanner.nextLine();
            if ("exit".equals(input)) {
                isRunning = false;
                System.out.println("---GOODBYE!---");
                return;
            } else if ("auth".equals(input)) {
                try {
                    dataHandler.authorize();
                } catch (AuthenticationException e) {
                    System.out.println("Can't authorize.");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    Collection<?> response = parser.processInput(input);
                    OutputFormatter.printResponse(response);
                } catch (IOException | InterruptedException e) {
                    System.out.println("Can't make request to Spotify Web API!");
                } catch (IllegalArgumentException e) {
                    System.out.printf("This request %s is not supported.\n", input);
                    System.out.println(USAGE);
                } catch (AuthenticationException e) {
                    System.out.println("Please, provide access for application.");
                } catch (IllegalStateException | IllegalCallerException e) {
                    System.out.println(e.getMessage());
                } catch (Exception e) {
                    System.out.println("Something is wrong!");
                    e.printStackTrace();
                }
            }
        }

    }
}
