// JS: this is what we were calling the "Name" class.
// I am going to hold out for Name as a better class name, 
// because it is confusing to have an Identity and a PatientIdentity
// which are not a class hierarchy, but two different things.  
public class Identity {
    private String firstName;
    private String lastName;

    public Identity(String firstName, String lastName) {
        if (firstName == null || lastName == null ||
                firstName.trim().isEmpty() ||
                lastName.trim().isEmpty()) {
            throw new IllegalArgumentException("Invalid name.");
        }

        this.firstName = firstName.trim();
        this.lastName = lastName.trim();
    }

    // JS: I see what is being done here, but it is a misnomer.  
    // The issue is not 'last name first' but whether the first or last name is the family name, 
    // This is better handled through polymoprhism.  
    public Identity(String firstName, String lastName,
                    boolean lastNameFirst) {
        this(lastNameFirst ? lastName : firstName,
                lastNameFirst ? firstName : lastName);
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String fullname() {
        return lastName + ", " + firstName;
    }

    public boolean match(Identity other) {
        if (other == null) {
            return false;
        }

        return firstName.equalsIgnoreCase(other.firstName) &&
                lastName.equalsIgnoreCase(other.lastName);
    }

    public boolean isLessThan(Identity other) {
        if (other == null) {
            return false;
        }

        int comparison =
                lastName.compareToIgnoreCase(other.lastName);

        if (comparison < 0) {
            return true;
        }

        if (comparison > 0) {
            return false;
        }

        return firstName.compareToIgnoreCase(other.firstName) < 0;
    }

    public String toString() {
        return fullname();
    }
}
