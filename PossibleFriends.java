
import java.util.Scanner;


public class PossibleFriends {

    private static Scanner scanner = new Scanner(System.in);

    public static void search(){

        int T,l;
        String []matrix = new String[51];
        int []  friends = new int[51];

        T = scanner.nextInt();
        String line = scanner.nextLine();
        l = line.length();
        matrix[0] = line;

        for (int t = 0; t < T; t++){
            line = scanner.nextLine();
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
                    char ch = matrix[i].charAt(j);


                    if (ch == 'N'){
                        for( int k = 0; k < l; k++) {
                            char ch1 = matrix[j].charAt(k);
                            char ch2 = matrix[i].charAt(k);

                            if((ch1 =='Y') && (ch2 =='Y'))             // k is a common friend of j and i
                            {
                                friends[j]++;
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

                if( friends[i] > temp )
                {
                    temp = friends[i];
                    temp1 = i;
                }





        }
            System.out.println( temp1 + " " + temp);




        }
        scanner.close();


    }
    public static void main(String[] args) throws java.lang.Exception
    {
        search();
    }


}
