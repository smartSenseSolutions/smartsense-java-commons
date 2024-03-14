package com.smartsensesolutions.commons.dao.sample.entity;

import com.smartsensesolutions.commons.dao.base.BaseEntity;
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

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private EActivityStatus status;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "level", nullable = false)
    private ELevel level;

    @OneToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "author_books_mapping",
            joinColumns = @JoinColumn(name = "author_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "book_id", referencedColumnName = "id")
    )
    private List<Books> books;

    @Column(name = "created_at", nullable = false)
    @CreationTimestamp
    private Date createdAt;

    @Column(name = "address_id", nullable = false)
    private Long addressId;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "address_id", insertable = false, updatable = false)
    private Address address;
}

