import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class LPMT {
    static String uuid;
    static String username;
    static char[] password;
    static Map<String, Runnable> commands = new HashMap<>();
    private static Admin admin = new Admin();
    private static Patient patient = new Patient();
    public static void main(String[] args) throws IOException, InterruptedException {
        initializeFunctionMap();
        login();

    }
  
    public static void login() throws IOException, InterruptedException {
        String welcomeMessage = Design.formatMessage("Welcome to LPMT - Your healthcare clock-in system.", Design.BLUE_COLOR);
        String loginMessage = Design.formatMessage("Please login to access our services", Design.GREEN_COLOR);

        int maxLength = Math.max(welcomeMessage.length(), loginMessage.length());
        String border = Design.createBorder(50);

        System.out.println(border);
        System.out.println(Design.padMessage(welcomeMessage, maxLength));
        System.out.println(Design.padMessage(loginMessage, maxLength));
        System.out.println(border);
        System.out.println(Design.formatMessage("Please select an option to login", Design.BLUE_COLOR));
        System.out.println("1. Login with email/username");
        System.out.println("2. Login with UUID");
        System.out.print(Design.formatMessage("please note that UUID is for first login only:", Design.YELLOW_COLOR));
        
        boolean proceed = false;
        int choice = 0;
        while (!proceed) {
            try {
                choice = Integer.parseInt(System.console().readLine());
                if (choice == 1 || choice == 2) {
                    proceed = true;
                } else {
                    // i want to pad this ones

                    System.out.print(Design.padMessage(Design.formatMessage("Invalid input. Please enter 1 or 2:", Design.RED_COLOR), 50));
                }
            } catch (NumberFormatException e) {
                System.out.print(Design.formatMessage("Invalid input. Please enter 1 or 2:", Design.RED_COLOR));
            }
        }
        
        if (choice == 1) {
            System.out.print(Design.formatInputMessage("Please enter your username: "));
            String username = System.console().readLine();
            System.out.print(Design.formatInputMessage("Please enter your password: "));
            char[] password = System.console().readPassword();
            callBash("loginUser", username, password);
        } else {
            boolean completeRegistrationFlag = false;
            while (!completeRegistrationFlag) {
                System.out.print(Design.formatInputMessage("Please enter your UUID: "));
                String uuid = System.console().readLine();
                String[] command = {"./usermanagement.sh", "checkUUID", uuid};
                String output = executeCommand(command).trim();
                if (output == null || output.isEmpty()) {
                    // pad this message
                    System.out.println(Design.padMessage(Design.formatMessage("Invalid UUID! Please try again!", Design.RED_COLOR), 60));
                } else {
                    System.out.println(Design.formatMessage("Congratulations! Your UUID is valid. Please proceed and complete your registration", Design.GREEN_COLOR));
                    String[] commandCompleteReg = patient.CompleteRegistration(uuid);
                    System.out.print(Design.formatMessage("Completing registration", Design.YELLOW_COLOR));
                    printLoadingDots(5);
                    String test = executeCommand(commandCompleteReg);
                    System.out.println(Design.padMessage(Design.formatMessage("Registration complete! Please login to access our services", Design.GREEN_COLOR), 70));
                    completeRegistrationFlag = true;
                    login();
                }
            }
        }
    }

    public static void printLoadingDots(int seconds) throws InterruptedException {
        for (int i = 0; i < seconds; i++) {
            Thread.sleep(100);
            System.out.print(".");
        }
        System.out.println();
    }

    public static void callBash(String function, String username, char[] password, String role) throws IOException, InterruptedException{
        String[] command ={"./usermanagement.sh", function, username, new String(password), role};
        executeCommand(command);
    }

    public static void callBash(String function, String username, char[] password) throws IOException, InterruptedException{
        // method to call bash script
        String[] command ={"./usermanagement.sh", function, username, new String(password)};
        String output = executeCommand(command).trim();
        String role;
        String[] lines = output.split("\n");
        if (lines.length > 0) {
            // Split the first line by ':' to get role and UUID
            String[] parts = lines[0].split(":");
            if (parts.length == 2) {
                role = parts[0].trim();
                uuid = parts[1].trim();
            } else {
                role = lines[0].trim();
            }
            role = role.split("\n")[0].trim();
            switch (function) {
                case "loginUser":
                    if ((role.equals("admin") || role.equals("patient"))){
                        // login successful with colors
                        System.out.println(Design.padMessage(Design.formatMessage("Login successful, welcome "+username, Design.GREEN_COLOR),50));
                        System.out.print(Design.formatMessage("Redirecting to dashboard", Design.YELLOW_COLOR));
                        printLoadingDots(5);
                        System.out.println(role);
                            if (role.equals("admin")){
                                admin.callAdminMenu();
                            } else {
                                patient.patientMenu(uuid);
                            }
                    } else {
                        System.out.print(Design.formatMessage("Invalid username or password,", Design.RED_COLOR));
                        System.out.println(Design.formatMessage("please try again!", Design.RED_COLOR));
                        login();
                    }
                    break;
                case "createUser":
                    System.out.println("User " + username + " created successfully!");
                    break;
            default:
                break;
            }
    }
    }

    static String executeCommand(String[] command) throws IOException, InterruptedException{
        Process process = Runtime.getRuntime().exec(command);
        int exitCode = process.waitFor();
        if(exitCode != 0){
            System.out.println("An error occurred while executing the command");
            return null;
        }
        return new String(process.getInputStream().readAllBytes()).trim();

    }

    public static void initializeFunctionMap(){
        commands.put("login", () -> {
            try {
                login();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        });
    }

}