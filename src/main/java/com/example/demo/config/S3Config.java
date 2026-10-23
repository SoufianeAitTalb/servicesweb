package com.example.demo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.http.auth.aws.signer.RegionSet;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

import java.net.URI;

@Configuration
public class S3Config {

    // Clés lues depuis les variables d'environnement ou le fichier .env (jamais dans le code : le repo est public)
    @Value("${S3_ACCESS_KEY}")
    private String accessKey;

    @Value("${S3_SECRET_KEY}")
    private String secretKey;

    private StaticCredentialsProvider credentials() {
        return StaticCredentialsProvider.create(AwsBasicCredentials.builder()
                .accessKeyId(accessKey)
                .secretAccessKey(secretKey)
                .build());
    }

    // Envoie les fichiers dans S3 depuis le backend
    @Bean
    public S3Client s3Client() {
        return S3Client.builder()
                .credentialsProvider(credentials())
                .endpointOverride(URI.create("https://s3.bober.ovh"))
                .region(Region.US_EAST_1)
                .sigv4aSigningRegionSet(RegionSet.GLOBAL)
                .forcePathStyle(true)
                .build();
    }

    // Génère les liens S3 signés (pour voir les fichiers)
    @Bean
    public S3Presigner s3Presigner() {
        return S3Presigner.builder()
                .credentialsProvider(credentials())
                .endpointOverride(URI.create("https://s3.bober.ovh"))
                .region(Region.US_EAST_1)
                // Liens en https://s3.bober.ovh/<bucket>/... : le certificat HTTPS ne couvre pas <bucket>.s3.bober.ovh
                .serviceConfiguration(S3Configuration.builder().pathStyleAccessEnabled(true).build())
                .build();
    }
}
