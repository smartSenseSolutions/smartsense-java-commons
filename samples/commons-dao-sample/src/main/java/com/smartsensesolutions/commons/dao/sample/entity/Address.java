package com.smartsensesolutions.commons.dao.sample.entity;

import com.smartsensesolutions.commons.dao.base.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;

@Entity
@Table(name = "address")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Address implements BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true)
    private Long id;
    @Column(name = "city", nullable = false)
    private String city;
    @Column(name = "house", nullable = false)
    private String house;
    @Column(name = "street", nullable = false)
    private String street;
    @Column(name = "active", nullable = false)
    private boolean active;
    @Column(name = "created_at", nullable = false)
    @CreationTimestamp
    private Date createdAt;
}

