import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;
import java.io.FileWriter;

public class polRoot {

    static float[] coeff = new float[20]; // coefficients of original equation 
    static float[] fx = new float[20];
    static int degree; // Max degree of polynomial

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
                System.out.println("Algorith has converged after #" + it + "iterations!");
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
                System.out.println("Algorithm has converged after #" + it + "iterations!");
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
                System.out.println("Algorithm has converged after #" + it + "iterations!");
                return a;
            }

            a = a - d;
            fa = getFunctionValue(a);
        }

        System.out.println("Maximum number of iterations reached!");
        return a;
    }

    // Uses Bisection method from range -10000 to 10000 until 100 iterations or epsilon 1. Then uses result as start for Newton method.
    public static float hybrid(){
        float zero = Newton(Bisection(-10000, 10000, 100, 1), 100000, (float)0.00000001, (float)0.000001);
        return zero;
    }



    public static void main(String[] args) throws IOException {
        float[] coeff = new float[20]; // coefficients of original equation 
        float[] fx = new float[20];

        fillArray("fun1.pol");
        System.out.println(Bisection(-1, 2, 1000, (float)0.00000001));
        System.out.println(Newton((float)5, 10000, (float)0.00001, (float)0.00001));
        System.out.println(Secant(0, 1, 10000, (float)0.000001));
        System.out.println("Hybrid is" + hybrid());
        //System.out.println(getFunctionValue(2));
    }
}