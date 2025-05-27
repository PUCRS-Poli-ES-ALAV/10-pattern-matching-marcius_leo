import java.math.BigInteger;
import java.util.Random;

public class PatternMatching {

    public static void main(String[] args) {
        String s1 = "ABCDCBDCBDACBDABDCBADF";
        String s2 = "ADF";

        System.out.println("Texto: " + s1);
        System.out.println("Padrão: " + s2);
        System.out.println("----------------------------------------");

        // --- 1. Algoritmo Ingênuo ---
        int indexIngenuo = ingenuo(s2, s1);
        System.out.println("Resultado Ingênuo: Padrão encontrado no índice " + indexIngenuo);
        System.out.println("----------------------------------------");


        // --- 2. Algoritmo Rabin-Karp ---
        int indexRabinKarp = rabinKarp(s2, s1);
        System.out.println("Resultado Rabin-Karp: Padrão encontrado no índice " + indexRabinKarp);
        System.out.println("----------------------------------------");


        // --- 3. Algoritmo KMP ---
        int indexKMP = kmp(s2, s1);
        System.out.println("Resultado KMP: Padrão encontrado no índice " + indexKMP);
        System.out.println("----------------------------------------");
        
        // Exemplo de como testar com strings grandes (descomente para usar)
        /*
        System.out.println("\n--- Teste com Strings Grandes ---");
        StringBuilder sbTexto = new StringBuilder(500001);
        for(int i=0; i<500000; i++) sbTexto.append('A');
        sbTexto.append('B');
        String textoGrande = sbTexto.toString();
        
        StringBuilder sbPadrao = new StringBuilder(250001);
        for(int i=0; i<250000; i++) sbPadrao.append('A');
        sbPadrao.append('B');
        String padraoGrande = sbPadrao.toString();
        
        System.out.println("Texto grande: " + textoGrande.length() + " caracteres");
        System.out.println("Padrão grande: " + padraoGrande.length() + " caracteres");
        
        // Testando KMP com a string grande
        int indexGrande = kmp(padraoGrande, textoGrande);
        System.out.println("Resultado KMP (Grande): Padrão encontrado no índice " + indexGrande);
        */
    }

    // ====================================================================
    // 1. ALGORITMO INGENUO (FORÇA BRUTA)
    // ====================================================================
    public static int ingenuo(String padrao, String texto) {
        int M = padrao.length();
        int N = texto.length();

        // Contadores
        long outerIterations = 0;
        long totalComparisons = 0;

        for (int i = 0; i <= N - M; i++) {
            outerIterations++;
            int j;
            for (j = 0; j < M; j++) {
                totalComparisons++;
                if (texto.charAt(i + j) != padrao.charAt(j)) {
                    break;
                }
            }
            if (j == M) {
                System.out.println("--- Análise Algoritmo Ingênuo ---");
                System.out.println("Iterações do loop externo: " + outerIterations);
                System.out.println("Comparações totais de caracteres: " + totalComparisons);
                return i; // Padrão encontrado no índice i
            }
        }

        System.out.println("--- Análise Algoritmo Ingênuo ---");
        System.out.println("Iterações do loop externo: " + outerIterations);
        System.out.println("Comparações totais de caracteres: " + totalComparisons);
        return -1; // Padrão não encontrado
    }

    // ====================================================================
    // 2. ALGORITMO DE RABIN-KARP
    // ====================================================================
    public static int rabinKarp(String padrao, String texto) {
        int M = padrao.length();
        int N = texto.length();

        if (M > N) return -1;
        
        // R é o tamanho do alfabeto (256 para ASCII)
        final int R = 256; 
        // Q é um número primo grande para o módulo
        final long Q = BigInteger.probablePrime(31, new Random()).longValue(); 

        long h = 1; // h = R^(M-1) % Q
        for (int i = 0; i < M - 1; i++) {
            h = (h * R) % Q;
        }

        long padraoHash = 0;
        long textoHash = 0;
        for (int i = 0; i < M; i++) {
            padraoHash = (R * padraoHash + padrao.charAt(i)) % Q;
            textoHash = (R * textoHash + texto.charAt(i)) % Q;
        }

        // Contadores
        long iterations = 0;
        long spuriousHits = 0;

        for (int i = 0; i <= N - M; i++) {
            iterations++;
            if (padraoHash == textoHash) {
                // Se os hashes correspondem, verifica caractere por caractere
                if (padrao.equals(texto.substring(i, i + M))) {
                    System.out.println("--- Análise Rabin-Karp ---");
                    System.out.println("Iterações (janelas verificadas): " + iterations);
                    System.out.println("Colisões espúrias: " + spuriousHits);
                    return i; // Padrão encontrado
                } else {
                    spuriousHits++;
                }
            }

            // Calcula o hash da próxima janela (rolling hash)
            if (i < N - M) {
                textoHash = (R * (textoHash - texto.charAt(i) * h) + texto.charAt(i + M)) % Q;
                if (textoHash < 0) {
                    textoHash += Q; // Garante que o hash seja positivo
                }
            }
        }

        System.out.println("--- Análise Rabin-Karp ---");
        System.out.println("Iterações (janelas verificadas): " + iterations);
        System.out.println("Colisões espúrias: " + spuriousHits);
        return -1; // Não encontrado
    }

    // ====================================================================
    // 3. ALGORITMO DE KNUTH-MORRIS-PRATT (KMP)
    // ====================================================================
    public static int kmp(String padrao, String texto) {
        int M = padrao.length();
        int N = texto.length();

        // 1. Pré-processamento: cria o array lps (Longest Proper Prefix Suffix)
        int[] lps = calcularLPS(padrao);
        
        // Contadores
        long textComparisons = 0;

        int i = 0; // ponteiro para o texto
        int j = 0; // ponteiro para o padrão

        while (i < N) {
            textComparisons++;
            if (padrao.charAt(j) == texto.charAt(i)) {
                i++;
                j++;
            }

            if (j == M) {
                System.out.println("--- Análise KMP ---");
                System.out.println("Comparações/Movimentos aproximados no texto: " + textComparisons);
                return i - j; // Padrão encontrado
                // Para encontrar todas ocorrências, j = lps[j-1];
            } else if (i < N && padrao.charAt(j) != texto.charAt(i)) {
                // Mismatch: usa o array lps para pular caracteres no padrão
                if (j != 0) {
                    j = lps[j - 1];
                } else {
                    i++;
                }
            }
        }
        
        System.out.println("--- Análise KMP ---");
        System.out.println("Comparações/Movimentos aproximados no texto: " + textComparisons);
        return -1; // Não encontrado
    }

    /**
     * Função auxiliar do KMP para calcular o array LPS.
     */
    private static int[] calcularLPS(String padrao) {
        int M = padrao.length();
        int[] lps = new int[M];
        int length = 0; // Comprimento do maior prefixo-sufixo anterior
        int i = 1;
        lps[0] = 0;

        while (i < M) {
            if (padrao.charAt(i) == padrao.charAt(length)) {
                length++;
                lps[i] = length;
                i++;
            } else {
                if (length != 0) {
                    length = lps[length - 1];
                } else {
                    lps[i] = 0;
                    i++;
                }
            }
        }
        return lps;
    }
}