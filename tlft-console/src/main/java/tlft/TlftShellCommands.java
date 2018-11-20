package tlft;

import client.FileTransferClient;
import client.FileTransferClientImpl;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

@ShellComponent
public class TlftShellCommands {
    private volatile FileTransferClient client;

    @ShellMethod("Start file transfer client")
    public void start(
        @ShellOption String accountFileName,
        @ShellOption String password,
        @ShellOption String rootContractAddress
    ) throws Exception {
        if (client != null)
            throw new Exception("Client is already started");

        client = new FileTransferClientImpl();

        client.start(accountFileName, password, rootContractAddress);
    }

    @ShellMethod("Test")
    public void test() {
        System.out.println("Test!!!");
    }
}
