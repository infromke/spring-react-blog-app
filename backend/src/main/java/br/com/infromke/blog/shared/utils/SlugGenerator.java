package br.com.infromke.blog.shared.utils;

import java.text.Normalizer;
import java.util.UUID;

public final class SlugGenerator {

    private SlugGenerator() {
        throw new UnsupportedOperationException("SlugGenerator is an utility class and cannot be instantiated");
    }

    public static String generate(String input) {
        if (input == null || input.isBlank()) {
            return UUID.randomUUID().toString().split("-")[0]; // retorna apenas o UUID
        }

        // remove acentos da string e a normaliza para letras puras
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);

        String baseSlug = normalized.toLowerCase()
                .replaceAll("\\p{M}", "")      // remove acentos
                .replaceAll("[^a-z0-9]", "-")  // substitui caracteres especiais por "-"
                .replaceAll("-{2,}", "-")      // evita hifens duplos
                .replaceAll("^-|-$", "");      // remove hifens no começo e no fim

        String shortId = UUID.randomUUID().toString().split("-")[0];
        return baseSlug + "-" + shortId;
    }

    public static String generateForUser(String fullName) {
        if (fullName == null || fullName.isBlank()) {
            return generate(null);
        }

        String[] names = fullName.trim().split("\\s+"); // desconsidera espaços extras
        String slugInput = (names.length > 1)
                ? names[0] + " " + names[names.length - 1]
                : names[0];

        // cria nome-e-uuid OU nome-sobrenome-e-uuid
        return generate(slugInput);
    }
}
