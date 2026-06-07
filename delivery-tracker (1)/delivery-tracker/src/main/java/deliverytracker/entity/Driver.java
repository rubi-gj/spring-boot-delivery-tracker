package deliverytracker.entity;

import jakarta.persistence.*;
/*
 * Driver.java — Entity representing a delivery driver (R7).
 * Stores driver credentials (username/password) for Driver Portal login.
 * Tracks current location and availability status.
 * One driver can be assigned to many packages (OneToMany inverse side).
 */

@Entity
@Table(name = "drivers")
public class Driver {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String phone;
    private String currentLocation;
    private boolean available;
    private String username;
    private String password;


    // R7 — Runs BEFORE first INSERT
    // Sets available = true by default for every new driver
    @PrePersist
    public void prePersist() {
        this.available = true;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getCurrentLocation() { return currentLocation; }
    public void setCurrentLocation(String l) { this.currentLocation = l; }

    public boolean isAvailable() { return available; }
    public void setAvailable(boolean a) { this.available = a; }

    public String getUsername() { return username; }
    public void setUsername(String u) { this.username = u; }

    public String getPassword() { return password; }
    public void setPassword(String p) { this.password = p; }
}