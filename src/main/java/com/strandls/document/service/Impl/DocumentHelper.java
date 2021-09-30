/**
 * 
 */
package com.strandls.document.service.Impl;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Row.MissingCellPolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.strandls.document.dao.BibTexItemTypeDao;
import com.strandls.document.pojo.BibFieldsData;
import com.strandls.document.pojo.Document;
import com.strandls.document.util.PropertyFileUtil;
import com.strandls.resource.pojo.UFile;

/**
 * @author Abhishek Rudra
 *
 */
public class DocumentHelper {

	private final Logger logger = LoggerFactory.getLogger(DocumentHelper.class);

	@Inject
	private BibTexItemTypeDao bibTexItemTypeDao;

	private Long defaultLanguageId = Long
			.parseLong(PropertyFileUtil.fetchProperty("config.properties", "defaultLanguageId"));

	@SuppressWarnings("deprecation")
	public Document bulkUploadPayload(Row dataRow, Map<String, Integer> fieldMapping, Long authorId, UFile ufile) {

		Map<String, String> itemTypeMap = new HashMap<String, String>();
		itemTypeMap.put("article", "Article");
		itemTypeMap.put("book", "Book");
		itemTypeMap.put("inbook", "Inbook");
		itemTypeMap.put("mastersthesis", "Mastersthesis");
		itemTypeMap.put("misc", "Miscellaneous");
		itemTypeMap.put("phdthesis", "Phdthesis");
		itemTypeMap.put("proceedings", "Proceedings");
		itemTypeMap.put("techreport", "Techreport");
		itemTypeMap.put("unpublished", "Unpublished");

		try {
			String authors = null;
			Cell authorCell = dataRow.getCell(fieldMapping.get("author"), MissingCellPolicy.RETURN_BLANK_AS_NULL);
			if (authorCell != null) {
				authorCell.setCellType(CellType.STRING);
				authors = authorCell.getStringCellValue();
			}
			String coAuthor = null;
			Cell coAuthorCell = dataRow.getCell(fieldMapping.get("coAuthor"), MissingCellPolicy.RETURN_BLANK_AS_NULL);
			if (coAuthorCell != null) {
				coAuthorCell.setCellType(CellType.STRING);
				coAuthor = coAuthorCell.getStringCellValue();
			}

			if (coAuthor != null && !coAuthor.isEmpty())
				authors = authors + "," + coAuthor;

			Document document = new Document();

			String attribution = null;
			if (fieldMapping.containsKey("attribution")) {
				Cell cell = dataRow.getCell(fieldMapping.get("attribution"), MissingCellPolicy.RETURN_BLANK_AS_NULL);
				if (cell != null) {
					cell.setCellType(CellType.STRING);
					attribution = cell.getStringCellValue();
				}
			}

			String contributors = null;
			if (fieldMapping.containsKey("contributors")) {
				Cell cell = dataRow.getCell(fieldMapping.get("contributors"), MissingCellPolicy.RETURN_BLANK_AS_NULL);
				if (cell != null) {
					cell.setCellType(CellType.STRING);
					contributors = cell.getStringCellValue();
				}
			}

			String notes = null;
			if (fieldMapping.containsKey("notes")) {
				Cell cell = dataRow.getCell(fieldMapping.get("notes"), MissingCellPolicy.RETURN_BLANK_AS_NULL);
				if (cell != null) {
					cell.setCellType(CellType.STRING);
					notes = cell.getStringCellValue();
				}
			}

			String doi = null;
			if (fieldMapping.containsKey("doi")) {
				Cell cell = dataRow.getCell(fieldMapping.get("doi"), MissingCellPolicy.RETURN_BLANK_AS_NULL);
				if (cell != null) {
					cell.setCellType(CellType.STRING);
					doi = cell.getStringCellValue();
				}
			}

			String title = null;
			if (fieldMapping.containsKey("title")) {
				Cell cell = dataRow.getCell(fieldMapping.get("title"), MissingCellPolicy.RETURN_BLANK_AS_NULL);
				if (cell != null) {
					cell.setCellType(CellType.STRING);
					title = cell.getStringCellValue();
				}
				if (cell == null)
					return null;
			}

			String type = null;
			if (fieldMapping.containsKey("type")) {
				Cell cell = dataRow.getCell(fieldMapping.get("type"), MissingCellPolicy.RETURN_BLANK_AS_NULL);
				if (cell != null) {
					cell.setCellType(CellType.STRING);
					type = cell.getStringCellValue();
				}
			}

			String rating = null;
			if (fieldMapping.containsKey("rating")) {
				Cell cell = dataRow.getCell(fieldMapping.get("rating"), MissingCellPolicy.RETURN_BLANK_AS_NULL);
				if (cell != null) {
					cell.setCellType(CellType.STRING);
					rating = cell.getStringCellValue();

				}
			}

			String journal = null;
			if (fieldMapping.containsKey("journal")) {
				Cell cell = dataRow.getCell(fieldMapping.get("journal"), MissingCellPolicy.RETURN_BLANK_AS_NULL);
				if (cell != null) {
					cell.setCellType(CellType.STRING);
					journal = cell.getStringCellValue();
				}
			}

			String bookTitle = null;
			if (fieldMapping.containsKey("bookTitle")) {
				Cell cell = dataRow.getCell(fieldMapping.get("bookTitle"), MissingCellPolicy.RETURN_BLANK_AS_NULL);
				if (cell != null) {
					cell.setCellType(CellType.STRING);
					bookTitle = cell.getStringCellValue();
				}
			}

			String year = null;
			if (fieldMapping.containsKey("year")) {
				Cell cell = dataRow.getCell(fieldMapping.get("year"), MissingCellPolicy.RETURN_BLANK_AS_NULL);
				if (cell != null) {
					cell.setCellType(CellType.STRING);
					year = cell.getStringCellValue();
					year = year.replace(".0", "");
				}
			}

			String month = null;
			if (fieldMapping.containsKey("month")) {
				Cell cell = dataRow.getCell(fieldMapping.get("month"), MissingCellPolicy.RETURN_BLANK_AS_NULL);
				if (cell != null) {
					cell.setCellType(CellType.STRING);
					month = cell.getStringCellValue();
				}
			}

			String volume = null;
			if (fieldMapping.containsKey("volume")) {
				Cell cell = dataRow.getCell(fieldMapping.get("volume"), MissingCellPolicy.RETURN_BLANK_AS_NULL);
				if (cell != null) {
					cell.setCellType(CellType.STRING);
					volume = cell.getStringCellValue();
					volume = volume.replace(".0", "");
				}
			}

			String number = null;
			if (fieldMapping.containsKey("number")) {
				Cell cell = dataRow.getCell(fieldMapping.get("number"), MissingCellPolicy.RETURN_BLANK_AS_NULL);
				if (cell != null) {
					cell.setCellType(CellType.STRING);
					number = cell.getStringCellValue();
					number = number.replace(".0", "");
				}
			}

			String pages = null;
			if (fieldMapping.containsKey("pages")) {
				Cell cell = dataRow.getCell(fieldMapping.get("pages"), MissingCellPolicy.RETURN_BLANK_AS_NULL);
				if (cell != null) {
					cell.setCellType(CellType.STRING);
					pages = cell.getStringCellValue();
					pages = pages.replace(".0", "");
				}
			}

			String publisher = null;
			if (fieldMapping.containsKey("publisher")) {
				Cell cell = dataRow.getCell(fieldMapping.get("publisher"), MissingCellPolicy.RETURN_BLANK_AS_NULL);
				if (cell != null) {
					cell.setCellType(CellType.STRING);
					publisher = cell.getStringCellValue();
				}
			}

			String school = null;
			if (fieldMapping.containsKey("school")) {
				Cell cell = dataRow.getCell(fieldMapping.get("school"), MissingCellPolicy.RETURN_BLANK_AS_NULL);
				if (cell != null) {
					cell.setCellType(CellType.STRING);
					school = cell.getStringCellValue();
				}
			}

			String edition = null;
			if (fieldMapping.containsKey("edition")) {
				Cell cell = dataRow.getCell(fieldMapping.get("edition"), MissingCellPolicy.RETURN_BLANK_AS_NULL);
				if (cell != null) {
					cell.setCellType(CellType.STRING);
					edition = cell.getStringCellValue();
					edition = edition.replace(".0", "");
				}
			}

			String series = null;
			if (fieldMapping.containsKey("series")) {
				Cell cell = dataRow.getCell(fieldMapping.get("series"), MissingCellPolicy.RETURN_BLANK_AS_NULL);
				if (cell != null) {
					cell.setCellType(CellType.STRING);
					series = cell.getStringCellValue();
					series = series.replace(".0", "");
				}
			}

			String address = null;
			if (fieldMapping.containsKey("address")) {
				Cell cell = dataRow.getCell(fieldMapping.get("address"), MissingCellPolicy.RETURN_BLANK_AS_NULL);
				if (cell != null) {
					cell.setCellType(CellType.STRING);
					address = cell.getStringCellValue();
				}
			}

			String chapter = null;
			if (fieldMapping.containsKey("chapter")) {
				Cell cell = dataRow.getCell(fieldMapping.get("chapter"), MissingCellPolicy.RETURN_BLANK_AS_NULL);
				if (cell != null) {
					cell.setCellType(CellType.STRING);
					chapter = cell.getStringCellValue();
					chapter = chapter.replace(".0", "");
				}
			}

			String note = null;
			if (fieldMapping.containsKey("note")) {
				Cell cell = dataRow.getCell(fieldMapping.get("note"), MissingCellPolicy.RETURN_BLANK_AS_NULL);
				if (cell != null) {
					cell.setCellType(CellType.STRING);
					note = cell.getStringCellValue();
				}
			}

			String editor = null;
			if (fieldMapping.containsKey("editor")) {
				Cell cell = dataRow.getCell(fieldMapping.get("editor"), MissingCellPolicy.RETURN_BLANK_AS_NULL);
				if (cell != null) {
					cell.setCellType(CellType.STRING);
					editor = cell.getStringCellValue();
				}
			}

			String organization = null;
			if (fieldMapping.containsKey("organization")) {
				Cell cell = dataRow.getCell(fieldMapping.get("organization"), MissingCellPolicy.RETURN_BLANK_AS_NULL);
				if (cell != null) {
					cell.setCellType(CellType.STRING);
					organization = cell.getStringCellValue();
				}
			}

			String howPublished = null;
			if (fieldMapping.containsKey("howPublished")) {
				Cell cell = dataRow.getCell(fieldMapping.get("howPublished"), MissingCellPolicy.RETURN_BLANK_AS_NULL);
				if (cell != null) {
					cell.setCellType(CellType.STRING);
					howPublished = cell.getStringCellValue();
				}
			}

			String institution = null;
			if (fieldMapping.containsKey("institution")) {
				Cell cell = dataRow.getCell(fieldMapping.get("institution"), MissingCellPolicy.RETURN_BLANK_AS_NULL);
				if (cell != null) {
					cell.setCellType(CellType.STRING);
					institution = cell.getStringCellValue();
				}
			}

			String url = null;
			if (fieldMapping.containsKey("url")) {
				Cell cell = dataRow.getCell(fieldMapping.get("url"), MissingCellPolicy.RETURN_BLANK_AS_NULL);
				if (cell != null) {
					cell.setCellType(CellType.STRING);
					url = cell.getStringCellValue();
				}
			}

			String language = null;
			if (fieldMapping.containsKey("language")) {
				Cell cell = dataRow.getCell(fieldMapping.get("language"), MissingCellPolicy.RETURN_BLANK_AS_NULL);
				if (cell != null) {
					cell.setCellType(CellType.STRING);
					language = cell.getStringCellValue();
				}
			}

			String file = null;
			if (fieldMapping.containsKey("file")) {
				Cell cell = dataRow.getCell(fieldMapping.get("file"), MissingCellPolicy.RETURN_BLANK_AS_NULL);
				if (cell != null) {
					cell.setCellType(CellType.STRING);
					file = cell.getStringCellValue();
				}
			}

			String itemtype = null;
			if (fieldMapping.containsKey("itemtype")) {
				Cell cell = dataRow.getCell(fieldMapping.get("itemtype"), MissingCellPolicy.RETURN_BLANK_AS_NULL);
				if (cell != null) {
					cell.setCellType(CellType.STRING);
					itemtype = cell.getStringCellValue();

					itemtype = itemTypeMap.get(itemtype);
				}
				if (cell == null)
					return null;
			}

			String isbn = null;
			if (fieldMapping.containsKey("isbn")) {
				Cell cell = dataRow.getCell(fieldMapping.get("isbn"), MissingCellPolicy.RETURN_BLANK_AS_NULL);
				if (cell != null) {
					cell.setCellType(CellType.STRING);
					isbn = cell.getStringCellValue();
				}
			}

			String extra = null;
			if (fieldMapping.containsKey("extra")) {
				Cell cell = dataRow.getCell(fieldMapping.get("extra"), MissingCellPolicy.RETURN_BLANK_AS_NULL);
				if (cell != null) {
					cell.setCellType(CellType.STRING);
					extra = cell.getStringCellValue();
				}
			}

			document = new Document(null, 0L, true, attribution, authorId, contributors, null, new Date(), notes, doi,
					new Date(), Long.parseLong(fieldMapping.get("licenseId").toString()), null, null, title, type,
					(ufile != null ? ufile.getId() : null), null, null, null, null, null, null, null, null, null,
					new Date(), null, 0, 0, defaultLanguageId, null, null, null, null, null, null, null, null, null, 1,
					(rating != null) ? Integer.parseInt(rating) : 0, false, null, null, authors, journal, bookTitle,
					year, month, volume, number, pages, publisher, school, edition, series, address, chapter, note,
					editor, organization, howPublished, institution, url, language, file, itemtype, isbn, extra);

			return document;
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return null;

	}

	public BibFieldsData convertDocumentToBibField(Document document) {
		BibFieldsData bibField = new BibFieldsData(document.getAuthor(), document.getJournal(), document.getBookTitle(),
				document.getTitle(), document.getYear(), document.getMonth(), document.getVolume(),
				document.getNumber(), document.getPages(), document.getPublisher(), document.getSchool(),
				document.getEdition(), document.getSeries(), document.getAddress(), document.getChapter(),
				document.getNote(), document.getType(), document.getEditor(), document.getOrganization(),
				document.getHowPublished(), document.getInstitution(), document.getNotes(), document.getDoi(),
				document.getUrl(), document.getLanguage(), document.getFile(), document.getItemtype(), null,
				document.getIsbn(), document.getExtra());

		bibField.setItemTypeId(bibTexItemTypeDao.findByName(bibField.getItemtype()).getId());

		return bibField;

	}

}
