import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;
import java.io.FileWriter;

public class polRoot {

    static double[] coeff = new double[20]; // coefficients of original equation 
    static double[] fx = new double[20];
    static int degree; // Max degree of polynomial
    static int totalIt; //Temp variable to store total amount of iterations (used to write to file)
    static int passOrFail = 0; //Set to 1 if fail to converge (used to write to file)

    public static void fillArray(String fileName) throws IOException{

        // Initiate file reader
        try {
            // Gets current working directory and concatenates the filename
            FileReader file = new FileReader(System.getProperty("user.dir").concat("/"+fileName));
            Scanner readInput = new Scanner(file);
            //Set degree
            degree = readInput.nextInt();

            //Fill coefficient array; Will start at the largest index and decrement until it reaches the constant
            for(int i = degree; i >= 0; i--){
                coeff[i] = readInput.nextDouble();
            }

            //Fill coefficients for derivative array
            for(int i = degree - 1; i >= 0; i--){
                fx[i] = coeff[i + 1] * (i + 1);
            }

            readInput.close();
            file.close();
        } catch (FileNotFoundException e) {
            System.out.println("File could not be found.");
        }

    }

    //Get F(xVal) or y coordinate at the inputted x value
    public static double getFunctionValue(double xVal){
        double funcValue = 0;
        for(int i = 0; i <= degree;i++){
            //System.out.println("Coeff at " + i + "is" + coeff[i] + " MathpowX is" + Math.pow(xVal, i));
            funcValue += coeff[i] * (double)Math.pow(xVal, i);
        }
        return funcValue;
    }

    //Get derivative of function at point x
    public static double getDerivative(double x){
        double derivValue = 0;
        for(int i = degree - 1; i >= 0; i--){
            derivValue += fx[i] * (double)Math.pow(x, i);
        }
        return derivValue;
    }

    public static void swap(double a, double b){
        double temp = 0;
        temp = a;
        a = b;
        b = temp;

        System.out.println("a is " + a + "b is " + b);
    }

    public static void fileWrite(double solution, String outputFile){
        try{
            FileWriter writer = new FileWriter(System.getProperty("user.dir").concat("/"+outputFile+".sol"));
                writer.write("Root: " + solution + "\tIterations: " + totalIt); // Writes solution to 8 decimal places
                if(passOrFail == 0){
                    writer.write("\tSuccess");
                } else{
                    writer.write("\tFail");
                }
                writer.close();
            }
            catch(Exception e){
                System.out.println("\nError trying to write solutions to file.");
            }
    }

    public static double Bisection(double a, double b, int maxIter, double eps){
        double fa = getFunctionValue(a); // F(a)
        double fb = getFunctionValue(b); // F(b)
        double c = 0;
        double fc = 0;
        // either F(A) or F(b) must be negative
        if(fa * fb >= 0){
            System.out.println("Inadequate values for a and b.");
            return (double) -1.0;
        }

        double error = b - a;

        for(int it = 1; it <= maxIter;it++){
            error = error/2;
            c = a + error;
            fc = getFunctionValue(c);
            
            // Check if error is less than epsilon or if zero is found
            if(Math.abs(error) < eps || fc == 0){
                System.out.println("Algorithm has converged after #" + it + " iterations!");
                totalIt = it;
                return c;
            }

            // Update values of b or a
            if(fa * fc < 0){
                b = c;
                fb = fc;
            } else{
                a = c;
                fa = fc;
            }
        }

        System.out.println("Max iterations reached without convergence...");
        totalIt = maxIter;
        passOrFail = 1;
        return c;
    }
    
    // Newton method of convergence
    public static double Newton(double x, int maxIter, double eps, double delta){
        double fx = getFunctionValue(x);

        for(int it = 1; it <= maxIter; it++){
            double fd = getDerivative(x);

            if(Math.abs(fd) < delta){
                System.out.println("Small Slope!");
                return x;
            }
        
            double d = fx/fd;
            x = x - d;
            fx = getFunctionValue(x);

            if(Math.abs(d) < eps){
                System.out.println("Algorithm has converged after #" + it + " iterations!");
                totalIt += it;
                return x;
            }
        }

        System.out.println("Max iterations reached without convergence...");
        totalIt = maxIter;
        passOrFail = 1;
        return x;
    }

