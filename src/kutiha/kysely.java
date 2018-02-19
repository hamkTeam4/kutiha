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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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

    //mysql-yhteyden avaaminen:
    public void loadDriver() {
        try {
            conn = DriverManager.getConnection("jdbc:mysql://localhost/kuti?" + "user=root&password=root");

        } catch (SQLException ex) {
            // handle any errors
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
        }
    }

    //Kysely käyttäjistä tietokannasta.
    //Käyttäjän Valinta 1:
    public void kyselyUsers() throws SQLException, IOException {

        loadDriver();

        try {
            stmt = conn.createStatement();
            String sql;
            sql = "SELECT user_ID, name, pin FROM kuti.users";
            rs = stmt.executeQuery(sql);

            while (rs.next()) {
                //Haku sarakkeiden nimellä
                int id = rs.getInt("user_ID");
                int pin = rs.getInt("pin");
                String Nimi = rs.getString("name");

                //Tulosten printtaus
                System.out.print("ID: " + id);
                //System.out.print(" | Nimi: " + Nimi + "\t \t");
                System.out.format(" | Nimi: %-20s", Nimi);
                System.out.println(" | PIN: " + pin);
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
                    sql = "SELECT user_ID, name, pin FROM kuti.users";
                    rs = stmt.executeQuery(sql);


                    /*
                    Kuten aiemmin käytettiin println tulosten näyttämiseen, 
                    tässä tapauksessa tehdään sama, mutta tekstitiedostoon
                     */
                    try (BufferedWriter out = new BufferedWriter(new FileWriter(kayttajat))) {
                        while (rs.next()) {
                            out.write("ID: " + Integer.toString(rs.getInt("user_ID")));
                            //out.write(" | Nimi: " + rs.getString("name"));
                            out.write(" | Nimi: " + String.format("%-20s", rs.getString("name")));
                            out.write(" | PIN: " + Integer.toString(rs.getInt("pin")));
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
    //Uusi metodi tapahtumien hakuun käyttäjä-ID:n perusteella. 
    //Käyttäjä syöttää ID:n pääohjelmassa ja tulee luokalle ID_in inttinä.
    //Käyttäjän valinta 2:
    public void kyselyTapahtumatByID(int ID_in) throws SQLException, IOException {

        loadDriver();

        //käytetään tietoturvallista PreparedStatementtia
        try {
            PreparedStatement haeID = conn.prepareStatement("SELECT log_number, aika, ovi_ID, user_ID, name, event  FROM kuti.tapahtumat WHERE user_ID=?");
            // käytetään hakua rajaavaa WHERE, jossa user_ID=?
            haeID.setInt(1, ID_in);
            // ylemmällä rivillä annetaan haun ?-merkille arvo, ID_in. 1 tarkoittaa ekaa ?-merkkiä ja ID_in mikä arvo siihen laitetaan
            rs = haeID.executeQuery();

            while (rs.next()) {
                //Haku sarakkeiden nimellä
                int log_number = rs.getInt("log_number");
                String aika = rs.getString("aika");
                int userID = rs.getInt("user_ID");
                String oviID = rs.getString("ovi_ID");
                String name = rs.getString("name");
                int event = rs.getInt("event");

                //Tulostus tuttuun tapaan
                System.out.print(String.format("%04d", log_number));
                System.out.print(" | " + aika);
                System.out.print(" | ID: " + userID);
                System.out.print(" | Nimi: " + name);
                System.out.print(" | Ovi: " + oviID);
                System.out.println(" | Tapahtuma: " + event);

            }
            System.out.println("\nTallennetaanko? k/e");
            Scanner scanner2 = new Scanner(System.in);
            String tallennusID;
            tallennusID = scanner2.nextLine();

            switch (tallennusID) {
                case "k":
                    //Tallennus kuten aiemmin                   
                    haeID = conn.prepareStatement("SELECT log_number, aika, ovi_ID, user_ID, name, event  FROM kuti.tapahtumat WHERE user_ID=?");
                    haeID.setInt(1, ID_in);
                    rs = haeID.executeQuery();

                    try (BufferedWriter out = new BufferedWriter(new FileWriter(tapahtumat_by_ID))) {
                        while (rs.next()) {
                            out.write(" " + Integer.toString(rs.getInt("log_number")));
                            out.write(" | " + rs.getString("aika"));
                            out.write(" | ID: " + Integer.toString(rs.getInt("user_ID")));
                            out.write(" | Nimi: " + rs.getString("name"));
                            out.write(" | Ovi: " + rs.getString("ovi_ID"));
                            out.write(" | Tapahtuma: " + Integer.toString(rs.getInt("event")));
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

    //Uusi metodi tapahtumien hakuun käyttäjän nimen perusteella. 
    //Käyttäjän valinta 3:
    //Tulostetaan nimilista:
    public void kyselyNimet() throws SQLException, IOException {

        loadDriver();

        try {

            stmt = conn.createStatement();
            String sql;
            sql = "SELECT name FROM kuti.users";
            rs = stmt.executeQuery(sql);

            while (rs.next()) {
                //Haku sarakkeiden nimellä
                String Nimi = rs.getString("name");
                //Tulosten printtaus
                System.out.println(Nimi);
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

    //Uusi metodi tapahtumien hakuun käyttäjän nimen perusteella. 
    //Käyttäjän valinta 3:
    //Käyttäjä syöttää nimen, jonka mukaan haku toteutetaan.
    public void kyselyTapahtumatByNimi(String Nimi_in) throws SQLException, IOException {

        loadDriver();
        //Nimilistan tulostus päättyy.
        //Suoritetaan valinta, ja tulsotetaan lista valitun henkilön mukaan..
        try {

            PreparedStatement haeNimi = conn.prepareStatement("SELECT log_number, aika, ovi_ID, user_ID, name, event  FROM kuti.tapahtumat WHERE name=?");

            // käytetään hakua rajaavaa WHERE, jossa name=?
            haeNimi.setString(1, Nimi_in);
            // ylemmällä rivillä annetaan haun ?-merkille arvo, name. 1 tarkoittaa ekaa ?-merkkiä ja Nimi_in mikä arvo siihen laitetaan
            rs = haeNimi.executeQuery();

            while (rs.next()) {
                //Haku sarakkeiden nimellä
                int log_number = rs.getInt("log_number");
                String aika = rs.getString("aika");
                int userID = rs.getInt("user_ID");
                String oviID = rs.getString("ovi_ID");
                String name = rs.getString("name");
                int event = rs.getInt("event");

                //Tulostus tuttuun tapaan
                System.out.print(String.format("%04d", log_number));
                System.out.print(" | " + aika);
                System.out.print(" | ID: " + userID);
                System.out.print(" | Nimi: " + name);
                System.out.print(" | Ovi: " + oviID);
                System.out.println(" | Tapahtuma: " + event);

            }

            //3. VALINTA TALLENNUS ALKAA:
            System.out.println("\nTallennetaanko? k/e");
            Scanner scanner3 = new Scanner(System.in);
            String tallennusNIMI;
            tallennusNIMI = scanner3.nextLine();

            switch (tallennusNIMI) {
                case "k":
                    //Tallennus kuten aiemmin                   
                    haeNimi = conn.prepareStatement("SELECT log_number, aika, ovi_ID, user_ID, name, event  FROM kuti.tapahtumat WHERE name=?");
                    haeNimi.setString(1, Nimi_in);
                    rs = haeNimi.executeQuery();

                    try (BufferedWriter out = new BufferedWriter(new FileWriter(tapahtumat_by_ID))) {
                        while (rs.next()) {
                            out.write(" " + Integer.toString(rs.getInt("log_number")));
                            out.write(" | " + rs.getString("aika"));
                            out.write(" | ID: " + Integer.toString(rs.getInt("user_ID")));
                            out.write(" | Nimi: " + rs.getString("name"));
                            out.write(" | Ovi: " + rs.getString("ovi_ID"));
                            out.write(" | Tapahtuma: " + Integer.toString(rs.getInt("event")));
                            out.newLine();
                        }
                        for (int i = 0; i < 50; ++i) {
                            System.out.println();
                        }
                        System.out.println("Tallennettu");

                    }
                    //Tallennus tapahtui tapahtumat_temp.txt tiedostoon, seuraavilla riveillä tiedosto nimetään henkilö-ID:n mukaan
                    //Esim tapahtumat_1001.txt 
                    Path yourFile = Paths.get("tapahtumat_temp.txt");
                     {

                        Files.move(yourFile, yourFile.resolveSibling("tapahtumat_" + Nimi_in + ".txt"), REPLACE_EXISTING);
                    }

                    break;
                case "e":
                    break;
                default:
                    System.out.println("Tuntematon valinta");
            }
            //3.VALINTA TALLENNUS PÄÄTTYY 

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

    public void kyselyTapahtumatByTapahtuma(int Event_in) throws SQLException, IOException {

        loadDriver();

        //käytetään tietoturvallista PreparedStatementtia
        try {
            PreparedStatement haeEvent = conn.prepareStatement("SELECT log_number, aika, ovi_ID, user_ID, name, event  FROM kuti.tapahtumat WHERE event=?");
            // käytetään hakua rajaavaa WHERE, jossa event=?
            haeEvent.setInt(1, Event_in);
            // ylemmällä rivillä annetaan haun ?-merkille arvo, Event_in. 1 tarkoittaa ekaa ?-merkkiä ja Event_in mikä arvo siihen laitetaan
            rs = haeEvent.executeQuery();

            while (rs.next()) {
                //Haku sarakkeiden nimellä
                int log_number = rs.getInt("log_number");
                String aika = rs.getString("aika");
                int userID = rs.getInt("user_ID");
                String oviID = rs.getString("ovi_ID");
                String name = rs.getString("name");
                int event = rs.getInt("event");

                //Tulostus tuttuun tapaan
                System.out.print(String.format("%04d", log_number));
                System.out.print(" | " + aika);
                System.out.print(" | ID: " + userID);
                System.out.print(" | Nimi: " + name);
                System.out.print(" | Ovi: " + oviID);
                System.out.println(" | Tapahtuma: " + event);

            }
            System.out.println("\nTallennetaanko? k/e");
            Scanner scanner4 = new Scanner(System.in);
            String tallennusEvent;
            tallennusEvent = scanner4.nextLine();

            switch (tallennusEvent) {
                case "k":
                    //Tallennus kuten aiemmin                   
                    haeEvent = conn.prepareStatement("SELECT log_number, aika, ovi_ID, user_ID, name, event  FROM kuti.tapahtumat WHERE Event=?");
                    haeEvent.setInt(1, Event_in);
                    rs = haeEvent.executeQuery();

                    try (BufferedWriter out = new BufferedWriter(new FileWriter(tapahtumat_by_ID))) {
                        while (rs.next()) {
                            out.write(" " + Integer.toString(rs.getInt("log_number")));
                            out.write(" | " + rs.getString("aika"));
                            out.write(" | ID: " + Integer.toString(rs.getInt("user_ID")));
                            out.write(" | Nimi: " + rs.getString("name"));
                            out.write(" | Ovi: " + rs.getString("ovi_ID"));
                            out.write(" | Tapahtuma: " + Integer.toString(rs.getInt("event")));
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
                    Files.move(yourFile, yourFile.resolveSibling("tapahtumat_" + Event_in + ".txt"), REPLACE_EXISTING);

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
