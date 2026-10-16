# Course Feedback Management System

A web application for managing course feedback with role-based access and a
controlled feedback lifecycle. The project is developed as part of a DevOps
course and will progressively add automated testing, CI/CD, containers, and
configuration management.

## Planned technology stack
- Java 21
- Spring Boot 3.x and Spring Security
- Maven
- Thymeleaf, HTML, CSS, and Bootstrap
- PostgreSQL with Spring Data JPA/Hibernate
- Git/GitHub, Jenkins, Selenium, Docker, and Puppet

## Planned capabilities
- Create, view, update, and search course/feedback records
- Role-based workflow: Draft -> Submitted -> Reviewed -> Closed
- Summary dashboard
- Automated build, test, packaging, deployment, and provisioning

## Local development (planned)
1. Install JDK 21 and Maven.
2. Configure PostgreSQL and application environment variables.
3. Run `mvn clean test`.
4. Start the application using `mvn spring-boot:run`.

## Contribution workflow
Create a branch using the naming policy, commit focused changes, open a pull
request, complete the review checklist, and merge only after required checks pass.

## Project status
Repository skeleton and documentation are planned first. Features and pipeline
capabilities will be added incrementally and documented as they are verified.
