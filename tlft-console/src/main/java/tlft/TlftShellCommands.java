package tlft;

import java.util.Collection;
import java.util.Collections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;
import tlft.client.FileTransferClient;
import tlft.client.FileTransferClientImpl;
import tlft.model.Participant;

@ShellComponent
public class TlftShellCommands {
    private static final Logger log = LoggerFactory.getLogger(TlftShellCommands.class);
    private volatile FileTransferClient client;

    /*
        start tlft-console/src/main/resources/keystore/test_key password
    */
    @ShellMethod("Start the file transfer client")
    public String start(
        @ShellOption String accountFileName,
        @ShellOption String password,
        @ShellOption(defaultValue = "") String rootContractAddress
    ) throws TlftShellCommandException {
        if (client != null)
            throw new TlftShellCommandException("Client is already started");

        try {
            client = new FileTransferClientImpl();

            client.start(accountFileName, password, rootContractAddress.isEmpty() ? null : rootContractAddress);
        }
        catch (Exception e) {
            client = null;

            log.error("Failed to start client", e);

            throw new TlftShellCommandException("Failed to start client, see log for details: " + e.getMessage());
        }

        return "Client started succesfully";
    }

    @ShellMethod("Show root contract address")
    public String showRootContractAddress() throws TlftShellCommandException {
        return safeCommandExecute(() -> client.getRootContractAddress());
    }

    @ShellMethod("Register my participant")
    public String register(String name) throws TlftShellCommandException {
        return safeCommandExecute(() -> {
            client.registerParticipant(name);

            return "Participant successfully registered with name: " + name;
        });
    }

    @ShellMethod("Show participant info by address")
    public String showParticipant(String address) throws TlftShellCommandException {
        return safeCommandExecute(() -> {
            Participant participant = client.getParticipant(address);

            return participant == null ? "Participant not found"
                : participantsFinePrint(Collections.singleton(participant));
        });
    }

    @ShellMethod("Show all participants info")
    public String showAllParticipants() throws TlftShellCommandException {
        return safeCommandExecute(() -> {
            Collection<Participant> participants = client.getAllParticipants();

            return participantsFinePrint(participants);
        });
    }

    @ShellMethod("Show address of my participant")
    public String showMyParticipantAddress() throws TlftShellCommandException {
        return safeCommandExecute(() -> client.getMyParticipantAddress());
    }

    @ShellMethod("Stop the file transfer client")
    public String stop() throws TlftShellCommandException {
        return safeCommandExecute(() -> {
            client.stop();

            client = null;

            return "Client stopped successfully.";
        });
    }

    private String safeCommandExecute(ThrowingSupplier<String> command) throws TlftShellCommandException {
        if (client == null)
            throw new TlftShellCommandException("Client is not started");

        try {
            return command.get();
        }
        catch (Exception e) {
            log.error("Failed to execute command", e);

            throw new TlftShellCommandException("Failed to execute command, see log for details: " + e.getMessage());
        }
    }

    private String participantsFinePrint(Collection<Participant> participants) {
        if (participants == null || participants.isEmpty())
            return "No participants found";

        String nl = System.getProperty("line.separator");

        StringBuilder sb = new StringBuilder();
        sb.append("Address                                    Name").append(nl);

        for (Participant participant : participants)
            sb.append(participant.getAddress()).append(' ').append(participant.getName()).append(nl);

        return sb.toString();
    }

    public interface ThrowingSupplier<T> {
        T get() throws Exception;
    }
}
