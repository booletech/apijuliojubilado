package br.edu.infnet.JulioJubiladoapi.model.service;

public enum ExportFormat {
    CSV("csv"),
    JSON("json");

    private final String value;

    ExportFormat(String value) {
        this.value = value;
    }

    public String extension() {
        return value;
    }

    public static ExportFormat from(String raw) {
        if (raw == null) {
            return CSV;
        }
        for (ExportFormat format : values()) {
            if (format.value.equalsIgnoreCase(raw)) {
                return format;
            }
        }
        throw new IllegalArgumentException("Formato invalido. Use csv ou json.");
    }
}
