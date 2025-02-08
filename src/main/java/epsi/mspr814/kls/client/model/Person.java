package epsi.mspr814.kls.client.model;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "persons")
public class Person {
    @Id
    @GeneratedValue
    private UUID uuid;

    @Column(name="first_name")
    private String firstName;

    @Column(name="last_name")
    private String lastName;
    private String email;
    private String phone;
    private String address;
    private String password;

    public Person(UUID uuid, String firstName, String lastName, String email, String phone, String address, String password) {
        this.uuid = uuid;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.password = password;
    }

    public Person() {

    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}