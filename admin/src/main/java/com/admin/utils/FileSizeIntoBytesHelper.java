package com.admin.utils;

import org.springframework.stereotype.Service;

@Service
public class FileSizeIntoBytesHelper {
    public long convertFileSizeToBytes(String fileSizeString) {
        fileSizeString = fileSizeString.toLowerCase().trim();

        if (fileSizeString.matches("\\d+(kb|mb|gb)")) {
            long numericValue = Long.parseLong(fileSizeString.replaceAll("[^0-9]", ""));
            String unit = fileSizeString.replaceAll("[0-9]", "");

            switch (unit) {
                case "kb":
                    return numericValue * 1024; // 1 KB = 1024 bytes
                case "mb":
                    return numericValue * 1024 * 1024; // 1 MB = 1024 KB = 1024 * 1024 bytes
                case "gb":
                    return numericValue * 1024 * 1024 * 1024; // 1 GB = 1024 MB = 1024 * 1024 KB = 1024 * 1024 * 1024 bytes
            }
        }

        // Return a default value (e.g., 0) or throw an exception for invalid input.
        return 0;
    }

}
