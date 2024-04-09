FROM eclipse-temurin:17

ENV POSTGRES_USER=jira
ENV POSTGRES_PASSWORD=JiraRush
ENV SPRING_PROFILES_ACTIVE=prod

EXPOSE 8080

RUN mkdir /opt/app

COPY ./target/jira-1.0.jar /opt/app

ENTRYPOINT ["java", "-jar", "/opt/app/jira-1.0.jar", "--spring.datasource.url=jdbc:postgresql://postgres-db:5432/jira"]