package com.example.aicodeassistant.service;

import com.example.aicodeassistant.model.AnalysisResponse;
import com.github.javaparser.*;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.comments.Comment;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import org.springframework.stereotype.Service;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class CodeAnalysisService {

    public AnalysisResponse analyzeCode(String filename, String code) {
        List<String> suggestions = new ArrayList<>();
        try {
            ParserConfiguration config = new ParserConfiguration();
            JavaParser parser = new JavaParser(config);
            ParseResult<CompilationUnit> result = parser.parse(ParseStart.COMPILATION_UNIT, Providers.provider(new StringReader(code)));
            if (result.isSuccessful() && result.getResult().isPresent()) {
                CompilationUnit cu = result.getResult().get();
                // Detect long methods
                cu.findAll(MethodDeclaration.class).forEach(m -> {
                    int begin = m.getBegin().map(p -> p.line).orElse(0);
                    int end = m.getEnd().map(p -> p.line).orElse(begin);
                    int lines = Math.max(0, end - begin + 1);
                    if (lines > 50) {
                        suggestions.add(String.format("Method '%s' is too long (%d lines). Consider refactoring into smaller methods.", m.getName(), lines));
                    }
                    // Check for missing javadoc
                    if (!m.getJavadoc().isPresent()) {
                        suggestions.add(String.format("Method '%s' has no Javadoc; add brief description and param info.", m.getName()));
                    }
                    // Check nested if depth
                    AtomicInteger maxDepth = new AtomicInteger(0);
                    m.accept(new VoidVisitorAdapter<Void>() {
                        @Override
                        public void visit(IfStmt n, Void arg) {
                            int depth = computeIfDepth(n);
                            if (depth > maxDepth.get()) maxDepth.set(depth);
                            super.visit(n, arg);
                        }
                        private int computeIfDepth(IfStmt stmt) {
                            int d = 1;
                            if (stmt.getElseStmt().isPresent() && stmt.getElseStmt().get() instanceof IfStmt) {
                                d += computeIfDepth((IfStmt) stmt.getElseStmt().get());
                            }
                            return d;
                        }
                    }, null);
                    if (maxDepth.get() >= 3) {
                        suggestions.add(String.format("Method '%s' has nested if-statements depth %d; consider simplifying logic.", m.getName(), maxDepth.get()));
                    }
                });
                // Find TODO comments and general comments
                cu.getAllContainedComments().forEach(c -> {
                    String content = c.getContent();
                    if (content.toLowerCase().contains("todo")) {
                        suggestions.add("Found TODO comment: address before merging to main.");
                    }
                });
                // Check for many imports (heuristic for unused imports)
                int importCount = cu.getImports().size();
                if (importCount > 10) {
                    suggestions.add(String.format("File has %d imports; verify if all are necessary.", importCount));
                }
                // Simple style/smell checks
                cu.findAll(MethodDeclaration.class).stream().filter(m -> m.getNameAsString().toLowerCase().contains("test")).forEach(m -> {
                    suggestions.add(String.format("Method '%s' looks like a test method - ensure it is in test sources.", m.getName()));
                });
            } else {
                suggestions.add("Parsing failed: ensure code is valid Java source.");
            }
        } catch (Exception e) {
            suggestions.add("Analysis error: " + e.getMessage());
        }
        return new AnalysisResponse(filename, suggestions);
    }
}
