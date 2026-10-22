package com.vfff.progettoiss;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class HudMenu implements GameManager.GameObserver {

    public Stage stage;
    private Table hudInBasso;
    private Table menuPrincipale;
    private Table pannelloListaNote;
    private Table pannelloCarte;
    private Table pannelloInventario;
    private Table pannelloNota;
    private Table pannelloDettaglio;
    private Image immagineDettaglio;
    private Table pannelloPrecedente;

    // Contenitori interni dinamici
    private Table listaDinamicaNote;
    private Table listaDinamicaCarte;
    private Table listaDinamicaInventario;

    // Texture per sfondi interfaccia
    private Texture sfondoPannello;
    private Texture sfondoPremuto;
    private Texture sfondoTrasparente;

    // Texture oggetti e carte
    private Texture texCarta;
    private Texture texChiavi;
    private Texture texPolaroid;
    private Texture texTrinket;
    private Texture texBottle;
    private Texture texKeyItem;

    private Label.LabelStyle stileTitoli;

    // Gestione dell'oggetto correntemente visualizzato nel dettaglio
    private String oggettoCorrenteDettaglio = null;
    private Button btnButta;

    public HudMenu() {
        stage = new Stage(new FitViewport(1280, 720));
        stage.getViewport().update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);

        BitmapFont font = new BitmapFont();
        font.getData().setScale(1.5f);
        stileTitoli = new Label.LabelStyle(font, Color.WHITE);

        // Creazione Sfondi procedurali
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(0f, 0f, 0f, 0.8f);
        pixmap.fill();
        sfondoPannello = new Texture(pixmap);
        pixmap.dispose();

        Pixmap pixmapDown = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmapDown.setColor(0.3f, 0.3f, 0.3f, 0.9f);
        pixmapDown.fill();
        sfondoPremuto = new Texture(pixmapDown);
        pixmapDown.dispose();

        Pixmap pixTras = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixTras.setColor(0f, 0f, 0f, 0f);
        pixTras.fill();
        sfondoTrasparente = new Texture(pixTras);
        pixTras.dispose();

        // Caricamento Texture esistenti e nuovi sprite dei ricordi
        texCarta = new Texture("tarotHangedMan.jpg");
        texChiavi = new Texture("chiavi-1.jpg");
        texPolaroid = new Texture("item_polaroid.jpg");
        texTrinket = new Texture("item_box.jpg");
        texBottle = new Texture("item_bottle.jpg");
        texKeyItem = new Texture("item_key.jpg");

        creaPannelloNota();
        creaPannelloListaNote();
        creaPannelloCarte();
        creaPannelloInventario();
        creaPannelloDettaglio();
        creaMenuPrincipale();
        creaMenuInBasso();

        GameManager.getInstance().setHudListener(this);
        if (GameManager.getInstance().hasFoundFirstNote) {
            hudInBasso.setVisible(true);
        }
    }

    private Button creaBottone(String testo) {
        Button.ButtonStyle stile = new Button.ButtonStyle();
        stile.up = new TextureRegionDrawable(new TextureRegion(sfondoPannello));
        stile.down = new TextureRegionDrawable(new TextureRegion(sfondoPremuto));

        Button bottone = new Button(stile);
        Label lbl = new Label(testo, stileTitoli);
        lbl.setAlignment(Align.center);
        lbl.setTouchable(Touchable.disabled);

        bottone.add(lbl).expand().fill();
        return bottone;
    }

    private Label creaTesto(String testo) {
        Label lbl = new Label(testo, stileTitoli);
        lbl.setAlignment(Align.center);
        lbl.setTouchable(Touchable.disabled);
        return lbl;
    }

    private void proteggiSfondo(Table tabella) {
        tabella.setBackground(new TextureRegionDrawable(new TextureRegion(sfondoTrasparente)));
        tabella.setTouchable(Touchable.enabled);
        tabella.addListener(new ClickListener() { });
    }

    // ==========================================
    // 1. MENU PRINCIPALE
    // ==========================================
    private void creaMenuPrincipale() {
        menuPrincipale = new Table();
        menuPrincipale.setFillParent(true);
        menuPrincipale.center();
        proteggiSfondo(menuPrincipale);

        Button btnCarte = creaBottone("Carte");
        Button btnNote = creaBottone("Note");
        Button btnInventario = creaBottone("Inventario");
        Button btnChiudi = creaBottone("X Chiudi");

        btnCarte.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent event, Actor actor) {
                menuPrincipale.setVisible(false);
                ricostruisciListeDinamiche();
                pannelloCarte.setVisible(true);
            }
        });

        btnNote.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent event, Actor actor) {
                menuPrincipale.setVisible(false);
                ricostruisciListeDinamiche();
                pannelloListaNote.setVisible(true);
            }
        });

        btnInventario.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent event, Actor actor) {
                menuPrincipale.setVisible(false);
                ricostruisciListeDinamiche();
                pannelloInventario.setVisible(true);
            }
        });

        btnChiudi.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent event, Actor actor) {
                menuPrincipale.setVisible(false);
            }
        });

        Table contenitoreBottoni = new Table();
        contenitoreBottoni.add(btnCarte).width(200).height(350).pad(20);
        contenitoreBottoni.add(btnNote).width(200).height(350).pad(20);
        contenitoreBottoni.add(btnInventario).width(200).height(350).pad(20);
        contenitoreBottoni.row();
        contenitoreBottoni.add(btnChiudi).colspan(3).width(250).height(80).padTop(40);

        menuPrincipale.add(contenitoreBottoni);
        menuPrincipale.setVisible(false);
        stage.addActor(menuPrincipale);
    }

    // ==========================================
    // 2. GENERATORE DINAMICO DEI PANNELLI
    // ==========================================
    private void ricostruisciListeDinamiche() {
        // --- NOTE ---
        listaDinamicaNote.clearChildren();
        if (GameManager.getInstance().listaNote.isEmpty()) {
            listaDinamicaNote.add(creaTesto("Nessuna nota ancora trovata.")).padBottom(20);
        } else {
            for (String nota : GameManager.getInstance().listaNote) {
                Button btnNota = creaBottone(nota);
                btnNota.addListener(new ChangeListener() {
                    @Override public void changed(ChangeEvent event, Actor actor) {
                        pannelloListaNote.setVisible(false);
                        pannelloNota.setVisible(true);
                    }
                });
                listaDinamicaNote.add(btnNota).width(500).height(80).padBottom(15).row();
            }
        }

        // --- CARTE ---
        listaDinamicaCarte.clearChildren();
        if (GameManager.getInstance().listaCarte.isEmpty()) {
            listaDinamicaCarte.add(creaTesto("Nessuna carta ancora trovata.")).padBottom(20);
        } else {
            for (String carta : GameManager.getInstance().listaCarte) {
                Button btnCarta = creaBottone(carta);
                btnCarta.addListener(new ChangeListener() {
                    @Override public void changed(ChangeEvent event, Actor actor) {
                        if (carta.equals("XII - L'Impiccato")) {
                            immagineDettaglio.setDrawable(new TextureRegionDrawable(new TextureRegion(texCarta)));
                        }
                        btnButta.setVisible(false); // Le carte non si possono buttare
                        oggettoCorrenteDettaglio = null;
                        pannelloPrecedente = pannelloCarte;
                        pannelloCarte.setVisible(false);
                        pannelloDettaglio.setVisible(true);
                    }
                });
                listaDinamicaCarte.add(btnCarta).width(400).height(80).padBottom(15).row();
            }
        }

        // --- INVENTARIO / OGGETTI ---
        listaDinamicaInventario.clearChildren();
        if (GameManager.getInstance().listaOggetti.isEmpty()) {
            listaDinamicaInventario.add(creaTesto("Borsa vuota.")).padBottom(20);
        } else {
            for (final String oggetto : GameManager.getInstance().listaOggetti) {
                Button btnOggetto = creaBottone(oggetto);
                btnOggetto.addListener(new ChangeListener() {
                    @Override public void changed(ChangeEvent event, Actor actor) {
                        oggettoCorrenteDettaglio = oggetto;

                        // Riconoscimento dei 4 oggetti del lutto
                        boolean eOggettoGrief = oggetto.equals("Polaroid Strappata")
                            || oggetto.equals("Carillon Arrugginito")
                            || oggetto.equals("Bottiglia Vuota")
                            || oggetto.equals("Vecchia Chiave");

                        // Il tasto "Butta" compare solo per gli oggetti eliminabili
                        btnButta.setVisible(eOggettoGrief);

                        // Assegnazione Texture corretta
                        if (oggetto.equals("Chiavi Appartamento")) {
                            immagineDettaglio.setDrawable(new TextureRegionDrawable(new TextureRegion(texChiavi)));
                        } else if (oggetto.equals("Polaroid Strappata")) {
                            immagineDettaglio.setDrawable(new TextureRegionDrawable(new TextureRegion(texPolaroid)));
                        } else if (oggetto.equals("Carillon Arrugginito")) {
                            immagineDettaglio.setDrawable(new TextureRegionDrawable(new TextureRegion(texTrinket)));
                        } else if (oggetto.equals("Bottiglia Vuota")) {
                            immagineDettaglio.setDrawable(new TextureRegionDrawable(new TextureRegion(texBottle)));
                        } else if (oggetto.equals("Vecchia Chiave")) {
                            immagineDettaglio.setDrawable(new TextureRegionDrawable(new TextureRegion(texKeyItem)));
                        }

                        pannelloPrecedente = pannelloInventario;
                        pannelloInventario.setVisible(false);
                        pannelloDettaglio.setVisible(true);
                    }
                });
                listaDinamicaInventario.add(btnOggetto).width(400).height(80).padBottom(15).row();
            }
        }
    }

    // ==========================================
    // 3. PANNELLO CARTE
    // ==========================================
    private void creaPannelloCarte() {
        pannelloCarte = new Table();
        pannelloCarte.setFillParent(true);
        pannelloCarte.center();
        proteggiSfondo(pannelloCarte);

        Table contenitore = new Table();
        Label titolo = creaTesto("--- CARTE ---");
        listaDinamicaCarte = new Table();

        Button btnIndietro = creaBottone("<- Indietro");
        btnIndietro.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent event, Actor actor) {
                pannelloCarte.setVisible(false);
                menuPrincipale.setVisible(true);
            }
        });

        contenitore.add(titolo).padBottom(30).row();
        contenitore.add(listaDinamicaCarte).padBottom(30).row();
        contenitore.add(btnIndietro).width(250).height(80);

        pannelloCarte.add(contenitore);
        pannelloCarte.setVisible(false);
        stage.addActor(pannelloCarte);
    }

    // ==========================================
    // 4. PANNELLO INVENTARIO
    // ==========================================
    private void creaPannelloInventario() {
        pannelloInventario = new Table();
        pannelloInventario.setFillParent(true);
        pannelloInventario.center();
        proteggiSfondo(pannelloInventario);

        Table contenitore = new Table();
        Label titolo = creaTesto("--- INVENTARIO ---");
        listaDinamicaInventario = new Table();

        Button btnIndietro = creaBottone("<- Indietro");
        btnIndietro.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent event, Actor actor) {
                pannelloInventario.setVisible(false);
                menuPrincipale.setVisible(true);
            }
        });

        contenitore.add(titolo).padBottom(30).row();
        contenitore.add(listaDinamicaInventario).padBottom(30).row();
        contenitore.add(btnIndietro).width(250).height(80);

        pannelloInventario.add(contenitore);
        pannelloInventario.setVisible(false);
        stage.addActor(pannelloInventario);
    }

    // ==========================================
    // 5. PANNELLO LISTA NOTE
    // ==========================================
    private void creaPannelloListaNote() {
        pannelloListaNote = new Table();
        pannelloListaNote.setFillParent(true);
        pannelloListaNote.center();
        proteggiSfondo(pannelloListaNote);

        Table contenitore = new Table();
        Label titolo = creaTesto("--- ELENCO NOTE ---");
        listaDinamicaNote = new Table();

        Button btnIndietro = creaBottone("<- Indietro");
        btnIndietro.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent event, Actor actor) {
                pannelloListaNote.setVisible(false);
                menuPrincipale.setVisible(true);
            }
        });

        contenitore.add(titolo).padBottom(30).row();
        contenitore.add(listaDinamicaNote).padBottom(30).row();
        contenitore.add(btnIndietro).width(250).height(80);

        pannelloListaNote.add(contenitore);
        pannelloListaNote.setVisible(false);
        stage.addActor(pannelloListaNote);
    }

    // ==========================================
    // 6. TESTO DELLA NOTA
    // ==========================================
    private void creaPannelloNota() {
        pannelloNota = new Table();
        pannelloNota.setFillParent(true);
        pannelloNota.center();
        proteggiSfondo(pannelloNota);

        Table boxLettera = new Table();
        boxLettera.setBackground(new TextureRegionDrawable(new TextureRegion(sfondoPannello)));
        Label testoNota = creaTesto("Avresti dovuto capirlo.\n\n- J.R.");
        boxLettera.add(testoNota).pad(40);

        Button btnChiudiNota = creaBottone("X Chiudi Nota");
        btnChiudiNota.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent event, Actor actor) {
                pannelloNota.setVisible(false);
                menuPrincipale.setVisible(true);
            }
        });

        pannelloNota.add(boxLettera).width(600).height(350).padBottom(40).row();
        pannelloNota.add(btnChiudiNota).width(250).height(80);

        pannelloNota.setVisible(false);
        stage.addActor(pannelloNota);
    }

    // ==========================================
    // 7. ICONA HUD
    // ==========================================
    private void creaMenuInBasso() {
        hudInBasso = new Table();
        hudInBasso.setFillParent(true);
        hudInBasso.bottom().right();

        Button btnApriMenu = creaBottone("MENU");
        btnApriMenu.addListener(new ChangeListener() {
            @Override public void changed(ChangeEvent event, Actor actor) {
                menuPrincipale.setVisible(true);
            }
        });

        hudInBasso.add(btnApriMenu).width(150).height(80).pad(30);
        hudInBasso.setVisible(false);
        stage.addActor(hudInBasso);
    }

    // ==========================================
    // 8. VISORE DETTAGLIO CON "X CHIUDI" E "BUTTA"
    // ==========================================
    private void creaPannelloDettaglio() {
        pannelloDettaglio = new Table();
        pannelloDettaglio.setFillParent(true);
        pannelloDettaglio.center();
        proteggiSfondo(pannelloDettaglio);

        immagineDettaglio = new Image();
        immagineDettaglio.setScaling(com.badlogic.gdx.utils.Scaling.fit);

        Button btnChiudi = creaBottone("X Chiudi");
        btnChiudi.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                pannelloDettaglio.setVisible(false);
                if (pannelloPrecedente != null) {
                    pannelloPrecedente.setVisible(true);
                } else {
                    menuPrincipale.setVisible(true);
                }
            }
        });

        btnButta = creaBottone("Butta");
        btnButta.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (oggettoCorrenteDettaglio != null) {
                    System.out.println("Oggetto gettato nel cestino: " + oggettoCorrenteDettaglio);
                    GameManager.getInstance().rimuoviOggetto(oggettoCorrenteDettaglio);
                    oggettoCorrenteDettaglio = null;
                }
                pannelloDettaglio.setVisible(false);
                ricostruisciListeDinamiche();
                pannelloInventario.setVisible(true);
            }
        });

        Table rigaBottoni = new Table();
        rigaBottoni.add(btnChiudi).width(220).height(80).padRight(30);
        rigaBottoni.add(btnButta).width(220).height(80);

        pannelloDettaglio.add(immagineDettaglio).size(450, 450).padBottom(30).row();
        pannelloDettaglio.add(rigaBottoni);

        pannelloDettaglio.setVisible(false);
        stage.addActor(pannelloDettaglio);
    }

    @Override
    public void onEvent(String eventType) {
        if (eventType.equals("SHOW_HUD")) {
            hudInBasso.setVisible(true);
        }
    }

    public void render() {
        if (Gdx.graphics.getWidth() != stage.getViewport().getScreenWidth() ||
            Gdx.graphics.getHeight() != stage.getViewport().getScreenHeight()) {
            stage.getViewport().update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
        }

        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
    }

    public void dispose() {
        stage.dispose();
        if (sfondoPannello != null) sfondoPannello.dispose();
        if (sfondoPremuto != null) sfondoPremuto.dispose();
        if (sfondoTrasparente != null) sfondoTrasparente.dispose();

        if (texCarta != null) texCarta.dispose();
        if (texChiavi != null) texChiavi.dispose();
        if (texPolaroid != null) texPolaroid.dispose();
        if (texTrinket != null) texTrinket.dispose();
        if (texBottle != null) texBottle.dispose();
        if (texKeyItem != null) texKeyItem.dispose();
    }
}
