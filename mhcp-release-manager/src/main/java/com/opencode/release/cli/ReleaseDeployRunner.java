package com.opencode.release.cli;


import com.opencode.release.service.ReleaseDeployService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;


@Component
public class ReleaseDeployRunner implements CommandLineRunner {
    private final ReleaseDeployService deploy;

    public ReleaseDeployRunner(ReleaseDeployService deploy) {
        this.deploy = deploy;
    }

    @Override
    public void run(String... args) {
        if (args.length < 1) {
            throw new IllegalArgumentException("Usage: java -jar mhcp-release-manager.jar <release-yaml-path> [gitCommit]");
        }


        String releaseYamlPath = args[0];
        String gitCommit = args.length > 1 ? args[1] : "local";


        deploy.deploy(releaseYamlPath, gitCommit);
        System.out.println("✅ Release deployed successfully");
    }
}