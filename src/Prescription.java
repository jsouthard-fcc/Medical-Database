import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

// JS: this is a nice prescription class.   
public class Prescription {
    private String medicationName;
    private Date datePrescribed;
    private String dosage;
    private String prescriber;

    public Prescription(String medicationName, Date datePrescribed,
                        String dosage, String prescriber) {
// JS: This is a good use of an exception.  Good care taken with the trims.  
        if (medicationName == null ||
                medicationName.trim().isEmpty() ||
                datePrescribed == null ||
                dosage == null || dosage.trim().isEmpty() ||
                prescriber == null || prescriber.trim().isEmpty()) {
            throw new IllegalArgumentException("Invalid prescription.");
        }

        this.medicationName = medicationName.trim();
        this.datePrescribed = new Date(datePrescribed.getTime());
        this.dosage = dosage.trim();
        this.prescriber = prescriber.trim();
    }

    // JS: Good use of factory method here.  
    public static Prescription create(String csvLine) {
        String[] fields = CSVUtil.parseLine(csvLine);

        if (fields.length != 7) {
            throw new IllegalArgumentException(
                    "Prescription must contain seven fields.");
        }

        SimpleDateFormat format =
                new SimpleDateFormat("yyyy-MM-dd");

        format.setLenient(false);

        try {
            Date issueDate = format.parse(fields[4]);

            if (!format.format(issueDate).equals(fields[4])) {
                throw new IllegalArgumentException(
                        "Invalid prescription date.");
            }

            return new Prescription(
                    fields[3], issueDate, fields[5], fields[6]);
        } catch (ParseException e) {
            throw new IllegalArgumentException(
                    "Invalid prescription date.");
        }
    }

    public String getMedicationName() {
        return medicationName;
    }

    public String getMedicineName() {
        return medicationName;
    }

    public Date getDatePrescribed() {
        return new Date(datePrescribed.getTime());
    }

    public Date getDateOfIssue() {
        return getDatePrescribed();
    }

    public String getDosage() {
        return dosage;
    }

    public String getPrescriber() {
        return prescriber;
    }

    public boolean comesBefore(Prescription other) {
        if (other == null) {
            return true;
        }

        return datePrescribed.after(other.datePrescribed);
    }

    public boolean match(Prescription other) {
        if (other == null) {
            return false;
        }

        return medicationName.equalsIgnoreCase(other.medicationName) &&
                datePrescribed.equals(other.datePrescribed) &&
                dosage.equalsIgnoreCase(other.dosage) &&
                prescriber.equalsIgnoreCase(other.prescriber);
    }

    public String toString() {
        SimpleDateFormat format =
                new SimpleDateFormat("yyyy-MM-dd");

        return medicationName + " | " +
                format.format(datePrescribed) + " | " +
                dosage + " | " + prescriber;
    }
}
