import java.text.SimpleDateFormat;
import java.util.Date;

// Js: This class is done very nicely, modulo the bit of confusion about the class name Identity,
// which I have noted elsewhere. 
public class PatientIdentity {
    private Identity identity;
    private Date dateOfBirth;

    public PatientIdentity(Identity identity, Date dateOfBirth) {
        if (identity == null || dateOfBirth == null) {
            throw new IllegalArgumentException("Invalid patient identity.");
        }

        this.identity = identity;
        this.dateOfBirth = new Date(dateOfBirth.getTime());
    }

    public Identity getIdentity() {
        return identity;
    }

    public Date getDateOfBirth() {
        return new Date(dateOfBirth.getTime());
    }

    public boolean match(PatientIdentity other) {
        if (other == null) {
            return false;
        }

        return identity.match(other.identity) &&
                dateOfBirth.equals(other.dateOfBirth);
    }

    public boolean isLessThan(PatientIdentity other) {
        if (other == null) {
            return false;
        }

        if (identity.isLessThan(other.identity)) {
            return true;
        }

        if (other.identity.isLessThan(identity)) {
            return false;
        }

        return dateOfBirth.before(other.dateOfBirth);
    }

    public String toString() {
        SimpleDateFormat format =
                new SimpleDateFormat("yyyy-MM-dd");

        return identity.toString() + " | " +
                format.format(dateOfBirth);
    }
}
