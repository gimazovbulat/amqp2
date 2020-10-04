package ru.itis;

import com.itextpdf.html2pdf.HtmlConverter;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class PdfCreator {
    public void formPdf(String templatePath, String fileName, List<KeyValue> keyValues) {
        String newStr = "";
        try {
            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(templatePath));
            int i;
            StringBuilder sb = new StringBuilder();
            while ((i = bis.read()) != -1) {
                sb.append((char) i);
            }
            String str = sb.toString();
            for (KeyValue keyValue : keyValues) {
                newStr = str.replace(keyValue.getKey(), keyValue.getValue());
            }
            FileOutputStream fos = new FileOutputStream(fileName);
            HtmlConverter.convertToPdf(newStr, fos);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
}
