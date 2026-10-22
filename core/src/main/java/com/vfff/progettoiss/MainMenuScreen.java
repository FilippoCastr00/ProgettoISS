package com.vfff.progettoiss;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class MainMenuScreen implements Screen {
    private Main game;
    private Stage stage;

    private Texture sfondoPulsanteNormale;
    private Texture sfondoPulsantePremuto;
    private Label.LabelStyle stileTestoNormale;
    private Label.LabelStyle stileTitolo;

    public MainMenuScreen(Main game) {
        this.game = game;
        stage = new Stage(new FitViewport(1280, 720));
        Gdx.input.setInputProcessor(stage);

        // 1. SETUP DEI FONT (Stile 8-bit scalato)
        BitmapFont font = new BitmapFont();
        stileTestoNormale = new Label.LabelStyle(font, Color.LIGHT_GRAY);
        stileTitolo = new Label.LabelStyle(font, Color.RED); // Titolo rosso sangue

        // 2. SETUP DEGLI SFONDI DEI BOTTONI (Generati proceduralmente per un look dark)
        Pixmap pixNormal = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixNormal.setColor(0.1f, 0.1f, 0.1f, 0f); // Trasparente di base
        pixNormal.fill();
        sfondoPulsanteNormale = new Texture(pixNormal);
        pixNormal.dispose();

        Pixmap pixDown = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixDown.setColor(0.4f, 0.0f, 0.0f, 0.8f); // Rosso scuro quando clicchi
        pixDown.fill();
        sfondoPulsantePremuto = new Texture(pixDown);
        pixDown.dispose();

        // 3. COSTRUZIONE DELL'INTERFACCIA
        Table tabellaPrincipale = new Table();
        tabellaPrincipale.setFillParent(true);
        tabellaPrincipale.center();

        // IL TITOLO: Animato per "respirare" (scale su e giù, fade colore)
        Label lblTitolo = new Label("ECHOES OF J.R.", stileTitolo);
        lblTitolo.setFontScale(4.0f); // Molto grande
        lblTitolo.setAlignment(Align.center);

        // Animazione inquieta per il titolo
        lblTitolo.addAction(Actions.forever(
            Actions.sequence(
                Actions.alpha(0.6f, 1.5f), // Sfuma
                Actions.alpha(1.0f, 1.5f)  // Torna nitido
            )
        ));

        // I PULSANTI
        Button btnNuovaPartita = creaBottoneMenu("NUOVA PARTITA");
        Button btnRiconoscimenti = creaBottoneMenu("RICONOSCIMENTI");

        // LOGICA DEI PULSANTI
        btnNuovaPartita.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                System.out.println("Inizio Nuova Partita...");
                // Passa alla Stanza -1!
                game.setScreen(new RoomMinusOneScreen(game));
            }
        });

        btnRiconoscimenti.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                System.out.println("Schermata riconoscimenti non ancora implementata.");
                // Effetto "tremolio" se provi a cliccare qualcosa non disponibile
                btnRiconoscimenti.addAction(Actions.sequence(
                    Actions.moveBy(10, 0, 0.05f), Actions.moveBy(-20, 0, 0.05f),
                    Actions.moveBy(20, 0, 0.05f), Actions.moveBy(-10, 0, 0.05f)
                ));
            }
        });

        // IMPAGINAZIONE
        tabellaPrincipale.add(lblTitolo).padBottom(100).row();
        tabellaPrincipale.add(btnNuovaPartita).width(400).height(80).padBottom(30).row();
        tabellaPrincipale.add(btnRiconoscimenti).width(400).height(80).row();

        stage.addActor(tabellaPrincipale);
    }

    private Button creaBottoneMenu(String testo) {
        Button.ButtonStyle stile = new Button.ButtonStyle();
        stile.up = new TextureRegionDrawable(new TextureRegion(sfondoPulsanteNormale));
        stile.down = new TextureRegionDrawable(new TextureRegion(sfondoPulsantePremuto));
        stile.over = new TextureRegionDrawable(new TextureRegion(sfondoPulsantePremuto)); // Si illumina se ci passi sopra col mouse

        Button bottone = new Button(stile);
        Label lbl = new Label(testo, stileTestoNormale);
        lbl.setFontScale(1.8f);
        lbl.setAlignment(Align.center);
        lbl.setTouchable(Touchable.disabled);

        bottone.add(lbl).expand().fill();
        return bottone;
    }

    @Override
    public void show() { }

    @Override
    public void render(float delta) {
        // Sfondo completamente nero e vuoto
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override public void pause() { }
    @Override public void resume() { }
    @Override public void hide() { }

    @Override
    public void dispose() {
        stage.dispose();
        sfondoPulsanteNormale.dispose();
        sfondoPulsantePremuto.dispose();
    }
}
