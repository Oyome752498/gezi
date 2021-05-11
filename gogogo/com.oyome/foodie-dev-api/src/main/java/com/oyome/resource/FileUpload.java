package com.oyome.resource;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "file")
//@PropertySource("classpath:file-upload-prod.properties")
@PropertySource("classpath:file-upload-dev.properties")

public class FileUpload {
    private String iamgeUrlFaceLocation;

    private String iamgeServerUrl;

    public String getIamgeServerUrl() {
        return iamgeServerUrl;
    }

    public void setIamgeServerUrl(String iamgeServerUrl) {
        this.iamgeServerUrl = iamgeServerUrl;
    }

    public String getIamgeUrlFaceLocation() {
        return iamgeUrlFaceLocation;
    }

    public void setIamgeUrlFaceLocation(String iamgeUrlFaceLocation) {
        this.iamgeUrlFaceLocation = iamgeUrlFaceLocation;
    }
}
