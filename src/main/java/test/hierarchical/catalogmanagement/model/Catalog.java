package test.hierarchical.catalogmanagement.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.LinkedHashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

@Entity
@Table(name = "catalogs")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class Catalog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    @ManyToOne(fetch = FetchType.LAZY)
    @EqualsAndHashCode.Exclude
    @JsonIgnore
    private Catalog parent;

    @OneToMany(cascade = CascadeType.PERSIST, mappedBy = "parent")
    @Fetch(value = FetchMode.SUBSELECT)
    private Set<Catalog> children;

    public Set<Catalog> getChildren() {
        return children == null ? new LinkedHashSet<>() : children;
    }

    @JsonProperty
    public Long getChildrenCount() {
        if (children == null || children.size() == 0) {
            return 0L;
        }
        Long count = children
                .stream()
                .map(Catalog::getChildrenCount)
                .reduce(0L, Long::sum);
        return count + children.size();
    }
}
