package Root.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Root.Database.DatabaseUtil;

@SuppressWarnings("rawtypes")
public class DBCheckRepositoryImpl implements DBCheckRepository {
	private DatabaseUtil db;

	public DBCheckRepositoryImpl(DatabaseUtil db) {
		this.db = db;
	}
	
	@Override
	public String getDBName() {
		return db.getJdbcConnectionInfo().getJdbcDBName();
	}

	@Override
	public Connection getTran() {
		return db.getConn();
	}

	@Override
	public void endTran(Object conn) {
		if (conn != null)
			db.freeConn((Connection) conn);
	}

	@Override
	public List<Map> checkArchiveUsage() {

		List<Map> result = new ArrayList<>();
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT NAME, ROUND(SPACE_USED/SPACE_LIMIT*100) USED_RATE, ");
		sb.append("ROUND(SPACE_LIMIT/1024/1024/1024) AS SPACE_LIMIT_GB, ");
		sb.append("ROUND(SPACE_USED/1024/1024/1024) AS SPACE_USED_GB, ");
		sb.append("ROUND(SPACE_RECLAIMABLE/1024/1024/1024) AS SPACE_RECLAIMABLE_GB, ");
		sb.append("NUMBER_OF_FILES, ");
		sb.append("TO_CHAR(SYSDATE, 'YYYY-MM-DD HH24:MI:SS') AS DNT ");
		sb.append("FROM V$RECOVERY_FILE_DEST ");
				
		try {
			conn = this.getTran();
			pstmt = conn.prepareStatement(sb.toString());
			rs = pstmt.executeQuery();
			
			while(rs.next()) {
	            String name = rs.getString("NAME");
	            String usedRate = rs.getString("USED_RATE");
	            String spaceLimitGb = rs.getString("SPACE_LIMIT_GB");
	            String spaceUsedGb = rs.getString("SPACE_USED_GB");
	            String spaceReclaimableGb = rs.getString("SPACE_RECLAIMABLE_GB");
	            String numberOfFiles = rs.getString("NUMBER_OF_FILES");
	            String dnt = rs.getString("DNT");
	            
				Map<String, Object> data = new HashMap<>();
				data.put("NAME", name);
				data.put("USED_RATE", usedRate);
				data.put("SPACE_LIMIT_GB", spaceLimitGb);
				data.put("SPACE_USED_GB", spaceUsedGb);
				data.put("SPACE_RECLAIMABLE_GB", spaceReclaimableGb);
				data.put("NUMBER_OF_FILES", numberOfFiles);
				data.put("DNT", dnt);
				result.add(data);
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			this.endTran(conn);
		}
		
		return result;
	}
	
	@Override
	public List<Map> checkTableSpaceUsage() {

		List<Map> result = new ArrayList<>();
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		StringBuffer sb = new StringBuffer();
		sb.append(" SELECT F.TNAME AS TableSpace, ");
		sb.append(" ROUND(SUM(D.BYTES)/1024/1024/1024, 2) AS \"Total(G)\", ");
		sb.append(" ROUND((SUM(D.BYTES) - SUM(F.BYTES))/1024/1024/1024, 2) AS \"UsedSpace(G)\", ");
		sb.append(" ROUND((SUM(D.BYTES) - SUM(F.BYTES))/SUM(D.BYTES)*100, 0) AS \"Used(%)\", ");
		sb.append(" ROUND(SUM(F.BYTES)/1024/1024/1024, 2) AS \"FreeSpace(G)\" ");
		sb.append("FROM ( ");
		sb.append(" SELECT SUM(BYTES) BYTES, TABLESPACE_NAME TNAME ");
		sb.append(" FROM DBA_FREE_SPACE ");
		sb.append(" GROUP BY TABLESPACE_NAME ");
		sb.append(") F, ( ");
		sb.append(" SELECT SUM(BYTES) BYTES, TABLESPACE_NAME TNAME ");
		sb.append(" FROM DBA_DATA_FILES ");
		sb.append(" GROUP BY TABLESPACE_NAME ");
		sb.append(") D ");
		sb.append("WHERE F.TNAME = D.TNAME ");
		sb.append("GROUP BY F.TNAME ");
		sb.append("ORDER BY 4 DESC ");

        
		try {
			conn = this.getTran();
			pstmt = conn.prepareStatement(sb.toString());
			rs = pstmt.executeQuery();
			
			while(rs.next()) {
	            String tableSpace = rs.getString("TableSpace");
	            String totalGb = rs.getString("Total(G)");
	            String usedSpaceGb = rs.getString("UsedSpace(G)");
	            String usedPercent = rs.getString("Used(%)");
	            String freeSpaceGb = rs.getString("FreeSpace(G)");
	           
				Map<String, Object> data = new HashMap<>();
				data.put("TableSpace", tableSpace);
				data.put("Total(G)", totalGb);
				data.put("UsedSpace(G)", usedSpaceGb);
				data.put("Used(%)", usedPercent);
				data.put("FreeSpace(G)", freeSpaceGb);
				result.add(data);
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			this.endTran(conn);
		}
		
		return result;
	}
	
	@Override
	public List<Map> checkASMDiskUsage() {

		List<Map> result = new ArrayList<>();
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		StringBuffer sb = new StringBuffer();
		sb.append(" SELECT NAME \"Name\", ");
		sb.append(" TYPE \"Type\", ");
		sb.append(" TOTAL_MB \"Tot_RAW(MB)\", ");
		sb.append(" CASE WHEN TYPE='EXTERN' THEN TOTAL_MB ");
		sb.append(" WHEN TYPE='NORMAL' THEN (TOTAL_MB-REQUIRED_MIRROR_FREE_MB)/2 ");
		sb.append(" WHEN TYPE='HIGH' THEN (TOTAL_MB-REQUIRED_MIRROR_FREE_MB)/3 ");
		sb.append(" ELSE 0 END AS \"Tot_Usable(MB)\", ");
		sb.append(" CASE WHEN TYPE='EXTERN' THEN COLD_USED_MB ");
		sb.append(" WHEN TYPE='NORMAL' THEN COLD_USED_MB/2 ");
		sb.append(" WHEN TYPE='HIGH' THEN COLD_USED_MB/3 ");
		sb.append(" ELSE 0 END AS \"Used(MB)\", ");
		sb.append(" USABLE_FILE_MB \"Free(MB)\", ");
		sb.append(" CASE WHEN TYPE = 'EXTERN' THEN (TOTAL_MB-USABLE_FILE_MB)/TOTAL_MB*100 ");
		sb.append(" WHEN TYPE='NORMAL' THEN ((TOTAL_MB-REQUIRED_MIRROR_FREE_MB)/2- ");
		sb.append("USABLE_FILE_MB)/((TOTAL_MB-REQUIRED_MIRROR_FREE_MB)/2)*100 ");
		sb.append(" WHEN TYPE='HIGH' THEN ((TOTAL_MB-REQUIRED_MIRROR_FREE_MB)/3- ");
		sb.append("USABLE_FILE_MB)/((TOTAL_MB-REQUIRED_MIRROR_FREE_MB)/3)*100 ");
		sb.append(" ELSE 0 END AS \"Used(%)\", ");
		sb.append(" 'Used < 90%' \"Standard\", ");
		sb.append(" CASE WHEN TYPE='EXTERN' AND (TOTAL_MB-USABLE_FILE_MB)/TOTAL_MB*100 >= ");
		sb.append("90 THEN '<span style=\"color:red\"><b> WARNING</b></span>' ");
		sb.append(" WHEN TYPE='NORMAL' AND ((TOTAL_MB-REQUIRED_MIRROR_FREE_MB)/2- ");
		sb.append("USABLE_FILE_MB)/((TOTAL_MB-REQUIRED_MIRROR_FREE_MB)/2)*100 >= 90 ");
		sb.append(" THEN '<span style=\"color:red\"><b> WARNING</b></span>' ");
		sb.append(" WHEN TYPE='HIGH' AND ((TOTAL_MB-REQUIRED_MIRROR_FREE_MB)/3- ");
		sb.append("USABLE_FILE_MB)/((TOTAL_MB-REQUIRED_MIRROR_FREE_MB)/3)*100 >= 90 ");
		sb.append(" THEN '<span style=\"color:red\"><b> WARNING</b></span>' ");
		sb.append(" ELSE 'GOOD' END AS \"Result\" ");
		sb.append("FROM V$ASM_DISKGROUP ");
		sb.append("UNION ALL ");
		sb.append("SELECT 'N/A' \"Name\", ");
		sb.append(" ' ' \"Type\", ");
		sb.append(" 0 \"Total_RAW(MB)\", ");
		sb.append(" 0 \"Total-Usable(MB)\", ");
		sb.append(" 0 \"Used(MB)\", ");
		sb.append(" 0 \"Free_Usable(MB)\", ");
		sb.append(" 0 \"Used%\", ");
		sb.append(" 'Used < 90%' \"Standard\", ");
		sb.append(" ' ' \"Result\" ");
		sb.append("FROM DUAL ");

		try {
			conn = this.getTran();
			pstmt = conn.prepareStatement(sb.toString());
			rs = pstmt.executeQuery();
			
			while(rs.next()) {
	            String name = rs.getString("Name");
	            String type = rs.getString("Type");
	            String totRawMb = rs.getString("Tot_RAW(MB)");
	            String totUsableMb = rs.getString("Tot_Usable(MB)");
	            String usedMb = rs.getString("Used(MB)");
	            String freeMb = rs.getString("Free(MB)");
	            String usedPercent = rs.getString("Used(%)");
	            String standard = rs.getString("Standard");
	            String resultMsg = rs.getString("Result");
	            
				Map<String, Object> data = new HashMap<>();
				data.put("Name", name);
				data.put("Type", type);
				data.put("Tot_RAW(MB)", totRawMb);
				data.put("Tot_Usable(MB)", totUsableMb);
				data.put("Used(MB)", usedMb);
				data.put("Free(MB)", freeMb);
				data.put("Used(%)", usedPercent);
				data.put("Standard", standard);
				data.put("Result", resultMsg);
				result.add(data);
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			this.endTran(conn);
		}
		
		return result;
	}

}
