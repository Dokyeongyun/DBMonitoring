package root.repository.implement;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import root.core.domain.ASMDiskUsage;
import root.core.domain.ArchiveUsage;
import root.core.domain.OSDiskUsage;
import root.core.domain.TableSpaceUsage;
import root.core.repository.constracts.ReportRepository;
import root.utils.DateUtils;

public class ReportRepositoryImplTest {

	public static String rootDirectory = "./report";
	public static ReportRepository repo;
	Date now = new Date();
	String monitoringDate = DateUtils.format(now, "yyyyMMdd");
	String monitoringTime = DateUtils.format(now, "HHmmss");

	@BeforeAll
	public static void setUp() {
		repo = ReportFileRepo.getInstance();
	}

	@BeforeEach
	public void before() {
		deleteDirectory(new File(rootDirectory));
	}

	@AfterEach
	public void after() {
		deleteDirectory(new File(rootDirectory));
	}

	@Test
	public void testWriteReportFile_ArchiveUsageObj() {
		// Arrange
		String fileName = "testDB";
		String fileExtension = ".txt";
		List<ArchiveUsage> monitoringResult = List.of(new ArchiveUsage(monitoringDate, monitoringTime, "+RECO", 110,
				1.073741824E12, 3.30987208704E11, 5.67697997824E11, 53.0, "2022-02-09, 01:00:28"));
		Class<ArchiveUsage> clazz = ArchiveUsage.class;

		// Act
		repo.writeReportFile(fileName, fileExtension, monitoringResult, clazz);

		// Assert
		File resultFile = new File(rootDirectory + "/ArchiveUsage/testDB.txt");
		assertTrue(resultFile.exists());
	}

	@Test
	public void testWriteReportFile_ArchiveUsageObj_WhenResultIsNull() {
		// Arrange
		String fileName = "testDB";
		String fileExtension = ".txt";
		List<ArchiveUsage> monitoringResult = null;
		Class<ArchiveUsage> clazz = ArchiveUsage.class;

		// Act
		repo.writeReportFile(fileName, fileExtension, monitoringResult, clazz);

		// Assert
		File resultFile = new File(rootDirectory + "/ArchiveUsage/testDB.txt");
		assertFalse(resultFile.exists());
	}

	@Test
	public void testWriteReportFile_ArchiveUsageObj_WhenResultIsEmpty() {
		// Arrange
		String fileName = "testDB";
		String fileExtension = ".txt";
		List<ArchiveUsage> monitoringResult = new ArrayList<>();
		Class<ArchiveUsage> clazz = ArchiveUsage.class;

		// Act
		repo.writeReportFile(fileName, fileExtension, monitoringResult, clazz);

		// Assert
		File resultFile = new File(rootDirectory + "/ArchiveUsage/testDB.txt");
		assertFalse(resultFile.exists());
	}

	@Test
	public void testWriteReportFile_ArchiveUsageObj_WhenReportFileAlreadyExist() {
		// Arrange
		String fileName = "testDB";
		String fileExtension = ".txt";
		List<ArchiveUsage> monitoringResult = List.of(new ArchiveUsage(monitoringDate, monitoringTime, "+RECO", 110,
				1.073741824E12, 3.30987208704E11, 5.67697997824E11, 53.0, "2022-02-09, 01:00:28"));
		Class<ArchiveUsage> clazz = ArchiveUsage.class;

		repo.writeReportFile(fileName, fileExtension, monitoringResult, clazz);

		// Act
		repo.writeReportFile(fileName, fileExtension, monitoringResult, clazz);

		// Assert
		File resultFile = new File(rootDirectory + "/ArchiveUsage/testDB.txt");
		assertTrue(resultFile.exists());
	}

	@Test
	public void testGetReportHeaders_ArchiveUsageObj() {
		// Arrange
		String fileName = "testDB";
		String fileExtension = ".txt";
		List<ArchiveUsage> monitoringResult = List.of(new ArchiveUsage(monitoringDate, monitoringTime, "+RECO", 110,
				1.073741824E12, 3.30987208704E11, 5.67697997824E11, 53.0, "2022-02-09, 01:00:28"));
		Class<ArchiveUsage> clazz = ArchiveUsage.class;

		repo.writeReportFile(fileName, fileExtension, monitoringResult, clazz);

		// Act
		List<String> result = repo.getReportHeaders(clazz, fileName);

		// Assert
		List<String> expected = new ArrayList<>(Arrays.asList("archiveName", "numberOfFiles", "totalSpace",
				"reclaimableSpace", "usedSpace", "usedPercent", "dnt", "monitoringDate", "monitoringTime"));
		assertEquals(expected, result);
	}

