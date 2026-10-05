package com.brainspark.nursepulse.platform.auditlogs.infrastructure.pdf;

import com.brainspark.nursepulse.platform.auditlogs.domain.model.aggregates.AuditLog;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Renders a list of audit log entries as a PDF document for offline/administrative review.
 */
@Service
public class AuditLogPdfExportService {

    private static final DateTimeFormatter TIMESTAMP_FORMAT =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm").withZone(ZoneOffset.UTC);
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public byte[] render(List<AuditLog> entries) {
        var document = new Document(PageSize.A4.rotate(), 24, 24, 32, 32);
        var outputStream = new ByteArrayOutputStream();
        try {
            PdfWriter.getInstance(document, outputStream);
            document.open();

            var titleFont = new Font(Font.HELVETICA, 16, Font.BOLD);
            var title = new Paragraph("NursePulse — Reporte de Auditoria", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);

            var subtitleFont = new Font(Font.HELVETICA, 9, Font.NORMAL);
            var subtitle = new Paragraph(
                    entries.size() + " movimiento(s) - generado el " + TIMESTAMP_FORMAT.format(java.time.Instant.now()) + " UTC",
                    subtitleFont
            );
            subtitle.setAlignment(Element.ALIGN_CENTER);
            subtitle.setSpacingAfter(16);
            document.add(subtitle);

            document.add(buildTable(entries));
            document.close();
        } catch (Exception exception) {
            throw new AuditLogPdfRenderingException(exception);
        }
        return outputStream.toByteArray();
    }

    private PdfPTable buildTable(List<AuditLog> entries) throws Exception {
        var table = new PdfPTable(new float[]{1f, 2f, 4f, 2f, 2f});
        table.setWidthPercentage(100);

        var headerFont = new Font(Font.HELVETICA, 10, Font.BOLD);
        for (var header : List.of("Codigo", "Accion", "Descripcion", "Usuario", "Fecha y hora")) {
            var cell = new PdfPCell(new Paragraph(header, headerFont));
            cell.setBackgroundColor(new java.awt.Color(230, 230, 230));
            cell.setPadding(6);
            table.addCell(cell);
        }

        var bodyFont = new Font(Font.HELVETICA, 9, Font.NORMAL);
        for (var entry : entries) {
            addCell(table, "AL-" + entry.getId(), bodyFont);
            addCell(table, entry.getActionType().name(), bodyFont);
            addCell(table, describe(entry), bodyFont);
            addCell(table, entry.getPerformedBy(), bodyFont);
            addCell(table, TIMESTAMP_FORMAT.format(entry.getPerformedAt()), bodyFont);
        }
        return table;
    }

    private void addCell(PdfPTable table, String text, Font font) {
        var cell = new PdfPCell(new Paragraph(text, font));
        cell.setPadding(5);
        table.addCell(cell);
    }

    private String describe(AuditLog entry) {
        var metadata = entry.getMetadata() != null ? entry.getMetadata().getValue() : null;
        if (metadata != null) {
            try {
                JsonNode node = OBJECT_MAPPER.readTree(metadata);
                if (node.hasNonNull("description")) {
                    return node.get("description").asText();
                }
            } catch (Exception ignored) {
                // Fall through to the generic description below when metadata isn't valid JSON.
            }
        }
        return entry.getActionType().name() + " - " + entry.getEntityType().name() + " #" + entry.getEntityId();
    }
}
