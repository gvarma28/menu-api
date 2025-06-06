package org.example.menuapi.repository;

import org.example.menuapi.entity.CuisineType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CuisineTypeRepository extends JpaRepository<CuisineType, UUID> {
    Optional<CuisineType> findByNameIgnoreCase(String name);
    boolean existsByNameIgnoreCase(String name);
}