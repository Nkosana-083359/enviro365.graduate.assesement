package com.eviro.assessment.grad001.nkosanamdlalose.service.impl;

import com.eviro.assessment.grad001.nkosanamdlalose.repository.AccountProfileRepository;
import com.eviro.assessment.grad001.nkosanamdlalose.service.FileParserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;


@Service
public class
FileParserServiceImpl implements FileParserService {

    @Autowired
    private AccountProfileRepository accountProfileRepository;


    @Value("${spring.datasource.url}")
    private static  String DB_URL;

    @Value("${spring.datasource.username}")
    private String DB_USER ;

    @Value("${spring.datasource.password}")
    private String DB_PASSWORD ;

    @Override
    public void parseCSV(File csvFile) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String sql = "INSERT INTO ACCOUNT_PROFILE  (NAME, SURNAME, HTTP_IMAGE_LINK) VALUES (?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);

            Files.lines(Paths.get(csvFile.getAbsolutePath()))
                    .skip(1) // skip header row
                    .map(line -> line.split(","))
                    .forEach(data -> {
                        try {
                            String name = data[0];
                            String surname = data[1];
                            String imageFormat = data[2];
                            String base64ImageData = data[3];
                            //byte[] imageBytes = Base64.getDecoder().decode(imageData);


                            File imageFile = convertCSVDataToImage(base64ImageData);

                            String httpImageLink = createImageLink(imageFile).toString();

                            pstmt.setString(1, name);
                            pstmt.setString(2, surname);
                            pstmt.setString(3, httpImageLink);

                            pstmt.executeUpdate();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    });
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public File convertCSVDataToImage(String base64ImageData) {
        byte[] imageData = Base64.getDecoder().decode(base64ImageData);
        Path imagePath = Paths.get("image.jpg");
        try {
            Files.write(imagePath, imageData);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return imagePath.toFile();
    }

    @Override
    public URL createImageLink(File fileImage) {
        try {
            return new URL("http://localhost:8090/com.eviro.assessment.grad001.nkosanamdlalose/" + fileImage.getName());
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
    }

}
