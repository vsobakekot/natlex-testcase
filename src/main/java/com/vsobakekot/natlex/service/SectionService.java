package com.vsobakekot.natlex.service;

import com.vsobakekot.natlex.exсeptions.DataNotFoundException;
import com.vsobakekot.natlex.model.Section;
import com.vsobakekot.natlex.repository.SectionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SectionService {

    private final SectionRepository sectionRepository;

    @Autowired
    public SectionService(SectionRepository sectionRepository) {
        this.sectionRepository = sectionRepository;
    }

    public boolean sectionExists(Long sectionId) {
        return sectionRepository.existsById(sectionId);
    }

    public Section saveSection(Section name){
        return sectionRepository.save(name);
    }

    public Section saveBlankSection(String name){
        return sectionRepository.save(new Section(name));
    }

    public Section getSectionById(Long sectionId) {
        return sectionRepository.findById(sectionId).orElseThrow(()->new DataNotFoundException("The section is not found. Wrong section ID."));
    }

    public Section updateSection(Long sectionId, Section newSection) {
        Section sectionToUpdate = getSectionById(sectionId);
        sectionToUpdate.setName(newSection.getName());
        sectionToUpdate.setGeologicalClasses(newSection.getGeologicalClasses());
        return saveSection(sectionToUpdate);
    }

    public void deleteSectionById(Long sectionId) {
        sectionRepository.deleteById(sectionId);
    }

    public List<Section> getAllSections() {
        List<Section> list = new ArrayList<>();
        sectionRepository.findAll().forEach(list::add);
        return list;
    }

    public List<Section> getAllSectionsByCode(String geologicalClassCode) {
        return sectionRepository.findSectionsByGeologicalCode(geologicalClassCode);
    }
}
