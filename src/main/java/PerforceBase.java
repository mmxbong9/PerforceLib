import com.perforce.p4java.exception.P4JavaException;
import com.perforce.p4java.option.UsageOptions;
import com.perforce.p4java.server.IOptionsServer;
import com.perforce.p4java.server.ServerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * Provides simple superclass support for the p4java demo classes, including
 * a standard method to get a new IServer object.<p>
 * <p>
 * The demo apps using this class rely on the system properties defined
 * below for customizing runtime behavior; consult the individual field
 * documentation for usage and default values.
 */

public abstract class PerforceBase {

    /**
     * The server URI to be used for the demo run. This is retrieved
     * from the current value of the system property com.perforce.p4settings.serverUri;
     * if no such system property has been set, the default used is
     * p4java://localhost:1666.
     */
    protected static final String serverUri;
    /**
     * The user name to be used for the demo run. This is retrieved
     * from the current value of the system property com.perforce.p4settings.userName;
     * if no such system property has been set, the default used is
     * "P4javaDemoUser".
     */
    protected static final String userName;
    /**
     * The Perforce client name to be used for the demo run. This is retrieved
     * from the current value of the system property com.perforce.p4settings.clientName;
     * if no such system property has been set, the default used is
     * "p4settings".
     */
    protected static final String clientName;
    /**
     * The Perforce user password to be used for the demo run. This is retrieved
     * from the current value of the system property com.perforce.p4settings.password;
     * if no such system property has been set, the default used is
     * "none" (which is not the same as not having a password).
     */
    protected static final String password;

    private static final String PROP_PREFIX = "com.perforce.p4settings.";
    private static final String CONFIG_FILE = "PerforceSettings.txt";

    private static final Properties fileProps = new Properties();

    static {
        loadFileProperties();

        serverUri  = loadConfig("serverUri", "p4java://your_ip_address:1666");
        userName   = loadConfig("userName", "your_id");
        clientName = loadConfig("clientName", "your_workspace_name");
        password   = loadConfig("password", "your_password");
    }

    private static void loadFileProperties() {
        Path cfg = Paths.get(CONFIG_FILE);
        if (Files.exists(cfg)) {
            try (InputStream in = Files.newInputStream(cfg)) {
                fileProps.load(in);
            } catch (IOException e) {
                System.err.println("설정 파일 로드 실패: " + e.getMessage());
            }
        }
    }

    private static String loadConfig(String key, String defaultValue) {
        String sys = System.getProperty(PROP_PREFIX + key);

        if (sys != null && !sys.isEmpty()) {
            return sys;
        }

        String fileVal = fileProps.getProperty(key);
        if (fileVal != null && !fileVal.isEmpty()) {
            return fileVal;
        }
        return defaultValue;
    }

    /**
     * Get an IOptionsServer object from the P4Java server factory and connect to it.
     * The URI passed to the server factory is cobbled together from the system
     * property com.perforce.p4settings.serverUri; if the property is not set,
     * the URI defaults to p4java://localhost:1666.
     *
     * @param props if not null, P4Java properties object to pass to the P4Java
     *              server factory.
     * @param opts  if not null, P4Java UsageOptions object to pass to the P4Java
     *              server factory.
     * @return connected IServer object ready for use.
     * @throws P4JavaException    thrown if the server factory or the connection method
     *                            detect any errors.
     * @throws URISyntaxException thrown if the server URI passed to the server
     *                            factory is syntactically invalid
     */
    protected static IOptionsServer getOptionsServer(Properties props, UsageOptions opts) throws P4JavaException, URISyntaxException {
        IOptionsServer server = ServerFactory.getOptionsServer(serverUri, props, opts);
        if (server != null) {
            server.connect();
        }
        return server;
    }
}
