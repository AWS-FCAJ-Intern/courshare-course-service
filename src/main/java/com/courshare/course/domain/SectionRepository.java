package com.courshare.course.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SectionRepository extends JpaRepository<Section, String> {

    List<Section> findByCourseIdOrderByOrderIndexAsc(String courseId);

    int countByCourseId(String courseId);
}
