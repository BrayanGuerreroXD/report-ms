package co.com.report.infrastructure.entrypoints.reactiveweb;

public interface UserContext {
    reactor.core.publisher.Mono<LoggedUser> currentUser();
}