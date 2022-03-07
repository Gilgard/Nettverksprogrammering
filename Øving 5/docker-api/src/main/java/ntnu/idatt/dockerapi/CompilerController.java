package ntnu.idatt.dockerapi;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collector;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerCmd;
import com.github.dockerjava.api.command.CreateContainerResponse;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import org.zalando.logbook.Logbook;

@CrossOrigin
@RestController
public class CompilerController {
    Logbook logbook = Logbook.create();

    @PostMapping("/")
    public String dock(@RequestParam String code) throws Exception {
        String result = "Kontakt er oppe, men ingenting ble gjort";

        //write code to file
        File main = new File("main.cpp");
        result = "Filen ble laget og er på path: " + main.getPath();
        FileWriter myWriter = new FileWriter(main);
        myWriter.write(code);
        myWriter.close();

        //run code in docker
        /*
        DockerClient dockerClient = Docker.getDockerClient();
        dockerClient.pingCmd().exec(); //ping docker client to see if it's awake :)
        dockerClient.buildImageCmd(new File("Dockerfile"));
        CreateContainerResponse container = dockerClient.createContainerCmd("Dockerfile").exec();
        dockerClient.startContainerCmd(container.getId());
        */

        // build docker container
        String buildCommands = "docker build -q -t gcc";
        Process build = Runtime.getRuntime().exec(buildCommands.split(" "));
        build.waitFor();

        // run docker container
        String runCommands = "docker run --rm gcc";
        Process run = Runtime.getRuntime().exec(runCommands.split(" "));
        BufferedInputStream stream = new BufferedInputStream(run.getInputStream());

        // read output from container
        result = readProcess(stream);

        // return
        return result;
    }

    private String readProcess(InputStream stream) throws Error {
        //build buffered stream to string


        BufferedInputStream buffy = new BufferedInputStream(stream);

        String string = new StringBuilder(buffy.read());

        return string;
    }
}
