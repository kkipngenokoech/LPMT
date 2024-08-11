import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

public class Admin {
    public void CreateUser() throws IOException, InterruptedException{
        // adding border and padding
        System.out.println(Design.createBorder(50));
        // padding this message
        System.out.println(Design.padMessage(Design.formatMessage("Create a new user",Design.BLUE_COLOR), 50));
        System.out.println(Design.createBorder(50));
        // i want to format the message with input
        System.out.print(Design.formatInputMessage("Please enter the username of the new user: "));
        String username = System.console().readLine();
        boolean proceed = false;
        String role = "";
        while(!proceed){
            System.out.print(Design.formatInputMessage("Please enter the role of the new user: "));
            role = System.console().readLine().toLowerCase();
            if(role.equals("admin") || role.equals("patient")){
                proceed = true;
            }else{
                System.out.print(Design.formatMessage("Invalid role. Please enter either 'admin' or 'patient'", Design.RED_COLOR));
            }
        }
        System.out.print(Design.formatMessage("Creating user", Design.YELLOW_COLOR));printLoadingDots(3);
        String[] command ={"./usermanagement.sh", "createUser", username, role};
        String output = LPMT.executeCommand(command);
        System.out.println(Design.padMessage(Design.formatMessage(output, Design.GREEN_COLOR), 65));
    }

    public void exportUserData() throws IOException, InterruptedException{
        // adding border and padding
        System.out.println(Design.createBorder(50));
        // padding this message
        System.out.println(Design.padMessage(Design.formatMessage("Export data analytics",Design.BLUE_COLOR), 50));
        System.out.println(Design.createBorder(50));
        System.out.print(Design.formatMessage("Exporting data analytics", Design.YELLOW_COLOR));
        printLoadingDots(2);
        String[] command ={"./usermanagement.sh", "exportUserData"};
        String output = LPMT.executeCommand(command);
        System.out.println(Design.padMessage(Design.formatMessage("LPMT analytics exported successfully", Design.GREEN_COLOR), 50));
    }


    public void ExportUserData() throws IOException, InterruptedException{
        // adding border and padding
        System.out.println(Design.createBorder(50));    
        // padding this message
        System.out.println(Design.padMessage(Design.formatMessage("Export patient data",Design.BLUE_COLOR), 50));
        System.out.println(Design.createBorder(50));  
        System.out.print(Design.formatMessage("Exporting patient data", Design.YELLOW_COLOR));
        printLoadingDots(2); 
        String[] commandGetUserData = {"./usermanagement.sh", "getUsersData"};
        String outputGetUserData = LPMT.executeCommand(commandGetUserData);
        double lpmtTotal = 0.00;
        double lpmtAverage = 0.00;
        int count = 0;
        // I want to find the number of patients per country so I need a sort of dictionary of 
        ArrayList<Integer> lpmtArray = new ArrayList<>();
        for (String line : outputGetUserData.split("\n")) {
            String[] parts = line.split(":");
            if (parts.length != 18) {
                continue;
            }
            // String uuid = parts[0];
            // String username = parts [1];
            // char[] password = parts[2].toCharArray();
            // String firstName = parts[4];
            // String lastName = parts[5];
            // String email = parts[6];
            String doi = parts[7]+":"+parts[8]+":"+parts[9];
            boolean onMedication = Boolean.parseBoolean(parts[10]);
            String soi = parts[11]+":"+parts[12]+":"+parts[13];
            String dob = parts[14]+":"+parts[15]+":"+parts[16];
            String country = parts[17];
            int lpmt = Patient.calculateLPMT(country, doi, soi, onMedication, dob);
            lpmtArray.add(lpmt);
            lpmtTotal += (double)lpmt;
            count += 1;
        };
        lpmtAverage = lpmtTotal/ count;
        Collections.sort(lpmtArray);
        double lpmtMedian;
        if (lpmtArray.size() % 2 == 0){
            lpmtMedian = ((double) lpmtArray.get(lpmtArray.size() / 2 - 1) + lpmtArray.get(lpmtArray.size() / 2)) / 2;     
           } else {
            lpmtMedian = (double) lpmtArray.get(lpmtArray.size() / 2);
        }
        double p25 = calculatePercentile(lpmtArray, 25);
        double p50 = calculatePercentile(lpmtArray, 50);
        double p75 = calculatePercentile(lpmtArray, 75);
        String[] command ={"./usermanagement.sh", "exportDataAnalytics" , Double.toString(lpmtAverage), Double.toString(lpmtMedian), Double.toString(p25), Double.toString(p50), Double.toString(p75)};
        String output = LPMT.executeCommand(command);
        System.out.println(Design.padMessage(Design.formatMessage("LPMT patient data exported successfully", Design.GREEN_COLOR), 50));
    }

