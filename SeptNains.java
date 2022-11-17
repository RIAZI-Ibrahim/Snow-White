// -*- coding: utf-8 -*-

import java.util.ArrayList;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;

public class SeptNains {
    final static BlancheNeige bn = new BlancheNeige();
    final static int nbNains = 7;
    final static String noms [] = {"Simplet", "Dormeur",  "Atchoum", "Joyeux", "Grincheux",
                                   "Prof", "Timide"};
  
    public static void main(String[] args) throws InterruptedException {
        affiche("Début du programme");
        final Nain nain [] = new Nain [nbNains];
        for(int i = 0; i < nbNains; i++) nain[i] = new Nain(noms[i]);
        for(int i = 0; i < nbNains; i++) nain[i].start();
        // L'interruption des nains au bout de 5 secondes.
        Thread.sleep(5000);
        affiche("Interruption des 7 nains.");
        for(int i = 0; i < nbNains; i++) {
            nain[i].interrupt();
        }
        //////////////////////////////////////////////
        // Attendre les nains termine leurs exécutions et l'affichage du message final.
        for(int i = 0; i < nbNains; i++) {
            nain[i].join();
        }
        affiche("Tous les nains ont terminé.");
        //////////////////////////////////////////////
    }

    static void affiche(String message) {
        SimpleDateFormat sdf=new SimpleDateFormat("'['hh'h 'mm'mn 'ss','SSS's] '");  
        Date heure = new Date(System.currentTimeMillis());
        System.out.println(sdf.format(heure) + "[" + Thread.currentThread().getName() + "] "
                           + message + ".");        
    }
    
    static class Nain extends Thread {
        public Nain(String nom) {
            this.setName(nom);
        }
        public void run() {
            while(!this.isInterrupted()) {
                bn.requérir();
                try {
                    bn.accéder();
                } catch (InterruptedException e) {
                    //e.printStackTrace();
                    //Terminer l'exécution du code du nain après son interruption (Relever le statut d'interruption).
                    Thread.currentThread().interrupt();
                    break;
                    //////////////////////////////////////////////////////////////
                }
                if (!this.isInterrupted()) affiche("a un accès (exclusif) à Blanche-Neige");
                try {
                   sleep(2000);
                } catch (InterruptedException e) {
                    //e.printStackTrace();
                    //Conserver le privilège du nain après son interruption.
                   try {
                        sleep(2000);
                    } catch (InterruptedException ex) {}
                    ///////////////////////////////////////////////////////////
                    // Terminer l'exécution du code du nain après son interruption (Relever le statut d'interruption).
                    Thread.currentThread().interrupt();
                    ///////////////////////////////////////////////////////////
                }
                affiche("s'apprête à quitter Blanche-Neige");
                bn.relâcher();
            }
            // L'affichage du message d'adieu du nain.
            affiche("dit \"Au revoir !\"");
            /////////////////////////////////////////
        }
    }
    
    static class BlancheNeige {
        private volatile boolean libre = true;     // Initialement, Blanche-Neige est libre.
        //La fille d'attente.
        private volatile LinkedList<String> fileAttent = new LinkedList<>();
        public synchronized void requérir() {
            affiche("veut un accès exclusif à la ressource");
            //Ajouter le nain a la fille d'attente.
            this.fileAttent.addLast(Thread.currentThread().getName());
            //System.out.println(this.fileAttent);
        }
        public synchronized void accéder() throws InterruptedException {
            while ( ! libre || Thread.currentThread().getName() != this.fileAttent.peek())
                wait() ;            // Le nain attend passivement
            libre = false;
            affiche("accède à la ressource");
            //System.out.println(this.fileAttent);
        }
        public synchronized void relâcher() {
            affiche("relâche la ressource");
            notifyAll();
            libre = true;
            //Retirer le nain de la fille d'attente.
            this.fileAttent.poll();
            //System.out.println(this.fileAttent);
        }
    }
}    



