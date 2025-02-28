package tn.esprit.pidev.models;

public class User {
    private int ID;
    private String email;
    private String motdepasse;
    private String genre;
    private String prenom;
    private String nom;
    private String numeroTelephone;
    private String adresse;
    private String photoProfil;
    private int roleID;
    private String nomOrganisation;

    // Constructors
    public User() {}

    public User(int ID, String email, String motdepasse, String genre, String prenom, 
                String nom, String numeroTelephone, String adresse, String photoProfil, 
                int roleID, String nomOrganisation) {
        this.ID = ID;
        this.email = email;
        this.motdepasse = motdepasse;
        this.genre = genre;
        this.prenom = prenom;
        this.nom = nom;
        this.numeroTelephone = numeroTelephone;
        this.adresse = adresse;
        this.photoProfil = photoProfil;
        this.roleID = roleID;
        this.nomOrganisation = nomOrganisation;
    }

    // Getters and Setters
    public int getID() { return ID; }
    public void setID(int ID) { this.ID = ID; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getMotdepasse() { return motdepasse; }
    public void setMotdepasse(String motdepasse) { this.motdepasse = motdepasse; }

    public String getGenre() { return genre; }
    public void setGenre(String genre) { this.genre = genre; }

    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getNumeroTelephone() { return numeroTelephone; }
    public void setNumeroTelephone(String numeroTelephone) { this.numeroTelephone = numeroTelephone; }

    public String getAdresse() { return adresse; }
    public void setAdresse(String adresse) { this.adresse = adresse; }

    public String getPhotoProfil() { return photoProfil; }
    public void setPhotoProfil(String photoProfil) { this.photoProfil = photoProfil; }

    public int getRoleID() { return roleID; }
    public void setRoleID(int roleID) { this.roleID = roleID; }

    public String getNomOrganisation() { return nomOrganisation; }
    public void setNomOrganisation(String nomOrganisation) { this.nomOrganisation = nomOrganisation; }

    // Helper method to get full name
    public String getFullName() {
        return (prenom != null ? prenom : "") + " " + (nom != null ? nom : "");
    }

    @Override
    public String toString() {
        return "User{" +
                "ID=" + ID +
                ", email='" + email + '\'' +
                ", genre='" + genre + '\'' +
                ", prenom='" + prenom + '\'' +
                ", nom='" + nom + '\'' +
                ", numeroTelephone='" + numeroTelephone + '\'' +
                ", adresse='" + adresse + '\'' +
                ", photoProfil='" + photoProfil + '\'' +
                ", roleID=" + roleID +
                ", nomOrganisation='" + nomOrganisation + '\'' +
                '}';
    }
} 