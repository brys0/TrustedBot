package de.pheromir.trustedbot.r6;

import java.util.Arrays;
import java.util.List;

/**
 * Helper Class to return the found Challenge-Operator Combination from the Streams
 *
 * @author MeFisto94
 */
public class ChallengeResult {
    public List<Challenges> challenges;
    public List<Operator> eligibleOperators;

    public ChallengeResult(Challenges challenge, List<Operator> eligibleOperators) {
        this(Arrays.asList(new Challenges[]{ challenge }), eligibleOperators);
    }

    public ChallengeResult(List<Challenges> challenges, List<Operator> eligibleOperators) {
        this.challenges = challenges;
        this.eligibleOperators = eligibleOperators;
    }

    public List<Challenges> getChallenges() {
        return challenges;
    }

    public List<Operator> getEligibleOperators() {
        return eligibleOperators;
    }
}
