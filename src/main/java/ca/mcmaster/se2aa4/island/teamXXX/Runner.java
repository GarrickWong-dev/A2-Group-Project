package ca.mcmaster.se2aa4.island.teamXXX;

import java.io.File;

import static eu.ace_design.island.runner.Runner.run;

public class Runner {

    public static void main(String[] args) {
        // Filename is passed as a command-line argument
        String filename = args[0];
        
        try {
            // Starting the exploration process with a specified configuration
            run(Explorer.class) // Define the class to run (Explorer in this case)
                .exploring(new File(filename)) // Set the file to explore
                .withSeed(42L) // Use a fixed seed for randomness (for reproducibility)
                .startingAt(1, 1, "EAST") // Define the starting position (x, y, and direction)
                .backBefore(7000) // Set the time limit for the exploration (milliseconds)
                .withCrew(5) // Set the crew size
                .collecting(1000, "WOOD") // Specify the type of resource to collect and how much
                .storingInto("./outputs") // Set the output directory for storing results
                .withName("Island") // Name the exploration
                .fire(); // Start the exploration process
        } catch(Exception e) {
            // If an error occurs during exploration, print the error message and stack trace
            System.err.println(e.getMessage());
            e.printStackTrace(System.err);
            System.exit(1); // Exit the program with an error code
        }
    }
}
