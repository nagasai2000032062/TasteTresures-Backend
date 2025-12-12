package ltts.com.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.*;

@Entity
@Table(name = "category")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = true, unique = false) // Adjust nullable and unique constraints as needed
    private String name;

    @Column(name = "slug", nullable = true)
    private String slug;

    // Getters and Setters

    
    public Long getId() {
        return id;
    }

    @Override
	public String toString() {
		return "Category [id=" + id + ", name=" + name + ", slug=" + slug + "]";
	}

	public Category(Long id, String name, String slug) {
		super();
		this.id = id;
		this.name = name;
		this.slug = slug;
	}

	public Category() {
		super();
	}

	public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }
}