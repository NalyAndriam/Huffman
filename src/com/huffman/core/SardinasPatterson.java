package com.huffman.core;

import java.util.*;

public class SardinasPatterson {

    /**
     * Vérifie si un langage est un code à l’aide de l’algorithme de Sardinas-Patterson.
     * @param langage Ensemble de mots (chaînes de caractères) représentant le langage
     * @return true si le langage est un code, false sinon
     */
    public boolean estUnCode(Set<String> langage) {
        if (langage == null || langage.isEmpty()) {
            return true; // Un langage vide est toujours un code
        }

        // Liste pour stocker les ensembles L_n successifs
        List<Set<String>> sequence = new ArrayList<>();
        sequence.add(new HashSet<>(langage)); // L_0 = langage

        // Calcul de L_1 = L^{-1}L - {ε}
        Set<String> L1 = calculQuotientGauche(langage, langage);
        L1.remove(""); // On enlève le mot vide si présent

        // Si le mot vide est présent dans L1, ce n’est pas un code
        if (L1.contains("")) {
            return false;
        }

        sequence.add(L1);
        int n = 1;

        // Boucle de calcul des ensembles L_n jusqu’à stabilisation ou détection de mot vide
        while (true) {
            Set<String> Ln = sequence.get(n);

            // L_{n+1} = L^{-1}L_n ∪ L_n^{-1}L
            Set<String> LnPlus1 = new HashSet<>();
            LnPlus1.addAll(calculQuotientGauche(langage, Ln));
            LnPlus1.addAll(calculQuotientGauche(Ln, langage));

            // Si le mot vide est trouvé dans L_{n+1}, ce n’est pas un code
            if (LnPlus1.contains("")) {
                return false;
            }

            // Si L_{n+1} est vide, alors c’est un code
            if (LnPlus1.isEmpty()) {
                return true;
            }

            // Si L_{n+1} est identique à un L_k précédent, alors la séquence se répète
            for (Set<String> precedent : sequence) {
                if (precedent.equals(LnPlus1)) {
                    return true; // Pas de mot vide, donc c’est un code
                }
            }

            // Ajouter L_{n+1} à la séquence et continuer
            sequence.add(LnPlus1);
            n++;
        }
    }

    /**
     * Calcule le quotient à gauche M^{-1}L : l’ensemble des suffixes obtenus en enlevant les préfixes de M dans L.
     * @param M Ensemble de mots préfixes
     * @param L Ensemble de mots cibles
     * @return Ensemble des suffixes résultants
     */
    private Set<String> calculQuotientGauche(Set<String> M, Set<String> L) {
        Set<String> resultat = new HashSet<>();
        for (String u : M) {
            resultat.addAll(calculResiduelGauche(u, L));
        }
        return resultat;
    }

    /**
     * Calcule le résiduel à gauche u^{-1}L : les suffixes de mots de L qui commencent par u.
     * @param u Mot préfixe
     * @param L Ensemble de mots cibles
     * @return Ensemble des suffixes obtenus
     */
    private Set<String> calculResiduelGauche(String u, Set<String> L) {
        Set<String> resultat = new HashSet<>();
        for (String v : L) {
            if (v.startsWith(u)) {
                String suffixe = v.substring(u.length());
                resultat.add(suffixe);
            }
        }
        return resultat;
    }

    // Exemple d’utilisation
    public static void main(String[] args) {
        SardinasPatterson sp = new SardinasPatterson();

        // Cas 1 : Langage {1, 00, 01, 10}
        Set<String> langage1 = new HashSet<>(Arrays.asList("1", "00", "01", "10"));
        System.out.println("Langage " + langage1 + " est un code : " + sp.estUnCode(langage1));

        // Cas 2 : Langage {000, 010, 011, 01001}
        Set<String> langage2 = new HashSet<>(Arrays.asList("000", "010", "011", "01001"));
        System.out.println("Langage " + langage2 + " est un code : " + sp.estUnCode(langage2));

        // Cas 3 : Langage préfixe {00, 010, 10, 110, 111}
        Set<String> langage3 = new HashSet<>(Arrays.asList("00", "010", "10", "110", "111"));
        System.out.println("Langage " + langage3 + " est un code : " + sp.estUnCode(langage3));
    }
}
