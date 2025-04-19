/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package feetask;

import java.util.*;
public class MemoryAllocator {
        private static class MemoryBlock {
        int startAddress;
        int endAddress;
        String process;  // null for free blocks
        
        public MemoryBlock(int startAddress, int endAddress, String process) {
            this.startAddress = startAddress;
            this.endAddress = endAddress;
            this.process = process;
        }
        
        public int getSize() {
            return endAddress - startAddress + 1;
        }
        
        @Override
        public String toString() {
            return String.format("Addresses [%d:%d] %s", 
                startAddress, 
                endAddress, 
                process == null ? "Unused" : "Process " + process);
        }
    }
    
    private int maxMemory;
    private List<MemoryBlock> memoryBlocks;
    
        public MemoryAllocator(int maxMemory) {
        this.maxMemory = maxMemory;
        this.memoryBlocks = new ArrayList<>();
        
        // Initialize with one large free block
        memoryBlocks.add(new MemoryBlock(0, maxMemory - 1, null));
    }
    
    /**
     * Allocates memory for a process using the specified strategy
     */
    public boolean requestMemory(String process, int size, char strategy) {
        // Check if process already exists
        for (MemoryBlock block : memoryBlocks) {
            if (block.process != null && block.process.equals(process)) {
                System.out.println("Error: Process " + process + " already has memory allocated.");
                return false;
            }
        }
        
        // Find a suitable block based on the strategy
        int blockIndex = -1;
        
        switch (strategy) {
            case 'F': // First Fit
                blockIndex = findFirstFit(size);
                break;
            case 'B': // Best Fit
                blockIndex = findBestFit(size);
                break;
            case 'W': // Worst Fit
                blockIndex = findWorstFit(size);
                break;
            default:
                System.out.println("Error: Unknown allocation strategy: " + strategy);
                return false;
        }
        
        if (blockIndex == -1) {
            System.out.println("Error: Not enough memory for allocation request.");
            return false;
        }
        
        // Allocate memory from the found block
        allocateFromBlock(blockIndex, process, size);
        return true;
    }
    
    
    private int findFirstFit(int size) {
        for (int i = 0; i < memoryBlocks.size(); i++) {
            MemoryBlock block = memoryBlocks.get(i);
            if (block.process == null && block.getSize() >= size) {
                return i;
            }
        }
        return -1; // No suitable block found
    }
    
    /**
     * Finds the smallest free block that is large enough
     */
    private int findBestFit(int size) {
        int bestFitIndex = -1;
        int bestFitSize = Integer.MAX_VALUE;
        
        for (int i = 0; i < memoryBlocks.size(); i++) {
            MemoryBlock block = memoryBlocks.get(i);
            int blockSize = block.getSize();
            
            if (block.process == null && blockSize >= size && blockSize < bestFitSize) {
                bestFitIndex = i;
                bestFitSize = blockSize;
            }
        }
        
        return bestFitIndex;
    }
    
    /**
     * Finds the largest free block
     */
    private int findWorstFit(int size) {
        int worstFitIndex = -1;
        int worstFitSize = -1;
        
        for (int i = 0; i < memoryBlocks.size(); i++) {
            MemoryBlock block = memoryBlocks.get(i);
            int blockSize = block.getSize();
            
            if (block.process == null && blockSize >= size && blockSize > worstFitSize) {
                worstFitIndex = i;
                worstFitSize = blockSize;
            }
        }
        
        return worstFitIndex;
    }
    
    /**
     * Allocates memory from a specific block for a process
     */
    private void allocateFromBlock(int blockIndex, String process, int size) {
        MemoryBlock block = memoryBlocks.get(blockIndex);
        int remainingSize = block.getSize() - size;
        
        if (remainingSize > 0) {
            // Split the block and allocate the first part
            MemoryBlock allocatedBlock = new MemoryBlock(block.startAddress, block.startAddress + size - 1, process);
            block.startAddress = block.startAddress + size;
            
            // Insert the allocated block before the free block
            memoryBlocks.add(blockIndex, allocatedBlock);
        } else {
            // Allocate the entire block
            block.process = process;
        }
    }
    
    /**
     * Releases memory allocated to a process
     */
    public boolean releaseMemory(String process) {
        int blockIndex = -1;
        
        // Find the block allocated to the process
        for (int i = 0; i < memoryBlocks.size(); i++) {
            MemoryBlock block = memoryBlocks.get(i);
            if (block.process != null && block.process.equals(process)) {
                blockIndex = i;
                break;
            }
        }
        
        if (blockIndex == -1) {
            System.out.println("Error: Process " + process + " does not have memory allocated.");
            return false;
        }
        
        // Mark the block as free
        MemoryBlock block = memoryBlocks.get(blockIndex);
        block.process = null;
        
        // Merge adjacent free blocks
        mergeAdjacentFreeBlocks();
        
        return true;
    }
    
    /**
     * Merges adjacent free memory blocks
     */
    private void mergeAdjacentFreeBlocks() {
        if (memoryBlocks.size() <= 1) {
            return;
        }
        
        for (int i = 0; i < memoryBlocks.size() - 1; i++) {
            MemoryBlock currentBlock = memoryBlocks.get(i);
            MemoryBlock nextBlock = memoryBlocks.get(i + 1);
            
            if (currentBlock.process == null && nextBlock.process == null) {
                // Merge blocks
                currentBlock.endAddress = nextBlock.endAddress;
                memoryBlocks.remove(i + 1);
                i--; // Recheck the current index
            }
        }
    }
    
    /**
     * Compacts memory by moving all allocated blocks to the beginning
     * and combining all free blocks into one large block at the end
     */
    public void compactMemory() {
        if (memoryBlocks.size() <= 1) {
            return;
        }
        
        List<MemoryBlock> allocatedBlocks = new ArrayList<>();
        int totalFreeSize = 0;
        
        // Collect all allocated blocks and calculate total free size
        for (MemoryBlock block : memoryBlocks) {
            if (block.process != null) {
                allocatedBlocks.add(block);
            } else {
                totalFreeSize += block.getSize();
            }
        }
        
        if (totalFreeSize == 0 || allocatedBlocks.isEmpty()) {
            return; // Nothing to compact
        }
        
        // Clear the current list
        memoryBlocks.clear();
        
        // Add all allocated blocks with adjusted addresses
        int currentAddress = 0;
        for (MemoryBlock block : allocatedBlocks) {
            int blockSize = block.getSize();
            MemoryBlock newBlock = new MemoryBlock(currentAddress, currentAddress + blockSize - 1, block.process);
            memoryBlocks.add(newBlock);
            currentAddress += blockSize;
        }
        
        // Add a single free block at the end if there's free space
        if (totalFreeSize > 0) {
            MemoryBlock freeBlock = new MemoryBlock(currentAddress, maxMemory - 1, null);
            memoryBlocks.add(freeBlock);
        }
    }
    
    /**
     * Reports the current status of memory allocation
     */
    public void printStatus() {
        for (MemoryBlock block : memoryBlocks) {
            System.out.println(block);
        }
    }
}
