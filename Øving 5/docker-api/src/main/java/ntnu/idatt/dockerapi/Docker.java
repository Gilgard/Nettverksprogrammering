package ntnu.idatt.dockerapi;

import java.time.Duration;

import com.github.dockerjava.api.*;
import com.github.dockerjava.core.*;
import com.github.dockerjava.httpclient5.*;
import com.github.dockerjava.transport.*;

public class Docker {
    public static DockerClient getDockerClient() {
        DockerClientConfig config = DefaultDockerClientConfig.createDefaultConfigBuilder().build();
        
        DockerHttpClient httpClient = new ApacheDockerHttpClient.Builder()
            .dockerHost(config.getDockerHost())
            .sslConfig(config.getSSLConfig())
            .maxConnections(100)
            .connectionTimeout(Duration.ofSeconds(30))
            .responseTimeout(Duration.ofSeconds(45))
            .build();

        DockerClient dockerClient = DockerClientImpl.getInstance(config, httpClient);
        return dockerClient;
    }
}