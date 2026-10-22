package com.vfff.progettoiss;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class RoomONeScreen implements Screen {
    private Main game;
    private Stage stageStanza;
    private HudMenu hud;

    private Texture texSfondo;

    // Tracciamento raccolta oggetti nella stanza
    private boolean polaroidRaccolta = false;
    private boolean trinketRaccolto = false;
    private boolean bottigliaRaccolta = false;
    private boolean chiaveRaccolta = false;

    private Label messageLabel;
    private float messageTimer = 0;

    public RoomONeScreen(Main game) {
        System.out.println("=== APARTMENT ROOM SCREEN CARICATA ===");
        this.game = game;

        stageStanza = new Stage(new FitViewport(2816, 1536));

        // 1. Sfondo integrato con elementi e illuminazione notturna
        texSfondo = new Texture("stanza1.jpg");
        texSfondo.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);

        Image sfondo = new Image(texSfondo);
        sfondo.setPosition(0, 0);
        sfondo.setSize(2816, 1536);
        stageStanza.addActor(sfondo);

        // 2. Setup Label messaggi
        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = new BitmapFont();
        labelStyle.fontColor = Color.WHITE;

        messageLabel = new Label("", labelStyle);
        messageLabel.setPosition(200, 120);
        messageLabel.setFontScale(3.0f);

        // 3. Hotspot invisibili per l'interazione diretta
        Button.ButtonStyle stileHotspot = new Button.ButtonStyle();

        // Cestino in basso a sinistra
        Button btnCestino = new Button(stileHotspot);
        btnCestino.setBounds(180, 50, 420, 480);

        // Polaroid sul tavolino
        Button btnPolaroid = new Button(stileHotspot);
        btnPolaroid.setBounds(1950, 320, 320, 260);

        // Chiave antica sul pavimento
        Button btnChiave = new Button(stileHotspot);
        btnChiave.setBounds(620, 180, 200, 120);

        // Bottiglia sul pavimento in primo piano
        Button btnBottiglia = new Button(stileHotspot);
        btnBottiglia.setBounds(1720, 40, 300, 180);

        // Carillon sul mobiletto
        Button btnTrinket = new Button(stileHotspot);
        btnTrinket.setBounds(1840, 580, 220, 180);

        // --- LISTENER DEGLI HOTSPOT ---

        btnPolaroid.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (!verificaCartaAttiva()) return;
                if (!polaroidRaccolta) {
                    polaroidRaccolta = true;
                    GameManager.getInstance().aggiungiOggetto("Polaroid Strappata");
                    GameManager.getInstance().notifyHud("SHOW_HUD");
                    btnPolaroid.remove();
                    mostraMessaggio("Hai raccolto la Polaroid Strappata. È nell'inventario.");
                }
            }
        });

        btnChiave.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (!verificaCartaAttiva()) return;
                if (!chiaveRaccolta) {
                    chiaveRaccolta = true;
                    GameManager.getInstance().aggiungiOggetto("Vecchia Chiave");
                    GameManager.getInstance().notifyHud("SHOW_HUD");
                    btnChiave.remove();
                    mostraMessaggio("Hai raccolto la Vecchia Chiave. È nell'inventario.");
                }
            }
        });

        btnBottiglia.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (!verificaCartaAttiva()) return;
                if (!bottigliaRaccolta) {
                    bottigliaRaccolta = true;
                    GameManager.getInstance().aggiungiOggetto("Bottiglia Vuota");
                    GameManager.getInstance().notifyHud("SHOW_HUD");
                    btnBottiglia.remove();
                    mostraMessaggio("Hai raccolto la Bottiglia Vuota. È nell'inventario.");
                }
            }
        });

        btnTrinket.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (!verificaCartaAttiva()) return;
                if (!trinketRaccolto) {
                    trinketRaccolto = true;
                    GameManager.getInstance().aggiungiOggetto("Carillon Arrugginito");
                    GameManager.getInstance().notifyHud("SHOW_HUD");
                    btnTrinket.remove();
                    mostraMessaggio("Hai raccolto il Carillon Arrugginito. È nell'inventario.");
                }
            }
        });

        btnCestino.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                int smaltiti = GameManager.getInstance().oggettiGriefSmaltiti;
                if (smaltiti == 0) {
                    mostraMessaggio("Il cestino è vuoto. Qualcosa richiede di essere cancellato.");
                } else if (smaltiti < 4) {
                    mostraMessaggio("Hai gettato " + smaltiti + " ricordi su 4 nel cestino.");
                } else {
                    mostraMessaggio("Hai ripulito ogni traccia del passato.");
                }
            }
        });

        stageStanza.addActor(btnCestino);
        stageStanza.addActor(btnPolaroid);
        stageStanza.addActor(btnChiave);
        stageStanza.addActor(btnBottiglia);
        stageStanza.addActor(btnTrinket);
        stageStanza.addActor(messageLabel);

        hud = new HudMenu();

        com.badlogic.gdx.InputMultiplexer multiplexer = new com.badlogic.gdx.InputMultiplexer();
        multiplexer.addProcessor(hud.stage);
        multiplexer.addProcessor(stageStanza);
        Gdx.input.setInputProcessor(multiplexer);
    }

    private boolean verificaCartaAttiva() {
        boolean possiedeCarta = GameManager.getInstance().listaCarte.contains("XII - L'Impiccato", false);
        if (!possiedeCarta) {
            mostraMessaggio("Non vedi alcun motivo per toccare questo oggetto.");
            return false;
        }
        return true;
    }

    private void mostraMessaggio(String testo) {
        messageLabel.setText(testo);
        messageTimer = 4.0f;
        System.out.println(testo);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (messageTimer > 0) {
            messageTimer -= delta;
            if (messageTimer <= 0) {
                messageLabel.setText("");
            }
        }

        stageStanza.act(delta);
        stageStanza.draw();
        hud.render();
    }

    @Override
    public void resize(int width, int height) {
        stageStanza.getViewport().update(width, height, true);
        if (hud != null) {
            hud.stage.getViewport().update(width, height, true);
        }
    }

    @Override public void show() {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}

    @Override
    public void dispose() {
        stageStanza.dispose();
        if (hud != null) hud.dispose();
        if (texSfondo != null) texSfondo.dispose();
    }
}
