package com.smartsensesolutions.java.commons.dao.sample.entity;

import com.smartsensesolutions.java.commons.base.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;
import java.util.List;

@Entity
@Table(name = "author")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Author implements BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true)
    private Long id;
    @Column(name = "author_name", nullable = false)
    private String authorName;
    @Column(name = "age", nullable = false)
    private Long age;
    @Column(name = "active", nullable = false)
    private boolean active;
    @OneToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "author_books_mapping",
            joinColumns = @JoinColumn(name = "author_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "book_id", referencedColumnName = "id")
    )
    private List<Books> books;
    @Column(name = "created_at", nullable = false)
    @CreationTimestamp
    private Date createdAt;
}

