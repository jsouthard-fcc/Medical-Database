import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PatientList {
    private static final int MAX_PATIENTS = 1000;

    private Patient[] patients;
    private int count;

    public PatientList() {
        patients = new Patient[MAX_PATIENTS];
        count = 0;
    }

    public boolean addPatient(Patient patient) {
        if (patient == null || count >= MAX_PATIENTS) {
            return false;
        }

        if (findPatient(patient.getIdentity()) != null) {
            return false;
        }

        addInSortedOrder(patient);
        return true;
    }

    private void addInSortedOrder(Patient patient) {
        int index = 0;

        while (index < count &&
                !patient.getIdentity().isLessThan(
                        patients[index].getIdentity())) {
            index++;
        }

        for (int i = count; i > index; i--) {
            patients[i] = patients[i - 1];
        }

        patients[index] = patient;
        count++;
    }

    public Patient findPatient(PatientIdentity identity) {
        if (identity == null) {
            return null;
        }

        return binarySearch(identity);
    }

    private Patient binarySearch(PatientIdentity identity) {
        int low = 0;
        int high = count - 1;

        while (low <= high) {
            int middle = low + (high - low) / 2;

            PatientIdentity current =
                    patients[middle].getIdentity();

            if (identity.match(current)) {
                return patients[middle];
            }

            if (identity.isLessThan(current)) {
                high = middle - 1;
            } else {
                low = middle + 1;
            }
        }

        return null;
    }

    public boolean saveToFile(String filename) {
        if (filename == null || filename.trim().isEmpty()) {
            return false;
        }

        try (FileWriter writer = new FileWriter(filename)) {
            Iterator iterator = iterator();
            Patient patient;

            while ((patient = iterator.next()) != null) {
                writer.write(patient.toCSV());
                writer.write("\n");
            }

            return true;
        } catch (IOException e) {
            System.out.println("Save error: " + e.getMessage());
            return false;
        }
    }

    public boolean importFromFile(String filename) {
        if (filename == null || filename.trim().isEmpty()) {
            return false;
        }

        SimpleDateFormat format =
                new SimpleDateFormat("yyyy-MM-dd");

        format.setLenient(false);

        boolean importedAll = true;
        int importedCount = 0;

        try (BufferedReader reader =
                     new BufferedReader(new FileReader(filename))) {

            String line;
            int lineNumber = 0;

            while ((line = reader.readLine()) != null) {
                lineNumber++;

                // Js: Please be wary of overusing continue.  It can make code very hard to figure out. 
                // It would be cleaner to feed the line into an error-checking method which 
                // could check these various errors in one place. Then your logica owuld be just
                //  if (lineValid(line)) { <process the tokens> }
                if (line.trim().isEmpty()) {
                    continue;
                }

                // JS: Not sure where you got the CSV Util, but it is overkill here. 
                // It mostly worries about possible single and doiuble quotes in the data, which we don't have. 
                String[] fields = CSVUtil.parseLine(line);

                if (fields.length != 4) {
                    System.out.println(
                            "Invalid patient row: " + lineNumber);
                    importedAll = false;
                    continue;
                }

                // JS: this looks like it is handling a line of field labels, which we don't have.
                if (fields[0].equalsIgnoreCase("lastName") &&
                        fields[1].equalsIgnoreCase("firstName") &&
                        fields[2].equalsIgnoreCase("dateOfBirth")) {
                    continue;
                }

                try {
                    String lastName = fields[0];
                    String firstName = fields[1];
                    String birthDateText = fields[2];
                    String patientID = fields[3];

                    if (lastName.isEmpty() ||
                            firstName.isEmpty() ||
                            birthDateText.isEmpty() ||
                            patientID.isEmpty()) {
                        System.out.println(
                                "Missing patient information at row: " +
                                        lineNumber);
                        importedAll = false;
                        continue;
                    }

                    Date birthDate = format.parse(birthDateText);

                    // JS: Not sure what the purpose of this test is. 
                    // We got the birthdate from the text.  ARe we testing the formatter?
                    if (!format.format(birthDate).equals(birthDateText)) {
                        System.out.println(
                                "Invalid birth date at row: " +
                                        lineNumber);
                        importedAll = false;
                        continue;
                    }

                    Identity identity =
                            new Identity(firstName, lastName);

                    PatientIdentity patientIdentity =
                            new PatientIdentity(identity, birthDate);

                    if (findPatient(patientIdentity) != null) {
                        System.out.println(
                                "Duplicate patient at row: " +
                                        lineNumber);
                        importedAll = false;
                        continue;
                    }

                    Patient patient =
                            new Patient(patientIdentity, patientID);

                    if (addPatient(patient)) {
                        importedCount++;
                    } else {
                        System.out.println(
                                "Unable to add patient at row: " +
                                        lineNumber);
                        importedAll = false;
                    }
                } catch (ParseException |
                         IllegalArgumentException e) {
                    System.out.println(
                            "Patient row " + lineNumber +
                                    ": " + e.getMessage());
                    importedAll = false;
                }
            }

            System.out.println(
                    "Total patients imported: " + importedCount);

            return importedAll;
        } catch (IOException e) {
            System.out.println(
                    "Patient file error: " + e.getMessage());
            return false;
        }
    }

    public boolean importPrescriptionsFromFile(String filename) {
        PrescriptionCsvReader reader =
                new PrescriptionCsvReader();

        return reader.importFromFile(filename, this);
    }

    private void mergeSort() {
        if (count <= 1) {
            return;
        }

        Patient[] temporary = new Patient[count];

        mergeSort(temporary, 0, count - 1);
    }

    private void mergeSort(Patient[] temporary, int left, int right) {
        if (left >= right) {
            return;
        }

        int middle = left + (right - left) / 2;

        mergeSort(temporary, left, middle);
        mergeSort(temporary, middle + 1, right);

        mergeSections(temporary, left, middle, right);
    }

    private void mergeSections(Patient[] temporary,
                               int left, int middle, int right) {
        int i = left;
        int j = middle + 1;
        int k = left;

        while (i <= middle && j <= right) {
            PatientIdentity first = patients[i].getIdentity();
            PatientIdentity second = patients[j].getIdentity();

            if (first.isLessThan(second) || first.match(second)) {
                temporary[k] = patients[i];
                i++;
            } else {
                temporary[k] = patients[j];
                j++;
            }

            k++;
        }

        while (i <= middle) {
            temporary[k] = patients[i];
            i++;
            k++;
        }

        while (j <= right) {
            temporary[k] = patients[j];
            j++;
            k++;
        }

        for (int index = left; index <= right; index++) {
            patients[index] = temporary[index];
        }
    }

    public int getPatientCount() {
        return count;
    }

    public void printPatients() {
        if (count == 0) {
            System.out.println("No patients in the list.");
            return;
        }

        for (int i = 0; i < count; i++) {
            System.out.println(patients[i]);
        }
    }

    public Iterator iterator() {
        return new Iterator();
    }

    public class Iterator {
        private int currentIndex;

        public Iterator() {
            currentIndex = 0;
        }

        public Patient peek() {
            if (currentIndex >= count) {
                return null;
            }

            return patients[currentIndex];
        }

        public Patient next() {
            if (currentIndex >= count) {
                return null;
            }

            Patient patient = patients[currentIndex];
            currentIndex++;

            return patient;
        }
    }

    public static PatientList merge(PatientList first,
                                    PatientList second) {
        PatientList merged = new PatientList();

        if (first != null) {
            Iterator iterator = first.iterator();
            Patient patient;

            while ((patient = iterator.next()) != null) {
                merged.addPatient(patient);
            }
        }

        if (second != null) {
            Iterator iterator = second.iterator();
            Patient patient;

            while ((patient = iterator.next()) != null) {
                merged.addPatient(patient);
            }
        }

        return merged;
    }
}
