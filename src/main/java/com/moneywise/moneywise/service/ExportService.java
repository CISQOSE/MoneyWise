package com.moneywise.moneywise.service;

import com.moneywise.moneywise.model.Transaction;
import com.moneywise.moneywise.model.Transaction.TypeTransaction;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

public class ExportService {

    // ══════════════════════════════════════════════
    //  EXPORT PDF
    // ══════════════════════════════════════════════
    public boolean exporterPDF(List<Transaction> transactions,
                               String nomUtilisateur,
                               String cheminFichier) {
        try (PDDocument document = new PDDocument()) {

            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);

            PDType1Font fontGras   = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);
            PDType1Font fontNormal = new PDType1Font(Standard14Fonts.FontName.HELVETICA);

            try (PDPageContentStream contenu =
                         new PDPageContentStream(document, page)) {

                float largeur = page.getMediaBox().getWidth();
                float hauteur = page.getMediaBox().getHeight();
                float margeG  = 50;
                float y       = hauteur - 60;

                // ── Titre ─────────────────────────────────
                contenu.beginText();
                contenu.setFont(fontGras, 20);
                contenu.newLineAtOffset(margeG, y);
                contenu.showText("MoneyWise — Rapport de transactions");
                contenu.endText();
                y -= 25;

                // ── Sous-titre ────────────────────────────
                contenu.beginText();
                contenu.setFont(fontNormal, 12);
                contenu.newLineAtOffset(margeG, y);
                contenu.showText("Utilisateur : " + nomUtilisateur +
                        "   |   Date : " + LocalDate.now());
                contenu.endText();
                y -= 30;

                // ── Ligne de séparation ───────────────────
                contenu.moveTo(margeG, y);
                contenu.lineTo(largeur - margeG, y);
                contenu.stroke();
                y -= 20;

                // ── En-têtes du tableau ───────────────────
                contenu.beginText();
                contenu.setFont(fontGras, 11);
                contenu.newLineAtOffset(margeG, y);
                contenu.showText("Date");
                contenu.newLineAtOffset(100, 0);
                contenu.showText("Description");
                contenu.newLineAtOffset(200, 0);
                contenu.showText("Type");
                contenu.newLineAtOffset(80, 0);
                contenu.showText("Montant");
                contenu.endText();
                y -= 20;

                // ── Lignes de transactions ─────────────────
                double totalRevenus  = 0;
                double totalDepenses = 0;

                for (Transaction t : transactions) {

                    if (y < 80) { // nouvelle page si nécessaire
                        contenu.close();
                        page = new PDPage(PDRectangle.A4);
                        document.addPage(page);
                        y = hauteur - 60;
                    }

                    contenu.beginText();
                    contenu.setFont(fontNormal, 10);
                    contenu.newLineAtOffset(margeG, y);
                    contenu.showText(t.getDate().toString());
                    contenu.newLineAtOffset(100, 0);
                    // Tronque la description si trop longue
                    String desc = t.getDescription();
                    if (desc.length() > 25) desc = desc.substring(0, 22) + "...";
                    contenu.showText(desc);
                    contenu.newLineAtOffset(200, 0);
                    contenu.showText(t.getType().name());
                    contenu.newLineAtOffset(80, 0);
                    contenu.showText(String.format("%.2f EUR", t.getMontant()));
                    contenu.endText();

                    if (t.getType() == TypeTransaction.ENTREE) {
                        totalRevenus += t.getMontant();
                    } else {
                        totalDepenses += t.getMontant();
                    }

                    y -= 18;
                }

                // ── Totaux ────────────────────────────────
                y -= 10;
                contenu.moveTo(margeG, y);
                contenu.lineTo(largeur - margeG, y);
                contenu.stroke();
                y -= 20;

                contenu.beginText();
                contenu.setFont(fontGras, 11);
                contenu.newLineAtOffset(margeG, y);
                contenu.showText("Total Revenus  : " +
                        String.format("%.2f EUR", totalRevenus));
                contenu.endText();
                y -= 18;

                contenu.beginText();
                contenu.setFont(fontGras, 11);
                contenu.newLineAtOffset(margeG, y);
                contenu.showText("Total Depenses : " +
                        String.format("%.2f EUR", totalDepenses));
                contenu.endText();
                y -= 18;

                contenu.beginText();
                contenu.setFont(fontGras, 12);
                contenu.newLineAtOffset(margeG, y);
                contenu.showText("Solde          : " +
                        String.format("%.2f EUR",
                                totalRevenus - totalDepenses));
                contenu.endText();
            }

