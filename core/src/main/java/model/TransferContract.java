package model;

public class TransferContract {
    String contractAddress;
    String sender;
    String receiver;
    String fileName;
    byte[] senderSign;
    byte[] receiverSign;
    byte[] secretKey;
    ContractState state;
    String discoveryHost;
    int discoveryPort;
    String reason;

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public TransferContract() {
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

    public void setContractAddress(String contractAddress) {
        this.contractAddress = contractAddress;
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
}
