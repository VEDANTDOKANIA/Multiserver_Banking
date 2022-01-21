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
                    var choice = br.readLine();
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

                        var password = br.readLine();

                        var mobilenumber = br.readLine();

                        var accountype = br.readLine();

                        ArrayList<String> transactionlist = new ArrayList<>();
                        transactionlist.add(0, dtf.format(now) + " " + 0 + "+");

                        if (accountype.equals("1")) {
                            write_accountmap(username, password, mobilenumber, "Saving");
                            write_transaction(username, transactionlist);
                        } else if (accountype.equals("2")) {
                            write_accountmap(username, password, mobilenumber, "Current");
                            write_transaction(username, transactionlist);
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
                        ArrayList<String> accountdetails = new ArrayList<>();
                        accountdetails = accountmap.get(br.readLine());
                        String number = (String) accountdetails.get(1);
                        int otp = (int) (Math.random() * (201) + 200);
                        sendsms(("Your otp is:" + String.valueOf(otp)+" \n" + "Please don't share OTP with anyone"), number);

                        if (Integer.parseInt(br.readLine()) == otp) {
                            bw.write("Your password is : " + accountdetails.get(0));
                            bw.newLine();
                            bw.flush();
                        }
                        break;

                    }

                    if (choice.equals("deposit")) {
                        var username = br.readLine();
                        var amount = br.readLine();

                        ArrayList<String> usertransactions = new ArrayList<>();
                        ArrayList<String> newtransactions = new ArrayList<>();
                        HashMap<String, ArrayList<String>> transactionmap = new HashMap<>();
                        transactionmap = read_transaction();

                        usertransactions = transactionmap.get(username);
                        if (usertransactions == null || usertransactions.isEmpty()) {

                            newtransactions.add(dtf.format(now) + " " + amount + "+");
                            transactionmap.put(username, newtransactions);
                            usertransactions = newtransactions;

                        } else {
                            usertransactions.add(dtf.format(now) + " " + amount + "+");
                            transactionmap.put(username, usertransactions);
                        }

                        write_transaction(username, usertransactions);
                        bw.write("Amount Added Successfully." + "Your new balance is:" + getBalance(username));
                        bw.newLine();
                        bw.flush();
                        break;
                    }

                    if (choice.equals("withdraw")) {
                        HashMap<String, ArrayList<String>> transactionmap = new HashMap<>();
                        var username = br.readLine();
                        var amount = br.readLine();

                        int balance = getBalance(username);
                        if (Integer.parseInt(amount) > balance) {
                            bw.write("Not sufficient balance");
                            bw.newLine();
                            bw.flush();
                        } else {
                            transactionmap = read_transaction();
                            ArrayList<String> transactiondetails = new ArrayList<>();
                            transactiondetails = transactionmap.get(username);
                            transactiondetails.add(dtf.format(now) + " " + amount + "-");

                            transactionmap.put(username, transactiondetails);
                            write_transaction(username, transactiondetails);
                            bw.write("Your new balance is: " + getBalance(username));
                            bw.newLine();
                            bw.flush();
                        }
                        break;


                    }

                    if (choice.equals("balance")) {
                        HashMap<String, ArrayList<String>> accountmap = new HashMap<>();
                        var username= br.readLine();
                        accountmap = read_accountmap();
                        ArrayList transactionlist = new ArrayList();
                        transactionlist = accountmap.get(username);
                        String number = (String) transactionlist.get(1);
                        sendsms(("Your account balance is: Rs." + getBalance(username)), number);
                        bw.write(String.valueOf(getBalance(username)));
                        bw.newLine();
                        bw.flush();
                        break;

                    }

                    if (choice.equals("generatestatement")) {
                        var username= br.readLine();
                        HashMap<String, ArrayList<String>> transactionmap = new HashMap<>();

                        ArrayList<String> transactionlist = new ArrayList<>();
                        transactionmap = read_transaction();
                        transactionlist = transactionmap.get(username);

                        String s1 = transactionlist.get(0) + "@";

                        if (transactionlist == null) {
                            bw.write("No transactions found");
                            bw.newLine();
                            bw.flush();
                        } else {
                            for (int i = 0; i < transactionlist.size(); i++) {
                                s1 = s1 + transactionlist.get(i) + "@";

                            }
                            s1 = s1 + String.valueOf(getBalance(username));

                            bw.write(s1);
                            bw.newLine();
                            bw.flush();

                        }
                        break;

                    }

                    if (choice.equals("search")) {
                        var username= br.readLine();
                        var options = br.readLine();

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
                        }
                        else if (options.equals("particularsix")) {
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
                            var start = br.readLine();
                            var end = br.readLine();
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
                        var userusername= br.readLine();
                        var username = br.readLine();
                        var amount = br.readLine();
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
                            ArrayList<String> payeetransaction = new ArrayList<>();
                            ArrayList<String> paidtransaction = new ArrayList<>();
                            transactionmap = read_transaction();
                            payeetransaction = transactionmap.get(userusername);
                            payeetransaction.add(dtf.format(now) + " " + amount + "-");
                            transactionmap.put(userusername, payeetransaction);
                            paidtransaction = transactionmap.get(username);
                            paidtransaction.add(dtf.format(now) + " " + amount + "+");
                            transactionmap.put(username, paidtransaction);
                            //System.out.println(l1);
                            write_transaction(userusername, payeetransaction);
                            write_transaction(username, paidtransaction);

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

