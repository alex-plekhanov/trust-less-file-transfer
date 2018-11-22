package tlft.model;

import java.util.Arrays;

public class TransferContract {
    private final String contractAddress;
    private String sender;
    private String receiver;
    private String fileName;
    private byte[] senderSign;
    private byte[] receiverSign;
    private byte[] secretKey;
    private ContractState state;
    private String discoveryHost;
    private int discoveryPort;
    private String reason;

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public TransferContract(String contractAddress) {
        this.contractAddress = contractAddress;
    }

    public TransferContract(String contractAddress, String sender, String receiver) {
        this.contractAddress = contractAddress;
        this.sender = sender;
        this.receiver = receiver;
    }

    public String getContractAddress() {
        return contractAddress;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public ContractState getState() {
        return state;
    }

    public synchronized void setState(ContractState state) {
        if (this.state == null || this.state.ordinal() < state.ordinal())
            this.state = state;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public byte[] getSenderSign() {
        return senderSign;
    }

    public void setSenderSign(byte[] senderSign) {
        this.senderSign = senderSign;
    }

    public byte[] getReceiverSign() {
        return receiverSign;
    }

    public void setReceiverSign(byte[] receiverSign) {
        this.receiverSign = receiverSign;
    }

    public byte[] getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(byte[] secretKey) {
        this.secretKey = secretKey;
    }

    public String getDiscoveryHost() {
        return discoveryHost;
    }

    public void setDiscoveryHost(String discoveryHost) {
        this.discoveryHost = discoveryHost;
    }

    public int getDiscoveryPort() {
        return discoveryPort;
    }

    public void setDiscoveryPort(int discoveryPort) {
        this.discoveryPort = discoveryPort;
    }

    @Override public boolean equals(Object o) {
        if (this == o)
            return true;

        if (o == null || getClass() != o.getClass())
            return false;

        TransferContract contract = (TransferContract)o;

        return contractAddress.equals(contract.contractAddress);
    }

    @Override public int hashCode() {
        return contractAddress.hashCode();
    }

    @Override public String toString() {
        return "TransferContract{" +
            "contractAddress='" + contractAddress + '\'' +
            ", sender='" + sender + '\'' +
            ", receiver='" + receiver + '\'' +
            ", fileName='" + fileName + '\'' +
            ", senderSign=" + Arrays.toString(senderSign) +
            ", receiverSign=" + Arrays.toString(receiverSign) +
            ", secretKey=" + Arrays.toString(secretKey) +
            ", state=" + state +
            ", discoveryHost='" + discoveryHost + '\'' +
            ", discoveryPort=" + discoveryPort +
            ", reason='" + reason + '\'' +
            '}';
    }
}
