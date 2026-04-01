package com.japaneseLearning.repository;

import com.japaneseLearning.entity.Course;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
    List<Course> findByCategory_CategoryId(Long categoryId);
    @org.springframework.data.jpa.repository.EntityGraph(attributePaths = {"lessons"})
    @Query("SELECT c FROM Course c")
    List<Course> findAllWithLessons();

    List<Course> findByIsFreeTrue();
    
    @Query("SELECT c FROM Course c WHERE c.isFree = true " +
           "OR c.courseId IN (SELECT e.course.courseId FROM UserCourseEnrollment e WHERE e.userId = :userId)")
    List<Course> findAccessibleCoursesByUser(@Param("userId") String userId);

    /**
     * JL-34: Search courses by title or description
     */
    @Query("SELECT c FROM Course c WHERE " +
           "LOWER(c.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(c.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Course> searchByKeyword(@Param("keyword") String keyword);

    @Query("SELECT c FROM Course c WHERE " +
           "(:keyword IS NULL OR :keyword = '' OR " +
           "LOWER(c.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(c.description) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
           "(:categoryId IS NULL OR c.category.categoryId = :categoryId) AND " +
           "(:isFree IS NULL OR c.isFree = :isFree) AND " +
           "(:minPrice IS NULL OR c.price >= :minPrice) AND " +
           "(:maxPrice IS NULL OR c.price <= :maxPrice)")
    Page<Course> searchWithFilters(
            @Param("keyword") String keyword,
            @Param("categoryId") Long categoryId,
            @Param("isFree") Boolean isFree,
            @Param("minPrice") Double minPrice,
            @Param("maxPrice") Double maxPrice,
            Pageable pageable
    );
}
