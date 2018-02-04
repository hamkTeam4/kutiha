/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package kutiha;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import java.sql.Connection;
import java.sql.DriverManager;
import static java.sql.JDBCType.VARCHAR;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import static java.sql.Types.VARCHAR;
import java.util.Scanner;

/**
 *
 * @author User
 */
public class kysely {

    private Connection conn = null;
    private Statement stmt = null;
    private ResultSet rs = null;
    private String sqlMesg;
    private String sqlState;
    private String vendorError;
    private final String kayttajat = "kayttajat.txt"; //tallennettavien tekstitiedostojen nimet ja sijainnit
    private final String tapahtumat_by_ID = "tapahtumat_temp.txt"; //tällä hetkellä tallentuvat javaprojektin kansioon

    
    //mysql-yhteyden avaaminen
    public void loadDriver() {
        try {
            conn = DriverManager.getConnection("jdbc:mysql://localhost/kuti?"
                    + "user=root&password=");

        } catch (SQLException ex) {
            // handle any errors
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
        }
    }

    
  // kysely käyttäjistä tietokannasta
    public void kyselyUsers() throws SQLException, IOException {

        loadDriver();

        try {
            stmt = conn.createStatement();
            String sql;
            sql = "SELECT ID, Nimi, PIN, OikeusSisa, OikeusUlko FROM kuti.users";
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                //Haku sarakkeiden nimellä
                int id = rs.getInt("ID");
                int pin = rs.getInt("PIN");
                String Nimi = rs.getString("Nimi");
                String OikeusSisa = rs.getString("OikeusSisa");
                String OikeusUlko = rs.getString("OikeusUlko");

                //Tulosten printtaus
                System.out.print("ID: " + id);
                System.out.print(", Nimi: " + Nimi);
                System.out.print(", PIN: " + pin);
                System.out.print(", OikeusSisa: " + OikeusSisa);
                System.out.println(", OikeusUlko: " + OikeusUlko);

            }
            
            /* 
            Yksinkertainen switch-case tapahtuma, kysyy tallennetaanko 
            Tuttu c-kielen perusteista
            */
            System.out.println("\nTallennetaanko? k/e");
            Scanner scanner1 = new Scanner(System.in);
            String tallennus;
            tallennus = scanner1.nextLine();

            switch (tallennus) {
                case "k":
                    /*
                    Tällä hetkellä tallennus-vaihtoehto tekee uuden haun tietokannalta
                    Vaatii lisää tutkimista
                    */
                    stmt = conn.createStatement();
                    sql = "SELECT ID, Nimi, PIN, OikeusSisa, OikeusUlko FROM kuti.users";
                    rs = stmt.executeQuery(sql);
                    
                    /*
                    Kuten aiemmin käytettiin println tulosten näyttämiseen, 
                    tässä tapauksessa tehdään sama, mutta tekstitiedostoon
                    */
                    
                    try (BufferedWriter out = new BufferedWriter(new FileWriter(kayttajat))) {
                        while (rs.next()) {
                            out.write("ID: " + Integer.toString(rs.getInt("ID")));
                            out.write(", Nimi: " + rs.getString("Nimi"));
                            out.write(", PIN: " + Integer.toString(rs.getInt("pin")));
                            out.write(", OikeusSisa: " + rs.getString("OikeusSisa"));
                            out.write(", OikeusUlko: " + rs.getString("OikeusUlko"));
                            out.newLine();
                        }
                        //Tulostetaan 50-riviä tyhjää, miellyttää silmää ja tekee käyttöliittymämäisen tunnelman konsolisovellukseen
                        for (int i = 0; i < 50; ++i) {
                            System.out.println();
                        }
                        System.out.println("Tallennettu kayttajat.txt");
                    }

                    break;
                case "e":
                    break;
                default:
                    System.out.println("Tuntematon valinta");
            }
        } catch (SQLException ex) {
            // handle any errors
            sqlMesg = "SQLException: " + ex.getMessage();
            sqlState = "SQLState: " + ex.getSQLState();
            vendorError = "VendorError: " + ex.getErrorCode();
        } finally {
            // it is a good idea to release
            // resources in a finally{} block
            // in reverse-order of their creation
            // if they are no-longer needed

            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException sqlEx) {
                } // ignore

