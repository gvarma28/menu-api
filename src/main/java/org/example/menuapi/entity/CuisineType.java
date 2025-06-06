package org.example.menuapi.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "cuisine_type")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CuisineType {

    @Id
    @GeneratedValue(generator = "UUID")
    @Column(columnDefinition = "UUID")
    private UUID id;

    @Column(unique = true, nullable = false)
    private String name;

    @ManyToMany(mappedBy = "cuisineTypes")
    private Set<Restaurant> restaurants;
}