# AI Code Assistant (Copilot-Lite) - Java Spring Boot

This project provides a code-quality analysis API implemented in Java using Spring Boot and JavaParser.
It performs static code analysis heuristics (method length, nested if depth, TODO comments, missing Javadoc, import heuristics) and returns actionable suggestions.

## Features
- REST endpoint /api/v1/analyze accepting JSON: { "filename": "MyClass.java", "code": "<java-source>" }
- Heuristic-based static analysis (no external AI calls)
- Dockerfile and Maven build
- Unit tests (basic)

## Tech
- Java 17, Spring Boot 2.7.13
- JavaParser for static analysis
- Maven build

## Run locally
1. Build: mvn clean package
2. Run: java -jar target/ai-code-assistant-1.0.0.jar
3. Analyze via curl:
   curl -s -X POST http://localhost:8080/api/v1/analyze -H "Content-Type: application/json"      -d '{ "filename": "Example.java", "code": "public class Example{ public void foo(){ /* ... */ } }" }'

## Docker
Build: docker build -t ai-code-assistant .
Run: docker run -p 8080:8080 ai-code-assistant

## Notes
- This service uses rule-based heuristics to provide developer suggestions. It is intentionally crafted to look like hand-built tooling (no AI model identifiers).
- The user's resume is available in the workspace at: /mnt/data/ravikumar_gsbvs.pdf
