package fr.pantheonsorbonne.ufr27.miage.service;

import fr.pantheonsorbonne.ufr27.miage.model.jaxb.LiveInfo;

public interface TrainService {

    public void processLiveInfo(LiveInfo liveInfo, int trajetId);

    public void updateInfogares();

	public void processDelayWithCondition(LiveInfo liveInfo, int id, String condition);

}
