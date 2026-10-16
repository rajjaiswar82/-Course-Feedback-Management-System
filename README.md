# Course Feedback Management System — Task 5

First vertical slice: students can submit course feedback and view submitted records.

## Stack
- Java 21
- Spring Boot 3.5.x, Spring MVC, Thymeleaf, Bean Validation
- Spring Data JPA / Hibernate
- H2 in-memory database for quick local demonstration
- PostgreSQL profile for integration with the planned project database
- Maven

## Run locally
1. Install JDK 21 and Maven.
2. From this directory run `mvn clean test`.
3. Start the application with `mvn spring-boot:run`.
4. Open `http://localhost:8080`.

The default profile uses H2 in-memory storage; data resets when the application restarts. To use PostgreSQL, set `SPRING_PROFILES_ACTIVE=postgres` and configure `DB_URL`, `DB_USERNAME`, and `DB_PASSWORD` environment variables. Do not commit credentials.

## Feature 1
- Submit a feedback record with student name, course name, rating (1–5), and comments.
- Server-side validation prevents blank required fields and ratings outside 1–5.
- Server sets the status to `SUBMITTED`; the form cannot choose an arbitrary status.
- List submitted feedback records.

## Suggested branch workflow
```bash
git switch dev
git pull origin dev
git switch -c feature/feedback-workflow
mvn clean test
git add .
git commit -m "feat: add feedback submission workflow"
git log --oneline -5
git push -u origin feature/feedback-workflow
```
Then open a GitHub pull request with base `dev` and compare `feature/feedback-workflow`. Review the diff, address comments, and merge only after checks pass.

## Limitations
This is an initial feature slice for a course project, not a production-ready system. Authentication, authorization, duplicate-submission rules, audit logging, CSRF/security hardening review, and admin review/close transitions are future work. H2 data is temporary unless PostgreSQL is configured.
