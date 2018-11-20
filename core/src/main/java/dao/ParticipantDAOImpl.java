package dao;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import model.Participant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import rx.Subscription;
import solidity.FileTransferRegistry;

public class ParticipantDAOImpl implements ParticipantDAO {
    private final Logger log = LoggerFactory.getLogger(ParticipantDAOImpl.class);

    private final Web3j web3j;
    private final FileTransferRegistry rootContract;
    private final Map<String, Participant> participantMap = new ConcurrentHashMap<>();
    private Subscription subscription;

    public ParticipantDAOImpl(Web3j web3j, FileTransferRegistry rootContract) {
        this.web3j = web3j;
        this.rootContract = rootContract;

        subscription = rootContract
            .participantRegisteredEventObservable(DefaultBlockParameterName.EARLIEST, DefaultBlockParameterName.LATEST)
            .subscribe(
                event -> {
                    participantMap.put(event.addr, new Participant(event.addr, event.name, event.pubKey));

                    log.debug("Participant added " + event.addr);
                }
            );
    }

    @Override public Map<String, Participant> getAllParticipantsMap() {
        return participantMap;
    }

    @Override public Collection<Participant> getAllParticipants() {
        return Collections.unmodifiableCollection(participantMap.values());
    }

    @Override public Participant getParticipant(String address) {
        return participantMap.get(address);
    }
}
