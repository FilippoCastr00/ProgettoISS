package com.vfff.progettoiss;


import com.badlogic.gdx.math.Rectangle;

public class InteractableFactory {

    // 1. NOTA A TERRA (Raccoglibile, scompare e aggiorna l'HUD)
    public static void createNoteObject(BaseRoomScreen room, float x, float y, float width, float height, String nomeNota) {
        Rectangle noteHitbox = new Rectangle(x, y, width, height);
        ClickCommand pickUpNote = new ClickCommand() {
            @Override
            public void execute() {
                GameManager.getInstance().aggiungiNota(nomeNota);
                GameManager.getInstance().hasFoundFirstNote = true;
                GameManager.getInstance().notifyHud("SHOW_HUD");
                room.interactables.remove(noteHitbox);
                System.out.println("Nota raccolta: " + nomeNota);
            }
        };
        room.interactables.put(noteHitbox, pickUpNote);
    }

    // 2. ZERBINO (Non scompare, rilascia la carta XIX e le chiavi)
    public static void createZerbinoObject(BaseRoomScreen room, float x, float y, float width, float height, String nomeCarta, String nomeOggetto) {
        Rectangle zerbinoHitbox = new Rectangle(x, y, width, height);
        final boolean[] isLooked = {false};

        ClickCommand inspectZerbino = new ClickCommand() {
            @Override
            public void execute() {
                if (!isLooked[0]) {
                    GameManager.getInstance().aggiungiCarta(nomeCarta);
                    GameManager.getInstance().aggiungiOggetto(nomeOggetto);
                    isLooked[0] = true;
                    System.out.println("Hai trovato le chiavi dell'appartamento e " + nomeCarta);
                    // Qui puoi attivare il popup grafico a schermo
                } else {
                    System.out.println("Sotto lo zerbino non c'è più nulla.");
                }
            }
        };
        room.interactables.put(zerbinoHitbox, inspectZerbino);
    }

    // 3. VASO VUOTO (Interazione a doppio stadio con topo)
    public static void createVasoObject(BaseRoomScreen room, float x, float y, float width, float height) {
        Rectangle vasoHitbox = new Rectangle(x, y, width, height);
        final int[] clickCount = {0};

        ClickCommand inspectVaso = new ClickCommand() {
            @Override
            public void execute() {
                clickCount[0]++;
                if (clickCount[0] == 1) {
                    System.out.println("Sembra essere vuoto.");
                } else {
                    System.out.println("Un topo fa capolino: 'Sicuro di voler entrare vecchio mio? Allora ricorda dove nascondi le chiavi'.");
                    // Qui puoi attivare l'immagine del topo e la linea di dialogo
                }
            }
        };
        room.interactables.put(vasoHitbox, inspectVaso);
    }

    // 4. PORTA D'INGRESSO CENTRALE (Verifica se possiedi le chiavi)
    public static void createPortaObject(BaseRoomScreen room, float x, float y, float width, float height) {
        Rectangle portaHitbox = new Rectangle(x, y, width, height);

        ClickCommand inspectPorta = new ClickCommand() {
            @Override
            public void execute() {
                boolean haLeChiavi = GameManager.getInstance().listaOggetti.contains("Chiavi Arrugginite", false);

                if (!haLeChiavi) {
                    System.out.println("Non posso sfondarla, devo trovare le chiavi.");
                } else {
                    System.out.println("Usa chiavi -> La porta si apre! (Passaggio alla stanza successiva)");
                    // Logica di cambio stanza verso l'interno dell'appartamento
                }
            }
        };
        room.interactables.put(portaHitbox, inspectPorta);
    }
}
