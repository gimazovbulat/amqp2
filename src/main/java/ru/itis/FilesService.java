package ru.itis;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

public class FilesService {
    public String downloadFile(Info info, String toPath) throws IOException {
        URL url = new URL(info.getUrl());
        String fileName = url.getFile();
        System.out.println(fileName);
        String newFileName = toPath + info.getEmail() + "_" + UUID.randomUUID() + fileName.substring(fileName.lastIndexOf("."));
        Files.copy(new BufferedInputStream(url.openStream()),
                Paths.get(newFileName));
        System.out.println("file downloaded");
        return newFileName;
    }
}
