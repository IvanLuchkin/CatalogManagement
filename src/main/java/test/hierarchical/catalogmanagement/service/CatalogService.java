package test.hierarchical.catalogmanagement.service;

import java.util.List;
import test.hierarchical.catalogmanagement.model.Catalog;

public interface CatalogService {
    void create(Catalog catalog);

    void update(Catalog catalog);

    void moveSubTree(Long subTreeRootId, Long newParentId);

    List<Catalog> getAll();

    Catalog getById(Long id);

    Catalog getSubTreeByRootId(Long id);

    void deleteById(Long id);
}
