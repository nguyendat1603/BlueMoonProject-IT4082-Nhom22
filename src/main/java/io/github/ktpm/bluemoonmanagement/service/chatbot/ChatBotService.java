package io.github.ktpm.bluemoonmanagement.service.chatbot;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import io.github.ktpm.bluemoonmanagement.model.chatbot.Intent;
import java.io.File;
import java.util.*;

public class ChatBotService {
    private List<Intent> intents = new ArrayList<>();
    private List<Sentence> sentences = new ArrayList<>();
    private Set<String> vocabulary = new HashSet<>();
    private final int K = 3;
    private final Random random = new Random();

    public void loadIntents(String filePath) {
        try {
            // Create ObjectMapper instance from Jackson
            ObjectMapper objectMapper = new ObjectMapper();

            // Read the JSON file and deserialize it into a Map
            Map<String, List<Intent>> map = objectMapper.readValue(new File(filePath),
                    new TypeReference<Map<String, List<Intent>>>() {});

            intents = map.get("intents");

            // Process each intent and its patterns
            for (Intent intent : intents) {
                for (String pattern : intent.getPatterns()) {
                    sentences.add(new Sentence(pattern.toLowerCase(), intent.getTag()));
                    vocabulary.addAll(Arrays.asList(pattern.toLowerCase().split("\\s+")));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getResponse(String input) {
        String tag = predictTag(input);
        for (Intent intent : intents) {
            if (intent.getTag().equals(tag)) {
                List<String> responses = intent.getResponses();
                return responses.get(random.nextInt(responses.size()));
            }
        }
        return "Xin lỗi, tôi chưa hiểu ý bạn.";
    }

    private String predictTag(String input) {
        input = input.toLowerCase();
        double[] inputVector = toVector(input);

        List<Neighbor> neighbors = new ArrayList<>();
        for (Sentence sentence : sentences) {
            double[] vector = toVector(sentence.text);
            double similarity = cosineSimilarity(inputVector, vector);
            neighbors.add(new Neighbor(sentence.tag, similarity));
        }

        neighbors.sort((a, b) -> Double.compare(b.similarity, a.similarity));

        Map<String, Integer> voteCounts = new HashMap<>();
        for (int i = 0; i < Math.min(K, neighbors.size()); i++) {
            Neighbor neighbor = neighbors.get(i);
            voteCounts.put(neighbor.tag, voteCounts.getOrDefault(neighbor.tag, 0) + 1);
        }

        return voteCounts.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("unknown");
    }

    private double[] toVector(String sentence) {
        double[] vector = new double[vocabulary.size()];
        String[] tokens = sentence.split("\\s+");
        List<String> vocabList = new ArrayList<>(vocabulary);

        for (String token : tokens) {
            int index = vocabList.indexOf(token);
            if (index != -1) {
                vector[index] += 1.0;
            }
        }
        return vector;
    }

    private double cosineSimilarity(double[] v1, double[] v2) {
        double dot = 0.0, normV1 = 0.0, normV2 = 0.0;
        for (int i = 0; i < v1.length; i++) {
            dot += v1[i] * v2[i];
            normV1 += v1[i] * v1[i];
            normV2 += v2[i] * v2[i];
        }
        if (normV1 == 0 || normV2 == 0) return 0.0;
        return dot / (Math.sqrt(normV1) * Math.sqrt(normV2));
    }

    private static class Sentence {
        String text;
        String tag;
        Sentence(String text, String tag) {
            this.text = text;
            this.tag = tag;
        }
    }

    private static class Neighbor {
        String tag;
        double similarity;
        Neighbor(String tag, double similarity) {
            this.tag = tag;
            this.similarity = similarity;
        }
    }
}
