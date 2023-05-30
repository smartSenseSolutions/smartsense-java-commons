package com.smartsensesolutions.java.commons.dao.sample.entity;

import com.smartsensesolutions.java.commons.base.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;

@Entity
@Table(name = "books")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Books implements BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true)
    private Long id;
    @Column(name = "book_name", nullable = false)
    private String bookName;
    @Column(name = "description", nullable = false)
    private String description;
    @Column(name = "active", nullable = false)
    private boolean active;
    @Column(name = "created_at", nullable = false)
    @CreationTimestamp
    private Date createdAt;
}
