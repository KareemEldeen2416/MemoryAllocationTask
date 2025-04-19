/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package feetask;

import java.util.Scanner;
public class FEEtask {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
                
        
        
        
            int maxMemory = 1024;
   
        MemoryAllocator allocator = new MemoryAllocator(maxMemory);
        Scanner scanner = new Scanner(System.in);
        
        System.out.print("allocator> ");
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine().trim();
            String[] tokens = line.split("\\s+");
            
            if (tokens.length == 0 || tokens[0].isEmpty()) {
                System.out.print("allocator> ");
                continue;
            }
            
            String command = tokens[0].toUpperCase();
            
            switch (command) {
                case "RQ":
                    if (tokens.length != 4) {
                        System.out.println("Error: RQ command requires exactly 3 parameters.");
                        break;
                    }
                    
                    String process = tokens[1];
                    int size;
                    char strategy;
                    
                    try {
                        size = Integer.parseInt(tokens[2]);
                        if (size <= 0) {
                            System.out.println("Error: Size must be positive.");
                            break;
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Error: Size must be an integer.");
                        break;
                    }
                    
                    if (tokens[3].length() != 1) {
                        System.out.println("Error: Strategy must be a single character (F, B, W).");
                        break;
                    }
                    
                    strategy = tokens[3].toUpperCase().charAt(0);
                    if (strategy != 'F' && strategy != 'B' && strategy != 'W') {
                        System.out.println("Error: Strategy must be F (first fit), B (best fit), or W (worst fit).");
                        break;
                    }
                    
                    allocator.requestMemory(process, size, strategy);
                    break;
                    
                case "RL":
                    if (tokens.length != 2) {
                        System.out.println("Error: RL command requires exactly 1 parameter.");
                        break;
                    }
                    
                    process = tokens[1];
                    allocator.releaseMemory(process);
                    break;
                    
                case "C":
                    allocator.compactMemory();
                    System.out.println("Memory compacted.");
                    break;
                    
                case "STAT":
                    allocator.printStatus();
                    break;
                    
                case "X":
                    System.out.println("Exiting allocator.");
                    scanner.close();
                    return;
                    
                default:
                    System.out.println("Error: Unknown command. Valid commands are RQ, RL, C, STAT, X.");
                    break;
            }
            
            System.out.print("allocator> ");
        }
        
        scanner.close();
    }
    
}
