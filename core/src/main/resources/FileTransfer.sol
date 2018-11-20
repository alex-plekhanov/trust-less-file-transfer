pragma solidity ^0.4.0;

contract FileTransfer {
    enum State { Initiated, Confirmed, Decrypted, Completed, Canceled }

    event TransferInitiated(address fileSender, address fileReceiver, string fileName,
        string cryptedFileSenderSign, string discoveryHost, uint16 discoveryPort);

    event TransferConfirmed(string cryptedFileReceiverSign);

    event TransferDecrypted(string secretKey);

    event TransferCanceled(string reason);

    event TransferCompleted(bool result, string reason);

    address registryContract;
    address fileSender;
    address fileReceiver;
    string cryptedFileSenderSign;
    string cryptedFileReceiverSign;
    string secretKey;
    State state;
    bool result;

    constructor(address _fileSender, address _fileReceiver, string _fileName,
        string _cryptedFileSenderSign, string _discoveryHost, uint16 _discoveryPort) public {
        registryContract = msg.sender;
        fileSender = _fileSender;
        fileReceiver = _fileReceiver;
        cryptedFileSenderSign = _cryptedFileSenderSign;
        state = State.Initiated;

        emit TransferInitiated(_fileSender, _fileReceiver, _fileName,
            _cryptedFileSenderSign, _discoveryHost, _discoveryPort);
    }

    function confirmCryptedFileReceived(string _cryptedFileReceiverSign) public {
        require (msg.sender == fileReceiver, 'Only receiver can call this function');
        require (state == State.Initiated, 'Wrong contract state');

        cryptedFileReceiverSign = _cryptedFileReceiverSign;
        state = State.Confirmed;

        emit TransferConfirmed(_cryptedFileReceiverSign);
    }

    function publishSecretKey(string _secretKey) public {
        require (msg.sender == fileSender, 'Only sender can call this function');
        require (state == State.Confirmed, 'Wrong contract state');

        secretKey = _secretKey;
        state = State.Decrypted;

        emit TransferDecrypted(_secretKey);
    }

    function cancel(string _reason) public {
        require (msg.sender == fileSender || msg.sender == fileReceiver, 'Only sender or receiver can call this function');
        require (state == State.Initiated || state == State.Confirmed, 'Wrong contract state');

        state = State.Canceled;

        emit TransferCanceled(_reason);
    }

    function complete(bool _result, string _reason) public {
        require (msg.sender == fileReceiver, 'Only receiver can call this function');
        require (state == State.Decrypted, 'Wrong contract state');

        state = State.Completed;
        result = _result;

        emit TransferCompleted(_result, _reason);
    }
}

contract FileTransferRegistry {
    struct Participant {
        string name;
        string pubKey;
    }

    mapping(address => Participant) registeredParticipants;

    event ParticipantRegistered(address indexed addr, string pubKey, string name);

    event FileTransferInitiated(address indexed sender, address indexed receiver, address fileTransferContract);

    function registerMe(string pubKey, string name) public {
        require (bytes(registeredParticipants[msg.sender].pubKey).length == 0, 'Participant is already registered');
        //require (address(keccak256(pubKey)) == msg.sender, 'Public key is not valid'); // TODO

        registeredParticipants[msg.sender].pubKey = pubKey;
        registeredParticipants[msg.sender].name = name;

        emit ParticipantRegistered(msg.sender, pubKey, name);
    }

    function initiateFileTransfer(address receiver, string fileName, string cryptedFileSign, string discoveryHost, uint16 discoveryPort) public {
        require (bytes(registeredParticipants[msg.sender].pubKey).length != 0, 'File transfer initiator is not registered');
        require (bytes(registeredParticipants[receiver].pubKey).length != 0, 'File receiver is not registered');

        FileTransfer fileTransfer = new FileTransfer(msg.sender, receiver, fileName, cryptedFileSign, discoveryHost, discoveryPort);

        emit FileTransferInitiated(msg.sender, receiver, fileTransfer);
    }
}