    public double calculatePercentile(ArrayList<Integer> sortedList, double percentile) {
        int size = sortedList.size();
        double index = (percentile / 100.0) * (size - 1);
        int lowerIndex = (int) Math.floor(index);
        int upperIndex = (int) Math.ceil(index);
        if (lowerIndex == upperIndex) {
            return sortedList.get(lowerIndex);
        } else {
            double lowerValue = sortedList.get(lowerIndex);
            double upperValue = sortedList.get(upperIndex);
            return lowerValue + (upperValue - lowerValue) * (index - lowerIndex);
        }
    }
    
    public void callAdminMenu() throws IOException, InterruptedException{
        boolean exit = false;
        while(!exit){
            // adding border and padding
            System.out.println(Design.createBorder(50));
            // padding this message
            System.out.println(Design.padMessage(Design.formatMessage("Admin Menu",Design.BLUE_COLOR), 50));
            System.out.println(Design.padMessage(Design.formatMessage("Please select an option: ",Design.GREEN_COLOR), 50));
            System.out.println(Design.createBorder(50));
            System.out.println("1. Create a new user");
            System.out.println("2. Export data analytics");
            System.out.println("3. Export patient data");
            System.out.println(Design.padMessage(Design.formatMessage("4. LOGOUT",Design.YELLOW_COLOR)+Design.formatMessage("5. EXIT", Design.RED_COLOR), 50));
            System.out.print(Design.formatInputMessage("Please enter your choice: "));
            int choice = -1;
            boolean proceed = false;
            while (!proceed) {
                try {
                    choice = Integer.parseInt(System.console().readLine());
                    if (choice >= 1 && choice <= 5) {
                        proceed = true;
                    } else {
                        System.out.print(Design.formatMessage("Invalid input. Please enter a number between 1 and 4:", Design.RED_COLOR));
                    }
                } catch (NumberFormatException e) {
                    System.out.print(Design.formatMessage("Invalid input. Please enter a number:", Design.RED_COLOR));
                }
            }
            switch (choice) {
                case 1:
                    CreateUser();
                    break;
                case 2:
                    ExportUserData();
                break;
                case 3:
                    exportUserData();
                    break;
                case 4:
                    exit=true;
                    System.out.print(Design.formatMessage("Logging out", Design.YELLOW_COLOR));printLoadingDots(3);
                    System.out.println(Design.padMessage((Design.formatMessage("Logged out successfully", Design.GREEN_COLOR)), 50));
                    LPMT.login();  
                    break;
                case 5:
                    exit=true;  
                    System.out.print(Design.formatMessage("Exiting", Design.RED_COLOR));printLoadingDots(3);
                    System.out.println(Design.padMessage((Design.formatMessage("Goodbye", Design.GREEN_COLOR)), 50));
                    System.exit(0);
                    break;
                default:
                    System.out.println("Invalid choice");
                    break;
            }
        }
    }

    private void printLoadingDots(int seconds) throws InterruptedException {
        for (int i = 0; i < seconds * 10; i++) {
            System.out.print(".");
            Thread.sleep(100); // Sleep for 100 milliseconds
        }
        System.out.println(); // Move to the next line after loading
    }
}