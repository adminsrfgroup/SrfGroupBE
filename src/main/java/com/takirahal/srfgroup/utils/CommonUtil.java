package com.takirahal.srfgroup.utils;

import com.takirahal.srfgroup.modules.user.dto.UserDTO;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;

public class CommonUtil {

    /**
     *
     * @return
     */
    public static Resource loadDefaultFile() {
        try {
            String directoryDefaultUpload = System.getProperty("user.home") + "/srf-group/upload-dir/";
            Path rootLocation = Paths.get(directoryDefaultUpload);
            Path file = rootLocation.resolve("default_image00.jpg");
            Resource resource = new UrlResource(file.toUri());
            return resource;
        } catch (MalformedURLException e) {
            return null;
        }
    }


    /**
     *
     * @param user
     * @return
     */
    public static String getFullNameUser(UserDTO user){
        return (!user.getFirstName().equals("") || !user.getLastName().equals("")) ? user.getFirstName()+" "+user.getLastName() : user.getEmail();
    }

    /**
     *
     * @param originalInput
     * @return
     */
    public static String encodeToString(String originalInput){
        return Base64.getEncoder().encodeToString(originalInput.getBytes())+"SrfGroup";
    }

    /**
     *
     * @param encodedString
     * @return
     */
    public static String decodeToString(String encodedString){
        byte[] decodedBytes = Base64.getDecoder().decode(encodedString.substring(0, encodedString.length()-8));
        return new String(decodedBytes);
    }
}
