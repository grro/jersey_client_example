package com.ccreanga.jersey.example.domain;

import org.springframework.data.cassandra.mapping.Column;
import org.springframework.data.cassandra.mapping.PrimaryKey;
import org.springframework.data.cassandra.mapping.Table;

import java.util.UUID;

@Table("security_attributes")
public class Profile {

    @PrimaryKey
    private UUID uuid;
    @Column(value = "name")
    private String name;
    @Column(value = "age")
    private int age;
    @Column(value = "payment")
    private int payment;
    @Column(value = "id_credit")
    private UUID creditUuid;
    @Column(value = "id_rent")
    private UUID rentUuid;

    public Profile() {
    }

    public Profile(String name, UUID rentUuid, UUID creditUuid, UUID uuid, int age,int payment) {
        this.name = name;
        this.rentUuid = rentUuid;
        this.creditUuid = creditUuid;
        this.uuid = uuid;
        this.age = age;
        this.payment = payment;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public UUID getCreditUuid() {
        return creditUuid;
    }

    public void setCreditUuid(UUID creditUuid) {
        this.creditUuid = creditUuid;
    }

    public UUID getRentUuid() {
        return rentUuid;
    }

    public void setRentUuid(UUID rentUuid) {
        this.rentUuid = rentUuid;
    }

    public int getPayment() {
        return payment;
    }

    public void setPayment(int payment) {
        this.payment = payment;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Profile profile = (Profile) o;

        if (age != profile.age) return false;
        if (payment != profile.payment) return false;
        if (name != null ? !name.equals(profile.name) : profile.name != null) return false;
        if (uuid != null ? !uuid.equals(profile.uuid) : profile.uuid != null) return false;
        if (creditUuid != null ? !creditUuid.equals(profile.creditUuid) : profile.creditUuid != null) return false;
        return rentUuid != null ? rentUuid.equals(profile.rentUuid) : profile.rentUuid == null;

    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + age;
        result = 31 * result + payment;
        result = 31 * result + (uuid != null ? uuid.hashCode() : 0);
        result = 31 * result + (creditUuid != null ? creditUuid.hashCode() : 0);
        result = 31 * result + (rentUuid != null ? rentUuid.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Profile{" +
                "name='" + name + '\'' +
                ", age=" + age +
                ", payment=" + payment +
                ", uuid=" + uuid +
                ", creditUuid=" + creditUuid +
                ", rentUuid=" + rentUuid +
                '}';
    }
}
