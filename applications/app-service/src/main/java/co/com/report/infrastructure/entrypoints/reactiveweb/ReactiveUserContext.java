package co.com.report.infrastructure.entrypoints.reactiveweb;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class ReactiveUserContext implements UserContext {

    @Override
    public Mono<LoggedUser> currentUser() {
        return ReactiveSecurityContextHolder.getContext()
            .map(ctx -> {
                Authentication auth = ctx.getAuthentication();
                if (auth != null && auth.getPrincipal() instanceof LoggedUser) {
                    return (LoggedUser) auth.getPrincipal();
                }
                throw new IllegalStateException("No authenticated user");
            });
    }
}