package pl.szczurowsky.ratorm.credentials;

import java.util.HashMap;

public class Credentials {

    private final HashMap<CredentialsType, Object> credentials = new HashMap<>();

    public Credentials() {

    }

    public Credentials(String login, String password, String databaseName, String host, int port) {
        credentials.put(CredentialsType.LOGIN, login);
        credentials.put(CredentialsType.PASSWORD, password);
        credentials.put(CredentialsType.DATABASE_NAME, databaseName);
        credentials.put(CredentialsType.HOST, host);
        credentials.put(CredentialsType.PORT, port);
    }

    public Credentials(HashMap<CredentialsType, Object> credentials) {
        this.credentials.putAll(credentials);
    }

    public void setCredentials(CredentialsType type, Object value) {
        credentials.put(type, value);
    }

}
