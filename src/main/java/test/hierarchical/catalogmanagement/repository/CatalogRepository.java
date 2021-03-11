package test.hierarchical.catalogmanagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import test.hierarchical.catalogmanagement.model.Catalog;

public interface CatalogRepository extends JpaRepository<Catalog, Long> {
}
