import java.text.SimpleDateFormat;

// JS: the name ComparableTree shows up to great disadvantage here. 
// We see this line and we get the idea that a Patient is a tree.
public class Patient implements ComparableTree<Patient> {
    private PatientIdentity identity;

    // JS: I am not clear what the patientID is doing, but if you want a universal identifier
    // to link database records, you do not want it to be a String. Too error prone. 
    // This is what UUIDs were created for. 
    private String patientID;
    private PrescriptionList prescriptions;

    public Patient(PatientIdentity identity) {
        this(identity, "");
    }

    public Patient(PatientIdentity identity, String patientID) {
        if (identity == null || patientID == null) {
            throw new IllegalArgumentException("Invalid patient.");
        }

        this.identity = identity;
        this.patientID = patientID.trim();
        prescriptions = new PrescriptionList();
    }

    public PatientIdentity getIdentity() {
        return identity;
    }

    public String getPatientID() {
        return patientID;
    }

    public PrescriptionList getPrescriptionList() {
        return prescriptions;
    }

    public boolean match(Patient other) {
        if (other == null) {
            return false;
        }

        return identity.match(other.identity);
    }

    public boolean isLessThan(Patient other) {
        if (other == null) {
            return false;
        }

        return identity.isLessThan(other.identity);
    }

    public boolean addPrescription(Prescription prescription) {
        return prescriptions.addPrescription(prescription);
    }

    public boolean removePrescription(Prescription prescription) {
        return prescriptions.removePrescription(prescription);
    }

    public boolean hasCurrentMedication(String medicationName) {
        return prescriptions.hasCurrentMedication(medicationName);
    }

    public void printCurrentMedications() {
        prescriptions.printCurrentMedications();
    }

    public void printPrescriptionHistory() {
        prescriptions.printPrescriptionHistory();
    }

    public void printMedicationHistory(String medicationName) {
        prescriptions.printMedicationHistory(medicationName);
    }

    public int getPrescriptionCount() {
        return prescriptions.getCount();
    }

    public String toCSV() {
        SimpleDateFormat format =
                new SimpleDateFormat("yyyy-MM-dd");

        return CSVUtil.quote(identity.getIdentity().getLastName()) +
                "," +
                CSVUtil.quote(identity.getIdentity().getFirstName()) +
                "," +
                format.format(identity.getDateOfBirth()) +
                "," +
                CSVUtil.quote(patientID);
    }

    public String toString() {
        return identity.toString();
    }
}
