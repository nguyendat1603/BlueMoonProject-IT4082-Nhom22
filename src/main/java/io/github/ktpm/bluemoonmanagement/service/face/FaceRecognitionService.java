package io.github.ktpm.bluemoonmanagement.service.face;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.StringJoiner;

public class FaceRecognitionService {

    public boolean recognizeFace(String email) {
        try {
            ProcessBuilder pb = new ProcessBuilder(
                    "/Users/mac/Downloads/Project-IT4082-main/BlueMoonManagement/venv/bin/python3",
                    "face_check.py", email
            );
            pb.redirectErrorStream(true);
            Process process = pb.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            String lastLine = null;

            while ((line = reader.readLine()) != null) {
                lastLine = line.trim();
            }

            process.waitFor();

            return lastLine != null && lastLine.equalsIgnoreCase("true");
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
