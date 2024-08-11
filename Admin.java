import java.io.IOException;

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
        String[] command ={"./usermanagement.sh", "exportDataAnalytics"};
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
        String[] command ={"./usermanagement.sh", "exportUserData"};
        String output = LPMT.executeCommand(command);
        System.out.println(Design.padMessage(Design.formatMessage("LPMT patient data exported successfully", Design.GREEN_COLOR), 50));
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
                    exportUserData();
                    break;
                case 3:
                    ExportUserData();
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