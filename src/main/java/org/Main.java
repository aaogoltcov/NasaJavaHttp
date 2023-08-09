package org;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpHeaders;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;

public class Main {
    public static final String REMOTE_SERVICE_URI = "https://api.nasa.gov/planetary/apod?api_key=6XaNRjevepztiJAUWyn4tV11yzVW9vob6KnuluFb";
    public static final ObjectMapper mapper = new ObjectMapper();

    public static void main(String[] args) throws IOException {
        try (
            CloseableHttpClient httpClient = HttpClientBuilder.create()
                .setDefaultRequestConfig(RequestConfig.custom()
                    .setConnectTimeout(5000)
                    .setSocketTimeout(30000)
                    .setRedirectsEnabled(false)
                    .build())
                .build()
        ) {
            HttpGet request = new HttpGet(REMOTE_SERVICE_URI);
            request.setHeader(HttpHeaders.ACCEPT, ContentType.APPLICATION_JSON.getMimeType());

            CloseableHttpResponse response = httpClient.execute(request);

            Nasa nasaResource = mapper.readValue(
                response.getEntity().getContent(), new
                    TypeReference<>() {
                    });

            URL nasaImageUrl = new URL(nasaResource.getUrl());
            BufferedImage nasaImage;

            try {
                nasaImage = ImageIO.read(nasaImageUrl);

                ImageIO.write(nasaImage, "jpg", new File(nasaResource.getTitle() + ".jpg"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}