package root.core.repository.implement;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import root.common.database.contracts.AbstractDatabase;
import root.core.domain.ASMDiskUsage;
import root.core.domain.ArchiveUsage;
import root.core.domain.MonitoringResult;
import root.core.domain.TableSpaceUsage;
import root.core.repository.constracts.DBCheckRepository;

public class DBCheckRepositoryImpl implements DBCheckRepository {
	private AbstractDatabase db;

	public DBCheckRepositoryImpl(AbstractDatabase db) {
		this.db = db;
	}
	
	@Override
	public String getDBName() {
		return db.getName();
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
	public MonitoringResult<ArchiveUsage> checkArchiveUsage() {

		List<ArchiveUsage> result = new ArrayList<>();
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT NAME, ROUND(SPACE_USED/SPACE_LIMIT*100) USED_RATE, ");
		sb.append("ROUND(SPACE_LIMIT) AS SPACE_LIMIT_B, ");
		sb.append("ROUND(SPACE_USED) AS SPACE_USED_B, ");
		sb.append("ROUND(SPACE_RECLAIMABLE) AS SPACE_RECLAIMABLE_B, ");
		sb.append("NUMBER_OF_FILES, ");
		sb.append("TO_CHAR(SYSDATE, 'YYYY-MM-DD HH24:MI:SS') AS DNT ");
		sb.append("FROM V$RECOVERY_FILE_DEST ");
				
		try {
			conn = this.getTran();
			pstmt = conn.prepareStatement(sb.toString());
			rs = pstmt.executeQuery();
			
			while(rs.next()) {
	            String name = rs.getString("NAME");
	            double usedRate = rs.getDouble("USED_RATE");
	            double spaceLimit = rs.getDouble("SPACE_LIMIT_B");
	            double spaceUsed = rs.getDouble("SPACE_USED_B");
	            double spaceReclaimable = rs.getDouble("SPACE_RECLAIMABLE_B");
	            int numberOfFiles = rs.getInt("NUMBER_OF_FILES");
	            String dnt = rs.getString("DNT");
	            
	            ArchiveUsage data = new ArchiveUsage(name, numberOfFiles, 
	            		spaceLimit, spaceReclaimable, spaceUsed, usedRate, dnt);
				result.add(data);
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			this.endTran(conn);
		}
		
		return new MonitoringResult<>(result);
	}
	
	@Override
	public MonitoringResult<TableSpaceUsage> checkTableSpaceUsage() {

		List<TableSpaceUsage> result = new ArrayList<>();
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		StringBuffer sb = new StringBuffer();
		sb.append(" SELECT F.TNAME AS TableSpace, ");
		sb.append(" ROUND(SUM(D.BYTES), 2) AS \"Total(B)\", ");
		sb.append(" ROUND((SUM(D.BYTES) - SUM(F.BYTES)), 2) AS \"UsedSpace(B)\", ");
		sb.append(" ROUND((SUM(D.BYTES) - SUM(F.BYTES))/SUM(D.BYTES)*100, 0) AS \"Used(%)\", ");
		sb.append(" ROUND(SUM(F.BYTES), 2) AS \"FreeSpace(B)\" ");
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
	            double totalSpace = rs.getDouble("Total(B)");
	            double usedSpace = rs.getDouble("UsedSpace(B)");
	            double usedPercent = rs.getDouble("Used(%)");
	            double freeSpace = rs.getDouble("FreeSpace(B)");
	           
				result.add(new TableSpaceUsage(tableSpace, totalSpace, freeSpace, usedSpace, usedPercent));
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			this.endTran(conn);
		}
		
		return new MonitoringResult<>(result);
	}
	
	@Override
	public MonitoringResult<ASMDiskUsage> checkASMDiskUsage() {

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
		sb.append("         TOTAL_MB * 1024 * 1024 \"Tot_RAW(B)\", ");
		sb.append("         CASE WHEN TYPE='EXTERN' THEN TOTAL_MB * 1024 * 1024 ");
		sb.append("              WHEN TYPE='NORMAL' THEN (TOTAL_MB-REQUIRED_MIRROR_FREE_MB)/2 ");
		sb.append("              WHEN TYPE='HIGH' THEN (TOTAL_MB-REQUIRED_MIRROR_FREE_MB)/3 ");
		sb.append(" 	    ELSE 0 END AS \"Tot_Usable(B)\", ");
		sb.append(" 	    CASE WHEN TYPE='EXTERN' THEN COLD_USED_MB * 1024 * 1024 ");
		sb.append(" 		 	 WHEN TYPE='NORMAL' THEN COLD_USED_MB/2 * 1024 * 1024 ");
		sb.append(" 		 	 WHEN TYPE='HIGH' THEN COLD_USED_MB/3 * 1024 * 1024 ");
		sb.append(" 	    ELSE 0 END AS \"Used(B)\", ");
		sb.append(" 	    USABLE_FILE_MB * 1024 * 1024 \"Free(B)\", ");
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
	            double totalRawSpace = rs.getDouble("Tot_RAW(B)");
	            double totalAvailableSpace = rs.getDouble("Tot_Usable(B)");
	            double availableSpace = rs.getDouble("Free(B)");
	            double usedSpace = rs.getDouble("Used(B)");
	            double usedPercent = rs.getDouble("Used(%)");
				String resultMsg = rs.getString("Result");

				result.add(new ASMDiskUsage(asmDiskGroupName, asmDiskGroupType, totalRawSpace, totalAvailableSpace,
						availableSpace, usedSpace, usedPercent, resultMsg));
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			this.endTran(conn);
		}
		
		return new MonitoringResult<>(result);
	}

}
