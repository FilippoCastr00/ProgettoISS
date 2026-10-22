package com.vfff.progettoiss;

import com.badlogic.gdx.utils.Array;

public class GameManager {
    private static GameManager instance;

    public boolean hasFoundFirstNote = false;

    // Liste dinamiche per gli elementi di gioco
    public Array<String> listaNote = new Array<>();
    public Array<String> listaCarte = new Array<>();
    public Array<String> listaOggetti = new Array<>();

    // Tracciamento del lutto
    public int oggettiGriefSmaltiti = 0;

    private GameObserver hudListener;

    private GameManager() { }

    public static GameManager getInstance() {
        if (instance == null) {
            instance = new GameManager();
        }
        return instance;
    }

    public void aggiungiNota(String nome) {
        if (!listaNote.contains(nome, false)) {
            listaNote.add(nome);
        }
    }

    public void aggiungiCarta(String nome) {
        if (!listaCarte.contains(nome, false)) {
            listaCarte.add(nome);
        }
    }

    public void aggiungiOggetto(String nome) {
        if (!listaOggetti.contains(nome, false)) {
            listaOggetti.add(nome);
        }
    }

    public void rimuoviOggetto(String nome) {
        if (listaOggetti.contains(nome, false)) {
            listaOggetti.removeValue(nome, false);

            // Se l'oggetto appartiene ai ricordi del lutto, incrementiamo il contatore
            if (nome.equals("Polaroid Strappata") || nome.equals("Carillon Arrugginito")
                || nome.equals("Bottiglia Vuota") || nome.equals("Vecchia Chiave")) {
                oggettiGriefSmaltiti++;
                if (oggettiGriefSmaltiti >= 4) {
                    notifyHud("GRIEF_COMPLETED");
                }
            }
            notifyHud("ITEM_REMOVED");
        }
    }

    public void setHudListener(GameObserver listener) {
        this.hudListener = listener;
    }

    public void notifyHud(String eventType) {
        if (hudListener != null) {
            hudListener.onEvent(eventType);
        }
    }

    public interface GameObserver {
        void onEvent(String eventType);
    }
}
