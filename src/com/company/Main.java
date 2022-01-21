package com.company;
import static com.company.Common_code.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Main {

    public static void main(String[] args) {
        ServerSocket server = null;

        try {

            // server is listening on port 1234
            server = new ServerSocket(1234);
            server.setReuseAddress(true);


            while (true) {
                Socket client = server.accept();
                System.out.println("New client connected"
                        + client.getInetAddress()
                        .getHostAddress());


                ClientHandler clientSock
                        = new ClientHandler(client);
                new Thread(clientSock).start();
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            if (server != null) {
                try {
                    server.close();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // ClientHandler class
    private static class ClientHandler implements Runnable  {
        private final Socket clientSocket;

        // Constructor
        public ClientHandler(Socket socket)
        {
            this.clientSocket = socket;
        }

        public void run()  {
            Scanner sc = new Scanner(System.in);
            ServerSocket serverSocket = null;
            InputStreamReader ir = null;
            OutputStreamWriter os = null;
            BufferedReader br = null;
            BufferedWriter bw = null;
            Socket s = null;
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd-HH-mm-ss");
            LocalDateTime now = LocalDateTime.now();
            try {
                ir = new InputStreamReader(clientSocket.getInputStream());
                os = new OutputStreamWriter(clientSocket.getOutputStream());
                br = new BufferedReader(ir);
                bw = new BufferedWriter(os);
            } catch (IOException e) {
                e.printStackTrace();
            }

            while (true) {
                try{
                while (true) {
                    String choice = br.readLine();
                    if (choice == null) {
                        break;
                    }

                    if (choice.equals("newuser")) {

                        String username;
                        while (true) {
                            username = br.readLine();
                            Boolean flag = verifyusername(username);

                            if (flag) {
                                bw.write("valid");
                                bw.newLine();
                                bw.flush();
                                break;
                            } else if (flag == false) {
                                bw.write("Invalid");
                                bw.newLine();
                                bw.flush();
                            }
                        }

                        String password = br.readLine();

                        String accountnumber = br.readLine();

                        String accountype = br.readLine();

                        ArrayList<String> l1 = new ArrayList<>();
                        l1.add(0, dtf.format(now) + " " + 0 + "+");

                        if (accountype.equals("1")) {
                            write_accountmap(username, password, accountnumber, "Saving");
                            write_transaction(username, l1);
                        } else if (accountype.equals("2")) {
                            write_accountmap(username, password, accountnumber, "Current");
                            write_transaction(username, l1);
                        }


                        bw.write(String.valueOf("Login Successful"));
                        bw.newLine();
                        bw.flush();
                        break;
                    }

                    if (choice.equals("existinguser")) {
                        var username = br.readLine();
                        String password;
                        while (true) {
                            password = br.readLine();
                            boolean flag = verifypassword(username, password);
                            if (flag == true) {
                                bw.write("valid");
                                bw.newLine();
                                bw.flush();
                                break;
                            } else {
                                bw.write("invalid");
                                bw.newLine();
                                bw.flush();
                            }

                        }

                        bw.write("Log in successfull");
                        bw.newLine();
                        bw.flush();

                        break;

                    }

                    if (choice.equals("forget")) {
                        HashMap<String, ArrayList<String>> accountmap = new HashMap<>();
                        accountmap = read_accountmap();
                        ArrayList<String> l1 = new ArrayList<>();
                        l1 = accountmap.get(br.readLine());
                        String number = (String) l1.get(1);
                        int otp = (int) (Math.random() * (201) + 200);
                        sendsms(("Your otp is:" + String.valueOf(otp)+" \n" + "Please don't share OTP with anyone"), number);

                        if (Integer.parseInt(br.readLine()) == otp) {
                            bw.write("Your password is : " + l1.get(0));
                            bw.newLine();
                            bw.flush();
                        }
                        break;

                    }

                    if (choice.equals("deposit")) {
                        String username = br.readLine();
                        String amount = br.readLine();

                        ArrayList<String> l1 = new ArrayList<>();
                        ArrayList<String> l2 = new ArrayList<>();
                        HashMap<String, ArrayList<String>> hp = new HashMap<>();
                        hp = read_transaction();

                        l1 = hp.get(username);
                        if (l1 == null || l1.isEmpty()) {

                            l2.add(dtf.format(now) + " " + amount + "+");
                            hp.put(username, l2);
                            l1 = l2;

                        } else {
                            l1.add(dtf.format(now) + " " + amount + "+");
                            hp.put(username, l1);
                        }

                        write_transaction(username, l1);
                        bw.write("Amount Added Successfully.   " + "  Your new balance is:" + getBalance(username));
                        bw.newLine();
                        bw.flush();
                        break;
                    }

                    if (choice.equals("withdraw")) {
                        HashMap<String, ArrayList<String>> transactionmap = new HashMap<>();
                        String username = br.readLine();
                        String amount = br.readLine();

                        int balance = getBalance(username);
                        if (Integer.parseInt(amount) > balance) {
                            bw.write("Not sufficient balance");
                            bw.newLine();
                            bw.flush();
                        } else {
                            transactionmap = read_transaction();
                            ArrayList<String> l1 = new ArrayList<>();
                            l1 = transactionmap.get(username);
                            l1.add(dtf.format(now) + " " + amount + "-");

                            transactionmap.put(username, l1);
                            write_transaction(username, l1);
                            bw.write("Your new balance is: " + getBalance(username));
                            bw.newLine();
                            bw.flush();
                        }
                        break;


                    }

                    if (choice.equals("balance")) {
                        HashMap<String, ArrayList<String>> accountmap = new HashMap<>();
                        String username= br.readLine();
                        accountmap = read_accountmap();
                        ArrayList l1 = new ArrayList();
                        l1 = accountmap.get(username);
                        String number = (String) l1.get(1);
                        sendsms(("Your account balance is: Rs." + getBalance(username)), number);
                        bw.write(String.valueOf(getBalance(username)));
                        bw.newLine();
                        bw.flush();
                        break;

                    }

                    if (choice.equals("generatestatement")) {
                        String username= br.readLine();
                        HashMap<String, ArrayList<String>> transactionmap = new HashMap<>();

                        ArrayList<String> l1 = new ArrayList<>();
                        transactionmap = read_transaction();
                        l1 = transactionmap.get(username);

                        String s1 = l1.get(0) + "@";

                        if (l1 == null) {
                            bw.write("No transactions found");
                            bw.newLine();
                            bw.flush();
                        } else {
                            for (int i = 0; i < l1.size(); i++) {
                                s1 = s1 + l1.get(i) + "@";

                            }
                            s1 = s1 + String.valueOf(getBalance(username));

                            bw.write(s1);
                            bw.newLine();
                            bw.flush();

                        }
                        break;

                    }

                    if (choice.equals("search")) {
                        String username= br.readLine();
                        String options = br.readLine();

                        if (options.equals("particular")) {
                            String startdate = dtf.format(now.minusMonths(1));
                            String enddate = dtf.format(now);
                            Date startDate = validateDate(startdate);
                            Date endDate = validateDate(enddate);
                            String search = searchtransaction(username, startDate, endDate);
                            if (search == null) {
                                bw.write("No transaction to print");
                                bw.newLine();
                                bw.flush();
                            } else {
                                bw.write(search);
                                bw.newLine();
                                bw.flush();
                            }
                        } else if (options.equals("particularsix")) {
                            String startdate = dtf.format(now.minusMonths(6));
                            String enddate = dtf.format(now);
                            Date startDate = validateDate(startdate);
                            Date endDate = validateDate(enddate);
                            String search = searchtransaction(username, startDate, endDate);
                            if (search == null) {
                                bw.write("No transaction to print");
                                bw.newLine();
                                bw.flush();
                            } else {
                                bw.write(search);
                                bw.newLine();
                                bw.flush();
                            }
                        } else if(options.equals("custom")){
                            String start = br.readLine();
                            String end = br.readLine();
                            System.out.println(start);
                            System.out.println(end);

                            Date startdate = validateDate(start);
                            Date enddate = validateDate(end);
                            String search = searchtransaction(username, startdate, enddate);
                            System.out.println(search);
                            if (search == null) {
                                bw.write("No transaction to print");
                                bw.newLine();
                                bw.flush();
                            } else {
                                bw.write(search);
                                bw.newLine();
                                bw.flush();
                            }
                        }
                        break;

                    }

                    if (choice.equals("transfer")) {
                        String userusername= br.readLine();
                        String username = br.readLine();
                        String amount = br.readLine();
                        HashMap<String, ArrayList<String>> transactionmap = new HashMap<>();
                        if (verifyusername(username) == true) {
                            bw.write("Username not found. Invalid transaction. Directing you to the main menu");
                            bw.newLine();
                            bw.flush();

                        } else if (getBalance(userusername) < Integer.parseInt(amount)) {
                            bw.write("Not enough balance to tranfer");
                            bw.newLine();
                            bw.flush();
                        } else if (verifyusername(username) == false) {
                            ArrayList<String> l1 = new ArrayList<>();
                            ArrayList<String> l2 = new ArrayList<>();
                            transactionmap = read_transaction();
                            l1 = transactionmap.get(userusername);
                            l1.add(dtf.format(now) + " " + amount + "-");
                            transactionmap.put(userusername, l1);
                            l2 = transactionmap.get(username);
                            l2.add(dtf.format(now) + " " + amount + "+");
                            transactionmap.put(username, l2);
                            //System.out.println(l1);
                            write_transaction(userusername, l1);
                            write_transaction(username, l2);

                            bw.write("Amount transferred successfully");
                            bw.newLine();
                            bw.flush();
                        }

                        break;
                    }

                }
                } catch (IOException | ParseException e) {
                    e.printStackTrace();
                }


            }
        }
    }

    }

