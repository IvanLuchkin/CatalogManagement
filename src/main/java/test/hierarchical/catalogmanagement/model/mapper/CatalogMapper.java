package test.hierarchical.catalogmanagement.model.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import test.hierarchical.catalogmanagement.model.Catalog;
import test.hierarchical.catalogmanagement.model.dto.CatalogRequestDto;
import test.hierarchical.catalogmanagement.model.dto.CatalogResponseDto;
import test.hierarchical.catalogmanagement.service.CatalogService;

@Component
public class CatalogMapper {
    private final CatalogService catalogService;

    @Autowired
    public CatalogMapper(CatalogService catalogService) {
        this.catalogService = catalogService;
    }

    public CatalogResponseDto toDto(Catalog catalog) {
        return CatalogResponseDto.builder()
                .id(catalog.getId())
                .name(catalog.getName())
                .childrenCount(catalog.getChildrenCount())
                .children(catalog.getChildren())
                .build();
    }

    public Catalog toCatalog(CatalogRequestDto dto) {
        Catalog catalog = new Catalog();
        catalog.setName(dto.getName());
        if (dto.getParentId() != null) {
            catalog.setParent(catalogService.getSubTreeByRootId(dto.getParentId()));
        }
        return catalog;
    }
}