    //Secant method of convergence
    public static double Secant(double a, double b, int maxIter, double eps){
        double fa = getFunctionValue(a);
        double fb = getFunctionValue(b);
        double temp = 0;
        if(Math.abs(fa) > Math.abs(fb)){
            //Swap a and b
            temp = a;
            a = b;
            b = temp;
            //Swap fa and fb
            temp = fa;
            fa = fb;
            fb = temp;
        }

        for(int it = 1; it <= maxIter; it++){
            if(Math.abs(fa) > Math.abs(fb)){
                //Swap a and b
                temp = a;
                a = b;
                b = temp;
                //Swap fa and fb
                temp = fa;
                fa = fb;
                fb = temp;
            }

            double d = 0;

            //Check if (fb - fa) is zero. If it is then return Fail and current value of a
            if((fb-fa) == 0){
                totalIt = it;
                passOrFail = 1;
                System.out.println("(fb - fa) is 0. Secant method can not divide by 0. Returning a. Iterations made: " + it);
                return a;
            }

            d = (b - a) / (fb - fa);
            b = a;
            fb = fa;
            d = d * fa;

            if(Math.abs(d) < eps){
                System.out.println("Algorithm has converged after #" + it + " iterations!");
                totalIt = it;
                return a;
            }

            a = a - d;
            fa = getFunctionValue(a);
        }

        System.out.println("Maximum number of iterations reached!");
        totalIt = maxIter;
        passOrFail = 1;
        return a;
    }

    // Uses Bisection method from range -10000 to 10000 until 100 iterations or epsilon 1. Then uses result as start for Newton method.
    public static double hybrid(double a, double b, int maxIter, double eps, double delta){
        double solution = Newton(Bisection(a, b, 100, (double)0.01), maxIter, eps, delta);
        return solution;
    }



    public static void main(String[] args) throws IOException {
        double initP = 0;  //Starting point #1
        double initP2 = 0; //Starting point #2
        int maxIter = 10000; //Maximum iterations
        double eps = (double) Math.pow(2, -126); //Epsilon 
        double delta = (double) 0.00001; //Acceptable range to converge
        String inputFile = args[args.length - 1];  // Sets input file by using the last argument in args[]
        String outputName = inputFile.substring( 0, inputFile.indexOf(".")); //removes the file extension from input file
        fillArray(inputFile); //Fill array with input file

        //Check for custom maxIter
        for(int i = 0; i < args.length; i++){
            if(args[i].equals("-maxIter") || args[i].equals("-maxIt")){
                maxIter = Integer.parseInt(args[i + 1]);
                System.out.println("Max Iterations is set to: "+maxIter);
            }
        }
        switch(args[0]){
            case "-newt":
                initP = Double.parseDouble(args[args.length - 2]);
                fileWrite(Newton(initP, maxIter, eps, delta), outputName);
                //System.out.println(Newton(initP, maxIter, eps, delta));
                break;
            case "-sec":
                initP = Double.parseDouble(args[args.length - 3]);
                initP2 = Double.parseDouble(args[args.length - 2]);
                fileWrite(Secant(initP, initP2, maxIter, eps), outputName);
                //System.out.println(Secant(initP, initP2, 10000, (double)0.000001));
                break;
            case "-hybrid":
                initP = Double.parseDouble(args[args.length - 3]);
                initP2 = Double.parseDouble(args[args.length - 2]);
                fileWrite(hybrid(initP,initP2, maxIter,(double)0.0000001,(double)0.000000001), outputName);
                //System.out.println(hybrid(initP,initP2, maxIter,eps,delta));
                break;
            default:
                initP = Double.parseDouble(args[args.length - 3]);
                initP2 = Double.parseDouble(args[args.length - 2]);
                fileWrite(Bisection(initP, initP2, maxIter, eps), outputName);
                //System.out.println(Bisection(initP, initP2, maxIter, eps));  
        }
    }
}