package tech.crom.client.java.scm.git.internal;

import com.jcraft.jsch.IdentityRepository;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.agentproxy.AgentProxyException;
import com.jcraft.jsch.agentproxy.Connector;
import com.jcraft.jsch.agentproxy.RemoteIdentityRepository;
import com.jcraft.jsch.agentproxy.USocketFactory;
import com.jcraft.jsch.agentproxy.connector.SSHAgentConnector;
import com.jcraft.jsch.agentproxy.usocket.JNAUSocketFactory;
import org.eclipse.jgit.transport.JschConfigSessionFactory;
import org.eclipse.jgit.transport.OpenSshConfig;
import org.eclipse.jgit.util.FS;

public class GitSessionFactory extends JschConfigSessionFactory {
    @Override
    protected void configure(OpenSshConfig.Host hc, Session session) {

    }

    @Override
    protected JSch createDefaultJSch(FS fs) throws JSchException {
        Connector con = null;
        try {
            if (SSHAgentConnector.isConnectorAvailable()) {
                USocketFactory usf = new JNAUSocketFactory();
                con = new SSHAgentConnector(usf);
            }
        } catch (AgentProxyException e) {
            System.out.println(e);
        }

        final JSch jsch = super.createDefaultJSch(fs);

        if (con != null) {
            JSch.setConfig("PreferredAuthentications", "publickey");

            IdentityRepository identityRepository = new RemoteIdentityRepository(con);
            jsch.setIdentityRepository(identityRepository);
        }

        return jsch;
    }
}
