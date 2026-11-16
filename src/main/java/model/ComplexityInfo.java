package model;

/**
 * Holds theoretical Big-O time complexity (Best, Average, Worst), extra space complexity,
 * stability status, and explanation for an algorithm or data structure.
 */
public class ComplexityInfo {
    private final String bestTime;
    private final String averageTime;
    private final String worstTime;
    private final String spaceComplexity;
    private final String stability;
    private final String explanation;

    public ComplexityInfo(String bestTime, String averageTime, String worstTime, String spaceComplexity, String explanation) {
        this(bestTime, averageTime, worstTime, spaceComplexity, "N/A", explanation);
    }

    public ComplexityInfo(
            String bestTime,
            String averageTime,
            String worstTime,
            String spaceComplexity,
            String stability,
            String explanation) {
        this.bestTime = bestTime;
        this.averageTime = averageTime;
        this.worstTime = worstTime;
        this.spaceComplexity = spaceComplexity;
        this.stability = stability;
        this.explanation = explanation;
    }

    public String getBestTime() {
        return bestTime;
    }

    public String getAverageTime() {
        return averageTime;
    }

    public String getWorstTime() {
        return worstTime;
    }

    public String getSpaceComplexity() {
        return spaceComplexity;
    }

    public String getStability() {
        return stability;
    }

    public String getExplanation() {
        return explanation;
    }
}
