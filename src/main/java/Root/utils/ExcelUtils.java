package root.utils;

import java.awt.font.FontRenderContext;
import java.awt.font.LineBreakMeasurer;
import java.awt.font.TextAttribute;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.AttributedString;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.ShapeTypes;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellUtil;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;
import org.apache.poi.xssf.usermodel.XSSFDrawing;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFSimpleShape;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelUtils {
	private XSSFWorkbook workbook;
	private XSSFSheet sheet;
	private int rowIndex = 0;
	private int colIndex = 0;
	private int maxCols = 0;

	// 1. DB �������� ������ ������ �ۼ����ִ� �޼��� �ۼ� (�������� �ű� ����)
	// 2. ���� ���������� �о� Ư�� ��¥�� DB ����͸� ����� �߰��ϴ� �޼��� �ۼ� (����͸� ��� �ۼ�)

	public ExcelUtils(String sheetName) {
		workbook = new XSSFWorkbook();
		sheet = workbook.createSheet(sheetName);
	}

	public int getRowIndex() {
		return rowIndex;
	}

	/**
	 * ���� ������ Ŀ�� ��ġ�� �����Ѵ�.
	 * 
	 * @param col ������ �� Column Index
	 * @param row ������ �� Row Index
	 */
	public void setOffset(int col, int row) {
		colIndex = col;
		rowIndex = row;
	}

	/**
	 * ���� �ʺ� �����Ѵ�.
	 * 
	 * @param colIndex
	 * @param width
	 */
	public void setColWidth(int colIndex, int width) {
		sheet.setColumnWidth(colIndex, width);
	}

	/**
	 * �� ��Ÿ�� �� �� ���� �����Ѵ�. �� ��, ������ ��ġ�� Row �Ǵ� Cell�� ���ٸ� �ش� ��ġ�� Row, Cell�� ���� �����Ѵ�.
	 * 
	 * @param colIndex  CellStyle, CellValue�� ������ ���� Column Index
	 * @param rowIndex  CellStyle, CellValue�� ������ ���� Row Index
	 * @param cellStyle CellStyle
	 * @param cellValue CellValue
	 */
	public void setCell(int colIndex, int rowIndex, XSSFCellStyle cellStyle, String cellValue) {
		Row row = sheet.getRow(rowIndex);
		if (row == null) {
			row = sheet.createRow(rowIndex);
		}

		Cell cell = row.getCell(colIndex);
		if (cell == null) {
			cell = CellUtil.createCell(row, colIndex, "");
		}
		cell.setCellStyle(cellStyle);
		cell.setCellValue(cellValue);
	}

	/**
	 * 
	 * @param rows
	 */
	public void addRow(List<String> rows) {
		addRow(null, (short) 0, rows);
	}

	public void addRow(String backgroundColor, List<String> rows) {
		addRow(backgroundColor, (short) 0, rows);
	}

	/**
	 * �ش� Sheet�� �������� ���ο� Row�� �߰��Ѵ�. �Ű������� �־��� backgroundColor, boldWeight�� �̿��Ͽ� ���ο�
	 * CellStyle�� �����Ѵ�.
	 * 
	 * @param backgroundColor
	 * @param boldweight
	 * @param cellStrings
	 */
	public void addRow(String backgroundColor, short boldweight, List<String> cellStrings) {
		Row header = sheet.createRow(rowIndex++);
		int cellIndex = colIndex;
		for (String value : cellStrings) {
			Cell cell = CellUtil.createCell(header, cellIndex++, "");
			cell.setCellValue(value);
			cell.setCellStyle(createCellStyle(backgroundColor, boldweight == 0 ? false : true));
		}
		if (maxCols < cellIndex) {
			maxCols = cellIndex;
		}
	}

	/**
	 * �ش� Sheet�� �������� ���ο� Row�� �߰��Ѵ�. �Ű������� �־��� style, cellStrings�� ���� �� Cell�� Style,
	 * Value�� �����Ѵ�.
	 * 
	 * @param style
	 * @param cellStrings
	 */
	public void addRow(List<Map<String, String>> style, List<String> cellStrings) {
		XSSFRow header = sheet.createRow(rowIndex++);
		int cellIndex = colIndex;
		for (String value : cellStrings) {
			int index = cellIndex - colIndex;
			Cell cell = CellUtil.createCell(header, cellIndex++, "");
			cell.setCellValue(value);
			String backgroundColor = null;
			short boldweight = 0;
			if (style.size() > index) {
				Map<String, String> styleMap = style.get(index);
				backgroundColor = styleMap.get("backgroundColor");
				if (styleMap.containsKey("boldweight")) {
					boldweight = Short.parseShort(styleMap.get("boldweight"));
				}
			}
			cell.setCellStyle(createCellStyle(backgroundColor, boldweight == 0 ? false : true));
		}
		if (maxCols < cellIndex) {
			maxCols = cellIndex;
		}
	}

	/**
	 * ���ο� CellStyle�� �����Ѵ�.
	 * 
	 * @param backgroundColor
	 * @param boldweight
	 * @return
	 */
	public XSSFCellStyle createCellStyle(String backgroundColor, boolean isBold) {
		XSSFCellStyle cellStyle = sheet.getWorkbook().createCellStyle();
		cellStyle.setAlignment(HorizontalAlignment.CENTER);
		cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
		if (backgroundColor != null) {
			cellStyle.setFillForegroundColor(IndexedColors.valueOf(backgroundColor).getIndex());
			cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		}
		setSolidBorder(cellStyle);

		if (isBold) {
			Font headerFont = this.sheet.getWorkbook().createFont();
			headerFont.setBold(isBold);
			cellStyle.setFont(headerFont);
		}
		return cellStyle;
	}

	/**
	 * FontHeight�� �����Ѵ�.
	 * 
	 * @param cellnum
	 * @param rownum
	 * @param height
	 */
	public void setFontHeight(int cellnum, int rownum, int height) {
		sheet.getRow(rownum).getCell(cellnum).getCellStyle().getFont().setFontHeight(height);
	}

	/**
	 * Cell�� ���Ĺ���� �����Ѵ�.
	 * 
	 * @param cellnum
	 * @param rownum
	 * @param align
	 */
	public void setCellAlignment(int cellnum, int rownum, HorizontalAlignment align) {
		sheet.getRow(rownum).getCell(cellnum).getCellStyle().setAlignment(align);
	}

	/**
	 * �� ���� �ؽ�Ʈ ������ �����Ѵ�.
	 * 
	 * @param cellnum
	 * @param rownum
	 * @param b
	 */
	public void setCellWrapText(int cellnum, int rownum, boolean b) {
		XSSFRow row = sheet.getRow(rownum);
		XSSFCellStyle rowStyle = row.getRowStyle();
		if (rowStyle == null) {
			XSSFCellStyle cellStyle = sheet.getWorkbook().createCellStyle();
			cellStyle.setWrapText(b);
			row.setRowStyle(cellStyle);
		} else {
			rowStyle.setWrapText(b);
		}
		row.getCell(cellnum).getCellStyle().setWrapText(b);
	}

	// hex to byte[]
	public byte[] hexToByteArray(String hex) {
		if (hex == null || hex.length() == 0) {
			return null;
		}

		byte[] ba = new byte[hex.length() / 2];
		for (int i = 0; i < ba.length; i++) {
			ba[i] = (byte) Integer.parseInt(hex.substring(2 * i, 2 * i + 2), 16);
		}
		return ba;
	}

	/**
	 * CellStyle�� �׵θ��� ������ �Ǽ����� �����Ѵ�.
	 * 
	 * @param cellStyle
	 */
	public void setSolidBorder(XSSFCellStyle cellStyle) {
		cellStyle.setTopBorderColor(IndexedColors.BLACK.getIndex());
		cellStyle.setBottomBorderColor(IndexedColors.BLACK.getIndex());
		cellStyle.setLeftBorderColor(IndexedColors.BLACK.getIndex());
		cellStyle.setRightBorderColor(IndexedColors.BLACK.getIndex());
		cellStyle.setBorderTop(BorderStyle.THIN);
		cellStyle.setBorderBottom(BorderStyle.THIN);
		cellStyle.setBorderLeft(BorderStyle.THIN);
		cellStyle.setBorderRight(BorderStyle.THIN);
	}

	/**
	 * ���������� �ۼ��Ѵ�.
	 * 
	 * @param outputStream
	 * @throws IOException
	 */
	public void write(OutputStream outputStream) throws IOException {
		// adjust column width to fit the content
		for (int i = 0; i < maxCols; i++) {
			sheet.autoSizeColumn(i);
			sheet.setColumnWidth(i, sheet.getColumnWidth(i) + 1500);
		}
		for (int i = 0; i < colIndex; i++) {
			setColWidth(i, 900);
		}
		this.sheet.getWorkbook().write(outputStream);
		this.workbook.close();
	}

	/**
	 * ���� �����Ѵ�.
	 * 
	 * @param sCol ������ ������ ���� Column Index
	 * @param sRow ������ ������ ���� Row Index
	 * @param eCol ������ ������ �� Column Index
	 * @param eRow ������ ������ �� Row Index
	 */
	public void merge(int sCol, int sRow, int eCol, int eRow) {
		sheet.addMergedRegion(new CellRangeAddress(sRow, eRow, sCol, eCol));
	}

	/**
	 * ���� �����ϰ�, �ش� ���� CellStyle�� �����Ѵ�.
	 * 
	 * @param sCol      ������ ������ ���� Column Index
	 * @param sRow      ������ ������ ���� Row Index
	 * @param eCol      ������ ������ �� Column Index
	 * @param eRow      ������ ������ �� Row Index
	 * @param cellStyle
	 * @param cellValue
	 */
	public void merge(int sCol, int sRow, int eCol, int eRow, XSSFCellStyle cellStyle, String cellValue) {
		sheet.addMergedRegion(new CellRangeAddress(sRow, eRow, sCol, eCol));
		for (int i = sCol; i <= eCol; i++) {
			for (int j = sRow; j <= eRow; j++) {
				Row row = sheet.getRow(j);
				if (row == null) {
					row = sheet.createRow(j);
				}

				Cell cell = row.getCell(i);
				if (cell == null) {
					cell = CellUtil.createCell(row, i, "");
				}
				cell.setCellStyle(cellStyle);
				cell.setCellValue(cellValue);
			}
		}
	}

	/**
	 * �簢���� �׸���.
	 * 
	 * @param rownum
	 * @param scolnum
	 * @param ecolnum
	 * @param dx1
	 * @param dx2
	 */
	public void drawRect(int rownum, int scolnum, int ecolnum, int dx1, int dx2) {
		XSSFDrawing patriarch = sheet.createDrawingPatriarch();
		XSSFClientAnchor a = new XSSFClientAnchor();
		a.setCol1(scolnum);
		a.setRow1(rownum);
		a.setDx1(pxToEmu(dx1));
		a.setDy1(pxToEmu(5));
		a.setDx2(pxToEmu(dx2));
		a.setDy2(pxToEmu(-5));
		a.setRow2(rownum + 1);
		a.setCol2(ecolnum);

		XSSFSimpleShape shape1 = patriarch.createSimpleShape(a);
		shape1.setShapeType(ShapeTypes.RECT);
		int red = 0, green = 0, blue = 0;
		red = Integer.parseInt("f0", 16);
		green = Integer.parseInt("ad", 16);
		blue = Integer.parseInt("4e", 16);
		shape1.setLineStyleColor(red, green, blue);
		shape1.setFillColor(red, green, blue);
	}

	public static int pxToEmu(int px) {
		return (int) Math.round(((double) px) * 72 * 20 * 635 / 96); // assume 96dpi
	}

	public static int emuToPx(int emu) {
		return (int) Math.round(((double) emu) * 96 / 72 / 20 / 635); // assume 96dpi
	}

	public float getDefaultRowHeightInPoints() {
		return this.sheet.getDefaultRowHeightInPoints();
	}

	public void setRowHeightInPoints(int rownum, float height) {
		sheet.getRow(rownum).setHeightInPoints(height);
	}

	public float getRowHeightInPoints(int rownum) {
		return sheet.getRow(rownum).getHeightInPoints();
	}

	/**
	 * ROW ���� �ڵ� ����
	 * 
	 * @param rownum
	 * @param cellValue
	 */
	public void setAutoRowFit(int cellnum, int rownum) {
		XSSFRow row = sheet.getRow(rownum);
		XSSFCell cell = row.getCell(cellnum);
		XSSFFont cellFont = cell.getCellStyle().getFont();
		int fontStyle = java.awt.Font.PLAIN;
		if (cellFont.getBold())
			fontStyle = java.awt.Font.BOLD;
		if (cellFont.getItalic())
			fontStyle = java.awt.Font.ITALIC;

		java.awt.Font currFont = new java.awt.Font(cellFont.getFontName(), fontStyle, cellFont.getFontHeightInPoints());

		String cellText = cell.getStringCellValue();
		AttributedString attrStr = new AttributedString(cellText);
		attrStr.addAttribute(TextAttribute.FONT, currFont);

		// Use LineBreakMeasurer to count number of lines needed for the text
		//
		FontRenderContext frc = new FontRenderContext(null, true, true);
		LineBreakMeasurer measurer = new LineBreakMeasurer(attrStr.getIterator(), frc);
		int nextPos = 0;
		int lineCnt = 1;
		float columnWidthInPx = sheet.getColumnWidthInPixels(cellnum);
		while (measurer.getPosition() < cellText.length()) {
			nextPos = measurer.nextOffset(columnWidthInPx);
			lineCnt++;
			measurer.setPosition(nextPos);
		}
		int fromIndex = -1;
		while ((fromIndex = cellText.indexOf("\n", fromIndex + 1)) >= 0) {
			lineCnt++;
		}
		if (lineCnt > 1) {
			row.setHeightInPoints(sheet.getDefaultRowHeightInPoints() * lineCnt * /* fudge factor */ 1.1f);
		}
	}

	public static List<List<String>> readExcel(File file) throws IOException, InvalidFormatException {
		return readExcel(new FileInputStream(file), file.getName(), 0);
	}

	public static List<List<String>> readExcel(File file, int sheetAt) throws IOException, InvalidFormatException {
		return readExcel(new FileInputStream(file), file.getName(), sheetAt);
	}

	public static List<List<String>> readExcel(InputStream is) throws IOException, InvalidFormatException {
		return readExcel(is, "xlsx", 0);
	}

	public static Workbook getWorkbook(InputStream inputStream, String fileName) throws IOException {
		Workbook workbook = null;

		if (fileName.endsWith("xlsx")) {
			workbook = new XSSFWorkbook(inputStream);
		} else if (fileName.endsWith("xls")) {
			workbook = new HSSFWorkbook(inputStream);
		} else {
			throw new IllegalArgumentException("The specified file is not Excel file");
		}

		return workbook;
	}

	public static List<List<String>> readExcel(InputStream is, String fileName, int sheetAt)
			throws IOException, InvalidFormatException {
		List<List<String>> resultList = new ArrayList<>();
		// ������ �б����� ���������� �����´�
		Workbook workbook = getWorkbook(is, fileName);
		int rowindex = 0;
		int columnindex = 0;
		// ��Ʈ �� (ù��°���� �����ϹǷ� 0�� �ش�)
		// ���� �� ��Ʈ�� �б����ؼ��� FOR���� �ѹ��� �����ش�
		Sheet sheet = workbook.getSheetAt(sheetAt);
		// ���� ��
		int rows = sheet.getPhysicalNumberOfRows();
		for (rowindex = 0; rowindex < rows; rowindex++) {
			// ���� �д´�
			Row row = sheet.getRow(rowindex);
			resultList.add(new ArrayList<String>());
			if (row != null) {
				// ���� ��
				int cells = row.getPhysicalNumberOfCells();
				for (columnindex = 0; columnindex <= cells; columnindex++) {
					// ������ �д´�
					Cell cell = row.getCell(columnindex);
					String value = "";
					// ���� ���ϰ�츦 ���� ��üũ
					if (rowindex == 0 && cell == null) {
						continue;
					}
					if (cell != null) {
						// Ÿ�Ժ��� ���� �б�
						switch (cell.getCellTypeEnum()) {
						case FORMULA:
							value = cell.getCellFormula();
							break;
						case NUMERIC:
							value = String.format("%1$,.0f", cell.getNumericCellValue());
							break;
						case STRING:
							value = cell.getStringCellValue() + "";
							break;
						case BLANK:
							value = cell.getBooleanCellValue() + "";
							break;
						case ERROR:
							value = cell.getErrorCellValue() + "";
							break;
						case BOOLEAN:
							value = cell.getBooleanCellValue() + "";
							break;
						case _NONE:
							break;
						default:
							break;
						}
					}
					if ("false".equals(value)) {
						value = "";
					}
					resultList.get(rowindex).add(value);
				}
			}
		}
		workbook.close();
		return resultList;
	}
}
