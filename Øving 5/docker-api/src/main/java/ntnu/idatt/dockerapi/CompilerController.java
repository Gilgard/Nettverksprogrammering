package ntnu.idatt.dockerapi;

import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.util.Scanner;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin
@RestController
public class CompilerController {
    @PostMapping("/")
    public String dock(@RequestParam String code) throws Exception {
        String result = "Kontakt er oppe, men ingenting ble gjort";

        //write code to file
        File main = new File("main.cpp");
        result = "Filen ble laget og er på path: " + main.getPath();
        FileWriter myWriter = new FileWriter(main);
        myWriter.write(code);
        myWriter.close();

        // build docker container
        String buildCommands = "docker build -q -t gcc .";
        Process build = Runtime.getRuntime().exec(buildCommands.split(" "));
        if (build.waitFor() != 0) return "Could not build docker container";

        // run docker container
        String runCommands = "docker run --rm gcc";
        Process run = Runtime.getRuntime().exec(runCommands.split(" "));
        if (run.waitFor() != 0) return "Could not run docker container";

        // read output from container
        result = readProcess(run.getInputStream());

        // return
        return result;
    }

    private String readProcess(InputStream stream) {
        //build buffered stream to string
        Scanner scanner = new Scanner(stream);
        StringBuilder stringBuilder = new StringBuilder();
        while (scanner.hasNext()) {
            stringBuilder.append(scanner.next());
        }
        scanner.close();
        return stringBuilder.toString();
    }
}
