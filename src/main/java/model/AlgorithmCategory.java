package model;

/**
 * High-level modules of the CSC2107 Algorithm Explorer application.
 * Maps directly to Module A (Recursion), Module B (Data Structures),
 * Module C (Sorting), and Module D (Time Complexity Analysis).
 */
public enum AlgorithmCategory {
    MODULE_A_RECURSION("Module A: Recursion Lab"),
    MODULE_B_STRUCTURES("Module B: Data Structures Lab"),
    MODULE_C_SORTING("Module C: Sorting Lab"),
    MODULE_D_COMPLEXITY("Module D: Time Complexity Lab");

    private final String displayName;

    AlgorithmCategory(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
