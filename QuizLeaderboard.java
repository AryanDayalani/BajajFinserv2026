import java.net.URI;
import java.net.http.*;
import java.util.*;
import java.util.regex.*;

public class QuizLeaderboard {

    static final String BASE_URL = "https://devapigw.vidalhealthtpa.com/srm-quiz-task";
    static final String REG_NO = "2024CS101"; // <-- change this

    public static void main(String[] args) throws Exception {

        HttpClient client = HttpClient.newHttpClient();

        Set<String> seen = new HashSet<>();
        Map<String, Integer> scores = new HashMap<>();

        for (int i = 0; i < 10; i++) {

            System.out.println("Polling: " + i);

            String url = BASE_URL + "/quiz/messages?regNo=" + REG_NO + "&poll=" + i;

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();

            HttpResponse<String> response =
                    client.send(request, HttpResponse.BodyHandlers.ofString());

            String body = response.body();

            // Regex to extract each event block
            Pattern eventPattern = Pattern.compile(
                    "\\{\\s*\"roundId\"\\s*:\\s*\"(.*?)\"\\s*,\\s*\"participant\"\\s*:\\s*\"(.*?)\"\\s*,\\s*\"score\"\\s*:\\s*(\\d+)\\s*\\}"
            );

            Matcher matcher = eventPattern.matcher(body);

            while (matcher.find()) {
                String roundId = matcher.group(1);
                String participant = matcher.group(2);
                int score = Integer.parseInt(matcher.group(3));

                String key = roundId + "_" + participant;

                if (!seen.contains(key)) {
                    seen.add(key);

                    scores.put(participant,
                            scores.getOrDefault(participant, 0) + score);

                    System.out.println("Added: " + key + " -> " + score);
                } else {
                    System.out.println("Duplicate ignored: " + key);
                }
            }

            Thread.sleep(5000); // mandatory delay
        }

        // Sort leaderboard
        List<Map.Entry<String, Integer>> leaderboard =
                new ArrayList<>(scores.entrySet());

        leaderboard.sort((a, b) -> b.getValue() - a.getValue());

        // Print leaderboard
        System.out.println("\nFinal Leaderboard:");
        for (var entry : leaderboard) {
            System.out.println(entry.getKey() + " -> " + entry.getValue());
        }

        // Build JSON manually
        StringBuilder leaderboardJson = new StringBuilder("[");
        for (int i = 0; i < leaderboard.size(); i++) {
            var entry = leaderboard.get(i);

            leaderboardJson.append("{\"participant\":\"")
                    .append(entry.getKey())
                    .append("\",\"totalScore\":")
                    .append(entry.getValue())
                    .append("}");

            if (i != leaderboard.size() - 1) {
                leaderboardJson.append(",");
            }
        }
        leaderboardJson.append("]");

        String finalJson = "{ \"regNo\": \"" + REG_NO + "\", \"leaderboard\": " + leaderboardJson + "}";

        // POST request
        HttpRequest postRequest = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/quiz/submit"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(finalJson))
                .build();

        HttpResponse<String> postResponse =
                client.send(postRequest, HttpResponse.BodyHandlers.ofString());

        System.out.println("\nSubmission Response:");
        System.out.println(postResponse.body());
    }
}
