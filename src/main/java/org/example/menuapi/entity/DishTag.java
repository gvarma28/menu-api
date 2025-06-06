package org.example.menuapi.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "dish_tag")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DishTag {

    @Id
    @GeneratedValue(generator = "UUID")
    @Column(columnDefinition = "UUID")
    private UUID id;

    @Column(unique = true, nullable = false)
    private String name;

    @ManyToMany(mappedBy = "tags")
    private Set<Dish> dishes;
}