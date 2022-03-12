import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;
import java.io.FileWriter;

public class polRoot {

    static float[] coeff = new float[20]; // coefficients of original equation 
    static float[] fx = new float[20];
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
                coeff[i] = readInput.nextFloat();
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
    public static float getFunctionValue(float xVal){
        float funcValue = 0;
        for(int i = 0; i <= degree;i++){
            //System.out.println("Coeff at " + i + "is" + coeff[i] + " MathpowX is" + Math.pow(xVal, i));
            funcValue += coeff[i] * (float)Math.pow(xVal, i);
        }
        return funcValue;
    }

    //Get derivative of function at point x
    public static float getDerivative(float x){
        float derivValue = 0;
        for(int i = degree - 1; i >= 0; i--){
            derivValue += fx[i] * (float)Math.pow(x, i);
        }
        return derivValue;
    }

    public static void swap(float a, float b){
        float temp = 0;
        temp = a;
        a = b;
        b = temp;

        System.out.println("a is " + a + "b is " + b);
    }

    public static void fileWrite(float solution, String outputFile){
        try{
            FileWriter writer = new FileWriter(System.getProperty("user.dir").concat("/"+outputFile+".sol"));
                writer.write("Root: " + solution + "\tIterations: " + totalIt);
                writer.close();
            }
            catch(Exception e){
                System.out.println("\nError trying to write solutions to file.");
            }
    }

    public static float Bisection(float a, float b, int maxIter, float eps){
        float fa = getFunctionValue(a); // F(a)
        float fb = getFunctionValue(b); // F(b)
        float c = 0;
        float fc = 0;
        // either F(A) or F(b) must be negative
        if(fa * fb >= 0){
            System.out.println("Inadequate values for a and b.");
            return (float) -1.0;
        }

        float error = b - a;

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
        return c;
    }
    
    // Newton method of convergence
    public static float Newton(float x, int maxIter, float eps, float delta){
        float fx = getFunctionValue(x);

        for(int it = 1; it <= maxIter; it++){
            float fd = getDerivative(x);

            if(Math.abs(fd) < delta){
                System.out.println("Small Slope!");
                return x;
            }
        
            float d = fx/fd;
            x = x - d;
            fx = getFunctionValue(x);

            if(Math.abs(d) < eps){
                System.out.println("Algorithm has converged after #" + it + " iterations!");
                return x;
            }
        }

        System.out.println("Max iterations reached without convergence...");
        return x;
    }

    //Secant method of convergence
    public static float Secant(float a, float b, int maxIter, float eps){
        float fa = getFunctionValue(a);
        float fb = getFunctionValue(b);
        float temp = 0;
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

            float d = 0;
            d = (b - a) / (fb - fa);
            b = a;
            fb = fa;
            d = d * fa;

            if(Math.abs(d) < eps){
                System.out.println("Algorithm has converged after #" + it + " iterations!");
                return a;
            }

            a = a - d;
            fa = getFunctionValue(a);
        }

        System.out.println("Maximum number of iterations reached!");
        return a;
    }

    // Uses Bisection method from range -10000 to 10000 until 100 iterations or epsilon 1. Then uses result as start for Newton method.
    public static float hybrid(float a, float b, int maxIter, float eps, float delta){
        float zero = Newton(Bisection(a, b, 100, (float)0.1), maxIter, eps, delta);
        return zero;
    }



    public static void main(String[] args) throws IOException {
        float initP = 0;  //Starting point
        float initP2 = 0; //Starting point 2
        int maxIter = 10000; //Maximum iterations
        float eps = (float) 0.00000000001; //Epsilon 
        float delta = (float) 0.00001; //Acceptable range to converge
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
                initP = Float.parseFloat(args[args.length - 2]);
                fileWrite(Newton(initP, maxIter, eps, delta), outputName);
                //System.out.println(Newton(initP, maxIter, eps, delta));
                break;
            case "-sec":
                initP = Float.parseFloat(args[args.length - 3]);
                initP2 = Float.parseFloat(args[args.length - 2]);
                fileWrite(Secant(initP, initP2, maxIter, (float)0.000001), outputName);
                //System.out.println(Secant(initP, initP2, 10000, (float)0.000001));
                break;
            case "-hybrid":
                initP = Float.parseFloat(args[args.length - 3]);
                initP2 = Float.parseFloat(args[args.length - 2]);
                fileWrite(hybrid(initP,initP2, maxIter,eps,delta), outputName);
                //System.out.println(hybrid(initP,initP2, maxIter,eps,delta));
                break;
            default:
                initP = Float.parseFloat(args[args.length - 3]);
                initP2 = Float.parseFloat(args[args.length - 2]);
                fileWrite(Bisection(initP, initP2, maxIter, eps), outputName);
                //System.out.println(Bisection(initP, initP2, maxIter, eps));  
        }
        /*
        System.out.println(Bisection(-1, 2, 1000, (float)0.00000001));
        System.out.println(Newton((float)5, 10000, (float)0.00001, (float)0.00001));
        System.out.println(Secant(0, 1, 10000, (float)0.000001));
        System.out.println("Hybrid is" + hybrid());
        fileWrite(Bisection(-1, 2, 1000, (float)0.00000001));
        */
    }
}