package com.wavemaker.employee.util;



import com.wavemaker.employee.exception.FileCreationException;

import java.io.File;
import java.io.IOException;

public class FileCreateUtil {
    public static File createFileIfNotExists(String FILE_PATH) throws FileCreationException {
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            try {
                if (!file.createNewFile()) {
                    throw new FileCreationException("Failed to create a new file at path: " + FILE_PATH, 500);
                }
            } catch (IOException exception) {
                throw new FileCreationException("An error occurred while creating the file at path: " + FILE_PATH, 500);
            }
        }
        return file;
    }
}
