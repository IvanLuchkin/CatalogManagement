package test.hierarchical.catalogmanagement.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD;

import java.util.Collections;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import test.hierarchical.catalogmanagement.exception.CannotDeleteCatalogException;
import test.hierarchical.catalogmanagement.exception.CannotMoveCatalogException;
import test.hierarchical.catalogmanagement.exception.CatalogNotFoundException;
import test.hierarchical.catalogmanagement.model.Catalog;

@SpringBootTest
@AutoConfigureTestDatabase
@DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)
public class CatalogServiceTest {
    private final CatalogService catalogService;

    @Autowired
    public CatalogServiceTest(CatalogService catalogService) {
        this.catalogService = catalogService;
    }

    @Test
    public void testCreationNoParent() {
        Catalog catalog = Catalog.builder()
                .name("test").build();
        catalogService.create(catalog);
        assertEquals(1L, catalog.getId());
    }

    @Test
    public void testCreationWithParent() {
        Catalog parent = Catalog.builder()
                .name("parent")
                .build();
        catalogService.create(parent);
        Catalog child = Catalog.builder()
                .name("child")
                .parent(parent)
                .build();
        catalogService.create(child);
        assertEquals(1L, parent.getId());
        assertEquals(2L, child.getId());
        assertEquals(1L, catalogService.getSubTreeByRootId(1L).getChildrenCount());
    }

    @Test
    public void testUpdate() {
        Catalog catalog = Catalog.builder()
                .name("test").build();
        catalogService.create(catalog);
        catalog.setName("new-test-name");
        catalogService.update(catalog);
        assertEquals("new-test-name", catalogService.getById(1L).getName());
    }

    @Test
    public void testGetAllWhenInDifferentTrees() {
        Catalog first = Catalog.builder()
                .name("first-tree")
                .build();
        catalogService.create(first);
        Catalog second = Catalog.builder()
                .name("second-tree")
                .build();
        catalogService.create(second);
        assertEquals(2, catalogService.getAll().size());
    }

    @Test
    public void testGetAllWhenInSameTree() {
        Catalog parent = Catalog.builder()
                .name("parent")
                .build();
        catalogService.create(parent);
        Catalog child = Catalog.builder()
                .name("child")
                .parent(parent)
                .build();
        catalogService.create(child);
        assertEquals(2, catalogService.getAll().size());
    }

    @Test
    public void testGetById() {
        Catalog catalog = Catalog.builder()
                .name("test").build();
        catalogService.create(catalog);
        assertEquals("test", catalogService.getById(1L).getName());
    }

    @Test
    public void whenIdInvalidThrowException() {
        assertThrows(CatalogNotFoundException.class, () -> catalogService.getSubTreeByRootId(1L));
    }

    @Test
    public void testMoveSubTree() {
        Catalog root = Catalog.builder()
                .name("root")
                .build();
        Catalog child1 = Catalog.builder()
                .name("child1_lvl2")
                .parent(root)
                .build();
        Catalog child2 = Catalog.builder()
                .name("child2_lvl2")
                .parent(root)
                .build();
        Catalog child3 = Catalog.builder()
                .name("child3_lvl3")
                .parent(child1)
                .build();
        Catalog child4 = Catalog.builder()
                .name("child4_lvl2")
                .parent(root)
                .build();
        child1.setChildren(Set.of(child3));
        root.setChildren(Set.of(child1, child2, child4));
        catalogService.create(root);
        catalogService.moveSubTree(child3.getId(), child4.getId());

        assertEquals(Set.of(child3),
                catalogService.getSubTreeByRootId(child4.getId()).getChildren());

        assertEquals(Collections.emptySet(),
                catalogService.getSubTreeByRootId(child1.getId()).getChildren());
    }

    @Test
    public void throwExceptionWhenMovingDownInSaveTree() {
        Catalog root = Catalog.builder()
                .name("root")
                .build();
        Catalog child1 = Catalog.builder()
                .name("child1_lvl2")
                .parent(root)
                .build();
        Catalog child2 = Catalog.builder()
                .name("child2_lvl2")
                .parent(root)
                .build();
        Catalog child3 = Catalog.builder()
                .name("child3_lvl3")
                .parent(child1)
                .build();
        Catalog child4 = Catalog.builder()
                .name("child4_lvl2")
                .parent(root)
                .build();
        child1.setChildren(Set.of(child3));
        root.setChildren(Set.of(child1, child2, child4));
        catalogService.create(root);
        assertThrows(CannotMoveCatalogException.class,
                () -> catalogService.moveSubTree(root.getId(), child3.getId()));
    }

    @Test
    public void testDeleteWithNoChildren() {
        Catalog catalog = Catalog.builder()
                .name("test")
                .build();
        catalogService.create(catalog);
        catalogService.deleteById(catalog.getId());
        assertEquals(0, catalogService.getAll().size());
    }

    @Test
    public void throwExceptionWhenDeletingWithChildren() {
        Catalog parent = Catalog.builder()
                .name("parent")
                .build();
        catalogService.create(parent);
        Catalog child = Catalog.builder()
                .name("child")
                .parent(parent)
                .build();
        catalogService.create(child);
        assertThrows(CannotDeleteCatalogException.class,
                () -> catalogService.deleteById(parent.getId()));
    }
}
