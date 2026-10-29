import java.util.Calendar;
import java.util.Date;

public class PrescriptionList {
    private class ListRecord {
        private Prescription prescription;
        private ListRecord next;

        public ListRecord(Prescription prescription) {
            this.prescription = prescription;
            next = null;
        }
    }

    private ListRecord head;

    // JS: Giuven how easy it is to count a linked list, I think I would just recompute
    // the count when asked for, instead of relying on a count variable that we now have to maintain. 
    // Count matters more for an array because the array structure itself does not tell us how
    // many boxes have actual data.  This makes the count() noperation order-N, but we don't
    // use it much for lists of prescriptions: other than in testing, we don't typically care.  
    private int count;

    public PrescriptionList() {
        head = null;
        count = 0;
    }

    // JS; Correct add routine. You handled the various cases well. 
    public boolean addPrescription(Prescription prescription) {
        if (prescription == null) {
            return false;
        }

        ListRecord record = new ListRecord(prescription);

        if (head == null ||
                prescription.comesBefore(head.prescription)) {
            record.next = head;
            head = record;
            count++;
            return true;
        }

        ListRecord current = head;

        // JS: Correct, we have to worry about one behind.  
        while (current.next != null &&
                !prescription.comesBefore(current.next.prescription)) {
            current = current.next;
        }

        record.next = current.next;
        current.next = record;
        count++;

        return true;
    }

    public boolean removePrescription(Prescription prescription) {
        if (prescription == null || head == null) {
            return false;
        }

        if (head.prescription.match(prescription)) {
            head = head.next;
            count--;
            return true;
        }

        ListRecord current = head;

        while (current.next != null) {
            if (current.next.prescription.match(prescription)) {
                current.next = current.next.next;
                count--;
                return true;
            }

            current = current.next;
        }

        return false;
    }

    public boolean hasCurrentMedication(String medicationName) {
        if (medicationName == null ||
                medicationName.trim().isEmpty()) {
            return false;
        }

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.YEAR, -1);

        Date cutoff = calendar.getTime();
        Date today = new Date();

        ListRecord current = head;

        while (current != null) {
            Date date = current.prescription.getDatePrescribed();

            if (date.before(cutoff)) {
                break;
            }

            if (!date.after(today) &&
                    current.prescription.getMedicationName()
                            .equalsIgnoreCase(medicationName.trim())) {
                return true;
            }

            current = current.next;
        }

        return false;
    }

    public void printCurrentMedications() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.YEAR, -1);

        Date cutoff = calendar.getTime();
        Date today = new Date();

        ListRecord current = head;
        boolean found = false;

        while (current != null) {
            Date date = current.prescription.getDatePrescribed();

            if (date.before(cutoff)) {
                break;
            }

            if (!date.after(today)) {
                System.out.println(current.prescription);
                found = true;
            }

            current = current.next;
        }

        if (!found) {
            System.out.println("No current medications.");
        }
    }

    public void printPrescriptionHistory() {
        if (head == null) {
            System.out.println("No prescription history.");
            return;
        }

        ListRecord current = head;

        while (current != null) {
            System.out.println(current.prescription);
            current = current.next;
        }
    }

    public void printMedicationHistory(String medicationName) {
        if (medicationName == null ||
                medicationName.trim().isEmpty()) {
            return;
        }

        ListRecord current = head;
        boolean found = false;

        while (current != null) {
            if (current.prescription.getMedicationName()
                    .equalsIgnoreCase(medicationName.trim())) {
                System.out.println(current.prescription);
                found = true;
            }

            current = current.next;
        }

        if (!found) {
            System.out.println("No history found for " +
                    medicationName + ".");
        }
    }

    public int getCount() {
        return count;
    }

    public int size() {
        return count;
    }
}
