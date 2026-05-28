package com.musiconline.bootstrap;

import com.musiconline.domain.Admin;
import com.musiconline.domain.AppUser;
import com.musiconline.domain.Role;
import com.musiconline.domain.Vinyl;
import com.musiconline.domain.VinylType;
import com.musiconline.repository.AdminRepository;
import com.musiconline.repository.AppUserRepository;
import com.musiconline.repository.VinylRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.time.LocalDate;

@Configuration
public class DataInitializer {

    private static final String DEMO_PASSWORD = "password";

    @Bean
    CommandLineRunner seed(AppUserRepository users,
                           AdminRepository admins,
                           VinylRepository vinyls,
                           PasswordEncoder encoder) {
        return args -> {
            if (users.count() > 0) return;

            // ── Admin accounts (mo_users + mo_admins) ──────────────────────
            // Each admin is stored in BOTH tables:
            //   mo_users  → for Spring Security authentication
            //   mo_admins → dedicated admin table (PDF Task 2 requirement)
            AppUser admin1User = saveUser(users, encoder, "admin@musiconline.com",  "admin1", Role.ADMIN);
            AppUser admin2User = saveUser(users, encoder, "admin2@musiconline.com", "admin2", Role.ADMIN);

            saveAdmin(admins, encoder, "admin@musiconline.com",  "admin1", "SUPER");
            saveAdmin(admins, encoder, "admin2@musiconline.com", "admin2", "MODERATOR");

            // ── Regular users and retailers (mo_users only) ─────────────────
            AppUser alice = saveUser(users, encoder, "alice@example.com",  "alice_vinyl", Role.USER);
            AppUser bob   = saveUser(users, encoder, "bob@example.com",    "bob_records", Role.RETAILER);
            AppUser carol = saveUser(users, encoder, "carol@example.com",  "carol_music", Role.USER);
            AppUser dave  = saveUser(users, encoder, "dave@example.com",   "dave_store",  Role.RETAILER);

            // ── Vinyl records / mo_vinyls (minimum 15) ──────────────────────
            // Albums (10)
            addVinyl(vinyls, "Abbey Road",                  "The Beatles",        VinylType.ALBUM,  "Rock",         "Apple Records",  LocalDate.of(1969,  9, 26), new BigDecimal("24.99"), "Near Mint",  alice);
            addVinyl(vinyls, "Rumours",                     "Fleetwood Mac",      VinylType.ALBUM,  "Rock",         "Warner Bros.",   LocalDate.of(1977,  2,  4), new BigDecimal("19.99"), "Very Good",  alice);
            addVinyl(vinyls, "Thriller",                    "Michael Jackson",    VinylType.ALBUM,  "Pop",          "Epic",           LocalDate.of(1982, 11, 30), new BigDecimal("29.99"), "Mint",       bob);
            addVinyl(vinyls, "Dark Side of the Moon",       "Pink Floyd",         VinylType.ALBUM,  "Progressive",  "Harvest",        LocalDate.of(1973,  3,  1), new BigDecimal("34.99"), "Very Good",  bob);
            addVinyl(vinyls, "Purple Rain",                 "Prince",             VinylType.ALBUM,  "Pop",          "Warner Bros.",   LocalDate.of(1984,  6, 25), new BigDecimal("22.50"), "Near Mint",  carol);
            addVinyl(vinyls, "Kind of Blue",                "Miles Davis",        VinylType.ALBUM,  "Jazz",         "Columbia",       LocalDate.of(1959,  8, 17), new BigDecimal("39.99"), "Good",       carol);
            addVinyl(vinyls, "Born to Run",                 "Bruce Springsteen",  VinylType.ALBUM,  "Rock",         "Columbia",       LocalDate.of(1975,  8, 25), new BigDecimal("18.99"), "Very Good",  dave);
            addVinyl(vinyls, "Nevermind",                   "Nirvana",            VinylType.ALBUM,  "Grunge",       "DGC Records",    LocalDate.of(1991,  9, 24), new BigDecimal("21.99"), "Near Mint",  dave);
            addVinyl(vinyls, "Songs in the Key of Life",    "Stevie Wonder",      VinylType.ALBUM,  "Soul",         "Tamla",          LocalDate.of(1976,  9, 28), new BigDecimal("27.99"), "Very Good",  alice);
            addVinyl(vinyls, "Achtung Baby",                "U2",                 VinylType.ALBUM,  "Rock",         "Island Records", LocalDate.of(1991, 11, 18), new BigDecimal("16.99"), "Good",       bob);

            // EPs (3)
            addVinyl(vinyls, "All I Want for Christmas Is You", "Mariah Carey", VinylType.EP, "Pop",         "Columbia",   LocalDate.of(1994, 11,  1), new BigDecimal("12.99"), "Mint",      carol);
            addVinyl(vinyls, "Twist and Shout",            "The Beatles",       VinylType.EP, "Rock",        "Parlophone", LocalDate.of(1963,  7, 12), new BigDecimal("14.99"), "Good",      dave);
            addVinyl(vinyls, "Cashflow",                   "Tame Impala",       VinylType.EP, "Psychedelic", "Modular",    LocalDate.of(2012,  5,  4), new BigDecimal("11.99"), "Near Mint", alice);

            // Singles (5)
            addVinyl(vinyls, "Bohemian Rhapsody",          "Queen",             VinylType.SINGLE, "Rock",          "EMI",           LocalDate.of(1975, 10, 31), new BigDecimal( "9.99"), "Very Good", bob);
            addVinyl(vinyls, "Johnny B. Goode",            "Chuck Berry",       VinylType.SINGLE, "Rock and Roll", "Chess Records", LocalDate.of(1958,  3, 31), new BigDecimal( "8.50"), "Good",      carol);
            addVinyl(vinyls, "Respect",                    "Aretha Franklin",   VinylType.SINGLE, "Soul",          "Atlantic",      LocalDate.of(1967,  4, 14), new BigDecimal("10.99"), "Very Good", dave);
            addVinyl(vinyls, "Smells Like Teen Spirit",    "Nirvana",           VinylType.SINGLE, "Grunge",        "DGC Records",   LocalDate.of(1991,  9, 10), new BigDecimal( "7.99"), "Near Mint", alice);
            addVinyl(vinyls, "Like a Rolling Stone",       "Bob Dylan",         VinylType.SINGLE, "Folk Rock",     "Columbia",      LocalDate.of(1965,  7, 20), new BigDecimal("11.50"), "Good",      bob);
        };
    }

