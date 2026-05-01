package co.com.report.domain.model;
import lombok.*;
import java.util.List;

@Getter @Setter @AllArgsConstructor @Builder(toBuilder = true) @NoArgsConstructor
public class Capacity { private String name; private List<Technology> technologies; }