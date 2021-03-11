package test.hierarchical.catalogmanagement.service;

import static java.lang.String.format;

import java.util.List;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import test.hierarchical.catalogmanagement.exception.CannotDeleteCatalogException;
import test.hierarchical.catalogmanagement.exception.CannotMoveCatalogException;
import test.hierarchical.catalogmanagement.exception.CatalogNotFoundException;
import test.hierarchical.catalogmanagement.model.Catalog;
import test.hierarchical.catalogmanagement.repository.CatalogRepository;

@Service
public class CatalogServiceImpl implements CatalogService {
    private final CatalogRepository catalogRepository;

    @Autowired
    public CatalogServiceImpl(CatalogRepository catalogRepository) {
        this.catalogRepository = catalogRepository;
    }

    @Override
    public void create(Catalog catalog) {
        Catalog parent = catalog.getParent();
        if (parent != null) {
            parent.getChildren().add(catalog);
            catalogRepository.save(parent);
        }
        catalogRepository.save(catalog);
    }

    @Override
    public void update(Catalog catalog) {
        catalogRepository.save(catalog);
    }

    @Override
    public Catalog getById(Long id) {
        return catalogRepository.findById(id).orElseThrow(() ->
                new CatalogNotFoundException(format("Catalog %s does not exist.", id)));
    }

    @Transactional
    @Override
    public Catalog getSubTreeByRootId(Long id) {
        Catalog catalog = catalogRepository.findById(id).orElseThrow(() ->
                new CatalogNotFoundException(format("Catalog %s does not exist.", id)));
        Hibernate.initialize(catalog.getChildren());
        return catalog;
    }

    @Transactional
    @Override
    public void moveSubTree(Long subTreeRootId, Long newParentId) {
        Catalog subTreeRoot = getSubTreeByRootId(subTreeRootId);
        Catalog oldParent = subTreeRoot.getParent();
        Catalog newParent = getSubTreeByRootId(newParentId);
        if (belongsToSubTree(subTreeRoot, newParent)) {
            throw new CannotMoveCatalogException(format(
                    "Catalog %s is located in the subtree of %s and cannot be moved into itself.",
                    newParentId, subTreeRootId));
        }
        oldParent.getChildren().remove(subTreeRoot);
        subTreeRoot.setParent(newParent);
        newParent.getChildren().add(subTreeRoot);
        catalogRepository.saveAll(List.of(oldParent, subTreeRoot, newParent));
    }

    @Override
    public List<Catalog> getAll() {
        return catalogRepository.findAll();
    }

    @Transactional
    @Override
    public void deleteById(Long id) {
        if (getSubTreeByRootId(id).getChildrenCount() == 0L) {
            catalogRepository.deleteById(id);
            return;
        }
        throw new CannotDeleteCatalogException(format(
                "Catalog %s has one or more descendants and cannot be deleted.", id));
    }

    private boolean belongsToSubTree(Catalog catalogToMove, Catalog iterationNode) {
        Catalog parent = iterationNode.getParent();
        if (parent == null) {
            return false;
        }
        if (parent.equals(catalogToMove)) {
            return true;
        }
        return belongsToSubTree(catalogToMove, parent);
    }
}