                rs = null;
            }

            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException sqlEx) {
                } // ignore

                stmt = null;
            }
        }

    }

    /**
     *
     * @param ID_in
     * @throws SQLException
     * @throws IOException
     */
    
    //Uusi metodi tapahtumien hakuun käyttäjä-ID:n perusteella. Käyttäjä syöttää ID:n pääohjelmassa ja tulee luokalle ID_in inttinä
    public void kyselyTapahtumatByID(int ID_in) throws SQLException, IOException {

        loadDriver();

        //käytetään tietoturvallista PreparedStatementtia
        try {
            PreparedStatement haeID = conn.prepareStatement("SELECT pvm, aika, ovi_ID, user_ID, nimi, virheet FROM kuti.tapahtumat WHERE user_ID=?");
            // käytetään hakua rajaavaa WHERE, jossa user_ID=?
            haeID.setInt(1, ID_in);
            // ylemmällä rivillä annetaan haun ?-merkille arvo, ID_in. 1 tarkoittaa ekaa ?-merkkiä ja ID_in mikä arvo siihen laitetaan
            ResultSet rsID = haeID.executeQuery();

            while (rsID.next()) {
                //Haku sarakkeiden nimellä
                String pvm = rsID.getString("pvm");
                String aika = rsID.getString("aika");
                int userID = rsID.getInt("user_ID");
                String oviID = rsID.getString("ovi_ID");
                String Nimi = rsID.getString("nimi");
                int virheet = rsID.getInt("virheet");

                //Tulostus tuttuun tapaan
                System.out.print(pvm);
                System.out.print(" " + aika);
                System.out.print(" / ID: " + userID);
                System.out.print(" / Nimi: " + Nimi);
                System.out.print(" / Ovi: " + oviID);
                System.out.println(" / Virheitä: " + virheet);

            }
            System.out.println("\nTallennetaanko? k/e");
            Scanner scanner2 = new Scanner(System.in);
            String tallennusID;
            tallennusID = scanner2.nextLine();

            switch (tallennusID) {
                case "k":
                    //Tallennus kuten aiemmin                   
                    haeID = conn.prepareStatement("SELECT pvm, aika, ovi_ID, user_ID, nimi, virheet FROM kuti.tapahtumat WHERE user_ID=?");
                    haeID.setInt(1, ID_in);
                    rsID = haeID.executeQuery();
                
                    try (BufferedWriter out = new BufferedWriter(new FileWriter(tapahtumat_by_ID))) {
                        while (rsID.next()) {
                            out.write(" " + rsID.getString("pvm"));
                            out.write(" / " + rsID.getString("aika"));
                            out.write(" / ID: " + Integer.toString(rsID.getInt("user_ID")));
                            out.write(" / Nimi: " + rsID.getString("nimi"));
                            out.write(" / Ovi: " + rsID.getString("ovi_ID"));
                            out.write(" / Virheitä: " + Integer.toString(rsID.getInt("virheet")));
                            out.newLine();
                        }
                        for (int i = 0; i < 50; ++i) {
                            System.out.println();
                        }
                        System.out.println("Tallennettu");

                    }
                    /*Tallennus tapahtui tapahtumat_temp.txt tiedostoon, seuraavilla riveillä tiedosto nimetään henkilö-ID:n mukaan
                    Esim tapahtumat_1001.txt */
                    Path yourFile = Paths.get("tapahtumat_temp.txt");
                    Files.move(yourFile, yourFile.resolveSibling("tapahtumat_" + ID_in + ".txt"), REPLACE_EXISTING);

                    break;
                case "e":
                    break;
                default:
                    System.out.println("Tuntematon valinta");
            }
        } catch (SQLException ex) {
            // handle any errors
            sqlMesg = "SQLException: " + ex.getMessage();
            sqlState = "SQLState: " + ex.getSQLState();
            vendorError = "VendorError: " + ex.getErrorCode();
        } finally {
            // it is a good idea to release
            // resources in a finally{} block
            // in reverse-order of their creation
            // if they are no-longer needed

            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException sqlEx) {
                } // ignore

                rs = null;
            }

            if (stmt != null) {
                try {
                    stmt.close();
                } catch (SQLException sqlEx) {
                } // ignore

                stmt = null;
            }
        }

    }
}