package com.vsobakekot.natlex.service;

import com.vsobakekot.natlex.exсeptions.DataNotFoundException;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
public class StorageService {

    @Value("${storage.import}")
    private Path importStorage;

    @Value("${storage.export}")
    private Path exportStorage;

    public void storeImportFile(MultipartFile file, String fileName) throws IOException {
        Files.copy(file.getInputStream(), importStorage.resolve(fileName));
    }

    public void storeExportFile(HSSFWorkbook hssfWorkbook, String fileName) throws IOException {
        hssfWorkbook.write(new File(exportStorage + fileName));
    }

    public Resource loadFile(String fileName) {
        try {
            Resource resource = new UrlResource(exportStorage.resolve(fileName).toUri());
            if(resource.exists()) {
                return resource;
            } else {
                throw new DataNotFoundException("File " + fileName + " is not found.");
            }
        } catch (MalformedURLException ex) {
            throw new DataNotFoundException("File " + fileName + " is not found.", ex);
        }
    }
}
