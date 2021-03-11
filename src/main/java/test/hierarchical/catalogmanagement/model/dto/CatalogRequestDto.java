package test.hierarchical.catalogmanagement.model.dto;

import lombok.Data;

@Data
public class CatalogRequestDto {
    private String name;
    private Long parentId;
}
