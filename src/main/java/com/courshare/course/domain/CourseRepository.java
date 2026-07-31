package com.courshare.course.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CourseRepository extends JpaRepository<Course, String> {

    Page<Course> findByPublishedTrue(Pageable pageable);

    Page<Course> findByPublishedTrueAndCategoryId(String categoryId, Pageable pageable);

    Page<Course> findByInstructorId(String instructorId, Pageable pageable);

    long countByInstructorId(String instructorId);

    long countByInstructorIdAndPublishedTrue(String instructorId);

    long countByInstructorIdAndPublishedFalse(String instructorId);

    @Query("""
            SELECT c FROM Course c
            WHERE c.published = true
              AND (cast(:categoryId as string) IS NULL OR c.categoryId = :categoryId)
              AND (cast(:search as string) IS NULL OR LOWER(c.title) LIKE LOWER(CONCAT('%', cast(:search as string), '%')))
            """)
    Page<Course> searchPublished(
            @Param("categoryId") String categoryId,
            @Param("search") String search,
            Pageable pageable
    );

    @Query("""
            SELECT c FROM Course c
            WHERE c.instructorId = :instructorId
              AND (cast(:categoryId as string) IS NULL OR c.categoryId = :categoryId)
              AND (cast(:published as boolean) IS NULL OR c.published = :published)
              AND (cast(:search as string) IS NULL OR LOWER(c.title) LIKE LOWER(CONCAT('%', cast(:search as string), '%')))
            """)
    Page<Course> searchInstructorCourses(
            @Param("instructorId") String instructorId,
            @Param("categoryId") String categoryId,
            @Param("published") Boolean published,
            @Param("search") String search,
            Pageable pageable
    );
}

