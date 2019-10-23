package com.vsobakekot.natlex.service;

import com.vsobakekot.natlex.exсeptions.DataNotFoundException;
import com.vsobakekot.natlex.model.Section;
import com.vsobakekot.natlex.repository.SectionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SectionService {

    private final SectionRepository sectionRepository;

    @Autowired
    public SectionService(SectionRepository sectionRepository) {
        this.sectionRepository = sectionRepository;
    }

    public boolean isExists(Long sectionId) {
        return sectionRepository.existsById(sectionId);
    }

    public Section saveSection(Section section){
        return sectionRepository.save(section);
    }

    public Section saveBlankSection(String sectionName){
        return saveSection(new Section(sectionName));
    }

    public Section getSectionById(Long sectionId) {
        return sectionRepository.findById(sectionId).orElseThrow(()->new DataNotFoundException("The section is not found. Wrong section ID."));
    }

    public Section updateSection(Long sectionId, Section newSection) {
        return sectionRepository.findById(sectionId).map(section -> {
            section.setName(newSection.getName());
            return sectionRepository.save(section);
        }).orElseThrow(() -> new DataNotFoundException("The section is not found. 0 sections have been updated"));
    }

    public void deleteSectionById(Long sectionId) {
        sectionRepository.deleteById(sectionId);
    }

    public List<Section> getAllSections() {
        return sectionRepository.findAll();
    }

    public List<Section> getAllSectionsByCode(String geologicalClassCode) {
        return sectionRepository.findSectionsByGeologicalCode(geologicalClassCode);
    }
}
