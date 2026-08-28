package com.g14.pindan.service;

import com.g14.pindan.entity.Groupon;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * Excel 导出工具（阶段5）：拼单列表 → .xlsx 字节流
 */
@Component
public class ExcelExportService {

    private static final String[] HEADERS = {"标题", "分类", "目标人数", "当前人数", "状态", "创建时间"};

    /**
     * 导出拼单列表为 xlsx，返回字节数组
     */
    public byte[] exportGroupons(List<Groupon> list, String sheetName) throws IOException {
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet(sheetName);

            // 表头：黄底加粗
            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            Font font = workbook.createFont();
            font.setBold(true);
            headerStyle.setFont(font);

            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < HEADERS.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(HEADERS[i]);
                cell.setCellStyle(headerStyle);
            }

            int rowIndex = 1;
            for (Groupon g : list) {
                Row row = sheet.createRow(rowIndex++);
                row.createCell(0).setCellValue(g.getTitle() == null ? "" : g.getTitle());
                row.createCell(1).setCellValue(g.getCategoryTag() == null ? "" : g.getCategoryTag());
                row.createCell(2).setCellValue(g.getTargetCount() == null ? 0 : g.getTargetCount());
                row.createCell(3).setCellValue(g.getCurrentCount() == null ? 0 : g.getCurrentCount());
                row.createCell(4).setCellValue(statusText(g.getStatus()));
                row.createCell(5).setCellValue(g.getCreatedAt() == null ? ""
                        : g.getCreatedAt().toString().replace("T", " "));
            }

            for (int i = 0; i < HEADERS.length; i++) {
                sheet.autoSizeColumn(i);
            }
            workbook.write(out);
            return out.toByteArray();
        }
    }

    private String statusText(Integer status) {
        if (status == null) {
            return "";
        }
        return switch (status) {
            case 0 -> "招募中";
            case 1 -> "已成团";
            case 2 -> "已结束";
            case 3 -> "已取消";
            default -> "未知";
        };
    }
}