            document.save(cheminFichier);
            System.out.println("✅ PDF exporté : " + cheminFichier);
            return true;

        } catch (IOException e) {
            System.err.println("❌ Erreur export PDF : " + e.getMessage());
            return false;
        }
    }

    // ══════════════════════════════════════════════
    //  EXPORT EXCEL
    // ══════════════════════════════════════════════
    public boolean exporterExcel(List<Transaction> transactions,
                                 String nomUtilisateur,
                                 String cheminFichier) {
        try (Workbook classeur = new XSSFWorkbook()) {

            Sheet feuille = classeur.createSheet("Transactions");

            // ── Styles ────────────────────────────────────
            CellStyle styleEntete = classeur.createCellStyle();
            Font fontEntete = classeur.createFont();
            fontEntete.setBold(true);
            fontEntete.setFontHeightInPoints((short) 12);
            styleEntete.setFont(fontEntete);
            styleEntete.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
            styleEntete.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            styleEntete.setAlignment(HorizontalAlignment.CENTER);
            Font fontEnteteTexte = classeur.createFont();
            fontEnteteTexte.setBold(true);
            fontEnteteTexte.setColor(IndexedColors.WHITE.getIndex());
            styleEntete.setFont(fontEnteteTexte);

            CellStyle styleRevenu = classeur.createCellStyle();
            Font fontVert = classeur.createFont();
            fontVert.setColor(IndexedColors.GREEN.getIndex());
            fontVert.setBold(true);
            styleRevenu.setFont(fontVert);

            CellStyle styleDepense = classeur.createCellStyle();
            Font fontRouge = classeur.createFont();
            fontRouge.setColor(IndexedColors.RED.getIndex());
            fontRouge.setBold(true);
            styleDepense.setFont(fontRouge);

            // ── Titre ─────────────────────────────────────
            Row rowTitre = feuille.createRow(0);
            Cell cellTitre = rowTitre.createCell(0);
            cellTitre.setCellValue("MoneyWise — Rapport : " + nomUtilisateur);

            // ── En-têtes ──────────────────────────────────
            Row rowEntete = feuille.createRow(2);
            String[] entetes = {"Date", "Description", "Type", "Montant (€)"};
            for (int i = 0; i < entetes.length; i++) {
                Cell cell = rowEntete.createCell(i);
                cell.setCellValue(entetes[i]);
                cell.setCellStyle(styleEntete);
            }

            // ── Données ───────────────────────────────────
            int numLigne = 3;
            double totalRevenus  = 0;
            double totalDepenses = 0;

            for (Transaction t : transactions) {
                Row row = feuille.createRow(numLigne++);

                row.createCell(0).setCellValue(t.getDate().toString());
                row.createCell(1).setCellValue(t.getDescription());

                Cell cellType   = row.createCell(2);
                Cell cellMontant = row.createCell(3);
                cellMontant.setCellValue(t.getMontant());

                if (t.getType() == TypeTransaction.ENTREE) {
                    cellType.setCellValue("ENTRÉE");
                    cellType.setCellStyle(styleRevenu);
                    cellMontant.setCellStyle(styleRevenu);
                    totalRevenus += t.getMontant();
                } else {
                    cellType.setCellValue("SORTIE");
                    cellType.setCellStyle(styleDepense);
                    cellMontant.setCellStyle(styleDepense);
                    totalDepenses += t.getMontant();
                }
            }

            // ── Totaux ────────────────────────────────────
            numLigne++;
            Row rowTotalR = feuille.createRow(numLigne++);
            rowTotalR.createCell(2).setCellValue("Total Revenus");
            rowTotalR.createCell(3).setCellValue(totalRevenus);

            Row rowTotalD = feuille.createRow(numLigne++);
            rowTotalD.createCell(2).setCellValue("Total Dépenses");
            rowTotalD.createCell(3).setCellValue(totalDepenses);

            Row rowSolde = feuille.createRow(numLigne);
            rowSolde.createCell(2).setCellValue("Solde");
            rowSolde.createCell(3).setCellValue(totalRevenus - totalDepenses);

            // ── Ajuste la largeur des colonnes ─────────────
            for (int i = 0; i < 4; i++) {
                feuille.autoSizeColumn(i);
            }

            // ── Sauvegarde ────────────────────────────────
            try (FileOutputStream fichier = new FileOutputStream(cheminFichier)) {
                classeur.write(fichier);
            }

            System.out.println("✅ Excel exporté : " + cheminFichier);
            return true;

        } catch (IOException e) {
            System.err.println("❌ Erreur export Excel : " + e.getMessage());
            return false;
        }
    }
}