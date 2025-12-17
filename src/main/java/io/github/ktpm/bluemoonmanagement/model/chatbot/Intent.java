package io.github.ktpm.bluemoonmanagement.model.chatbot;

import java.util.List;

public class Intent {
    private String tag;
    private List<String> patterns;
    private List<String> responses;

    // Public getters and setters
    public String getTag() { return tag; }
    public void setTag(String tag) { this.tag = tag; }

    public List<String> getPatterns() { return patterns; }
    public void setPatterns(List<String> patterns) { this.patterns = patterns; }

    public List<String> getResponses() { return responses; }
    public void setResponses(List<String> responses) { this.responses = responses; }
}
