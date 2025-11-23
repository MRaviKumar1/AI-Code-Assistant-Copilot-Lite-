package com.example.aicodeassistant.model;

import java.util.List;

public class AnalysisResponse {
    private String filename;
    private List<String> suggestions;
    public AnalysisResponse() {}
    public AnalysisResponse(String filename, List<String> suggestions) {
        this.filename = filename;
        this.suggestions = suggestions;
    }
    public String getFilename() { return filename; }
    public void setFilename(String filename) { this.filename = filename; }
    public List<String> getSuggestions() { return suggestions; }
    public void setSuggestions(List<String> suggestions) { this.suggestions = suggestions; }
}