    private static AppUser saveUser(AppUserRepository repo, PasswordEncoder enc,
                                    String email, String username, Role role) {
        AppUser u = new AppUser();
        u.setEmail(email);
        u.setPasswordHash(enc.encode(DEMO_PASSWORD));
        u.setUsername(username);
        u.setRole(role);
        return repo.save(u);
    }

    private static void saveAdmin(AdminRepository repo, PasswordEncoder enc,
                                  String email, String username, String level) {
        Admin a = new Admin();
        a.setEmail(email);
        a.setUsername(username);
        a.setPasswordHash(enc.encode(DEMO_PASSWORD));
        a.setAdminLevel(level);
        a.setActive(true);
        repo.save(a);
    }

    private static void addVinyl(VinylRepository repo, String title, String artist,
                                  VinylType type, String genre, String label,
                                  LocalDate releaseDate, BigDecimal price,
                                  String condition, AppUser seller) {
        Vinyl v = new Vinyl();
        v.setTitle(title);
        v.setArtist(artist);
        v.setType(type);
        v.setGenre(genre);
        v.setLabel(label);
        v.setReleaseDate(releaseDate);
        v.setPrice(price);
        v.setCondition(condition);
        v.setSeller(seller);
        v.setAvailable(true);
        repo.save(v);
    }
}
