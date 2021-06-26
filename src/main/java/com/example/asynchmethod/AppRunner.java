package com.example.asynchmethod;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Component
public class AppRunner implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(AppRunner.class);

    private final GitHubLookupService gitHubLookupService;

    public AppRunner(GitHubLookupService gitHubLookupService) {
        this.gitHubLookupService = gitHubLookupService;
    }

    @Override
    public void run(String... args) throws Exception {

        long start = System.currentTimeMillis();

        List<String> userNames = new ArrayList<>();
        userNames.add("PivotalSoftware");
        userNames.add("CloudFoundry");
        userNames.add("Spring-Projects");

        List<CompletableFuture<User>> userResults = new ArrayList<>();

        for (String userName : userNames) {
            userResults.add(gitHubLookupService.findUser(userName));
        }

        CompletableFuture.allOf(userResults.toArray(new CompletableFuture[0]))
                .exceptionally(ex -> null)
                .join();

        logger.info("Elapsed time: " + (System.currentTimeMillis() - start));
        userResults.forEach(user -> {
            try {
                logger.info("--> " + user.get());
            } catch (InterruptedException | ExecutionException e) {
                logger.error(e.getMessage());
                e.printStackTrace();
            }
        });
    }
}
