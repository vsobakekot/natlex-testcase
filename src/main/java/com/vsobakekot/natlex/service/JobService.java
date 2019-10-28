package com.vsobakekot.natlex.service;

import com.vsobakekot.natlex.exсeptions.DataNotFoundException;
import com.vsobakekot.natlex.exсeptions.ExportErrorResultException;
import com.vsobakekot.natlex.exсeptions.ExportInProgressException;
import com.vsobakekot.natlex.model.GeologicalClass;
import com.vsobakekot.natlex.model.Job;
import com.vsobakekot.natlex.model.Section;
import com.vsobakekot.natlex.model.enums.JobResultStatus;
import com.vsobakekot.natlex.model.enums.JobType;
import com.vsobakekot.natlex.repository.JobRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.LinkedList;
import java.util.List;

@Service
@Slf4j
public class JobService {

    private final JobRepository jobRepository;
    private final SectionService sectionService;
    private final StorageService storageService;

    @Autowired
    public JobService(JobRepository jobRepository, SectionService sectionService, StorageService storageService) {
        this.jobRepository = jobRepository;
        this.sectionService = sectionService;
        this.storageService = storageService;
    }

    public JobResultStatus getJobStatus(Long jobId, JobType jobType) {
        return jobRepository.findByIdAndType(jobId,jobType)
                            .orElseThrow(()-> new DataNotFoundException("The job is not found. Wrong Job ID."))
                            .getStatus();
    }

    public List<Job> getAllJobs() {
        return jobRepository.findAll();
    }

    public Job startNewJob(JobType jobType) {
        Job newJob = new Job(jobType);
        newJob.setStatus(JobResultStatus.IN_PROGRESS);
        jobRepository.save(newJob);

        log.info("{} - NEW {} JOB #{} STARTED.", newJob.getCreatedAt(), newJob.getType(), newJob.getId());

        return newJob;
    }

    @Async
    public void importXLS(MultipartFile file, Job importJob) {

        try {
            String importFileName = importJob.getId().toString() + ".xls";
            storageService.storeImportFile(file, importFileName);

            log.info("File {} was stored.", importFileName);

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

            log.info("Parsing {} was successfully finished. JOB #{} is DONE", importFileName, importJob.getId());

        } catch (Exception e) {
            importJob.setStatus(JobResultStatus.ERROR);
            jobRepository.save(importJob);

            log.error("JOB #{} is FAILED.", importJob.getId());
        }
    }

    @Async
    public void exportXLS(Job exportJob) {

        try {
            List<Section> sections = sectionService.getAllSections();

            HSSFWorkbook hssfWorkbook = new HSSFWorkbook();
            HSSFSheet sheet = hssfWorkbook.createSheet("Sections");

            HSSFRow headerRow = sheet.createRow(0);
            addCellToRow(headerRow,"Section name");

            for (Section s : sections) {
                HSSFRow currentRow = sheet.createRow(sheet.getLastRowNum()+1);
                addCellToRow(currentRow,s.getName());

                List<GeologicalClass> geologicalClasses = s.getGeologicalClasses();

                for (GeologicalClass gc : geologicalClasses) {
                    if (currentRow.getLastCellNum() == headerRow.getLastCellNum()) {
                        int index = Math.floorDiv(headerRow.getLastCellNum(), 2) + 1;
                        addCellToRow(headerRow,String.format("Class %d name", index));
                        addCellToRow(headerRow,String.format("Class %d code", index));
                    }
                    addCellToRow(currentRow,(gc.getName()));
                    addCellToRow(currentRow,(gc.getCode()));
                }
            }

            String exportFileName = exportJob.getId().toString() + ".xls";
            storageService.storeExportFile(hssfWorkbook, exportFileName);

            exportJob.setStatus(JobResultStatus.DONE);
            jobRepository.save(exportJob);

            log.info("File {} was successfully generated. JOB #{} is DONE",exportFileName, exportJob.getId());

        } catch (Exception e) {
            exportJob.setStatus(JobResultStatus.ERROR);
            jobRepository.save(exportJob);

            log.error("JOB #{} is FAILED.", exportJob.getId());
        }
    }

    public Resource downloadXLS(Long jobId) {
        if (getJobStatus(jobId, JobType.EXPORT).equals(JobResultStatus.DONE)) {
            return storageService.loadFile(jobId.toString() + ".xls");
        }
        if (getJobStatus(jobId, JobType.EXPORT).equals(JobResultStatus.IN_PROGRESS)) {
            throw new ExportInProgressException("Export is still in progress now, try again later!");
        }
        throw new ExportErrorResultException("Export job ended with errors, create new export!");
    }

    private void addCellToRow(HSSFRow row, String value) {
        HSSFCell newCell = row.createCell(Math.max(row.getLastCellNum(), 0),CellType.STRING);
        newCell.setCellValue(value);
    }
}
