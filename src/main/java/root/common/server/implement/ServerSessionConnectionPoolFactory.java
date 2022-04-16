package root.common.server.implement;

import java.util.Properties;

import org.apache.commons.pool2.BaseKeyedPooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

import root.core.domain.JschConnectionInfo;

public class ServerSessionConnectionPoolFactory extends BaseKeyedPooledObjectFactory<JschConnectionInfo, Session> {

	@Override
	public Session create(JschConnectionInfo connInfo) throws Exception {
		JSch jsch = new JSch();
		Session session = jsch.getSession(connInfo.getUserName(), connInfo.getHost(), connInfo.getPort());
		session.setPassword(connInfo.getPassword());

		Properties config = new Properties();
		config.put("StrictHostKeyChecking", "no"); // 호스트 정보를 검사하지 않는다.
		config.put("PreferredAuthentications", "password");
		session.setConfig(config);

		session.connect();
		return session;
	}

	@Override
	public void destroyObject(JschConnectionInfo key, PooledObject<Session> p) throws Exception {
		p.getObject().disconnect();
	}

	@Override
	public boolean validateObject(JschConnectionInfo key, PooledObject<Session> p) {
		return p.getObject().isConnected();
	}

	@Override
	public PooledObject<Session> wrap(Session obj) {
		return new DefaultPooledObject<>(obj);
	}
}