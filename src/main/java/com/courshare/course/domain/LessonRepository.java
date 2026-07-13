package com.courshare.course.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface LessonRepository extends JpaRepository<Lesson, String> {

    int countBySectionId(String sectionId);

    @Query("SELECT COUNT(l) FROM Lesson l WHERE l.sectionId IN "
            + "(SELECT s.id FROM Section s WHERE s.courseId = :courseId)")
    int countByCourseId(@Param("courseId") String courseId);
}
