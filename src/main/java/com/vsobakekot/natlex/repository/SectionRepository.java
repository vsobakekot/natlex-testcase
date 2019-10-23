package com.vsobakekot.natlex.repository;

import com.vsobakekot.natlex.model.Section;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SectionRepository extends JpaRepository<Section,Long> {

    @Query(value = "SELECT * FROM section s JOIN geological_class gc ON s.id = gc.section_id WHERE gc.code=?1", nativeQuery = true)
    List<Section> findSectionsByGeologicalCode(String code);
}
