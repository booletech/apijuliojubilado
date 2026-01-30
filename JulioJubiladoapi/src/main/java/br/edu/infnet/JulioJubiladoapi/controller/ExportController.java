package br.edu.infnet.JulioJubiladoapi.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.edu.infnet.JulioJubiladoapi.model.service.ExportFormat;
import br.edu.infnet.JulioJubiladoapi.model.service.ExportService;
import br.edu.infnet.JulioJubiladoapi.model.service.ExportService.ExportPayload;

@RestController
@RequestMapping("/api/export")
@Validated
public class ExportController {

    private final ExportService exportService;

    public ExportController(ExportService exportService) {
        this.exportService = exportService;
    }

    @GetMapping("/clientes")
    public ResponseEntity<byte[]> exportClientes(
            @RequestParam(name = "format", defaultValue = "csv") String format) {
        ExportPayload payload = exportService.exportClientes(ExportFormat.from(format));
        return buildResponse(payload);
    }

    @GetMapping("/tarefas")
    public ResponseEntity<byte[]> exportTarefas(
            @RequestParam(name = "format", defaultValue = "csv") String format) {
        ExportPayload payload = exportService.exportTarefas(ExportFormat.from(format));
        return buildResponse(payload);
    }

    @GetMapping("/tickets")
    public ResponseEntity<byte[]> exportTickets(
            @RequestParam(name = "format", defaultValue = "csv") String format) {
        ExportPayload payload = exportService.exportTickets(ExportFormat.from(format));
        return buildResponse(payload);
    }

    private ResponseEntity<byte[]> buildResponse(ExportPayload payload) {
        return ResponseEntity.ok()
                .contentType(payload.contentType())
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + payload.filename() + "\"")
                .header(HttpHeaders.CACHE_CONTROL, "no-store")
                .body(payload.data());
    }
}
