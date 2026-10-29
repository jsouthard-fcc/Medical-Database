import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PrescriptionCsvReader {
    public boolean importFromFile(String filename,
                                  PatientList patients) {
        if (filename == null || filename.trim().isEmpty() ||
                patients == null) {
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

            // JS: Some of the same comments on the patient reader apply here, too.  
            while ((line = reader.readLine()) != null) {
                lineNumber++;

                if (line.trim().isEmpty()) {
                    continue;
                }

                String[] fields = CSVUtil.parseLine(line);

                if (fields.length != 7) {
                    System.out.println(
                            "Invalid prescription row: " + lineNumber);
                    importedAll = false;
                    continue;
                }

                if (fields[0].equalsIgnoreCase("lastName") &&
                        fields[1].equalsIgnoreCase("firstName") &&
                        fields[2].equalsIgnoreCase("dateOfBirth")) {
                    continue;
                }

                try {
                    String lastName = fields[0];
                    String firstName = fields[1];
                    String birthDateText = fields[2];

                    Date birthDate = format.parse(birthDateText);

                    if (!format.format(birthDate).equals(birthDateText)) {
                        importedAll = false;
                        continue;
                    }

                    Identity identity =
                            new Identity(firstName, lastName);

                    PatientIdentity patientIdentity =
                            new PatientIdentity(identity, birthDate);

                    Patient patient =
                            patients.findPatient(patientIdentity);

                    if (patient == null) {
                        System.out.println(
                                "Patient not found at row: " +
                                        lineNumber);
                        importedAll = false;
                        continue;
                    }

                    Prescription prescription =
                            Prescription.create(line);

                    if (patient.addPrescription(prescription)) {
                        importedCount++;
                    } else {
                        importedAll = false;
                    }
                } catch (ParseException |
                         IllegalArgumentException e) {
                    System.out.println(
                            "Prescription row " + lineNumber +
                                    ": " + e.getMessage());
                    importedAll = false;
                }
            }

            System.out.println(
                    "Total prescriptions imported: " + importedCount);

            return importedAll;
        } catch (IOException e) {
            System.out.println(
                    "Prescription file error: " + e.getMessage());
            return false;
        }
    }
}
