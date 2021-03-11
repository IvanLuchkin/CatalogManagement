package test.hierarchical.catalogmanagement.controller;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import test.hierarchical.catalogmanagement.model.Catalog;
import test.hierarchical.catalogmanagement.model.dto.CatalogRequestDto;
import test.hierarchical.catalogmanagement.model.dto.CatalogResponseDto;
import test.hierarchical.catalogmanagement.model.mapper.CatalogMapper;
import test.hierarchical.catalogmanagement.repository.CatalogRepository;
import test.hierarchical.catalogmanagement.service.CatalogService;

@RestController
@RequestMapping("/catalogs")
public class CatalogController {
    private final CatalogService catalogService;
    private final CatalogMapper catalogMapper;
    private final CatalogRepository catalogRepository;

    @Autowired
    public CatalogController(CatalogService catalogService,
                             CatalogMapper catalogMapper,
                             CatalogRepository catalogRepository) {
        this.catalogService = catalogService;
        this.catalogMapper = catalogMapper;
        this.catalogRepository = catalogRepository;
    }

    @PostMapping
    public ResponseEntity<Void> createCatalog(@RequestBody CatalogRequestDto requestDto) {
        Catalog catalog = catalogMapper.toCatalog(requestDto);
        catalogService.create(catalog);
        return ResponseEntity.ok().body(null);
    }

    @GetMapping
    public ResponseEntity<List<CatalogResponseDto>> getAllCatalogs() {
        return ResponseEntity.ok().body(catalogService.getAll().stream()
                .map(catalogMapper::toDto)
                .collect(Collectors.toList()));
    }

    @GetMapping("/subtree/{id}")
    public ResponseEntity<CatalogResponseDto> cherryPickCatalogById(@PathVariable Long id) {
        return ResponseEntity.ok().body(catalogMapper.toDto(catalogService.getById(id)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CatalogResponseDto> getSubTreeByRootId(@PathVariable Long id) {
        return ResponseEntity.ok().body(catalogMapper.toDto(catalogService.getSubTreeByRootId(id)));
    }

    @PutMapping
    public ResponseEntity<Void> updateCatalog(@RequestBody Catalog catalog) {
        catalogService.update(catalog);
        return ResponseEntity.ok().body(null);
    }

    @PutMapping("/move")
    public ResponseEntity<Void> moveSubTree(@RequestParam Long childId,
                                            @RequestParam Long parentId) {
        catalogService.moveSubTree(childId, parentId);
        return ResponseEntity.ok().body(null);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCatalogById(@PathVariable Long id) {
        catalogService.deleteById(id);
        return ResponseEntity.ok().body(null);
    }
}
