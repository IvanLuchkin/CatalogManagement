package test.hierarchical.catalogmanagement.model.dto;

import java.util.Set;
import lombok.Builder;
import lombok.Data;
import test.hierarchical.catalogmanagement.model.Catalog;

@Data
@Builder
public class CatalogResponseDto {
    private Long id;
    private String name;
    private Long childrenCount;
    private Set<Catalog> children;
}