/*
  $ java SeptNains
  [09h 02mn 15,517s] [main] Début du programme.
  [09h 02mn 15,562s] [Simplet] veut un accès exclusif à la ressource.
  [09h 02mn 15,563s] [Simplet] accède à la ressource.
  [09h 02mn 15,563s] [Simplet] a un accès (exclusif) à Blanche-Neige.
  [09h 02mn 15,563s] [Joyeux] veut un accès exclusif à la ressource.
  [09h 02mn 15,563s] [Atchoum] veut un accès exclusif à la ressource.
  [09h 02mn 15,563s] [Dormeur] veut un accès exclusif à la ressource.
  [09h 02mn 15,563s] [Timide] veut un accès exclusif à la ressource.
  [09h 02mn 15,563s] [Prof] veut un accès exclusif à la ressource.
  [09h 02mn 15,564s] [Grincheux] veut un accès exclusif à la ressource.
  [09h 02mn 17,568s] [Simplet] s'apprête à quitter Blanche-Neige.
  [09h 02mn 17,568s] [Joyeux] accède à la ressource.
  [09h 02mn 17,569s] [Joyeux] a un accès (exclusif) à Blanche-Neige.
  [09h 02mn 17,569s] [Simplet] relâche la ressource.
  [09h 02mn 17,569s] [Simplet] veut un accès exclusif à la ressource.
  [09h 02mn 17,570s] [Simplet] accède à la ressource.
  [09h 02mn 17,570s] [Simplet] a un accès (exclusif) à Blanche-Neige.
  [09h 02mn 17,570s] [Atchoum] accède à la ressource.
  [09h 02mn 17,570s] [Atchoum] a un accès (exclusif) à Blanche-Neige.
  [09h 02mn 17,570s] [Grincheux] accède à la ressource.
  [09h 02mn 17,571s] [Grincheux] a un accès (exclusif) à Blanche-Neige.
  [09h 02mn 17,571s] [Prof] accède à la ressource.
  [09h 02mn 17,571s] [Prof] a un accès (exclusif) à Blanche-Neige.
  [09h 02mn 17,571s] [Timide] accède à la ressource.
  [09h 02mn 17,572s] [Timide] a un accès (exclusif) à Blanche-Neige.
  [09h 02mn 17,572s] [Dormeur] accède à la ressource.
  ...
*/


/* En remplaçant if par while
  $ java SeptNains
  [09h 05mn 12,134s] [main] Début du programme.
  [09h 05mn 12,174s] [Simplet] veut un accès exclusif à la ressource.
  [09h 05mn 12,174s] [Simplet] accède à la ressource.
  [09h 05mn 12,174s] [Simplet] a un accès (exclusif) à Blanche-Neige.
  [09h 05mn 12,174s] [Timide] veut un accès exclusif à la ressource.
  [09h 05mn 12,174s] [Prof] veut un accès exclusif à la ressource.
  [09h 05mn 12,174s] [Atchoum] veut un accès exclusif à la ressource.
  [09h 05mn 12,175s] [Grincheux] veut un accès exclusif à la ressource.
  [09h 05mn 12,175s] [Joyeux] veut un accès exclusif à la ressource.
  [09h 05mn 12,175s] [Dormeur] veut un accès exclusif à la ressource.
  [09h 05mn 14,175s] [Simplet] s'apprête à quitter Blanche-Neige.
  [09h 05mn 14,176s] [Simplet] relâche la ressource.
  [09h 05mn 14,176s] [Simplet] veut un accès exclusif à la ressource.
  [09h 05mn 14,176s] [Simplet] accède à la ressource.
  [09h 05mn 14,177s] [Simplet] a un accès (exclusif) à Blanche-Neige.
  [09h 05mn 16,182s] [Simplet] s'apprête à quitter Blanche-Neige.
  [09h 05mn 16,183s] [Simplet] relâche la ressource.
  [09h 05mn 16,183s] [Simplet] veut un accès exclusif à la ressource.
  [09h 05mn 16,183s] [Simplet] accède à la ressource.
  [09h 05mn 16,184s] [Simplet] a un accès (exclusif) à Blanche-Neige.
  [09h 05mn 18,184s] [Simplet] s'apprête à quitter Blanche-Neige.
  [09h 05mn 18,184s] [Simplet] relâche la ressource.
  [09h 05mn 18,185s] [Simplet] veut un accès exclusif à la ressource.
  [09h 05mn 18,185s] [Simplet] accède à la ressource.
  [09h 05mn 18,185s] [Simplet] a un accès (exclusif) à Blanche-Neige.
  [09h 05mn 20,189s] [Simplet] s'apprête à quitter Blanche-Neige.
  [09h 05mn 20,189s] [Simplet] relâche la ressource.
  [09h 05mn 20,189s] [Simplet] veut un accès exclusif à la ressource.
  [09h 05mn 20,190s] [Simplet] accède à la ressource.
  [09h 05mn 20,190s] [Simplet] a un accès (exclusif) à Blanche-Neige.
  ...
*/
