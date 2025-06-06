package org.example.menuapi.repository;

import org.example.menuapi.entity.Restaurant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RestaurantRepository extends JpaRepository<Restaurant, UUID> {

    /**
     * Find restaurant by tiny ID
     */
    Optional<Restaurant> findByTinyId(String tinyId);

    /**
     * Check if tiny ID already exists
     */
    boolean existsByTinyId(String tinyId);

    /**
     * Find restaurants by name (case-insensitive partial match)
     */
    @Query("SELECT r FROM Restaurant r WHERE LOWER(r.restaurantName) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Restaurant> findByRestaurantNameContainingIgnoreCase(@Param("name") String name);

    /**
     * Find restaurants by city
     */
    List<Restaurant> findByCityIgnoreCase(String city);

    /**
     * Find restaurants by state
     */
    List<Restaurant> findByStateIgnoreCase(String state);

    /**
     * Find restaurants by city and state
     */
    List<Restaurant> findByCityIgnoreCaseAndStateIgnoreCase(String city, String state);

    /**
     * Find restaurants by pincode
     */
    List<Restaurant> findByPincode(String pincode);

    /**
     * Find restaurants within a radius (in kilometers) using Haversine formula
     */
    @Query(value = """
        SELECT * FROM restaurant r 
        WHERE r.latitude IS NOT NULL AND r.longitude IS NOT NULL
        AND (6371 * acos(cos(radians(:latitude)) * cos(radians(r.latitude)) 
        * cos(radians(r.longitude) - radians(:longitude)) 
        + sin(radians(:latitude)) * sin(radians(r.latitude)))) <= :radiusKm
        ORDER BY (6371 * acos(cos(radians(:latitude)) * cos(radians(r.latitude)) 
        * cos(radians(r.longitude) - radians(:longitude)) 
        + sin(radians(:latitude)) * sin(radians(r.latitude))))
        """, nativeQuery = true)
    List<Restaurant> findRestaurantsWithinRadius(
            @Param("latitude") BigDecimal latitude,
            @Param("longitude") BigDecimal longitude,
            @Param("radiusKm") Double radiusKm
    );

    /**
     * Find restaurants within a radius with pagination
     */
    @Query(value = """
        SELECT * FROM restaurant r 
        WHERE r.latitude IS NOT NULL AND r.longitude IS NOT NULL
        AND (6371 * acos(cos(radians(:latitude)) * cos(radians(r.latitude)) 
        * cos(radians(r.longitude) - radians(:longitude)) 
        + sin(radians(:latitude)) * sin(radians(r.latitude)))) <= :radiusKm
        ORDER BY (6371 * acos(cos(radians(:latitude)) * cos(radians(r.latitude)) 
        * cos(radians(r.longitude) - radians(:longitude)) 
        + sin(radians(:latitude)) * sin(radians(r.latitude))))
        """, nativeQuery = true)
    Page<Restaurant> findRestaurantsWithinRadius(
            @Param("latitude") BigDecimal latitude,
            @Param("longitude") BigDecimal longitude,
            @Param("radiusKm") Double radiusKm,
            Pageable pageable
    );

    /**
     * Find restaurants by cuisine type
     */
    @Query("SELECT DISTINCT r FROM Restaurant r JOIN r.cuisineTypes ct WHERE ct.name = :cuisineName")
    List<Restaurant> findByCuisineType(@Param("cuisineName") String cuisineName);

    /**
     * Find restaurants by multiple cuisine types (OR condition)
     */
    @Query("SELECT DISTINCT r FROM Restaurant r JOIN r.cuisineTypes ct WHERE ct.name IN :cuisineNames")
    List<Restaurant> findByCuisineTypes(@Param("cuisineNames") List<String> cuisineNames);

    /**
     * Find restaurants by UI template
     */
    List<Restaurant> findByUiTemplate(String uiTemplate);

    /**
     * Find restaurants with location data (latitude and longitude not null)
     */
    @Query("SELECT r FROM Restaurant r WHERE r.latitude IS NOT NULL AND r.longitude IS NOT NULL")
    List<Restaurant> findRestaurantsWithLocation();

    /**
     * Search restaurants by name, city, or state
     */
    @Query("""
        SELECT r FROM Restaurant r 
        WHERE LOWER(r.restaurantName) LIKE LOWER(CONCAT('%', :searchTerm, '%'))
        OR LOWER(r.city) LIKE LOWER(CONCAT('%', :searchTerm, '%'))
        OR LOWER(r.state) LIKE LOWER(CONCAT('%', :searchTerm, '%'))
        """)
    List<Restaurant> searchRestaurants(@Param("searchTerm") String searchTerm);

    /**
     * Search restaurants with pagination
     */
    @Query("""
        SELECT r FROM Restaurant r 
        WHERE LOWER(r.restaurantName) LIKE LOWER(CONCAT('%', :searchTerm, '%'))
        OR LOWER(r.city) LIKE LOWER(CONCAT('%', :searchTerm, '%'))
        OR LOWER(r.state) LIKE LOWER(CONCAT('%', :searchTerm, '%'))
        """)
    Page<Restaurant> searchRestaurants(@Param("searchTerm") String searchTerm, Pageable pageable);

    /**
     * Get restaurants with dish count
     */
    @Query("""
        SELECT r, COUNT(d) as dishCount 
        FROM Restaurant r 
        LEFT JOIN r.dishes d 
        GROUP BY r
        """)
    List<Object[]> findRestaurantsWithDishCount();

    /**
     * Find restaurants that are open on a specific day
     */
    @Query("""
        SELECT DISTINCT r FROM Restaurant r 
        JOIN r.timings rt 
        WHERE rt.dayOfWeek = :dayOfWeek 
        AND rt.openTime IS NOT NULL 
        AND rt.closeTime IS NOT NULL
        """)
    List<Restaurant> findRestaurantsOpenOnDay(@Param("dayOfWeek") String dayOfWeek);

    /**
     * Find restaurants by city with cuisine type filter
     */
    @Query("""
        SELECT DISTINCT r FROM Restaurant r 
        JOIN r.cuisineTypes ct 
        WHERE LOWER(r.city) = LOWER(:city) 
        AND ct.name = :cuisineName
        """)
    List<Restaurant> findByCityAndCuisineType(
            @Param("city") String city,
            @Param("cuisineName") String cuisineName
    );

    /**
     * Get restaurant statistics by city
     */
    @Query("""
        SELECT r.city, COUNT(r) as restaurantCount 
        FROM Restaurant r 
        WHERE r.city IS NOT NULL 
        GROUP BY r.city 
        ORDER BY restaurantCount DESC
        """)
    List<Object[]> getRestaurantCountByCity();

    /**
     * Get restaurant statistics by state
     */
    @Query("""
        SELECT r.state, COUNT(r) as restaurantCount 
        FROM Restaurant r 
        WHERE r.state IS NOT NULL 
        GROUP BY r.state 
        ORDER BY restaurantCount DESC
        """)
    List<Object[]> getRestaurantCountByState();

    /**
     * Find recently added restaurants
     */
    @Query("SELECT r FROM Restaurant r ORDER BY r.createdAt DESC")
    List<Restaurant> findRecentlyAddedRestaurants(Pageable pageable);

    /**
     * Find recently updated restaurants
     */
    @Query("SELECT r FROM Restaurant r ORDER BY r.updatedAt DESC")
    List<Restaurant> findRecentlyUpdatedRestaurants(Pageable pageable);

    /**
     * Complex search with multiple filters
     */
    @Query("""
        SELECT DISTINCT r FROM Restaurant r 
        LEFT JOIN r.cuisineTypes ct 
        WHERE (:name IS NULL OR LOWER(r.restaurantName) LIKE LOWER(CONCAT('%', :name, '%')))
        AND (:city IS NULL OR LOWER(r.city) = LOWER(:city))
        AND (:state IS NULL OR LOWER(r.state) = LOWER(:state))
        AND (:cuisineType IS NULL OR ct.name = :cuisineType)
        """)
    Page<Restaurant> findWithFilters(
            @Param("name") String name,
            @Param("city") String city,
            @Param("state") String state,
            @Param("cuisineType") String cuisineType,
            Pageable pageable
    );
}