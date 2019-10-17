package com.vsobakekot.natlex.service;

import com.vsobakekot.natlex.enums.JobResultStatus;
import com.vsobakekot.natlex.enums.JobType;
import com.vsobakekot.natlex.exсeptions.DataNotFoundException;
import com.vsobakekot.natlex.model.GeologicalClass;
import com.vsobakekot.natlex.model.Job;
import com.vsobakekot.natlex.model.Section;
import com.vsobakekot.natlex.repository.JobRepository;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Service
public class JobService {

    private final Logger JOB_LOGGER = LoggerFactory.getLogger(this.getClass());

    private final JobRepository jobRepository;
    private final SectionService sectionService;
    private final StorageService storageService;

    @Autowired
    public JobService(JobRepository jobRepository, SectionService sectionService, StorageService storageService) {
        this.jobRepository = jobRepository;
        this.sectionService = sectionService;
        this.storageService = storageService;
    }

    public boolean jobExists(Long jobId) {
        return jobRepository.existsById(jobId);
    }

    public Job getJobById(Long jobId) {
            return jobRepository.findById(jobId).orElseThrow(DataNotFoundException::new);
    }
    
    public JobResultStatus getJobStatus(Long jobId) {
        return getJobById(jobId).getStatus();
    }

    public boolean jobIsDone(Long jobId) {
        return getJobStatus(jobId).equals(JobResultStatus.DONE);
    }

    public boolean jobIsInProgress(Long jobId) {
        return getJobStatus(jobId).equals(JobResultStatus.IN_PROGRESS);
    }

    public boolean jobIsImport(Long jobId) {
        return getJobById(jobId).getType().equals(JobType.IMPORT);
    }
    
    public List<Job> getAllJobs() {
        List<Job> list = new ArrayList<>();
        jobRepository.findAll().forEach(list::add);
        return list;
    }

    public Job startNewJob(JobType jobType) {
        Job newJob = new Job(jobType);
        newJob.setStatus(JobResultStatus.IN_PROGRESS);
        jobRepository.save(newJob);

        JOB_LOGGER.info("NEW {} JOB #{} STARTED.",newJob.getType(), newJob.getId());

        return newJob;
    }

    @Async
    public void importXLS(MultipartFile file, Job importJob) {

        try {
            String importFileName = importJob.getId().toString() + ".xls";
            storageService.storeImportFile(file, importFileName);

            JOB_LOGGER.info("File {} was stored.", importFileName);

            HSSFWorkbook xlsFile = new HSSFWorkbook(file.getInputStream());
            HSSFSheet sheet = xlsFile.getSheetAt(0);

            for (int r = 1; r < sheet.getPhysicalNumberOfRows(); r++) {
                HSSFRow currentRow = sheet.getRow(r);
                if (currentRow == null) {
                    continue;
                }
                String tempSectionName = currentRow.getCell(0).getStringCellValue();
                Section blank = sectionService.saveBlankSection(tempSectionName);

                List<GeologicalClass> tempGeologicalClasses = new LinkedList<>();

                for (int c = 1; c < currentRow.getLastCellNum(); c+= 2) {
                    HSSFCell currentNameCell = currentRow.getCell(c);
                    if(currentNameCell == null) {
                        continue;
                    }
                    String geologicalClassName = currentNameCell.getStringCellValue();

                    HSSFCell currentCodeCell = currentRow.getCell(c+1);
                    if(currentCodeCell == null) {
                        continue;
                    }
                    String geologicalClassCode = currentCodeCell.getStringCellValue();
                    tempGeologicalClasses.add(new GeologicalClass(geologicalClassName, geologicalClassCode, blank));
                }
                blank.setGeologicalClasses(tempGeologicalClasses);
                sectionService.saveSection(blank);
            }
            importJob.setStatus(JobResultStatus.DONE);
            jobRepository.save(importJob);

            JOB_LOGGER.info("Parsing {} was successfully finished. JOB #{} is DONE", importFileName, importJob.getId());

        } catch (Exception e) {
            importJob.setStatus(JobResultStatus.ERROR);
            jobRepository.save(importJob);

            JOB_LOGGER.error("JOB #{} is FAILED.", importJob.getId());

            e.printStackTrace();
        }
    }

    @Async
    public void exportXLS(Job exportJob) {

        try {
            List<Section> sections = sectionService.getAllSections();

            HSSFWorkbook hssfWorkbook = new HSSFWorkbook();
            HSSFSheet sheet = hssfWorkbook.createSheet("Sections");

            HSSFRow headerRow = sheet.createRow(0);
            HSSFCell headerSectionNameCell = headerRow.createCell(0, CellType.STRING);
            headerSectionNameCell.setCellValue("Section name");

            for (Section s : sections) {
                HSSFRow currentRow = sheet.createRow(sheet.getLastRowNum()+1);

                HSSFCell sectionNameCell = currentRow.createCell(0,CellType.STRING);
                sectionNameCell.setCellValue(s.getName());

                List<GeologicalClass> geologicalClasses = s.getGeologicalClasses();

                for (GeologicalClass gc : geologicalClasses) {
                    if (currentRow.getLastCellNum() == headerRow.getLastCellNum()) {
                        int index = Math.floorDiv(headerRow.getLastCellNum(), 2) + 1;
                        HSSFCell headerGeologicalClassNameCell = headerRow.createCell(headerRow.getLastCellNum(), CellType.STRING);
                        headerGeologicalClassNameCell.setCellValue(String.format("Class %d name", index));
                        HSSFCell headerGeologicalClassCodeCell = headerRow.createCell(headerRow.getLastCellNum(), CellType.STRING);
                        headerGeologicalClassCodeCell.setCellValue(String.format("Class %d code", index));
                    }
                    HSSFCell geologicalClassNameCell = currentRow.createCell(currentRow.getLastCellNum(),CellType.STRING);
                    geologicalClassNameCell.setCellValue(gc.getName());
                    HSSFCell geologicalClassCodeCell = currentRow.createCell(currentRow.getLastCellNum(),CellType.STRING);
                    geologicalClassCodeCell.setCellValue(gc.getCode());
                }
            }

            String exportFileName = exportJob.getId().toString() + ".xls";
            storageService.storeExportFile(hssfWorkbook, exportFileName);

            exportJob.setStatus(JobResultStatus.DONE);
            jobRepository.save(exportJob);

            JOB_LOGGER.info("File {} was successfully generated. JOB #{} is DONE",exportFileName, exportJob.getId());

        } catch (Exception e) {
            exportJob.setStatus(JobResultStatus.ERROR);
            jobRepository.save(exportJob);

            JOB_LOGGER.error("JOB #{} is FAILED.", exportJob.getId());

            e.printStackTrace();
        }
    }

    public Resource downloadXLS(Long jobId) {
            String fileName = jobId.toString() + ".xls";
            return storageService.loadFile(fileName);
    }
}
