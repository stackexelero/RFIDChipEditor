package com.stackexcelero.utility;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class IOManager {
	private static final String folderPath = "C://dump";
	
	public static void writeToFile(String fileName, byte[] data) throws IOException {
        Path path = Paths.get(folderPath + "//" + fileName);
        Files.write(path, data);
    }
	public static byte[] readFromFile(String fileName) throws IOException {
        Path path = Paths.get(folderPath + "//" + fileName);
        return Files.readAllBytes(path);
    }
}
