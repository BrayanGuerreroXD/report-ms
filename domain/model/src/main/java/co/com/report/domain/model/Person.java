package co.com.report.domain.model;
import lombok.*;

@Getter @Setter @AllArgsConstructor @Builder(toBuilder = true) @NoArgsConstructor
public class Person { private String name; private String email; }