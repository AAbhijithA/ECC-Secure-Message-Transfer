/*
* Elliptic Curve Equation used
* E(1,1) -> y^2 = x^3 + x + 1
* n is 256
* na, nb < 256
* Global Variable G -> Order of n is 333 => E((333^2 + 333 + 1)^(1/2))
* 1 <= k <= 1000
* Authors: Abhijith Ajith, Aravind Krishnan, Amoghavarsh SH
*/

import java.util.*;
import java.io.*;
import java.net.*;

class precomp {
    //Precomputation of hash Key value and Decryption
    HashMap<Double,Integer> decoder = new HashMap<Double,Integer>();

    public precomp(){
        for(int i=0;i<=255;i++){
            double eq = Math.sqrt((i*i*i)+i+1);
            decoder.put(eq,i);
        }
    }

    public double findClosestKey(double dou) {
        double minDiff = Double.MAX_VALUE;
        double closestKey = 0.0;
        for (double key : decoder.keySet()) {
            double diff = Math.abs(dou - key);
            if (diff < minDiff) {
                minDiff = diff;
                closestKey = key;
            }
        }
        return closestKey;
    }

    public char mapper(double dou){
        double closestKey = findClosestKey(dou);
        int dec = decoder.get(closestKey);
        return (char) dec;
    }
}

class ServerFunctions{

    precomp obj = new precomp();

    //ECC Function
    public double ECC(int x){
        int ysq = (x*x*x) + (x*x) + 1;
        double E = Math.sqrt(ysq);
        return E;
    }

    //Private Key Generation & Utility
    public int PrivateKeyGenerator(){
        int l = 1, h = 256;
        int nb = (int) (Math.random() * (h - l)) + l;
        return nb;
    }

    public String ConvertString(double x){
        String s = String.valueOf(x);
        return s;
    }

    public char decrypter(double x, double y,int nb){
        double Pm = y - (nb * x);
        char cha = obj.mapper(Pm);
        return cha;
    }

}
public class server {

    //Initializing parameters & variables
    static int globalX = 333;
    static ServerSocket Serversoc = null;
    static Socket soc = null;
    static DataInputStream input = null;
    static DataOutputStream output = null;
    static BufferedReader bufRead = null;
    public static void main(String[] args) throws Exception{
        try{
            //Initializing Sockets between Client & Server
            Serversoc = new ServerSocket(3333);
            soc = Serversoc.accept();
            input = new DataInputStream(soc.getInputStream());
            output = new DataOutputStream(soc.getOutputStream());
            bufRead = new BufferedReader(new InputStreamReader(System.in));

            //Initializing server functionalities and cumputed G value
            ServerFunctions f = new ServerFunctions();
            double G = f.ECC(globalX);
            while(true){
                int nb = f.PrivateKeyGenerator();
                System.out.println("Generated private key (server): " + nb);
                double Pb = nb * G;
                String publicKey = f.ConvertString(Pb);
                System.out.println("Sending public key (server): " + Pb);
                output.writeUTF(publicKey);
                output.flush();
                String message = "";
                int i = 1;
                while(true){
                    String KV = input.readUTF();
                    if(KV.equals("MessageSent")) break;
                    System.out.println("Recived from Client for character " + i + " {" + KV + "}");
                    String[] LR = KV.split(",");
                    double L = Double.parseDouble(LR[0]);
                    double R = Double.parseDouble(LR[1]);
                    char c = f.decrypter(L, R, nb);
                    message = message + c;
                    i += 1;
                }
                System.out.println("Message Recieved: " + message);
            }
        }
        catch(Exception e){
            System.out.println(e);
        }
        
        //Closing all the objects
        Serversoc.close();
        soc.close();
        input.close();
        output.close();
        bufRead.close();
    }
    
}
