package com.maktabty.ktaby.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.CascadeType;
import java.util.List;

@Entity
@Table(name = "book_types")
public class BookType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String typeName;

    @ManyToOne
    private Category category;

    @OneToMany(mappedBy = "bookType", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Book> books;


    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTypeName() { return typeName; }
    public void setTypeName(String typeName) { this.typeName = typeName; }
    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }
    public List<Book> getBooks() { return books; }
    public void setBooks(List<Book> books) { this.books = books; }

    @Override
    public String toString() {
        return "BookType{" +
                "id=" + id +
                ", typeName='" + typeName + '\'' +
                ", category=" + category +
                '}';
    }
}
