import java.util.*;

// ----------- CORE CLASSES -----------

abstract class User {
    protected String username;
    protected String password;

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public void setPassword(String password) { this.password = password; }

    public boolean login(String inputPassword) {
        return this.password.equals(inputPassword);
    }

    public abstract void showMenu(Scanner scanner);
}

class Admin extends User {
    @Override
    public void showMenu(Scanner scanner) {
        while (true) {
            System.out.println("\n--- Admin Dashboard ---");
            System.out.println("1. Add Car");
            System.out.println("2. Remove Car");
            System.out.println("3. Update Car Status");
            System.out.println("4. View Cars");
            System.out.println("5. View Bookings");
            System.out.println("6. View Earnings");
            System.out.println("7. View Users");
            System.out.println("8. Logout");
            System.out.print("Option: ");
            String choice = scanner.nextLine();

            try {
                switch (choice) {
                    case "1": SystemManager.addCar(scanner); break;
                    case "2": SystemManager.removeCar(scanner); break;
                    case "3": SystemManager.updateCarStatus(scanner); break;
                    case "4": SystemManager.viewCars(); break;
                    case "5": SystemManager.viewBookings(); break;
                    case "6": SystemManager.viewEarnings(); break;
                    case "7": SystemManager.viewUsers(); break;
                    case "8": return;
                    default: System.out.println("Invalid option.");
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }
}

class Customer extends User {
    private List<Booking> myBookings = new ArrayList<>();

    @Override
    public void showMenu(Scanner scanner) {
        while (true) {
            System.out.println("\n--- Customer Dashboard ---");
            System.out.println("1. Book Car");
            System.out.println("2. View My Bookings");
            System.out.println("3. Return Car");
            System.out.println("4. Logout");
            System.out.print("Option: ");
            String choice = scanner.nextLine();

            try {
                switch (choice) {
                    case "1": SystemManager.bookCar(scanner, this, myBookings); break;
                    case "2": SystemManager.viewCustomerBookings(myBookings); break;
                    case "3": SystemManager.returnCar(scanner, myBookings); break;
                    case "4": return;
                    default: System.out.println("Invalid option.");
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }
}

class Car {
    private final UUID id = UUID.randomUUID();
    private final String make, model, type, fuel, transmission;
    private final int year;
    private final double pricePerDay;
    private String status = "Available";

    public Car(String make, String model, int year, String type, double price, String fuel, String transmission) {
        this.make = make; this.model = model; this.year = year; this.type = type;
        this.pricePerDay = price; this.fuel = fuel; this.transmission = transmission;
    }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public double getPricePerDay() { return pricePerDay; }
    public UUID getId() { return id; }

    @Override
    public String toString() {
        return make + " " + model + " (" + year + ") - " + type + ", Rs." + pricePerDay + "/day";
    }
}

class Booking {
    private final UUID bookingId = UUID.randomUUID();
    private final Car car;
    private final Customer customer;
    private final String startDate, endDate;
    private final double totalPrice;

    public Booking(Car car, Customer customer, String start, String end, double total) {
        this.car = car;
        this.customer = customer;
        this.startDate = start;
        this.endDate = end;
        this.totalPrice = total;
    }

    public Car getCar() { return car; }
    public double getTotalPrice() { return totalPrice; }
    public UUID getBookingId() { return bookingId; }

    @Override
    public String toString() {
        return "Booking#" + bookingId + " | Car: " + car + ", From: " + startDate + ", To: " + endDate + ", Total: Rs." + totalPrice;
    }
}

// ----------- SYSTEM MANAGER -----------

class SystemManager {
    static List<Car> cars = new ArrayList<>();
    static List<User> users = new ArrayList<>();
    static List<Booking> bookings = new ArrayList<>();

    static {
        cars.add(new Car("Toyota", "Corolla", 2020, "Sedan", 1500, "Petrol", "Auto"));
        cars.add(new Car("Tesla", "Model 3", 2022, "Electric", 3000, "Electric", "Auto"));
        Admin admin = new Admin();
        admin.setUsername("admin");
        admin.setPassword("admin");
        users.add(admin);
    }

    public static void addCar(Scanner scanner) {
        System.out.print("Make: "); String make = scanner.nextLine();
        System.out.print("Model: "); String model = scanner.nextLine();
        System.out.print("Year: "); int year = Integer.parseInt(scanner.nextLine());
        System.out.print("Type: "); String type = scanner.nextLine();
        System.out.print("Price/day: "); double price = Double.parseDouble(scanner.nextLine());
        System.out.print("Fuel: "); String fuel = scanner.nextLine();
        System.out.print("Transmission: "); String trans = scanner.nextLine();

        cars.add(new Car(make, model, year, type, price, fuel, trans));
        System.out.println("Car added successfully.");
    }

    public static void removeCar(Scanner scanner) {
        viewCars();
        System.out.print("Enter car index to remove: ");
        int index = Integer.parseInt(scanner.nextLine()) - 1;
        if (index >= 0 && index < cars.size()) {
            Car car = cars.remove(index);
            bookings.removeIf(b -> b.getCar().getId().equals(car.getId()));
            System.out.println("Car and related bookings removed.");
        } else {
            throw new IllegalArgumentException("Invalid index.");
        }
    }

    public static void updateCarStatus(Scanner scanner) {
        viewCars();
        System.out.print("Car index to update: ");
        int index = Integer.parseInt(scanner.nextLine()) - 1;
        if (index >= 0 && index < cars.size()) {
            System.out.print("New Status: ");
            String status = scanner.nextLine();
            cars.get(index).setStatus(status);
            System.out.println("Status updated.");
        } else {
            throw new IllegalArgumentException("Invalid index.");
        }
    }

    public static void viewCars() {
        System.out.println("\n-- Car List --");
        for (int i = 0; i < cars.size(); i++) {
            Car c = cars.get(i);
            System.out.printf("%02d: %s [%s]\n", i + 1, c, c.getStatus());
        }
    }

    public static void viewBookings() {
        bookings.forEach(System.out::println);
    }

    public static void viewEarnings() {
        double total = bookings.stream().mapToDouble(Booking::getTotalPrice).sum();
        System.out.println("Total Earnings: Rs. " + total);
    }

    public static void viewUsers() {
        users.forEach(u -> System.out.println(u.getUsername() + " (" + (u instanceof Admin ? "Admin" : "Customer") + ")"));
    }

    public static void bookCar(Scanner scanner, Customer customer, List<Booking> myBookings) throws Exception {
        viewCars();
        System.out.print("Choose car index: ");
        int index = Integer.parseInt(scanner.nextLine()) - 1;
        if (index < 0 || index >= cars.size()) throw new Exception("Invalid index.");

        Car car = cars.get(index);
        if (!car.getStatus().equals("Available")) throw new Exception("Car not available.");

        System.out.print("Start Day (1-29): "); int s = Integer.parseInt(scanner.nextLine());
        System.out.print("End Day (1-29): "); int e = Integer.parseInt(scanner.nextLine());
        if (s > e || s < 1 || e > 29) throw new Exception("Invalid dates.");

        double total = car.getPricePerDay() * (e - s + 1);
        Booking booking = new Booking(car, customer, s + "/Jul", e + "/Jul", total);
        car.setStatus("Rented");
        myBookings.add(booking);
        bookings.add(booking);
        System.out.println("Booking confirmed.");
    }

    public static void viewCustomerBookings(List<Booking> myBookings) {
        if (myBookings.isEmpty()) System.out.println("No bookings.");
        else myBookings.forEach(System.out::println);
    }

    public static void returnCar(Scanner scanner, List<Booking> myBookings) {
        if (myBookings.isEmpty()) {
            System.out.println("No active bookings.");
            return;
        }
        for (int i = 0; i < myBookings.size(); i++) {
            System.out.println(i + 1 + ": " + myBookings.get(i));
        }
        System.out.print("Select booking to return: ");
        int index = Integer.parseInt(scanner.nextLine()) - 1;
        if (index >= 0 && index < myBookings.size()) {
            Booking b = myBookings.remove(index);
            b.getCar().setStatus("Available");
            bookings.removeIf(book -> book.getBookingId().equals(b.getBookingId()));
            System.out.println("Car returned successfully.");
        } else {
            System.out.println("Invalid selection.");
        }
    }

    public static User authenticate(String username, String password) {
        return users.stream().filter(u -> u.getUsername().equals(username) && u.login(password)).findFirst().orElse(null);
    }

    public static boolean usernameExists(String username) {
        return users.stream().anyMatch(u -> u.getUsername().equals(username));
    }
}

// ----------- MAIN ENTRY POINT -----------

public class RentXApp {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Welcome to RentX Car Rental System");

        while (true) {
            System.out.println("\n1. Login\n2. Sign Up\n3. Admin Login\n4. Exit");
            System.out.print("Choose option: ");
            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    System.out.print("Username: ");
                    String user = scanner.nextLine();
                    System.out.print("Password: ");
                    String pass = scanner.nextLine();
                    User u = SystemManager.authenticate(user, pass);
                    if (u instanceof Customer) u.showMenu(scanner);
                    else System.out.println("Invalid login.");
                    break;
                case "2":
                    System.out.print("New Username: ");
                    String newUser = scanner.nextLine();
                    System.out.print("Password: ");
                    String newPass = scanner.nextLine();
                    if (SystemManager.usernameExists(newUser)) {
                        System.out.println("Username taken.");
                    } else {
                        Customer c = new Customer();
                        c.setUsername(newUser);
                        c.setPassword(newPass);
                        SystemManager.users.add(c);
                        System.out.println("Registered successfully.");
                    }
                    break;
                case "3":
                    System.out.print("Admin Username: ");
                    String adminUser = scanner.nextLine();
                    System.out.print("Password: ");
                    String adminPass = scanner.nextLine();
                    User admin = SystemManager.authenticate(adminUser, adminPass);
                    if (admin instanceof Admin) admin.showMenu(scanner);
                    else System.out.println("Invalid admin login.");
                    break;
                case "4":
                    System.out.println("Goodbye!");
                    return;
                default:
                    System.out.println("Invalid option.");
            }
        }
    }
}
