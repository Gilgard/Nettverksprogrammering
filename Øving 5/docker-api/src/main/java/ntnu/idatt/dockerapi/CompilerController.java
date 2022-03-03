package ntnu.idatt.dockerapi;

import java.io.File;
import java.io.FileWriter;
import java.io.OutputStream;

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
        OutputStream result = null;

        //write code to file
        File main = new File(ClassLoader.getSystemResource("main.cpp").toURI());
        FileWriter myWriter = new FileWriter(main);
        myWriter.write(code);
        myWriter.close();

        //run code in docker
        Process build = Runtime.getRuntime().exec("docker build \"./compile/\" -q -t gcc");
        if(build.waitFor() == 0) {
            Process run = Runtime.getRuntime().exec("docker run --rm gcc");
            run.waitFor();
            result = run.getOutputStream();
        }
        
        if(result == null) {
            return "Something went terribly wrong";
        }
        //return
        return result.toString();
    }

    /*
    private str executeCommand(String command) {
        try {
            log(command);
            Process process = Runtime.getRuntime().exec(command);
            logOutput(process.getInputStream(), "");
            logOutput(process.getErrorStream(), "Error: ");
            process.waitFor();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void logOutput(InputStream inputStream, String prefix) {
        new Thread(() -> {
            Scanner scanner = new Scanner(inputStream, "UTF-8");
            while (scanner.hasNextLine()) {
                synchronized (this) {
                    log(prefix + scanner.nextLine());
                }
            }
            scanner.close();
        }).start();
    }
    */
}
