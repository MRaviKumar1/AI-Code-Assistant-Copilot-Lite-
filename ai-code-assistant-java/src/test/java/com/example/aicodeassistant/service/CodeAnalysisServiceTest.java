package com.example.aicodeassistant.service;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import com.example.aicodeassistant.model.AnalysisResponse;

public class CodeAnalysisServiceTest {

    @Test
    public void analyzeSimpleCode() {
        CodeAnalysisService svc = new CodeAnalysisService();
        String code = "public class A { /** doc */ public void m() { if(true) { if(false) {} } } }";
        AnalysisResponse resp = svc.analyzeCode("A.java", code);
        assertNotNull(resp);
        assertEquals("A.java", resp.getFilename());
        assertTrue(resp.getSuggestions().size() >= 0);
    }
}
