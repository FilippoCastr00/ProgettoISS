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
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class RoomMinusOneScreen implements Screen {
    private Main game;
    private Stage stageStanza;
    private HudMenu hud;

    private Texture texSfondo;
    private Texture texRatto;
    private Texture texManoChiave;

    private boolean notaRaccolta = false;
    private boolean zerbinoIsLooked = false;
    private int clickCountVaso = 0;

    private Label messageLabel;
    private float messageTimer = 0;

    // Popup
    private Table ratPopup;
    private Table portaPopup; // Popup a rettangolo per la porta

    // Timer per l'uscita della porta
    private float doorActionTimer = 0;
    private boolean isDoorActionActive = false;

    public RoomMinusOneScreen(Main game) {
        System.out.println("=== ROOM MINUS ONE CARICATA CORRETTAMENTE ===");
        this.game = game;

        stageStanza = new Stage(new FitViewport(2816, 1536));

        texSfondo = new Texture("ingresso.jpg");
        texSfondo.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);

        Image sfondo = new Image(texSfondo);
        sfondo.setPosition(0, 0);
        sfondo.setSize(2816, 1536);
        stageStanza.addActor(sfondo);

        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = new BitmapFont();
        labelStyle.fontColor = Color.WHITE;

        messageLabel = new Label("", labelStyle);
        messageLabel.setPosition(200, 120);
        messageLabel.setFontScale(3.0f);

        Button.ButtonStyle stileHotspot = new Button.ButtonStyle();

        // =========================================================================
        // COORDINATE SULLA RISOLUZIONE 2816x1536
        // =========================================================================

        Button btnNotaHotspot = new Button(stileHotspot);
        btnNotaHotspot.setBounds(250, 180, 350, 220);

        Button btnPorta = new Button(stileHotspot);
        btnPorta.setBounds(1180, 480, 470, 780);

        Button btnZerbinoHotspot = new Button(stileHotspot);
        btnZerbinoHotspot.setBounds(1080, 220, 670, 230);

        Button btnVasoHotspot = new Button(stileHotspot);
        btnVasoHotspot.setBounds(1760, 250, 220, 380);

        // =========================================================================
        // 1. CREAZIONE POPUP DEL RATTO
        // =========================================================================
        texRatto = new Texture("ratto.jpg");
        Image imgRatto = new Image(texRatto);
        imgRatto.setScaling(com.badlogic.gdx.utils.Scaling.fit);

        Label.LabelStyle ratLabelStyle = new Label.LabelStyle();
        ratLabelStyle.font = new BitmapFont();
        ratLabelStyle.fontColor = Color.YELLOW;
        Label ratLabel = new Label("DPG siamo alieni brbr sksk.", ratLabelStyle);
        ratLabel.setFontScale(3.0f);

        Button.ButtonStyle btnStyle = new Button.ButtonStyle();
        btnStyle.up = new TextureRegionDrawable(createSolidColorTexture(new Color(0.3f, 0.3f, 0.3f, 1f)));
        btnStyle.over = new TextureRegionDrawable(createSolidColorTexture(new Color(0.5f, 0.5f, 0.5f, 1f)));
        btnStyle.down = new TextureRegionDrawable(createSolidColorTexture(new Color(0.2f, 0.2f, 0.2f, 1f)));

        Button btnChiudiRatto = new Button(btnStyle);
        Label lblChiudiRatto = new Label("CHIUDI", labelStyle);
        lblChiudiRatto.setFontScale(2.5f);
        btnChiudiRatto.add(lblChiudiRatto).pad(15, 40, 15, 40);

        ratPopup = new Table();
        ratPopup.setSize(1400, 1000);
        ratPopup.setPosition((2816 - 1400) / 2f, (1536 - 1000) / 2f);
        ratPopup.setBackground(new TextureRegionDrawable(createSolidColorTexture(Color.DARK_GRAY)));
        ratPopup.defaults().pad(25);

        ratPopup.add(imgRatto).size(900, 550).expand().fill().row();
        ratPopup.add(ratLabel).row();
        ratPopup.add(btnChiudiRatto).size(300, 100);

        ratPopup.setVisible(false);
        stageStanza.addActor(ratPopup);

        btnChiudiRatto.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                ratPopup.setVisible(false);
            }
        });

        // =========================================================================
        // 2. CREAZIONE POPUP DELLA PORTA (Rettangolo centrato con l'immagine della chiave)
        // =========================================================================
        texManoChiave = new Texture("mano.jpg"); // Assicurati di usare l'immagine in pixel art generata
        Image imgManoChiave = new Image(texManoChiave);
        imgManoChiave.setScaling(com.badlogic.gdx.utils.Scaling.fit);

        Label portaLabel = new Label("Inserisci la chiave nella serratura...", ratLabelStyle);
        portaLabel.setFontScale(3.0f);

        portaPopup = new Table();
        portaPopup.setSize(1400, 1000);
        portaPopup.setPosition((2816 - 1400) / 2f, (1536 - 1000) / 2f);
        portaPopup.setBackground(new TextureRegionDrawable(createSolidColorTexture(Color.DARK_GRAY)));
        portaPopup.defaults().pad(30);

        // Riempie il rettangolo con l'immagine e il testo descrittivo
        portaPopup.add(imgManoChiave).size(950, 650).expand().fill().row();
        portaPopup.add(portaLabel);

        portaPopup.setVisible(false);
        stageStanza.addActor(portaPopup);


        // --- LOGICA DELLE INTERAZIONI ---

        btnNotaHotspot.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (ratPopup.isVisible() || isDoorActionActive) return;
                if (!notaRaccolta) {
                    GameManager.getInstance().aggiungiNota("Lettera Usurata");
                    GameManager.getInstance().hasFoundFirstNote = true;
                    GameManager.getInstance().notifyHud("SHOW_HUD");
                    notaRaccolta = true;
                    btnNotaHotspot.remove();
                    mostraMessaggio("Hai raccolto una Lettera Usurata.");
                }
            }
        });

        btnZerbinoHotspot.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (ratPopup.isVisible() || isDoorActionActive) return;
                if (!zerbinoIsLooked) {
                    GameManager.getInstance().aggiungiCarta("XII - L'Impiccato");
                    GameManager.getInstance().aggiungiOggetto("Chiavi Appartamento");
                    GameManager.getInstance().notifyHud("SHOW_HUD");
                    zerbinoIsLooked = true;
                    mostraMessaggio("Hai trovato le chiavi dell'appartamento e il tarocco.");
                } else {
                    mostraMessaggio("Sotto lo zerbino non c'è più nulla.");
                }
            }
        });

        btnVasoHotspot.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (ratPopup.isVisible() || isDoorActionActive) return;
                clickCountVaso++;
                if (clickCountVaso == 1) {
                    mostraMessaggio("Sembra essere vuoto.");
                } else {
                    ratPopup.setVisible(true);
                    ratPopup.toFront();
                }
            }
        });

        btnPorta.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (ratPopup.isVisible() || isDoorActionActive) return;
                boolean haLeChiavi = GameManager.getInstance().listaOggetti.contains("Chiavi Appartamento", false);

                if (!haLeChiavi) {
                    mostraMessaggio("La porta è chiusa. Devo trovare le chiavi.");
                } else {
                    // Mostra il rettangolo popup della porta e avvia il timer di 1 secondo
                    portaPopup.setVisible(true);
                    portaPopup.toFront();
                    isDoorActionActive = true;
                    doorActionTimer = 0;
                }
            }
        });

        stageStanza.addActor(btnPorta);
        stageStanza.addActor(btnNotaHotspot);
        stageStanza.addActor(btnZerbinoHotspot);
        stageStanza.addActor(btnVasoHotspot);
        stageStanza.addActor(messageLabel);

        hud = new HudMenu();

        com.badlogic.gdx.InputMultiplexer multiplexer = new com.badlogic.gdx.InputMultiplexer();
        multiplexer.addProcessor(hud.stage);
        multiplexer.addProcessor(stageStanza);
        Gdx.input.setInputProcessor(multiplexer);
    }

    private Texture createSolidColorTexture(Color color) {
        com.badlogic.gdx.graphics.Pixmap pixmap = new com.badlogic.gdx.graphics.Pixmap(1, 1, com.badlogic.gdx.graphics.Pixmap.Format.RGBA8888);
        pixmap.setColor(color);
        pixmap.fill();
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return texture;
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

        // Gestione del timer di 1 secondo per l'uscita dopo l'uso della chiave
        if (isDoorActionActive) {
            doorActionTimer += delta;
            if (doorActionTimer >= 2.0f) {
                System.out.println ("=== APERTURA PORTA COMPLETATA: USCITA DAL GIOCO ===");
                game.setScreen(new RoomONeScreen(game));
                //Gdx.app.exit();
            }
        }

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
        if (hud != null) {
            hud.dispose();
        }
        if (texSfondo != null) {
            texSfondo.dispose();
        }
        if (texRatto != null) {
            texRatto.dispose();
        }
        if (texManoChiave != null) {
            texManoChiave.dispose();
        }
    }
}
