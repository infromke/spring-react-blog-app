package br.com.infromke.blog.infra.utils;

import java.text.Normalizer;
import java.util.UUID;

public class SlugGenerator {
    public static String generate(String input) {
        if (input == null || input.isBlank()) {
            return UUID.randomUUID().toString().split("-")[0];
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
}
