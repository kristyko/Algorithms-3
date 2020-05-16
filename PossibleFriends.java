
import java.util.Scanner;


public class PossibleFriends {

    private static Scanner scanner = new Scanner(System.in);

    public static void search(){

        int T,l;    //T - number of cases
        String []matrix = new String[51];   //for input square matrix with symbols 'Y' on i,j character j of line i and i of line j 
                                            // in case i and j are friends or 'N' if not
                                            //The first line of the matrix corresponds to person 0, the next line to person 1...
        int []  friends = new int[51];      //  matrix to store zeroes and ones.
                                            // add 1 to place which is for i in j in friends matrix if they have a common friend k
                                         

        T = scanner.nextInt();
        String line = scanner.nextLine();
        l = line.length();
        matrix[0] = line;  //one row of matrix, looks like: YNYY

        for (int t = 0; t < T; t++){
            line = scanner.nextLine();   //reading input
            l = line.length();
            matrix[0] = line;

            for(int i = 0; i < l; i++){
                friends[i] = 0;
                if (i > 0){
                    matrix[i] = scanner.nextLine();
                }

            }
            for(int i = 0; i < l; i++){

                for(int j = i + 1; j < l; j++){
                    char ch = matrix[i].charAt(j);  //check symbol in matrix


                    if (ch == 'N'){
                        for( int k = 0; k < l; k++) {
                            char ch1 = matrix[j].charAt(k);
                            char ch2 = matrix[i].charAt(k);

                            if((ch1 =='Y') && (ch2 =='Y'))             // k is a common friend of j and i
                            {
                                friends[j]++;       // adding 1 to place which is for i in j in friends matrix if they have a common friend k
                                friends[i]++;
                                break;
                            }
                        }

                    }
                }
            }

            int temp = 0;
            int temp1 = 0;
            for( int i = 0; i < l; i++ ){

                if( friends[i] > temp )  //search for a person who has more possible friends
                {
                    temp = friends[i];
                    temp1 = i;
                }





        }
            System.out.println( temp1 + " " + temp); // print out the ID of the person that has more possible friends and
                                                    // the number of possible friends this person has




        }
        scanner.close();


    }
    public static void main(String[] args) throws java.lang.Exception
    {
        search();
    }


}
