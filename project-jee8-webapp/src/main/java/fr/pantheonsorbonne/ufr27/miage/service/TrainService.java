package fr.pantheonsorbonne.ufr27.miage.service;

import fr.pantheonsorbonne.ufr27.miage.jpa.Gare;
import fr.pantheonsorbonne.ufr27.miage.jpa.Trajet;
import fr.pantheonsorbonne.ufr27.miage.model.jaxb.LiveInfo;

import java.time.Duration;
import java.util.List;

public interface TrainService {

    public void processLiveInfo(LiveInfo liveInfo);

    public void triggerDelay(String idTrain, Duration duration);

	public void processCancel(int id, String conditions);

	public Trajet processGetTrain(int id);

	public List<Trajet> processGetTrainList();

	public List<Trajet> processGetTrainArriveeGareList(Gare gare);

	public List<Trajet> processGetTrainDepartGareList(Gare gare);
}
