package com.smartsensesolutions.java.commons.dao.sample.entity;

import com.smartsensesolutions.java.commons.base.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;
import java.util.List;

@Entity
@Table(name = "country")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Country implements BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true)
    private Long id;
    @Column(name = "country_name", nullable = false)
    private String countryName;
    @Column(name = "population", nullable = false)
    private Long population;
    @Column(name = "active", nullable = false)
    private boolean active;
    @OneToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "country_author_mapping",
            joinColumns = @JoinColumn(name = "country_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "author_id", referencedColumnName = "id")
    )
    private List<Author> authors;
    @Column(name = "created_at", nullable = false)
    @CreationTimestamp
    private Date createdAt;
}