	@Test
	public void testGetReportContentsInCsv_ArchiveUsageObj() {
		// Arrange
		String fileName = "testDB";
		String fileExtension = ".txt";
		List<ArchiveUsage> monitoringResult = List.of(new ArchiveUsage(monitoringDate, monitoringTime, "+RECO", 110,
				1.073741824E12, 3.30987208704E11, 5.67697997824E11, 53.0, "2022-02-09, 01:00:28"));
		Class<ArchiveUsage> clazz = ArchiveUsage.class;

		repo.writeReportFile(fileName, fileExtension, monitoringResult, clazz);

		// Act
		String result = repo.getReportContentsInCsv(clazz, fileName);

		// Assert
		String expected = String.format(
				"\"+RECO\",\"110\",\"1.073741824E12\",\"3.30987208704E11\",\"5.67697997824E11\",\"53.0\",\"2022-02-09, 01:00:28\",\"%s\",\"%s\"\r\n",
				monitoringDate, monitoringTime);
		assertEquals(expected, result);
	}

	@Test
	public void testWriteReportFile_TableSpaceUsageObj() {
		// Arrange
		String fileName = "testDB";
		String fileExtension = ".txt";
		List<TableSpaceUsage> monitoringResult = List.of(
				new TableSpaceUsage(monitoringDate, monitoringTime, "TBS1", 17.67, 16.83, 95, .84),
				new TableSpaceUsage(monitoringDate, monitoringTime, "TBS2", 3.2, 2.9, 91, .3),
				new TableSpaceUsage(monitoringDate, monitoringTime, "TBS3", 1080, 960.09, 89, 119.91),
				new TableSpaceUsage(monitoringDate, monitoringTime, "TBS4", 2130, 1719.55, 81, 410.45));
		Class<TableSpaceUsage> clazz = TableSpaceUsage.class;

		// Act
		repo.writeReportFile(fileName, fileExtension, monitoringResult, clazz);

		// Assert
		File resultFile = new File(rootDirectory + "/TableSpaceUsage/testDB.txt");
		assertTrue(resultFile.exists());
	}

	@Test
	public void testWriteReportFile_ASMDiskUsageObj() {
		// Arrange
		String fileName = "testDB";
		String fileExtension = ".txt";
		List<ASMDiskUsage> monitoringResult = List.of(
				new ASMDiskUsage(monitoringDate, monitoringTime, "DATA", "NORMAL", 1.280302907392E13, 4883968.0,
						3.54643083264E11, 4.766568546304E12, 93.08, "WARNING"),
				new ASMDiskUsage(monitoringDate, monitoringTime, "RECO", "NORMAL", 3.19975063552E12, 1220608.0,
						7.1829553152E11, 5.61604722688E11, 43.88, "GOOD"));
		Class<ASMDiskUsage> clazz = ASMDiskUsage.class;

		// Act
		repo.writeReportFile(fileName, fileExtension, monitoringResult, clazz);

		// Assert
		File resultFile = new File(rootDirectory + "/ASMDiskUsage/testDB.txt");
		assertTrue(resultFile.exists());
	}

	@Test
	public void testWriteReportFile_OSDiskUsageObj() {
		// Arrange
		String fileName = "testDB";
		String fileExtension = ".txt";
		List<OSDiskUsage> monitoringResult = List.of(
				new OSDiskUsage(monitoringDate, monitoringTime, "/fileSystem", "/mounted", 16.83, 95, .84, 95),
				new OSDiskUsage(monitoringDate, monitoringTime, "/fileSystem2", "/mounted2", 3.2, 2.9, 91, .3),
				new OSDiskUsage(monitoringDate, monitoringTime, "/fileSystem3", "/mounted3", 1080, 960.09, 89, 119.91),
				new OSDiskUsage(monitoringDate, monitoringTime, "/fileSystem4", "/mounted4", 2130, 171.55, 81, 410.45));
		Class<OSDiskUsage> clazz = OSDiskUsage.class;

		// Act
		repo.writeReportFile(fileName, fileExtension, monitoringResult, clazz);

		// Assert
		File resultFile = new File(rootDirectory + "/OSDiskUsage/testDB.txt");
		assertTrue(resultFile.exists());
	}

	private boolean deleteDirectory(File directoryToBeDeleted) {
		File[] allContents = directoryToBeDeleted.listFiles();
		if (allContents != null) {
			for (File file : allContents) {
				deleteDirectory(file);
			}
		}
		return directoryToBeDeleted.delete();
	}
}
