package ntnu.idatt.dockerapi;

import java.io.File;
import java.io.FileWriter;

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
        DockerClient dockerClient = Docker.getDockerClient();
        dockerClient.pingCmd().exec(); //ping docker client to see if it's awake :)
        dockerClient.buildImageCmd(new File("Dockerfile"));
        CreateContainerResponse container = dockerClient.createContainerCmd("Dockerfile").exec();
        dockerClient.startContainerCmd(container.getId());
        


        // Process build = Runtime.getRuntime().exec("docker build \"./compile/\" -q -t gcc");
        // if(build.waitFor() == 0) {
        //     Process run = Runtime.getRuntime().exec("docker run --rm gcc");
        //     run.waitFor();
        //     result = run.getOutputStream();
        // }
        
        // if(result == null) {
        //     return "Something went terribly wrong";
        // }

        //return
        return result;
    }
}
