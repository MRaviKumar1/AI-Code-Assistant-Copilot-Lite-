package com.example.aicodeassistant.controller;

import com.example.aicodeassistant.service.CodeAnalysisService;
import com.example.aicodeassistant.model.AnalysisRequest;
import com.example.aicodeassistant.model.AnalysisResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class AnalysisController {

    private final CodeAnalysisService service;

    public AnalysisController(CodeAnalysisService service) {
        this.service = service;
    }

    @PostMapping("/analyze")
    public ResponseEntity<AnalysisResponse> analyze(@RequestBody AnalysisRequest request) {
        AnalysisResponse resp = service.analyzeCode(request.getFilename(), request.getCode());
        return ResponseEntity.ok(resp);
    }
}
