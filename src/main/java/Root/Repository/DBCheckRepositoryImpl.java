package Root.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import Root.Database.DatabaseUtil;
import Root.Model.ASMDiskUsage;
import Root.Model.ArchiveUsage;
import Root.Model.TableSpaceUsage;

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
	public List<ArchiveUsage> checkArchiveUsage() {

		List<ArchiveUsage> result = new ArrayList<>();
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
	            int numberOfFiles = rs.getInt("NUMBER_OF_FILES");
	            String dnt = rs.getString("DNT");
	            
	            ArchiveUsage data = new ArchiveUsage(name, numberOfFiles, spaceReclaimableGb + "G", spaceUsedGb + "G", usedRate + "%", spaceLimitGb + "G", dnt);
	            data.setReclaimableSpace(Double.parseDouble(spaceReclaimableGb));
	            data.setUsedSpace(Double.parseDouble(spaceUsedGb));
	            data.setUsedPercent(Double.parseDouble(usedRate));
	            data.setTotalSpace(Double.parseDouble(spaceLimitGb));

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
	public List<TableSpaceUsage> checkTableSpaceUsage() {

		List<TableSpaceUsage> result = new ArrayList<>();
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
	           
				TableSpaceUsage data = new TableSpaceUsage(tableSpace, freeSpaceGb + "G", usedSpaceGb + "G", usedPercent + "%", totalGb + "G");
				data.setAvailableSpace(Double.parseDouble(freeSpaceGb));
				data.setUsedSpace(Double.parseDouble(usedSpaceGb));
				data.setUsedPercent(Double.parseDouble(usedPercent));
				data.setTotalSpace(Double.parseDouble(totalGb));
				
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
	public List<ASMDiskUsage> checkASMDiskUsage() {

		List<ASMDiskUsage> result = new ArrayList<>();
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT S1.*, ");
		sb.append("	      CASE WHEN S1.\"Used(%)\" >= 90 THEN 'WARNING' ELSE 'GOOD' END AS \"Result\" ");
		sb.append("FROM (");
		sb.append("		SELECT NAME \"ASM_DISK_GROUP_NAME\", ");
		sb.append("     	TYPE \"Type\", ");
		sb.append("         TOTAL_MB \"Tot_RAW(MB)\", ");
		sb.append("         CASE WHEN TYPE='EXTERN' THEN TOTAL_MB ");
		sb.append("              WHEN TYPE='NORMAL' THEN (TOTAL_MB-REQUIRED_MIRROR_FREE_MB)/2 ");
		sb.append("              WHEN TYPE='HIGH' THEN (TOTAL_MB-REQUIRED_MIRROR_FREE_MB)/3 ");
		sb.append(" 	    ELSE 0 END AS \"Tot_Usable(MB)\", ");
		sb.append(" 	    CASE WHEN TYPE='EXTERN' THEN COLD_USED_MB ");
		sb.append(" 		 	 WHEN TYPE='NORMAL' THEN COLD_USED_MB/2 ");
		sb.append(" 		 	 WHEN TYPE='HIGH' THEN COLD_USED_MB/3 ");
		sb.append(" 	    ELSE 0 END AS \"Used(MB)\", ");
		sb.append(" 	    USABLE_FILE_MB \"Free(MB)\", ");
		sb.append(" 	    CASE WHEN TYPE='EXTERN' THEN ROUND((TOTAL_MB-USABLE_FILE_MB)/TOTAL_MB*100,2) ");
		sb.append("				 WHEN TYPE='NORMAL' THEN ROUND(((TOTAL_MB-REQUIRED_MIRROR_FREE_MB)/2-USABLE_FILE_MB)/((TOTAL_MB-REQUIRED_MIRROR_FREE_MB)/2)*100,2) ");
		sb.append("				 WHEN TYPE='HIGH'   THEN ROUND(((TOTAL_MB-REQUIRED_MIRROR_FREE_MB)/3-USABLE_FILE_MB)/((TOTAL_MB-REQUIRED_MIRROR_FREE_MB)/3)*100,2) ");
		sb.append("		    ELSE 0 END AS \"Used(%)\" ");
		sb.append("		FROM V$ASM_DISKGROUP ");
		sb.append("		) S1	");

		try {
			conn = this.getTran();
			pstmt = conn.prepareStatement(sb.toString());
			rs = pstmt.executeQuery();
			
			while(rs.next()) {
	            String asmDiskGroupName = rs.getString("ASM_DISK_GROUP_NAME");
	            String asmDiskGroupType = rs.getString("Type");
	            String totalRawSpace = rs.getString("Tot_RAW(MB)");
	            String totalAvailableSpace = rs.getString("Tot_Usable(MB)");
	            String availableSpace = rs.getString("Free(MB)");
	            String usedSpace = rs.getString("Used(MB)");
	            String usedPercent = rs.getString("Used(%)");
	            String resultMsg = rs.getString("Result");
	            
	            ASMDiskUsage data = new ASMDiskUsage(asmDiskGroupName, asmDiskGroupType, totalRawSpace + "MB", totalAvailableSpace + "MB", availableSpace + "MB", usedSpace + "MB", usedPercent + "%", resultMsg);
	            data.setTotalRawSpace(Double.parseDouble(totalRawSpace));
	            data.setTotalAvailableSpace(Double.parseDouble(totalAvailableSpace));
	            data.setAvailableSpace(Double.parseDouble(availableSpace));
	            data.setUsedSpace(Double.parseDouble(usedSpace));
	            data.setUsedPercent(Double.parseDouble(usedPercent));
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
