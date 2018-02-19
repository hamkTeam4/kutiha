/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kutiha;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.Scanner;

/**
 *
 * @author User
 */
public class Kutiha {

    /**
     * @param args the command line arguments
     * @throws java.sql.SQLException
     * @throws java.io.FileNotFoundException
     */
    public static void main(String[] args) throws SQLException, FileNotFoundException, IOException {

        try {
            // The newInstance() call is a work around for some
            // broken Java implementations

            Class.forName("com.mysql.jdbc.Driver").newInstance();
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException ex) {
            // handle the error
        }

        System.out.println("*********************************************");
        System.out.println("*                                           *");
        System.out.println("* KULUNVALVONTATIETOKANNAN HALLINTASOVELLUS *");
        System.out.println("*                KUTIHA 0.8.1                 *");
        System.out.println("*                                           *");
        System.out.println("*********************************************");

        System.out.println("*********************************************");
        System.out.println("*          (C)2018 HAMK INTIM17A6           *");
        System.out.println("*             Kalliojärvi Alix              *");
        System.out.println("*             Kivioja Miska                 *");
        System.out.println("*             Koivusalo Kimmo               *");
        System.out.println("*             Laitinen Jonne                *");
        System.out.println("*********************************************");

        Scanner scanner = new Scanner(System.in);
        Scanner scanner2 = new Scanner(System.in);
        Scanner scanner3 = new Scanner(System.in);
        Scanner scanner4 = new Scanner(System.in);
        //Scanner scanner1 = new Scanner(System.in);
        kysely k = new kysely();
        int valinta;
        boolean quit = false;
        String tallennus;
        int hakuID;
        String hakuNimi;
        int hakuEvent;

        do {
            System.out.println("_____________________________________________");
            System.out.println("\nValinnat:\n");
            System.out.println("1. Näytä kaikki käyttäjätiedot");
            System.out.println("2. Tapahtumahaku henkilötunnisteen mukaan");
            System.out.println("3. Tapahtumahaku henkilön mukaan");
            System.out.println("4. Tapahtumahaku tapahtumaluokan mukaan");
            System.out.println("5. Poistu");
            System.out.println("6. README.md");
            System.out.println("_____________________________________________\n");
            System.out.print("Anna valinta: ");
            valinta = scanner.nextInt();

            switch (valinta) {
                case 1:
                    k.kyselyUsers();
                    break;
                case 2:
                    System.out.println("Syötä käyttäjän ID");
                    hakuID = scanner2.nextInt();
                    k.kyselyTapahtumatByID(hakuID);
                    break;
                case 3:
                    System.out.println("Käyttäjät:");
                    k.kyselyNimet();
                    System.out.println("\nSyötä nimi: ");
                    hakuNimi = scanner3.nextLine();
                    k.kyselyTapahtumatByNimi(hakuNimi);
                    break;
                case 4:
                    System.out.println("Tapahtumat:\n");
                    System.out.println("1=OPEN");
                    System.out.println("2=ILLEGAL RFID");
                    System.out.println("3=NO ENTRY");
                    System.out.println("4=WRONG PIN\n");
                    System.out.println("Syötä tapahtumaluokka:");
                    hakuEvent = scanner4.nextInt();
                    k.kyselyTapahtumatByTapahtuma(hakuEvent);
                    break;
                case 5:
                    // quit.
                    System.out.println("Olet poistunut sovelluksesta - TURVALLISESTI.");
                    quit = true;
                    break;
                case 6:
                    try {
                        // Printtaa readme.md:n projektin kansiosta                      
                        FileInputStream fstream = new FileInputStream("README.md");
                        // uudet oliot
                        DataInputStream in = new DataInputStream(fstream);
                        BufferedReader br = new BufferedReader(new InputStreamReader(in));
                        String strLine;
                        //Rivi riviltä tekstitiedoston lukeminen
                        while ((strLine = br.readLine()) != null) {                           
                            System.out.println(strLine);
                        }
                        //inputti kiinni
                        in.close();
                    } catch (Exception e) {
                        System.err.println("Error: " + e.getMessage());
                    }
                    break;
                default:
                    for (int i = 0; i < 50; ++i) {
                        System.out.println();
                    }
                    System.out.println("Tuntematon valinta");
            }

        } while (!quit);
    }
}
