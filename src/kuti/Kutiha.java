/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kutiha;

import java.io.FileNotFoundException;
import java.io.IOException;
import static java.lang.System.in;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

/**
 *
 * @author User
 */
public class Kutiha {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws SQLException, FileNotFoundException, IOException {
        // TODO code application logic here

        try {
            // The newInstance() call is a work around for some
            // broken Java implementations

            Class.forName("com.mysql.jdbc.Driver").newInstance();
        } catch (Exception ex) {
            // handle the error
        }

        Connection conn = null;

        System.out.println("Kulunvalvonnan Tietokannan Hallintasovellus (KUTIHA) 0.1");

        Scanner scanner = new Scanner(System.in);
        Scanner scanner1 = new Scanner(System.in);
        //Scanner scanner1 = new Scanner(System.in);
        kysely k = new kysely();
        int valinta;
        boolean quit = false;
        String tallennus;
        int hakuID;

        do {
            System.out.println("\nValinnat:\n");
            System.out.println("1. Näytä kaikki käyttäjätiedot");
            System.out.println("2. Tapahtumahaku henkilön mukaan");
            System.out.println("3. TODO");
            System.out.println("4. TODO");
            System.out.println("5. Poistu \n \n");
            System.out.print("Anna valinta: ");
            valinta = scanner.nextInt();

            switch (valinta) {
                case 1:
                    k.kyselyUsers();
                    break;
                case 2:                   
                    System.out.println("Syötä käyttäjän ID");
                    hakuID = scanner1.nextInt();
                    k.kyselyTapahtumatByID(hakuID);
                    break;
                case 3:
                    System.out.println("TODO");
                    break;
                case 4:
                    System.out.println("TODO");
                    break;
                case 5:
                    // quit.
                    quit = true;
                    break;
                default:
                    for (int i = 0; i < 50; ++i) System.out.println();
                    System.out.println("Tuntematon valinta");               
            }
        } while (!quit);
    }
}
