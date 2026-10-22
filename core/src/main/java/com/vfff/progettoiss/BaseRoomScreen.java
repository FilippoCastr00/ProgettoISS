package com.vfff.progettoiss;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;

import java.util.HashMap;
import java.util.Map;

public abstract class BaseRoomScreen extends ScreenAdapter {

    // Mappa che collega un'area fisica (Rectangle) a un'azione da eseguire (ClickCommand)
    protected Map<Rectangle, ClickCommand> interactables = new HashMap<>();

    // Il "traduttore" delle coordinate del mouse
    protected OrthographicCamera camera;

    // L'interfaccia utente (Menu e Inventario)
    protected HudMenu hud;

    @Override
    public void show() {
        // 1. Inizializziamo la telecamera grande quanto lo schermo
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        // 2. Costruiamo l'HUD
        hud = new HudMenu();

        // 3. IL MULTIPLEXER: Il gestore del traffico dei click
        InputMultiplexer multiplexer = new InputMultiplexer();

        // Priorità 1: I click sui bottoni dell'interfaccia 2D (il Menu)
        multiplexer.addProcessor(hud.stage);

        // Priorità 2: I click fisici sugli oggetti della stanza
        multiplexer.addProcessor(new InputAdapter() {
            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                // Catturiamo le coordinate "grezze" del sistema operativo
                Vector3 touchPos = new Vector3(screenX, screenY, 0);

                // Chiediamo alla telecamera di tradurle nelle coordinate del gioco
                camera.unproject(touchPos);

                // Controlliamo se abbiamo cliccato una hitbox
                checkClick(touchPos.x, touchPos.y);

                return true; // Click gestito
            }
        });

        // Diciamo a libGDX di usare il nostro multiplexer come ascoltatore principale
        Gdx.input.setInputProcessor(multiplexer);

        // 4. Avviamo il setup specifico della stanza figlia
        setupRoom();
    }

    @Override
    public void hide() {
        // Quando chiudiamo o cambiamo stanza, scolleghiamo il mouse
        // per evitare bug e click accidentali durante i caricamenti
        Gdx.input.setInputProcessor(null);
    }

    // Metodo che ogni singola stanza dovrà personalizzare (per caricare immagini e oggetti)
    protected abstract void setupRoom();

    // Metodo universale per controllare se il mouse ha colpito un oggetto
    public void checkClick(float x, float y) {
        System.out.println("Click del mouse alle coordinate: X=" + x + ", Y=" + y);

        for (Map.Entry<Rectangle, ClickCommand> entry : interactables.entrySet()) {
            if (entry.getKey().contains(x, y)) {
                // Eseguiamo il comando incapsulato
                entry.getValue().execute();

                // Rimuoviamo la hitbox dalla mappa per non cliccarla due volte
                interactables.remove(entry.getKey());
                break; // Usciamo dal ciclo per evitare conflitti con oggetti sovrapposti
            }
        }
    }
}
