package com.vsobakekot.natlex.controller;

import com.vsobakekot.natlex.model.Section;
import com.vsobakekot.natlex.service.SectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping(path = "/api/sections", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class SectionsRestController {

    private final SectionService sectionService;

    @Autowired
    public SectionsRestController(SectionService sectionService) {
        this.sectionService = sectionService;
    }

    @PostMapping
    public ResponseEntity<?> createSection(@Valid @RequestBody Section section) {
        if (sectionService.sectionExists(section.getId())) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(sectionService.saveSection(section), HttpStatus.CREATED);
    }

    // add pagination for it (ask)
    @GetMapping
    public ResponseEntity<?> getAllSections() {
        if (sectionService.getAllSections().isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(sectionService.getAllSections(), HttpStatus.OK);
    }

    @GetMapping ("/{sectionId}")
    public ResponseEntity<?> getSection(@PathVariable Long sectionId) {
        if (sectionId == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        return new ResponseEntity<>(sectionService.getSectionById(sectionId),HttpStatus.OK);
    }

    @PutMapping("/{sectionId}")
    public ResponseEntity<?> updateSection(@PathVariable Long sectionId, @Valid @RequestBody Section newSection) {
        if (sectionId == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        if (!sectionService.sectionExists(sectionId)) {
            return new ResponseEntity<>(sectionService.saveSection(newSection), HttpStatus.CREATED);
        }
        return new ResponseEntity<>(sectionService.updateSection(sectionId,newSection), HttpStatus.OK);
    }

    @DeleteMapping("/{sectionId}")
    public ResponseEntity<?> deleteSection(@PathVariable Long sectionId) {
        if (sectionId == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        sectionService.deleteSectionById(sectionId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/by-code")
    public ResponseEntity<?> filterByCode(@RequestParam String geologicalClassCode) {
        if (geologicalClassCode == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(sectionService.getAllSectionsByCode(geologicalClassCode), HttpStatus.OK);
    }
}
