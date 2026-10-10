package com.bank.aml.common.util;

import org.apache.commons.text.similarity.JaroWinklerSimilarity;
import org.apache.commons.text.similarity.LevenshteinDistance;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public final class FuzzyMatchUtils {

    private static final JaroWinklerSimilarity JARO_WINKLER = new JaroWinklerSimilarity();
    private static final LevenshteinDistance LEVENSHTEIN = new LevenshteinDistance();

    private FuzzyMatchUtils() {}

    public static String normalize(String input) {
        if (input == null) return "";
        return input.trim()
                .toLowerCase()
                .replaceAll("[^a-z0-9\\s]", " ")
                .replaceAll("\\s+", " ")
                .trim();
    }

    public static double calculateJaroWinklerScore(String s1, String s2) {
        String norm1 = normalize(s1);
        String norm2 = normalize(s2);
        if (norm1.isEmpty() || norm2.isEmpty()) return 0.0;
        if (norm1.equalsIgnoreCase(norm2)) return 100.0;
        return JARO_WINKLER.apply(norm1, norm2) * 100.0;
    }

    public static int calculateLevenshteinDistance(String s1, String s2) {
        String norm1 = normalize(s1);
        String norm2 = normalize(s2);
        return LEVENSHTEIN.apply(norm1, norm2);
    }

    public static double calculateTokenSetScore(String s1, String s2) {
        String norm1 = normalize(s1);
        String norm2 = normalize(s2);
        if (norm1.isEmpty() || norm2.isEmpty()) return 0.0;
        if (norm1.equalsIgnoreCase(norm2)) return 100.0;

        Set<String> tokens1 = new HashSet<>(Arrays.asList(norm1.split("\\s+")));
        Set<String> tokens2 = new HashSet<>(Arrays.asList(norm2.split("\\s+")));

        Set<String> intersection = new HashSet<>(tokens1);
        intersection.retainAll(tokens2);

        Set<String> union = new HashSet<>(tokens1);
        union.addAll(tokens2);

        if (union.isEmpty()) return 0.0;
        return ((double) intersection.size() / union.size()) * 100.0;
    }

    public static double calculateBestMatchScore(String query, String candidate) {
        if (query == null || candidate == null) return 0.0;
        double jw = calculateJaroWinklerScore(query, candidate);
        double tokenSet = calculateTokenSetScore(query, candidate);

        // Weighted aggregate: emphasize token-set similarity for reversed names (e.g. "Petrovic, Viktor")
        return Math.max(jw, (jw * 0.4) + (tokenSet * 0.6));
    }
}
