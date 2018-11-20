package dao;

import java.util.Collection;
import java.util.Map;
import model.Participant;

public interface ParticipantDAO {
    Map<String, Participant> getAllParticipantsMap();

    Collection<Participant> getAllParticipants();

    /**
     * @param address Participant address.
     */
    Participant getParticipant(String address);
}
